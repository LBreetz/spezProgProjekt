package models

import collection.mutable

object TodoListMemory {
  private val users = mutable.Map[String, String]("Test" -> "Test")

  def validateUser(username: String, password: String): Boolean = {
    users.get(username).map(_ == password).getOrElse(false)
  }

  def createUser(username: String, password: String): Boolean = ???

  def getTask(username: String): Seq[String] = ???

  def addTask(username: String, task: String): Unit = ???

  def removeTask(username: String, index: Int): Boolean = ???

}
