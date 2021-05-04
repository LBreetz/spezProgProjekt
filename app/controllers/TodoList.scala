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
        Redirect(routes.TodoList.todoList)
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
        Redirect(routes.TodoList.todoList)
      } else {
        Redirect(routes.HomeController.index)
      }

    }.getOrElse(Redirect(routes.HomeController.index))
  }

  def todoList = Action {
    val username = "Test"
    val todo = TodoListMemory.getTask(username)
    Ok(views.html.TodoList(todo))
  }

}
