package nbrno

import javax.sql.DataSource
import org.postgresql.ds.{PGSimpleDataSource}
import scala.util.Properties
import scala.slick.driver.PostgresDriver.simple._
import nbrno.domain.Rapper
import java.sql.Timestamp
import scala.slick.session.Database

object Migration extends App {

  val dataSource: DataSource = {
    val ds = new PGSimpleDataSource
    ds.setDatabaseName("nbrno")
    ds.setUser(Properties.envOrElse("DB_USER", "clocking"))
    ds.setPassword(Properties.envOrElse("DB_PASSWORD", "clocking"))
    ds.setServerName("localhost")
    ds.setPortNumber(5432)
    ds
  }

  val dbHandler : DatabaseHandler = new DatabaseHandler(dataSource)

  implicit val session = Database.forDataSource(dataSource).createSession()

  dbHandler.Ratings.ddl.drop
  dbHandler.Users.ddl.drop
  dbHandler.Rappers.ddl.drop
  (dbHandler.Rappers.ddl ++ dbHandler.Ratings.ddl ++ dbHandler.Users.ddl).create

  val rsp = new Rapper(1, "RSP", Some(0), Timestamp.valueOf("2013-10-13 13:37:00"))
  val chirag = new Rapper(2, "Chirag", Some(0), Timestamp.valueOf("2013-10-13 13:37:00"))
  val lars = new Rapper(3, "Lars Vaular", Some(0), Timestamp.valueOf("2013-10-13 13:37:00"))

  dbHandler.Rappers.insertAll(rsp, chirag, lars)

  session.close()
}
