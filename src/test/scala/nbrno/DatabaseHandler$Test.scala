package nbrno

import scala.slick.driver.H2Driver.simple._
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSuite}
import javax.sql.DataSource
import scala.slick.session.Database
import org.h2.jdbcx.JdbcDataSource
import java.sql.Timestamp
import nbrno.domain.{User, Rapper}

class DatabaseHandler$Test extends FunSuite with BeforeAndAfter with BeforeAndAfterAll{

  val user1 = new User(Some(1), "user-1", Some("email-1"), Some("password-1"), Some("hash-1"), Some("ip-1"),
    Some(Timestamp.valueOf("2013-06-20 13:37:00")))
  val user2 = new User(Some(2), "user-2", Some("email-2"), Some("password-2"), Some("hash-2"), Some("ip-2"),
    Some(Timestamp.valueOf("2013-06-20 13:37:00")))

  val rapper1 = new Rapper(1, "rapper-1", Some(0), Timestamp.valueOf("2013-06-20 13:37:00"))
  val rapper2 = new Rapper(2, "rapper-2", Some(0), Timestamp.valueOf("2013-06-20 13:37:00"))
  val rapper3 = new Rapper(3, "rapper-3", Some(0), Timestamp.valueOf("2013-06-20 13:37:00"))

  val dataSource: DataSource = {
    val ds = new JdbcDataSource
    ds.setURL("jdbc:h2:mem:test1")
    ds
  }

  var dbHandler = new DatabaseHandler(dataSource)
  implicit val session = Database.forDataSource(dataSource).createSession()

  override def beforeAll {
    //create all tables
    (dbHandler.Rappers.ddl ++ dbHandler.Ratings.ddl ++ dbHandler.Users.ddl).create

    //populate rappers and users
    dbHandler.Rappers.insertAll(rapper1, rapper2, rapper3)
    dbHandler.Users.insertAll(user1, user2)
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
    dbHandler.vote(user1.id.get, rapper1.id, true)

    assert(dbHandler.getRappersWithTotalScore.find(_.id == 1).get.score.get == 1)
  }

  test("only one vote per user get counted"){
    dbHandler.vote(user1.id.get,rapper1.id, true)
    dbHandler.vote(user1.id.get,rapper1.id, true)

    assert(dbHandler.getRappersWithTotalScore.find(_.id == 1).get.score.get == 1)
  }

  test("votes from two users get counted"){
    dbHandler.vote(user1.id.get,rapper1.id, true)
    dbHandler.vote(user2.id.get,rapper1.id, true)

    assert(dbHandler.getRappersWithTotalScore.find(_.id == rapper1.id).get.score.get == 2)
  }

  test("vote first up then down gives right score"){
    dbHandler.vote(user1.id.get,rapper1.id, true)
    dbHandler.vote(user1.id.get,rapper1.id, false)

    assert(dbHandler.getRappersWithTotalScore.find(_.id == rapper1.id).get.score.get == -1)
  }

  test("getVotes returns all users votes"){
    dbHandler.vote(user1.id.get,rapper1.id,true)
    dbHandler.vote(user1.id.get,rapper2.id,true)
    dbHandler.vote(user1.id.get,rapper3.id,true)

    assert(dbHandler.getVotes(user1.username).length == 3)
  }

  test("getVotes returns one vote per rapper"){
    dbHandler.vote(user1.id.get,rapper1.id,true)
    dbHandler.vote(user1.id.get,rapper1.id,false)
    dbHandler.vote(user1.id.get,rapper1.id,true)

    assert(dbHandler.getVotes(user1.username).length == 1)
  }

  test("getVotes only returns this users vote"){
    dbHandler.vote(user1.id.get,rapper1.id,true)
    dbHandler.vote(user1.id.get,rapper2.id,false)
    dbHandler.vote(user1.id.get,rapper3.id,true)

    dbHandler.vote(user2.id.get,rapper3.id,true)

    assert(dbHandler.getVotes(user1.username).length == 3)
  }

  test("stats returns the right numbers"){
    val stats = dbHandler.getStats

    assert(stats.get("numRappers") === Some(3))
    assert(stats.get("numUsers") === Some(2))
    assert(stats.get("numVotes") === None)
  }
}