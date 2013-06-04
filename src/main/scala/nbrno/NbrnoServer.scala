package nbrno

import unfiltered.jetty.Http
import scala.util.Properties
import java.net.URL
import scala.collection.{immutable}
import java.util.UUID
import nbrno.domain.User

object NbrnoServer extends App {

    //token is key, username is value
  var sessionStore : immutable.HashMap[String, User] = new immutable.HashMap[String, User]

  def addUserToSessionStore (user : User) : String = {
    val token : String = UUID.randomUUID().toString
    sessionStore += token -> user
    token
  }

  def getUserFromSessionStore (token : String) : Option[User] = sessionStore.get(token)

  Http(Properties.envOrElse("PORT", "8081").toInt).resources(new URL(getClass().getResource("/www/"), "."))
    .filter(RappersPlan).filter(SignupPlan).run()

}