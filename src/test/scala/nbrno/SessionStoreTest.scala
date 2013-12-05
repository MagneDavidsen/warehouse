package nbrno

import org.scalatest.FunSuite
import nbrno.domain.User
import org.scalatest.matchers.ShouldMatchers

class SessionStoreTest extends FunSuite with ShouldMatchers with MockDBEnvironment{

  test("adding users works"){
    val token1 : String = sessionStore.addUser(User(Some(1), "user-1", None, None, None, None, None))
    val token2 : String = sessionStore.addUser(User(Some(2), "user-2", None, None, None, None, None))
    val token3 : String = sessionStore.addUser(User(Some(3), "user-3", None, None, None, None, None))

    sessionStore.size() should be (3)
  }

  test("getting users works"){
    val token4 : String = sessionStore.addUser(User(Some(4), "user-4", None, None, None, None, None))

    sessionStore.getUser(token4).get.username should be ("user-4")
  }

  test("removing users works"){
    val token5 : String = sessionStore.addUser(User(Some(5), "user-5", None, None, None, None, None))
    sessionStore.removeUser(token5)

    sessionStore.getUser(token5) should be (null) //because of no DB-implementation
  }
}