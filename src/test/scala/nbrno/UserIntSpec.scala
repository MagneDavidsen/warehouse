package nbrno

import scala.slick.session.Database
import org.scalatest.{BeforeAndAfterAll, FunSpec}
import org.scalatest.matchers.ShouldMatchers
import dispatch._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

class UserIntSpec extends InMemDBEnvironment with DBTestData with FunSpec with BeforeAndAfterAll with ShouldMatchers {

  case class JsonUser(val username: String, val email: String, val password: String)

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
      val req = setJsonHeaders(myHost / "api" / "user" / "signup").setBody(json).POST

      println(req.toString)

      val validSignup = Http(req OK as.String)
      validSignup()

      databaseHandler.validateUser("user-1", "pass-1").get should have ('username ("user-1"), 'email (Some("email-1")))
    }
  }

  def setJsonHeaders(endpoint: Req): Req = {
    endpoint.setHeader("Content-Type", "application/json;charset=UTF-8")
      .setHeader("Accept", "application/json")
  }
}
