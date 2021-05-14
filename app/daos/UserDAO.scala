package daos

import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                       (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val Users = TableQuery[UserTable]

  def allUser(): Future[Seq[User]] = db.run(Users.result)

  def insertUser(user: User): Future[Unit] = db.run(Users += user).map { _ => () }

  def checkUserExists(username: String): Boolean = {
    val users = allUser()
    for (user <- users) (if (user.head.name == username) {
      println("Nutzer existiert")
      true
    } else {
      println("Nutzer existiert nicht")
      false
    })
    false
  }

  def checkPassword(username: String, password: String) ={
    val query = Users.filter(_.name === username)
    val pass = db.run[Seq[User]](query.result)
    for (p <- pass) (if (p.head.password.toString == password) {
      println("passwort korrekt")
      true
    } else{
      println("passwort nicht korrekt")
      false
    })
    false
  }


  private class UserTable(tag: Tag) extends Table[User](tag, "USER") {

    def name = column[String]("NAME", O.PrimaryKey)
    def password = column[String]("PASSWORD")

    def * = (name, password) <> (User.tupled, User.unapply)
  }


}
