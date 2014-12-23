package warehouse

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable
import scala.slick.session.Database

object Migration extends App {

  val dbHandler = ComponentRegistry.databaseHandler

  implicit val session = Database.forDataSource(ComponentRegistry.dataSource).createSession()

  if(MTable.getTables(dbHandler.Items.tableName).list.size > 0) dbHandler.Items.ddl.drop

  (dbHandler.Items.ddl).create

  session.close()
}
