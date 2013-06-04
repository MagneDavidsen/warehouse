package nbrno

import unfiltered.jetty.Http
import scala.util.Properties
import java.net.URL
import scala.collection.{immutable}
import java.util.UUID

object NbrnoServer extends App {

    //token is key, username is value
  var sessionStore : immutable.HashMap[String, String] = new immutable.HashMap[String, String]

  def addUsernameToSessionStore (username : String) : String = {
    val token : String = UUID.randomUUID().toString
    sessionStore += token  -> username
    token
  }

  def getUsernameFromSessionStore (token : String) : Option[String] = sessionStore.get(token)

  Http(Properties.envOrElse("PORT", "8081").toInt).resources(new URL(getClass().getResource("/www/"), "."))
    .filter(RappersPlan).filter(SignupPlan).run()

}