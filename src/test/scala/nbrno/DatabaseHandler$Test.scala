package nbrno

import scala.slick.driver.H2Driver.simple._
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSuite}
import javax.sql.DataSource
import scala.slick.session.Database
import org.h2.jdbcx.JdbcDataSource
import java.sql.Timestamp
import nbrno.domain.{User, Rapper}

class DatabaseHandler$Test extends FunSuite with BeforeAndAfter with BeforeAndAfterAll{

  val dataSource: DataSource = {
    val ds = new JdbcDataSource
    ds.setURL("jdbc:h2:mem:test1")
    ds
  }

  var dbHandler = new DatabaseHandler(dataSource)
  implicit val session = Database.forDataSource(dataSource).createSession()

  override def beforeAll {
    (dbHandler.Rappers.ddl ++ dbHandler.Ratings.ddl ++ dbHandler.Users.ddl).create

    dbHandler.Rappers.insertAll(
      new Rapper(1, "RSP", Some(1), Timestamp.valueOf("2013-06-20 13:37:00")),
      new Rapper(2, "Klish", Some(1), Timestamp.valueOf("2013-06-20 13:37:00")),
      new Rapper(3, "Mats Dawg", Some(1), Timestamp.valueOf("2013-06-20 13:37:00")))

    dbHandler.Users.insertAll(
      new User(Some(1), "magnekd", Some("magne.davidsen@gmail.com"), Some("pass"), Some("hash"), Some("ip"),
        Some(Timestamp.valueOf("2013-06-20 13:37:00"))),
      new User(Some(2), "rolf", Some("rolf@gmail.com"), Some("pass"), Some("hash"), Some("ip"),
        Some(Timestamp.valueOf("2013-06-20 13:37:00"))))
  }

  after{
    //delete all ratings between tests
    dbHandler.Ratings.ddl.drop
    dbHandler.Ratings.ddl.create
  }

  test("getRappersWithTotalScore returns correct number of rappers"){
    assert(dbHandler.getRappersWithTotalScore.length === 3)
  }

  test("vote get counted"){
    //magnekd votes up for RSP
    dbHandler.vote(1,1, true)

    //RSP has total score of 1
    assert(dbHandler.getRappersWithTotalScore.find(_.id == 1).get.score.get == 1)
  }

  test("only one vote per user get counted"){
    //magnekd votes up for RSP
    dbHandler.vote(1,1, true)
    dbHandler.vote(1,1, true)

    //RSP has total score of 1
    assert(dbHandler.getRappersWithTotalScore.find(_.id == 1).get.score.get == 1)
  }

  test("votes from two users get counted"){
    //magnekd and rolf votes up for RSP
    dbHandler.vote(1,1, true)
    dbHandler.vote(2,1, true)

    //RSP has total score of 2
    assert(dbHandler.getRappersWithTotalScore.find(_.id == 1).get.score.get == 2)
  }

  test("vote first up then down gives right score"){
    //magnekd and rolf votes up for RSP
    dbHandler.vote(1,1, true)
    dbHandler.vote(1,1, false)

    //RSP has total score of -1
    assert(dbHandler.getRappersWithTotalScore.find(_.id == 1).get.score.get == -1)
  }
}