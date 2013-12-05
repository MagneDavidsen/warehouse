package nbrno

import scala.slick.driver.PostgresDriver.simple._
import nbrno.domain.Rapper
import java.sql.Timestamp
import scala.slick.session.Database
import scala.slick.jdbc.meta.MTable

object Migration extends App {

  val dbHandler = ComponentRegistry.databaseHandler

  implicit val session = Database.forDataSource(ComponentRegistry.dataSource).createSession()

  if(MTable.getTables(dbHandler.Sessions.tableName).list.size > 0) dbHandler.Sessions.ddl.drop
  if(MTable.getTables(dbHandler.Ratings.tableName).list.size > 0) dbHandler.Ratings.ddl.drop
  if(MTable.getTables(dbHandler.Users.tableName).list.size > 0) dbHandler.Users.ddl.drop
  if(MTable.getTables(dbHandler.Rappers.tableName).list.size > 0) dbHandler.Rappers.ddl.drop

  (dbHandler.Sessions.ddl ++ dbHandler.Rappers.ddl ++ dbHandler.Ratings.ddl ++ dbHandler.Users.ddl).create

  val rsp = new Rapper(1, "RSP", Some(0), Timestamp.valueOf("2013-10-13 13:37:00"))
  val chirag = new Rapper(2, "Chirag", Some(0), Timestamp.valueOf("2013-10-13 13:37:00"))
  val lars = new Rapper(3, "Lars Vaular", Some(0), Timestamp.valueOf("2013-10-13 13:37:00"))

  dbHandler.Rappers.insertAll(rsp, chirag, lars)

  session.close()
}
