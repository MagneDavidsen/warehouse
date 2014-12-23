package nbrno

import org.scalatest.{FunSpecLike, BeforeAndAfterAll, FunSpec}
import dispatch._
import org.scalatest.matchers.ShouldMatchers
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import scala.slick.session.Database

class StatsIntSpec extends InMemDBEnvironment with DBTestData with FunSpecLike with BeforeAndAfterAll with ShouldMatchers{

  val server = unfiltered.jetty.Http.anylocal.filter(statsPlan)
  implicit val session = Database.forDataSource(dataSource).createSession()

  override def beforeAll(){
    createAllTables
    insertTestData

    server.start()
  }

  override def afterAll {
    session.close()
    server.stop()
  }

  describe("the stats api"){
    it("should return json with numRatings, numUsers and numRappers"){
      val myHost = host("localhost", server.port)
      val myRequest = myHost / "api" / "stats"

      val stats = Http(myRequest OK as.String)

      stats() should fullyMatch regex ("\\{\"numRatings\":[\\d+],\"numUsers\":[\\d+],\"numRappers\":[\\d+]\\}")
    }
  }
}