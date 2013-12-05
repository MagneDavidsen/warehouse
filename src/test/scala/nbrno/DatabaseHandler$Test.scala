
package nbrno

import scala.slick.driver.H2Driver.simple._
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSuite}
import scala.slick.session.Database
import java.sql.Timestamp
import nbrno.domain.{User, Rapper}
import org.scalatest.matchers.ShouldMatchers

class DatabaseHandler$Test extends FunSuite with ShouldMatchers with BeforeAndAfter with BeforeAndAfterAll with InMemDBEnvironment{

  val user1 = new User(Some(1), "user-1", Some("email-1"), Some("password-1"), Some("hash-1"), Some("ip-1"),
    Some(Timestamp.valueOf("2013-06-20 13:37:00")))
  val user2 = new User(Some(2), "user-2", Some("email-2"), Some("password-2"), Some("hash-2"), Some("ip-2"),
    Some(Timestamp.valueOf("2013-06-20 13:37:00")))

  val rapper1 = new Rapper(1, "rapper-1", Some(0), Timestamp.valueOf("2013-06-20 13:37:00"))
  val rapper2 = new Rapper(2, "rapper-2", Some(0), Timestamp.valueOf("2013-06-20 13:37:00"))
  val rapper3 = new Rapper(3, "rapper-3", Some(0), Timestamp.valueOf("2013-06-20 13:37:00"))

  val sessionId: String = "session-id"

  implicit val session = Database.forDataSource(dataSource).createSession()

  override def beforeAll {
    //create all tables
    (databaseHandler.Sessions.ddl ++ databaseHandler.Rappers.ddl ++ databaseHandler.Ratings.ddl ++ databaseHandler.Users.ddl).create

    //populate rappers and users
    databaseHandler.Rappers.insertAll(rapper1, rapper2, rapper3)
    databaseHandler.Users.insertAll(user1, user2)
  }

  after{
    //delete all ratings between tests
    databaseHandler.Ratings.ddl.drop
    databaseHandler.Ratings.ddl.create
  }

  test("getRappersWithTotalScore returns correct number of rappers"){
    assert(databaseHandler.getRappersWithTotalScore.length === 3)
  }

  test("vote get counted"){
    databaseHandler.vote(user1.id.get, rapper1.id, true)

    assert(databaseHandler.getRappersWithTotalScore.find(_.id == 1).get.score.get == 1)
  }

  test("only one vote per user get counted"){
    databaseHandler.vote(user1.id.get,rapper1.id, true)
    databaseHandler.vote(user1.id.get,rapper1.id, true)

    assert(databaseHandler.getRappersWithTotalScore.find(_.id == 1).get.score.get == 1)
  }

  test("votes from two users get counted"){
    databaseHandler.vote(user1.id.get,rapper1.id, true)
    databaseHandler.vote(user2.id.get,rapper1.id, true)

    assert(databaseHandler.getRappersWithTotalScore.find(_.id == rapper1.id).get.score.get == 2)
  }

  test("vote first up then down gives right score"){
    databaseHandler.vote(user1.id.get,rapper1.id, true)
    databaseHandler.vote(user1.id.get,rapper1.id, false)

    assert(databaseHandler.getRappersWithTotalScore.find(_.id == rapper1.id).get.score.get == -1)
  }

  test("getVotes returns all users votes"){
    databaseHandler.vote(user1.id.get,rapper1.id,true)
    databaseHandler.vote(user1.id.get,rapper2.id,true)
    databaseHandler.vote(user1.id.get,rapper3.id,true)

    assert(databaseHandler.getVotes(user1.username).length == 3)
  }

  test("getVotes returns one vote per rapper"){
    databaseHandler.vote(user1.id.get,rapper1.id,true)
    databaseHandler.vote(user1.id.get,rapper1.id,false)
    databaseHandler.vote(user1.id.get,rapper1.id,true)

    assert(databaseHandler.getVotes(user1.username).length == 1)
  }

  test("getVotes only returns this users vote"){
    databaseHandler.vote(user1.id.get,rapper1.id,true)
    databaseHandler.vote(user1.id.get,rapper2.id,false)
    databaseHandler.vote(user1.id.get,rapper3.id,true)

    databaseHandler.vote(user2.id.get,rapper3.id,true)

    assert(databaseHandler.getVotes(user1.username).length == 3)
  }

  test("stats returns the right numbers"){
    val stats = databaseHandler.getStats

    assert(stats.get("numRappers") === Some(3))
    assert(stats.get("numUsers") === Some(2))
    assert(stats.get("numVotes") === None)
  }

  test("save and retrieve session works"){
    databaseHandler.saveSession(sessionId, user1.id.get)

    assert(databaseHandler.retrieveSession(sessionId).get.id === user1.id)
  }

  test("remove session works"){
    databaseHandler.saveSession(sessionId, user1.id.get)
    databaseHandler.removeSession(sessionId)

    assert(databaseHandler.retrieveSession(sessionId) === None)
  }

  test("reset password sets password"){
    val newPassword = "newPassword"

    databaseHandler.resetPassword("email-1", newPassword)

    databaseHandler.validateUser("user-1", newPassword) should not be (None)
    databaseHandler.validateUser("user-1", "password-1") should be (None)
  }
}