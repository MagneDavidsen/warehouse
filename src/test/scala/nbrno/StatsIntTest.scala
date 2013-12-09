package nbrno

import org.scalatest.{BeforeAndAfterAll, FunSpec}
import dispatch._
import org.scalatest.matchers.ShouldMatchers
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

class StatsIntTest extends FunSpec with BeforeAndAfterAll with ShouldMatchers{

  val server = unfiltered.jetty.Http.anylocal.filter(StatsPlan)

  override def beforeAll(){
    server.start()
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