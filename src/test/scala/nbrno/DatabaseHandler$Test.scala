package nbrno

import scala.slick.driver.H2Driver.simple._
import org.scalatest.{BeforeAndAfter, FunSuite}
import javax.sql.DataSource
import scala.slick.session.Database
import scala.slick.lifted.Query
import org.h2.jdbcx.JdbcDataSource
import java.sql.Timestamp
import nbrno.domain.{User, Rapper}

class DatabaseHandler$Test extends FunSuite with BeforeAndAfter{

  val dataSource: DataSource = {
    val ds = new JdbcDataSource
    ds.setURL("jdbc:h2:mem:test1")
    ds
  }

  var dbHandler = new DatabaseHandler(dataSource)

  implicit val session = Database.forDataSource(dataSource).createSession()

  before {
    Database.forDataSource(dataSource) withSession {
      (dbHandler.Rappers.ddl ++ dbHandler.Ratings.ddl ++ dbHandler.Users.ddl).create

      dbHandler.Rappers.insertAll(
        (new Rapper(1, "RSP", Some(1), Timestamp.valueOf("2013-06-20 13:37:00"))),
        (new Rapper(2, "Klish", Some(1), Timestamp.valueOf("2013-06-20 13:37:00"))),
        (new Rapper(3, "Mats Dawg", Some(1), Timestamp.valueOf("2013-06-20 13:37:00")))
      )

      dbHandler.Users.insertAll(
        (new User(Some(1), "magnekd", Some("magne.davidsen@gmail.com"), Some("pass"), Some("hash"), Some("ip"),
          Some(Timestamp.valueOf("2013-06-20 13:37:00"))))
      )
    }
  }

  after {
    session.close()
  }


  test("getRappersWithTotalScore returns correct number of rappers"){
    Database.forDataSource(dataSource) withSession {



      assert(dbHandler.getRappersWithTotalScore.length === 3)
    }
  }
}
