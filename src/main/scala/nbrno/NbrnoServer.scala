package nbrno

import unfiltered.jetty.Http
import scala.util.Properties
import java.net.{URI, URL}
import scala.collection.{immutable}
import java.util.UUID
import nbrno.domain.User
import javax.sql.DataSource
import org.postgresql.ds.{PGSimpleDataSource}
import unfiltered.Cookie

object NbrnoServer extends App {

  //token is key, username is value
  val sessionStore : SessionStore = new SessionStore(new immutable.HashMap[String, User])

  def getDataSource : DataSource = {
    val databaseUrl : Option[String] = Option(System.getenv("DATABASE_URL"))
    databaseUrl match{
      case Some(value) => {
        val dbUrl = new URI(value)
        val ds = new PGSimpleDataSource
        ds.setUser(dbUrl.getUserInfo.split(":"){0})
        ds.setPassword(dbUrl.getUserInfo.split(":"){1})
        ds.setServerName(dbUrl.getHost)
        ds.setPortNumber(dbUrl.getPort)
        ds.setDatabaseName(dbUrl.getPath.tail) //remove leading slash
        ds
      }
      case None => {
        //fallback for dev
        val ds = new PGSimpleDataSource
        ds.setDatabaseName("nbrno")
        ds.setUser(Properties.envOrElse("DB_USER", "clocking"))
        ds.setPassword(Properties.envOrElse("DB_PASSWORD", "clocking"))
        ds.setServerName("localhost")
        ds.setPortNumber(5432)
        ds
      }
    }
  }

  val dbHandler : DatabaseHandler = new DatabaseHandler(getDataSource)

  Http(Properties.envOrElse("PORT", "8081").toInt).resources(new URL(getClass().getResource("/www/"), "."))
    .filter(RappersPlan).filter(UserPlan).filter(StatsPlan).run()

}



class SessionStore(var map:immutable.HashMap[String, User]){

  def addUser(user : User) : String = {
    val token : String = UUID.randomUUID().toString
    map += token -> user
    token
  }

  def getUser(token : String) : Option[User] = map.get(token)
  def getUserFromCookie(cookie : Option[Cookie]) : Option[User] = {
    cookie match {
      case Some(cookie) => getUser(cookie.value)
      case None => None
    }
  }
  def removeUser(token : String) = map-=(token)
  def size() : Int = map.size
}

