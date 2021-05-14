package daos

import models.{Tasks, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import daos.UserDAO

import scala.collection.mutable.ListBuffer


class TaksDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, userDAO: UserDAO)
                       (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val TasksVal = TableQuery[TaskTable]

  def allTasks(name: String) ={
    val tasksquery = TasksVal.filter(_.username === name).result
    val tasks: Future[Seq[Tasks]] = db.run[Seq[Tasks]](tasksquery)
    tasks
  }

  private class TaskTable(tag: Tag) extends Table[Tasks](tag, "TASKS") {
    def id = column[Int]("ID", O.PrimaryKey)
    def task = column[String]("TASK")
    def username = column[String]("USERNAME")

    def * = (id, task, username) <> (Tasks.tupled, Tasks.unapply)
  }

}
