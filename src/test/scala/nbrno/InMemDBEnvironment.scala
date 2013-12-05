package nbrno

import javax.sql.DataSource
import org.h2.jdbcx.JdbcDataSource
import org.specs2.mock.Mockito

trait InMemDBEnvironment extends DatabaseHandlerComponent with DataSourceComponent with SessionStoreComponent{

  val dataSource: DataSource = {
    val ds = new JdbcDataSource
    ds.setURL("jdbc:h2:mem:test1")
    ds
  }

  val databaseHandler = new DatabaseHandler
  val sessionStore = new SessionStore
}

trait MockDBEnvironment extends DatabaseHandlerComponent with DataSourceComponent with SessionStoreComponent with Mockito{

  val sessionStore = new SessionStore
  val dataSource: DataSource = mock[DataSource]
  val databaseHandler = mock[DatabaseHandler]
}