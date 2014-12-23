package warehouse

import javax.sql.DataSource
import org.h2.jdbcx.JdbcDataSource
import org.specs2.mock.Mockito
import scala.slick.driver.H2Driver.simple._
import warehouse.domain.{Item}

import java.sql.{Blob, Timestamp}

trait DBTestData extends Mockito {this: InMemDBEnvironment =>

  val item1 = new Item(Some(1), "item-1", 100, Some("String"), "OLA", Some(Timestamp.valueOf("2013-06-20 13:37:00")))
  val item2 = new Item(Some(2), "item-2", 200, Some("String"), "OLA", Some(Timestamp.valueOf("2013-06-20 13:37:00")))


  def createAllTables(implicit session: Session) = {
    (databaseHandler.Items.ddl).create
  }

  def insertTestData(implicit session: Session) = {
    databaseHandler.Items.insertAll(item1, item2)
  }
}

class InMemDBEnvironment extends DatabaseHandlerComponent with DataSourceComponent with PlanComponent{
  val dataSource: DataSource = {
    val ds = new JdbcDataSource
    ds.setURL("jdbc:h2:mem:test1")
    ds
  }

  val databaseHandler = new DatabaseHandler
  val itemsPlan = new ItemsPlan
}

class MockDBEnvironment extends DatabaseHandlerComponent with DataSourceComponent with Mockito with PlanComponent{
  val dataSource: DataSource = mock[DataSource]
  val databaseHandler = mock[DatabaseHandler]
  val itemsPlan = new ItemsPlan
}