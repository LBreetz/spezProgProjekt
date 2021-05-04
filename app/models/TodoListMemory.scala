package models

import collection.mutable

object TodoListMemory {
  private val users = mutable.Map[String, String]("Test" -> "Test")
  private val tasks = mutable.Map[String, List[String]]("Test" -> List("spezProg Projekt beenden", "Haushalt", "Platzhalter", "Platzhalter", "Platzhalter"))

  def validateUser(username: String, password: String): Boolean = {
    users.get(username).map(_ == password).getOrElse(false)
  }

  def createUser(username: String, password: String): Boolean = {
    //simply adding user to users list if they dont exist allready
    if (users.contains(username)) false else {
      users(username) = password
      true
    }
  }

  def getTask(username: String): Seq[String] = {
    tasks.get(username).getOrElse(Nil)
  }

  def addTask(username: String, task: String): Unit = ???

  def removeTask(username: String, index: Int): Boolean = ???

}
