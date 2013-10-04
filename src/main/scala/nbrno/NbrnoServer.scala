package nbrno

import unfiltered.jetty.Http
import scala.util.Properties
import java.net.URL
import scala.collection.{immutable}
import java.util.UUID
import nbrno.domain.User
import scala.collection.immutable.HashMap
import javax.sql.DataSource
import org.postgresql.ds.PGPoolingDataSource

object NbrnoServer extends App {

  //token is key, username is value
  val sessionStore : SessionStore = new SessionStore(new immutable.HashMap[String, User])

  val dataSource: DataSource = {
    val ds = new PGPoolingDataSource
    ds.setDataSourceName("nbrno")
    ds.setDatabaseName("nbrno")
    ds.setUser(Properties.envOrElse("DB_USER", "clocking"))
    ds.setPassword(Properties.envOrElse("DB_PASSWORD", "clocking"))
    ds.setServerName("localhost")
    ds.setPortNumber(5432)
    ds
  }

  val dbHandler : DatabaseHandler = new DatabaseHandler(dataSource)

  Http(Properties.envOrElse("PORT", "8081").toInt).resources(new URL(getClass().getResource("/www/"), "."))
    .filter(RappersPlan).filter(UserPlan).run()

}

class SessionStore(var map:immutable.HashMap[String, User]){

  def addUser(user : User) : String = {
    val token : String = UUID.randomUUID().toString
    map += token -> user
    token
  }

  def getUser(token : String) : Option[User] = map.get(token)

  def removeUser(token : String) = map-=(token)

  def size() : Int = map.size
}