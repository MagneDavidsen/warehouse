package nbrno

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import nbrno.domain.User
import scala.collection.immutable
import javax.sql.DataSource
import org.h2.jdbcx.JdbcDataSource

@RunWith(classOf[JUnitRunner])
class SessionStoreTest extends FunSuite {

  val dataSource: DataSource = {
    val ds = new JdbcDataSource
    ds.setURL("jdbc:h2:mem:test1")
    ds
  }

  var dbHandler = new DatabaseHandler(dataSource)

  val sessionStore : SessionStore = new SessionStore(new immutable.HashMap[String, User], dbHandler)

  test("one is one"){
    assert(1 == 1)

  }

  test("adding users works"){

    val token1 : String = sessionStore.addUser(User(None, "user-1", None, None, None, None, None))
    val token2 : String = sessionStore.addUser(User(None, "user-2", None, None, None, None, None))
    val token3 : String = sessionStore.addUser(User(None, "user-3", None, None, None, None, None))

    assert(sessionStore.size() == 3)

  }

  test("getting users works"){
    val token4 : String = sessionStore.addUser(User(None, "user-4", None, None, None, None, None))
    assert(sessionStore.getUser(token4).get.username == "user-4")
  }

  test("removing users works"){
    val token5 : String = sessionStore.addUser(User(None, "user-5", None, None, None, None, None))
    sessionStore.removeUser(token5)
    assert(sessionStore.getUser(token5) == None)
  }
}