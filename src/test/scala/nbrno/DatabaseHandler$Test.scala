package nbrno

import scala.slick.driver.PostgresDriver.simple._
import org.scalatest.FunSuite
import javax.sql.DataSource
import org.postgresql.ds.PGPoolingDataSource
import scala.util.Properties
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.lifted.Query

class DatabaseHandler$Test extends FunSuite {

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

  test("getRappersWithTotalScore returns correct number of rappers"){
    Database.forDataSource(dataSource) withSession {
      assert(dbHandler.getRappersWithTotalScore.length === Query(dbHandler.Rappers).list.length)
    }
  }
}
