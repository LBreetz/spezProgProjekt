package controllers

import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}

import javax.inject._
import play.api._
import play.api.mvc._
import daos.UserDAO
import daos.TaskDAO
import models.User
import models.Tasks

import javax.inject._
import play.api._
import play.api.data.Form
import play.api.data.Forms.{mapping, number, text}
import play.api.mvc._

import scala.concurrent.ExecutionContext


@Singleton
class TodoList @Inject()(userDAO: UserDAO, taskDAO: TaskDAO, controllerComponents: ControllerComponents)
                        (implicit executionContext: ExecutionContext)extends AbstractController(controllerComponents){

  val userForm = Form(
    mapping(
      "username" -> text(),
      "password" -> text())(User.apply)(User.unapply))

  def validateLoginPost = Action { implicit request =>
    val  postVals = request.body.asFormUrlEncoded //input field names (username, password) are keys for postVals map -> as you can see by using args(username)...
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head

      if (userDAO.validateLogin(username, password)){
        Redirect(routes.TodoList.todoList(username)).withSession("username" -> username)// store username in session
      } else Redirect(routes.HomeController.index).flashing("error" -> "invalid username or password!")

    }.getOrElse(Redirect(routes.HomeController.index))
  }

  def createNewUser = Action { implicit request =>
    val  postVals = request.body.asFormUrlEncoded //input field names (username, password) are keys for postVals map -> as you can see by using args(username)...
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head
      val user: User = User(username, password)
      if (!userDAO.checkUserExists(username)){
        userDAO.insertUser(user).map(_ => Redirect(routes.TodoList.todoList(username)).withSession("username" -> username))
        Redirect(routes.TodoList.todoList(username)).withSession("username" -> username)
      } else {
        Redirect(routes.HomeController.create).flashing("error" -> "user already exists!")
      }
    }.getOrElse(Redirect(routes.HomeController.index).flashing("error" -> "an error has occurred!"))
  }

  def todoList(username: String) = Action.async { implicit request =>
    taskDAO.allTasksFromUser(username).map { case (tasks) => Ok(views.html.TodoList(tasks))}
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
        if (task == "") Redirect(routes.TodoList.todoList(username))
        else {
          val taskObj = Tasks(taskDAO.autoIncrementID,task, username)
          taskDAO.insertTask(taskObj)
          Redirect(routes.TodoList.todoList(username))
        }
      }.getOrElse(Redirect(routes.TodoList.todoList(username)))
    }.getOrElse(Redirect(routes.HomeController.index()))
  }

  def deleteTask = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { username =>
      val postVals = request.body.asFormUrlEncoded
      postVals.map { args =>
        val index = args("index").head
        taskDAO.deleteTask(index.toInt)
        taskDAO.idUpdate(index.toInt)
        Redirect(routes.TodoList.todoList(username))
      }.getOrElse(Redirect(routes.TodoList.todoList(username)))
    }.getOrElse(Redirect(routes.HomeController.index()))
  }

  def showAllUser= Action.async { implicit request =>
    userDAO.allUser().map { case (user) => Ok(views.html.test(user))}
  }

  def showAllTasks= Action.async { implicit request =>
    taskDAO.allTasks().map {case (tasks) => Ok(views.html.testTask(tasks))}
  }

}
