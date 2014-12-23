package warehouse


import scala.slick.session.Database
import org.scalatest.{FunSpecLike, BeforeAndAfterAll}
import org.scalatest.matchers.ShouldMatchers
import dispatch._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

class ItemsIntSpec extends InMemDBEnvironment with DBTestData with FunSpecLike with BeforeAndAfterAll with ShouldMatchers {

  val server = unfiltered.jetty.Http.anylocal.filter(itemsPlan)
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

  describe("create item service"){
    it("should accept return 201 when correct payload is sent"){

      val json = write(JsonItem("reference", 100, "packages", None))
      val req = TestHelper.setJsonHeaders(myHost / "api" / "items").setBody(json).POST

      println(req.toString)

      val response = Http(req)

      response().getStatusCode should be (201)
    }
  }


}
object TestHelper {
  def setJsonHeaders(endpoint: Req): Req = {
    endpoint.setHeader("Content-Type", "application/json;charset=UTF-8")
      .setHeader("Accept", "application/json")
  }
}

case class JsonItem(val reference: String, val quantity: Int, val packages: String, picture: Option[Array[Byte]])
