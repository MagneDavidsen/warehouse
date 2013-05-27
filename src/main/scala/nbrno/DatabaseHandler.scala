package nbrno

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.lifted.Query

object DatabaseHandler {

  object Rappers extends Table[(Int, String)]("rappers") {
    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")
    def * = id ~ name
  }

  def getRappers: String = {
    Database.forURL("jdbc:postgresql://localhost:5432/nbrno", user = "clocking", password = "clocking", driver = "org.postgresql.Driver") withSession {
      Rappers.ddl
      var rapperString : String = ""

      Query(Rappers) foreach {case (id, name) => rapperString += "id: " ++ id.toString ++ ", name: " ++ name ++ "\n"}

      rapperString
    }
  }
}
