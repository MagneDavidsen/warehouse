package nbrno

import scala.slick.session.Database
import org.scalatest.{BeforeAndAfterAll, FunSpec}
import org.scalatest.matchers.ShouldMatchers
import dispatch._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

class UserIntSpec extends InMemDBEnvironment with DBTestData with FunSpec with BeforeAndAfterAll with ShouldMatchers {

  val server = unfiltered.jetty.Http.anylocal.filter(userPlan)
  lazy val myHost = host("localhost", server.port)
  implicit val session = Database.forDataSource(dataSource).createSession()

  override def beforeAll(){
    createAllTables
    server.start()
  }

  override def afterAll {
    session.close()
  }

  describe("the signup service"){
    it("should save the user to the database"){

      val req = (myHost / "api" / "user" / "signup").POST
        .setHeader("Content-Type", "application/json;charset=UTF-8")
        .setHeader("Accept", "application/json")
        .setBody("{\"username\":\"user-1\",\"email\":\"email-1\",\"password\":\"pass-1\"}")

      val validSignup = Http(req OK as.String)
      validSignup()

      databaseHandler.validateUser("user-1", "pass-1").get should have ('username ("user-1"), 'email (Some("email-1")))
    }
  }
}
