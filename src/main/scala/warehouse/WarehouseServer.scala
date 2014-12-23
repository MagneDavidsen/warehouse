package warehouse

import java.net.{URI, URL}
import java.util
import javax.servlet.{FilterConfig, ServletContext}
import javax.sql.DataSource

import org.eclipse.jetty.servlets.CrossOriginFilter
import org.postgresql.ds.PGSimpleDataSource
import unfiltered.jetty.Http

import scala.util.Properties


class WarehouseCrossOriginFilter extends CrossOriginFilter {

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

object WarehouseServer extends App{

  val crossOriginFilter: CrossOriginFilter = new WarehouseCrossOriginFilter()

  val server = Http(Properties.envOrElse("PORT", "8081").toInt).filter(crossOriginFilter).resources(new URL(getClass().getResource("/www/"), "."))
    .filter(ComponentRegistry.itemsPlan)

  server.run()

}

object ComponentRegistry extends DatabaseHandlerComponent with DataSourceComponent with PlanComponent{

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
        ds.setDatabaseName("warehouse")
        ds.setUser(Properties.envOrElse("DB_USER", "warehouse"))
        ds.setPassword(Properties.envOrElse("DB_PASSWORD", "warehouse"))
        ds.setServerName("192.168.59.104")
        ds.setPortNumber(49533)
        ds
      }
    }
  }

  val databaseHandler = new DatabaseHandler
  val itemsPlan = new ItemsPlan
}