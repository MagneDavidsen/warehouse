package nbrno

import scala.slick.session.Database
import org.scalatest.{BeforeAndAfterAll, FunSpec}
import org.scalatest.matchers.ShouldMatchers
import dispatch._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write


class VoteIntSpec extends InMemDBEnvironment with DBTestData with FunSpec with BeforeAndAfterAll with ShouldMatchers {

  val server = unfiltered.jetty.Http.anylocal.filter(userPlan)
  lazy val myHost = host("localhost", server.port)
  implicit val session = Database.forDataSource(dataSource).createSession()
  implicit val formats = DefaultFormats

  var token: String = _

  def userSignUp() {
    val json = write(JsonUser("user-1", "email-1", "pass-1"))
    val req = TestHelper.setJsonHeaders(myHost / "api" / "user" / "signup").setBody(json).POST

    println(req.toString)

    val validSignup = Http(req OK as.String)
    validSignup()
  }

  def userLogin(): String = {
    val json = write(JsonUser("user-1", "", "pass-1"))
    val req = TestHelper.setJsonHeaders(myHost / "api" / "user" / "login").setBody(json).POST

    println(req.toString)

    val response = Http(req)

    response().getCookies.get(0).getValue
  }


  override def beforeAll(){
    createAllTables
    insertTestData

    server.start()

    userSignUp()
    token = userLogin()
  }

  override def afterAll {
    session.close()
    server.stop()
  }

  describe("vote service"){
    it("should register votes from logged in user"){
      val myHost = host("localhost", server.port)
      val myRequest = myHost / "api" / "rappers" / "1"

      val stats = Http(myRequest OK as.String)

      stats() should fullyMatch regex ("\\{\"numRatings\":[\\d+],\"numUsers\":[\\d+],\"numRappers\":[\\d+]\\}")
    }
  }
}