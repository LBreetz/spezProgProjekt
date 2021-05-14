package models

case class User(name: String, password: String) {
  def getUsername = name
}
