package controllers

import models.TodoListMemory
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}

import javax.inject._
import play.api._
import play.api.mvc._
import daos.UserDAO
import daos.TaksDAO
import models.User
import models.Tasks

import javax.inject._
import play.api._
import play.api.data.Form
import play.api.data.Forms.{mapping, number, text}
import play.api.mvc._

import scala.concurrent.ExecutionContext


@Singleton
class TodoList @Inject()(userDao: UserDAO, taksDAO: TaksDAO, controllerComponents: ControllerComponents)
                        (implicit executionContext: ExecutionContext)extends AbstractController(controllerComponents){

  def validateLoginPost = Action { implicit request =>
    val  postVals = request.body.asFormUrlEncoded //input field names (username, password) are keys for postVals map -> as you can see by using args(username)...
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head

      if (userDao.checkUserExists(username) & userDao.checkPassword(username, password)) {
        Redirect(routes.TodoList.todoList).withSession("username" -> username) // store username in session
      } else if (!userDao.checkUserExists(username) || !userDao.checkPassword(username, password)){
        Redirect(routes.HomeController.index).flashing("error" -> "invalid username or password!")
      }
      Redirect(routes.TodoList.todoList).withSession("username" -> username) // store username in session
    }.getOrElse(Redirect(routes.HomeController.index))
  }

  def createNewUser = Action { implicit request =>
    val  postVals = request.body.asFormUrlEncoded //input field names (username, password) are keys for postVals map -> as you can see by using args(username)...
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head
      val user: User = User(username, password)
      if (!userDao.checkUserExists(username)){
        userDao.insertUser(user).map(_ => Redirect(routes.TodoList.todoList).withSession("username" -> username))
        Redirect(routes.TodoList.todoList).withSession("username" -> username)
      } else {
        Redirect(routes.HomeController.create).flashing("error" -> "user already exists!")
      }
    }.getOrElse(Redirect(routes.HomeController.index).flashing("error" -> "an error has occurred!"))
  }

  def todoList = Action { implicit request =>
    val sessionUsername = request.session.get("username") //get username from session
    sessionUsername.map { username =>
      taksDAO.allTasks(username).map { case (tasks) => Ok(views.html.TodoList(tasks))}
      Ok("")
    }.getOrElse(Redirect(routes.HomeController.index))
  }

  def logout = Action{
    Redirect(routes.HomeController.index).withNewSession
  }

  def addTask = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { username =>
      val postVals = request.body.asFormUrlEncoded
      postVals.map { args =>
        val task = args("newTask").head
        if (task == "") Redirect(routes.TodoList.todoList)
        else {
          TodoListMemory.addTask(username, task);
          Redirect(routes.TodoList.todoList)
        }
      }.getOrElse(Redirect(routes.TodoList.todoList))
    }.getOrElse(Redirect(routes.HomeController.index()))
  }

  def deleteTask = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { username =>
      val postVals = request.body.asFormUrlEncoded
      postVals.map { args =>
        val index = args("index").head.toInt
        TodoListMemory.removeTask(username, index);
        Redirect(routes.TodoList.todoList)
      }.getOrElse(Redirect(routes.TodoList.todoList))
    }.getOrElse(Redirect(routes.HomeController.index()))
  }

  def showAllUser= Action.async { implicit request =>
    userDao.allUser().map { case (user) => Ok(views.html.test(user))}
  }

}
