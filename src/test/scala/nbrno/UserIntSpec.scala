package nbrno

import scala.slick.session.Database
import org.scalatest.{FunSpecLike, BeforeAndAfterAll}
import org.scalatest.matchers.ShouldMatchers
import dispatch._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

class UserIntSpec extends InMemDBEnvironment with DBTestData with FunSpecLike with BeforeAndAfterAll with ShouldMatchers {

  val server = unfiltered.jetty.Http.anylocal.filter(userPlan)
  lazy val myHost = host("localhost", server.port)
  implicit val session = Database.forDataSource(dataSource).createSession()
  implicit val formats = DefaultFormats

  override def beforeAll(){
    createAllTables
    server.start()
  }

  override def afterAll {
    session.close()
    server.stop()
  }

  describe("the signup service"){
    it("should save the user to the database"){

      val json = write(JsonUser("user-1", "email-1", "pass-1"))
      val req = TestHelper.setJsonHeaders(myHost / "api" / "user" / "signup").setBody(json).POST

      println(req.toString)

      val validSignup = Http(req OK as.String)
      validSignup()

      databaseHandler.validateUser("user-1", "pass-1").get should have ('username ("user-1"), 'email (Some("email-1")))
    }
  }

  describe("the login service"){
    it("should accept a user that has signed up"){

      val json = write(JsonUser("user-1", "", "pass-1"))
      val req = TestHelper.setJsonHeaders(myHost / "api" / "user" / "login").setBody(json).POST

      println(req.toString)

      val response = Http(req)

      response().getStatusCode should be (200)
    }
  }

  describe("the login service"){
    it("should not accept a user with a wrong password"){

      val json = write(JsonUser("user-1", "", "pass-2"))
      val req = TestHelper.setJsonHeaders(myHost / "api" / "user" / "login").setBody(json).POST

      println(req.toString)

      val response = Http(req)

      response().getStatusCode should be (400)
    }
  }


}
object TestHelper {
  def setJsonHeaders(endpoint: Req): Req = {
    endpoint.setHeader("Content-Type", "application/json;charset=UTF-8")
      .setHeader("Accept", "application/json")
  }
}

case class JsonUser(val username: String, val email: String, val password: String)