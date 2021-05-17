package daos

import controllers.routes
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                       (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val Users = TableQuery[UserTable]

  def allUser(): Future[Seq[User]] = db.run(Users.result)

  def insertUser(user: User): Future[Unit] = db.run(Users += user).map { _ => () }

  def checkUserExists(username: String): Boolean = {
    val names: Future[Seq[User]] = db.run(Users.filter(_.name === username).result)
    val test = Await.result(names, 1.seconds)
    if (test.head.name == username){
      println("Nutzer existiert")
      true
    } else false
  }

  def checkPassword(username: String, password: String): Boolean ={
    val names: Future[Seq[User]] = db.run(Users.filter(_.name === username).result)
    val test = Await.result(names, 1.seconds)
    if (test.head.password == password){
      println("password korrekt")
      true
    } else false
  }

  def validateLogin(username: String, password: String): Boolean ={
    var result = false
    if(checkUserExists(username) & checkPassword(username, password)){
      print("true")
      result = true
    } else {
      result = false
    }
    result
  }


  private class UserTable(tag: Tag) extends Table[User](tag, "USER") {

    def name = column[String]("NAME", O.PrimaryKey)
    def password = column[String]("PASSWORD")

    def * = (name, password) <> (User.tupled, User.unapply)
  }


}
