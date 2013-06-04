package nbrno.domain

case class SignupUser(username:String, password:String)
case class User(id:Option[Int], username:String, email:String, passhash:Option[String])
case class Rapper(id:Option[Int], name:String)
case class Rating(id:Int, userId:Int, rapperId:Int, rating:Int)