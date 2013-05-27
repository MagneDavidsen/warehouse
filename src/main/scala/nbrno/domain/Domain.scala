package nbrno.domain

case class User(id:Option[Int], username:String, email:String, passhash:String)
case class Rapper(id:Option[Int], name:String)