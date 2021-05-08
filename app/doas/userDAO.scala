package doas

import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class userDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                       (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val Users = TableQuery[UserTable]

  def all(): Future[Seq[User]] = db.run(Users.result)

  def insert(user: User): Future[Unit] = db.run(Users += user).map { _ => () }

  private class UserTable(tag: Tag) extends Table[User](tag, "USER") {

    def name = column[String]("NAME", O.PrimaryKey)
    def password = column[String]("PASSWORD")
    def task = column[String]("TASK")

    def * = (name, password, task) <> (User.tupled, User.unapply)
  }

}
