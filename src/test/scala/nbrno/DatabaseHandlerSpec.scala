
package nbrno

import scala.slick.driver.H2Driver.simple._
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import scala.slick.session.Database

class DatabaseHandlerSpec extends InMemDBEnvironment with DBTestData with FunSuite with ShouldMatchers with BeforeAndAfter with BeforeAndAfterAll{

  implicit val session = Database.forDataSource(dataSource).createSession()

  override def beforeAll {
    createAllTables
    insertTestData
  }

  override def afterAll {
    session.close()
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

  test("getVotes returns correct vote when voting up then down"){
    databaseHandler.vote(user1.id.get,rapper1.id,true)
    databaseHandler.vote(user1.id.get,rapper1.id,false)

    assert(databaseHandler.getVotes(user1.username).head.rating == -1)
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