package nbrno

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.lifted.Query
import nbrno.domain.User
import com.lambdaworks.crypto.SCryptUtil

object DatabaseHandler {

  object Rappers extends Table[(Int, String)]("rappers") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = id ~ name
  }

  object Users extends Table[(Int, String, String, String)]("users"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def username = column[String]("username")
    def email = column[String]("email")
    def passhash = column[String]("passhash")

    def * = id ~ username ~ email ~ passhash
    def forInsert = username ~ email ~ passhash <> ({ t => User(None, t._1, t._2, t._3)}, { (u: User) => Some((u.username, u.email, u.passhash))})
  }

  def getRappers: String = {
    Database.forURL("jdbc:postgresql://localhost:5432/nbrno", user = "clocking", password = "clocking", driver = "org.postgresql.Driver") withSession {
      var rapperString: String = ""

      Query(Rappers) foreach {
        case (id, name) => rapperString += "id: " ++ id.toString ++ ", name: " ++ name ++ "\n"
      }

      rapperString
    }
  }

  def createUser(username: String, email: String, password: String): User = {
    Database.forURL("jdbc:postgresql://localhost:5432/nbrno", user = "clocking", password = "clocking", driver = "org.postgresql.Driver") withSession {
      val passhash = SCryptUtil.scrypt(password, 512, 8 ,8)
      val userId = Users.forInsert returning Users.id insert User(None, username, email, passhash)
      User(Some(userId.toString.toInt), username, email, passhash)
    }
  }

}
