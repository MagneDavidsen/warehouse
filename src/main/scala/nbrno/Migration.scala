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

  val rapperNames = Array("RSP", "Chirag", "Lars Vaular", "Store P", "Klish", "Vinni", "Mae", "Jørg-1", "Thorstein Hyl", "Pumba")
  val rappers: Array[Rapper] = rapperNames.zipWithIndex.map{ case (name, index) => Rapper(index, name, Some(0), new Timestamp(System.currentTimeMillis))}

  rappers.foreach{rapper: Rapper => dbHandler.Rappers.insert(rapper)}

  session.close()
}
