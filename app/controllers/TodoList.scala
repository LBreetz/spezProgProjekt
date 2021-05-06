package controllers

import models.TodoListMemory
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}


@Singleton
class TodoList @Inject()(val controllerComponents: ControllerComponents) extends BaseController{

  def validateLoginPost = Action { implicit request =>
    val  postVals = request.body.asFormUrlEncoded //input field names (username, password) are keys for postVals map -> as you can see by using args(username)...
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head
      if (TodoListMemory.validateUser(username, password)) {
        Redirect(routes.TodoList.todoList).withSession("username" -> username) // store username in session
      } else {
        Redirect(routes.HomeController.index).flashing("error" -> "invalid username or password!")
      }

    }.getOrElse(Redirect(routes.HomeController.index))
  }

  def createNewUser = Action { implicit request =>
    val  postVals = request.body.asFormUrlEncoded //input field names (username, password) are keys for postVals map -> as you can see by using args(username)...
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head
      if (TodoListMemory.createUser(username, password)) {
        Redirect(routes.TodoList.todoList).withSession("username" -> username)
      } else {
        Redirect(routes.HomeController.index).flashing("error" -> "user creation failed!")
      }

    }.getOrElse(Redirect(routes.HomeController.index))
  }

  def todoList = Action { implicit request =>
    val sessionUsername = request.session.get("username") //get username from session
    sessionUsername.map { username =>
    val todo = TodoListMemory.getTask(username)
    Ok(views.html.TodoList(todo))
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

}
