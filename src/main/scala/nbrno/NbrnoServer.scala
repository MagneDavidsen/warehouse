package nbrno

import unfiltered.jetty.Http
import scala.util.Properties
import java.net.{URI, URL}
import scala.collection.immutable
import java.util.UUID
import nbrno.domain.User
import javax.sql.DataSource
import org.postgresql.ds.PGSimpleDataSource
import unfiltered.Cookie
import org.eclipse.jetty.servlets.CrossOriginFilter
import javax.servlet.{ServletContext, FilterConfig}
import java.util


class NbrnoCrossOriginFilter extends CrossOriginFilter {

  override def init(filterConfig: FilterConfig) {
    val allowedOriginsConfig: String =  "*"
    val allowedMethodsConfig: String = "GET,POST,HEAD"
    val allowedHeadersConfig = "X-Requested-With,Content-Type,Accept,Origin"
    val preflightMaxAgeConfig: String = "1800"
    val allowedCredentialsConfig: String =  "true"
    val exposedHeadersConfig: String = ""
    val chainPreflightConfig: String =  "false"

    super.init(new FilterConfig {
      def getFilterName: String = "cross-origin"

      def getInitParameterNames: util.Enumeration[_] = null

      def getInitParameter(name: String): String = {
        name match {
          case CrossOriginFilter.ALLOWED_ORIGINS_PARAM => allowedOriginsConfig
          case CrossOriginFilter.ALLOWED_METHODS_PARAM => allowedMethodsConfig
          case CrossOriginFilter.ALLOWED_HEADERS_PARAM => allowedHeadersConfig
          case CrossOriginFilter.PREFLIGHT_MAX_AGE_PARAM => preflightMaxAgeConfig
          case CrossOriginFilter.ALLOW_CREDENTIALS_PARAM => allowedCredentialsConfig
          case CrossOriginFilter.EXPOSED_HEADERS_PARAM => exposedHeadersConfig
          case CrossOriginFilter.CHAIN_PREFLIGHT_PARAM => chainPreflightConfig
          case _ => null
        }
      }
      def getServletContext: ServletContext = null
    })
  }
}

object NbrnoServer extends App{

  val crossOriginFilter: CrossOriginFilter = new NbrnoCrossOriginFilter()

  val server = Http(Properties.envOrElse("PORT", "8081").toInt).filter(crossOriginFilter).resources(new URL(getClass().getResource("/www/"), "."))
    .filter(ComponentRegistry.rappersPlan).filter(ComponentRegistry.userPlan).filter(ComponentRegistry.statsPlan)

  server.run()

}

trait SessionStoreComponent{this: DatabaseHandlerComponent =>
  val sessionStore: SessionStore

  class SessionStore(var map:immutable.HashMap[String, User] = new immutable.HashMap[String, User]){

    def addUser(user : User) : String = {
      val token : String = UUID.randomUUID().toString
      map += token -> user
      databaseHandler.saveSession(token, user.id.get)
      token
    }

    def getUser(token : String) : Option[User] = {
      map.get(token) match {
        case Some(user) => Option(user)
        case None => databaseHandler.retrieveSession(token)
      }
    }

    def getUserFromCookie(cookie : Option[Cookie]) : Option[User] = {
      cookie match {
        case Some(cookie) => getUser(cookie.value)
        case None => None
      }
    }
    def removeUser(token : String) = {
      map-=(token)
      databaseHandler.removeSession(token)
    }

    def size() : Int = map.size
  }
}

object ComponentRegistry extends DatabaseHandlerComponent with DataSourceComponent with SessionStoreComponent with PlanComponent{

  val dataSource : DataSource = {
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

  val databaseHandler = new DatabaseHandler
  val sessionStore = new SessionStore
  val statsPlan = new StatsPlan
  val rappersPlan = new RappersPlan
  val userPlan = new UserPlan
}