package controllers

import models.TodoListMemory
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}


@Singleton
class TodoList @Inject()(val controllerComponents: ControllerComponents) extends BaseController{

  def validateLoginPost = Action { request =>
    val  postVals = request.body.asFormUrlEncoded //input field names (username, password) are keys for postVals map -> as you can see by using args(username)...
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head
      if (TodoListMemory.validateUser(username, password)) {
        Redirect(routes.TodoList.todoList).withSession("username" -> username) // store username in session/cookie
      } else {
        Redirect(routes.HomeController.index)
      }

    }.getOrElse(Redirect(routes.HomeController.index))
  }

  def createNewUser = Action { request =>
    val  postVals = request.body.asFormUrlEncoded //input field names (username, password) are keys for postVals map -> as you can see by using args(username)...
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head
      if (TodoListMemory.createUser(username, password)) {
        Redirect(routes.TodoList.todoList).withSession("username" -> username)
      } else {
        Redirect(routes.HomeController.index)
      }

    }.getOrElse(Redirect(routes.HomeController.index))
  }

  def todoList = Action { request =>
    val sessionUsername = request.session.get("username") //get username from session/cookie
    sessionUsername.map { username =>
    val todo = TodoListMemory.getTask(username)
    Ok(views.html.TodoList(todo))
    }.getOrElse(Redirect(routes.HomeController.index))
  }

  def logout = Action{
    Redirect(routes.HomeController.index).withNewSession
  }

}
