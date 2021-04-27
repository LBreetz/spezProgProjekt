package controllers

import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}


@Singleton
class TodoList @Inject()(val controllerComponents: ControllerComponents) extends BaseController{

  def todoList = Action {
    val todo = List("ToDo1", "ToDo2", "ToDo3", "ToDo4")
    Ok(views.html.TodoListIndex(todo))
  }

}
