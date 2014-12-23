
package warehouse

import scala.slick.driver.H2Driver.simple._
import org.scalatest.{FunSuiteLike, BeforeAndAfterAll, BeforeAndAfter}
import org.scalatest.matchers.ShouldMatchers
import scala.slick.session.Database

class DatabaseHandlerSpec extends InMemDBEnvironment with DBTestData with FunSuiteLike with ShouldMatchers with BeforeAndAfter with BeforeAndAfterAll{

  implicit val session = Database.forDataSource(dataSource).createSession()

  override def beforeAll {
    createAllTables
    insertTestData
  }

  override def afterAll {
    session.close()
  }

}