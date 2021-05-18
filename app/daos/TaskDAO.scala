package daos

import models.{Tasks, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import collection.mutable

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import daos.UserDAO

import scala.collection.mutable.ListBuffer


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

  def deleteTask(taskname: String) = {
    val deleteQuery = tasksVal.filter(_.task === taskname).delete
    db.run(deleteQuery)
  }


  private class TaskTable(tag: Tag) extends Table[Tasks](tag, "TASKS") {
    def task = column[String]("TASK", O.PrimaryKey)
    def username = column[String]("USERNAME")

    def * = (task, username) <> (Tasks.tupled, Tasks.unapply)
  }

}
