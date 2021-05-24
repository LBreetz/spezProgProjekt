package daos

import models.{Tasks, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import collection.mutable
import javax.inject.Inject
import scala.concurrent.{Await, ExecutionContext, Future}
import daos.UserDAO

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt


class TaskDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, userDAO: UserDAO)
                       (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val tasksVal = TableQuery[TaskTable]

  def allTasks(): Future[Seq[Tasks]] = db.run(tasksVal.result)

  def allTasksFromUser(name: String): Future[Seq[Tasks]] ={
    db.run(tasksVal.filter(_.username === name).result)
  }

  def insertTask(taskObj: Tasks)={
    db.run(tasksVal += taskObj).map { _ => () }
  }

  def deleteTask(taskid: Int) = {
    val deleteQuery = tasksVal.filter(_.id === taskid).delete
    db.run(deleteQuery)
  }

  def autoIncrementID: Int = {
    try {
      val tasksListFuture = allTasks()
      val taskList = Await.result(tasksListFuture, 1.second)
      val ids = for (task <- taskList) yield task.id
      val newId = ids.max + 1
      newId
    } catch {
      case e : UnsupportedOperationException => 1
    }
  }

  def idUpdate(id: Int) = {
    val idQuery = tasksVal.filter(_.id > id).map(_.id).result
    val higherIdsFuture = db.run(idQuery)
    val higherIds = Await.result(higherIdsFuture, 1.second)
    for (id <- higherIds) {
      db.run(tasksVal.filter(_.id === id).map(_.id).update(id-1))
    }
  }


  private class TaskTable(tag: Tag) extends Table[Tasks](tag, "TASKS") {
    def id = column[Int]("ID", O.PrimaryKey)
    def task = column[String]("TASK")
    def username = column[String]("USERNAME")

    def * = (id, task, username) <> (Tasks.tupled, Tasks.unapply)
  }

}
