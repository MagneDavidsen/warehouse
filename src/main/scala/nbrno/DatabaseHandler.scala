package nbrno

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.lifted.Query
import nbrno.domain.{Rating, Rapper, User}
import com.lambdaworks.crypto.SCryptUtil
import scala.collection.mutable
import javax.sql.DataSource
import org.postgresql.ds.{PGPoolingDataSource, PGConnectionPoolDataSource, PGSimpleDataSource}
import scala.util.Properties


// <p>If you're sure you want to use this, then you must set the properties
// dataSourceName, databaseName, user, and password (if required for the user).
// The settings for serverName, portNumber, initialConnections, and
// maxConnections are optional.  Note that <i>only connections
// for the default user will be pooled!</i>  Connections for other users will
// be normal non-pooled connections, and will not count against the maximum pool
// size limit.</p


object DatabaseHandler {

  val dataSource: DataSource = {
    val ds = new PGPoolingDataSource
    ds.setDataSourceName("nbrno")
    ds.setDatabaseName("nbrno")
    ds.setUser(Properties.envOrElse("DB_USER", "clocking"))
    ds.setPassword(Properties.envOrElse("DB_PASSWORD", "clocking"))
    ds.setServerName("localhost")
    ds.setPortNumber(5432)
    ds
  }

  object Rappers extends Table[(Int, String)]("rappers") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = id ~ name
  }

  object UserObject {
    def fromRow(id: Option[Int], username: String, email: String, passhash: Option[String]): User = User(id, username, email, passhash)
    def toRow(u: User) = Some(u.id, u.username, u.email, u.passhash)
  }

  object Users extends Table[User]("users"){
    def id = columnToOptionColumn(column[Int]("id", O.PrimaryKey, O.AutoInc))
    def username = column[String]("username")
    def email = column[String]("email")
    def passhash = columnToOptionColumn(column[String]("passhash"))

    def * = id ~ username ~ email ~ passhash <> (UserObject.fromRow _, UserObject.toRow _)
    def forInsert = username ~ email ~ passhash <> ({ t => User(None, t._1, t._2, t._3)}, { (u: User) => Some((u.username, u.email, u.passhash))})
  }

  object RatingObject {
    def fromRow(id : Int, user_id : Int, rapper_id : Int, rating : Int) : Rating = Rating(id, user_id, rapper_id, rating)
    def toRow(r : Rating) = Some(r.id, r.userId, r.rapperId, r.rating)
  }

  object Ratings extends Table[Rating]("ratings"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def user_id = column[Int]("user_id")
    def rapper_id = column[Int]("rapper_id")
    def rating = column[Int]("rating")

    def * = id ~ user_id ~rapper_id ~ rating <> (RatingObject.fromRow _, RatingObject.toRow _)
    def forInsert = user_id ~ rapper_id ~ rating

  }

  def getRappers: List[Rapper] = {
    Database.forDataSource(dataSource) withSession {
      var rappers: mutable.MutableList[Rapper] = new mutable.MutableList[Rapper]
      Query(Rappers) foreach {case (id, name) => rappers += new Rapper(Some(id), name)}
      rappers.toList
  }}

  def createUser(username: String, email: String, password: String): User = {
    Database.forDataSource(dataSource) withSession {
      val passhash = SCryptUtil.scrypt(password, 512, 8 ,8)
      val userId = Users.forInsert returning Users.id insert User(None, username, email, Some(passhash))
      User(userId, username, email, None)
    }
  }

  def availableUsername(username : String) : Boolean = {
    Database.forDataSource(dataSource) withSession {
      val query = for{
        u <- Users if u.username === username
      } yield u
      val user : Option[User] = query.firstOption
      user.isEmpty
    }
  }

  def validateUser(username : String, password : String) : Boolean = {
    Database.forDataSource(dataSource) withSession {
      val query = for{
        u <- Users if u.username === username
      } yield u
      val user : Option[User] = query.firstOption
      user.isDefined && SCryptUtil.check(password, user.get.passhash.get)
    }
  }

  def vote(userId : Int, rapperId : Int, voteUp : Boolean){
    Database.forDataSource(dataSource) withSession {
      val query =  for{
        r <- Ratings if(r.user_id === userId && r.rapper_id === rapperId)
      } yield r
      val rating : Option[Rating] = query.firstOption
      rating
    }
  }

}
