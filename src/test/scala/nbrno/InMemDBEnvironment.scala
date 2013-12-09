package nbrno

import javax.sql.DataSource
import org.h2.jdbcx.JdbcDataSource
import org.specs2.mock.Mockito
import scala.slick.driver.H2Driver.simple._
import nbrno.domain.{Rapper, User}
import java.sql.Timestamp

trait DBTestData{this: InMemDBEnvironment =>
  val user1 = new User(Some(1), "user-1", Some("email-1"), Some("password-1"), Some("hash-1"), Some("ip-1"),
    Some(Timestamp.valueOf("2013-06-20 13:37:00")))
  val user2 = new User(Some(2), "user-2", Some("email-2"), Some("password-2"), Some("hash-2"), Some("ip-2"),
    Some(Timestamp.valueOf("2013-06-20 13:37:00")))

  val rapper1 = new Rapper(1, "rapper-1", Some(0), Timestamp.valueOf("2013-06-20 13:37:00"))
  val rapper2 = new Rapper(2, "rapper-2", Some(0), Timestamp.valueOf("2013-06-20 13:37:00"))
  val rapper3 = new Rapper(3, "rapper-3", Some(0), Timestamp.valueOf("2013-06-20 13:37:00"))

  val sessionId: String = "session-id"

  def createAllTables(implicit session: Session) = {
    (databaseHandler.Sessions.ddl ++ databaseHandler.Rappers.ddl ++ databaseHandler.Ratings.ddl ++ databaseHandler.Users.ddl).create
  }

  def insertTestData(implicit session: Session) = {
    databaseHandler.Rappers.insertAll(rapper1, rapper2, rapper3)
    databaseHandler.Users.insertAll(user1, user2)
  }
}

class InMemDBEnvironment extends DatabaseHandlerComponent with DataSourceComponent with SessionStoreComponent with PlanComponent{

  val dataSource: DataSource = {
    val ds = new JdbcDataSource
    ds.setURL("jdbc:h2:mem:test1")
    ds
  }

  val databaseHandler = new DatabaseHandler
  val sessionStore = new SessionStore
  val statsPlan = new StatsPlan
  val rappersPlan = new RappersPlan
  val userPlan = new UserPlan

}

class MockDBEnvironment extends DatabaseHandlerComponent with DataSourceComponent with SessionStoreComponent with Mockito with PlanComponent{

  val sessionStore = new SessionStore
  val dataSource: DataSource = mock[DataSource]
  val databaseHandler = mock[DatabaseHandler]
  val statsPlan = new StatsPlan
  val rappersPlan = new RappersPlan
  val userPlan = new UserPlan
}