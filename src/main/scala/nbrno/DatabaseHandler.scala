package nbrno

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database
import Database.threadLocalSession
import nbrno.domain.{Rating, Rapper, User}
import com.lambdaworks.crypto.SCryptUtil
import javax.sql.DataSource
import java.sql.Timestamp
import java.util.Date
import scala.collection.immutable.HashMap

class DatabaseHandler(dataSource : DataSource) {

  object Rappers extends Table[Rapper]("rappers") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def createdAt = column[Timestamp]("created_at")
    def * = id ~ name ~ createdAt <> ({t => Rapper(t._1, t._2, None, t._3)}, {(r:Rapper) => Some(r.id, r.name, r.createdAt)})
  }

  object Sessions extends Table[(Int, String, Int, Timestamp)]("sessions") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def sessionId = column[String]("session_id")
    def userId = column[Int]("user_id")
    def createdAt = column[Timestamp]("created_at")
    def * = id ~ sessionId ~ userId ~ createdAt
    def forInsert = sessionId ~ userId ~ createdAt
  }

  object UserObject {
    def fromRow(id: Option[Int], username: String, email:Option[String], passhash: Option[String],
                createdFromIp:Option[String],createdAt:Option[Timestamp]): User =
                User(id, username, email, None, passhash, createdFromIp, createdAt)

    def toRow(u: User) = Some(u.id, u.username, u.email, u.passhash, u.createdFromIp, u.createdAt)
  }

  object Users extends Table[User]("users"){
    def id = columnToOptionColumn(column[Int]("id", O.PrimaryKey, O.AutoInc))
    def username = column[String]("username")
    def email = columnToOptionColumn(column[String]("email"))
    def passhash = columnToOptionColumn(column[String]("passhash"))
    def createdFromIp = columnToOptionColumn(column[String]("created_from_ip"))
    def createdAt = columnToOptionColumn(column[Timestamp]("created_at"))

    def * = id ~ username ~ email ~ passhash ~ createdFromIp ~ createdAt <> (UserObject.fromRow _, UserObject.toRow _)
    def forInsert = username ~ email ~ passhash ~ createdFromIp ~ createdAt <>
      ({ t => User(None, t._1, t._2, None, t._3, t._4, None)}, { (u: User) => Some((u.username, u.email, u.passhash, u.createdFromIp, u.createdAt))})
  }

  object RatingObject {
    def fromRow(id : Option[Int], user_id : Int, rapper_id : Int, rating : Int, updatedAt:Timestamp) : Rating =
      Rating(id, user_id, rapper_id, rating, updatedAt)
    def toRow(r : Rating) = Some(r.id, r.userId, r.rapperId, r.rating, r.updatedAt)
  }

  object Ratings extends Table[Rating]("ratings"){
    def id = columnToOptionColumn(column[Int]("id", O.PrimaryKey, O.AutoInc))
    def user_id = column[Int]("user_id")
    def rapper_id = column[Int]("rapper_id")
    def rating = column[Int]("rating")
    def updatedAt = column[Timestamp]("updated_at")

    def * = id ~ user_id ~rapper_id ~ rating ~ updatedAt <> (RatingObject.fromRow _, RatingObject.toRow _)
    def forInsert = user_id ~ rapper_id ~ rating ~ updatedAt <>
      ({t => Rating(None, t._1, t._2, t._3, t._4)}, {(r : Rating) => Some((r.userId, r.rapperId, r.rating, r.updatedAt))})
  }

  def getVotes(username : String) : List[Rating] = {
    Database.forDataSource(dataSource) withSession {
      val userId : Option[Int] = Query(Users).filter(_.username === username).first.id
      Query(Ratings).filter(_.user_id === userId).list
    }
  }

  //TODO: Refactor when I understand Slick
  def getRappersWithTotalScore : List[Rapper] = {
    Database.forDataSource(dataSource) withSession {
      val rappersRatings = for {
        (rappers, ratings) <- Rappers leftJoin Ratings on (_.id === _.rapper_id)
      } yield (rappers, ratings.rating.?)

      val grouped = rappersRatings.list.groupBy{case (rapper, rating) => rapper.id}

      grouped.map{case (rapperId, rr) =>
        (Rapper(rapperId, rr.head._1.name, Some(rr.map{
          case (rapper, rating) => if(rating.isDefined) rating.get else 0 }.sum), rr.head._1.createdAt))}.toList
     }}

  def getUser(userId : Int) : Option[User] = {
    Database.forDataSource(dataSource) withSession {
      val query = for{
        u <- Users if u.id === userId
      } yield u
      query.firstOption
    }
  }

  def createUser(user : User, ip : String): User = {
    val now = new Timestamp(new Date().getTime)
    Database.forDataSource(dataSource) withSession {
      val passhash = SCryptUtil.scrypt(user.password.get, 512, 8 ,8)
      val userId = Users.forInsert returning Users.id insert
        User(None, user.username, user.email, None, Some(passhash), Some(ip), Some(now))

      User(userId, user.username, user.email, None, None,Some(ip), Some(now))
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

  def validateUser(username : String, password : String) : Option[User] = {
    Database.forDataSource(dataSource) withSession {
      val query = for{
        u <- Users if u.username === username
      } yield u
      val user : Option[User] = query.firstOption
      if(user.isDefined && SCryptUtil.check(password, user.get.passhash.get)) user
      else None
    }
  }

  def vote(userId : Int, rapperId : Int, voteUp : Boolean) = {
    Database.forDataSource(dataSource) withSession {
      val points : Int = if(voteUp) 1 else -1
      val query =  for{
        r <- Ratings if(r.user_id === userId && r.rapper_id === rapperId)
      } yield r
      val rating : Option[Rating] = query.firstOption
      if(rating.isDefined) updateRating(rating.get, points)
      else createRating(userId, rapperId, points)
    }
  }

  def getStats = {
    Database.forDataSource(dataSource) withSession {
      val numRappers = Query(Rappers.length).first
      val numUsers = Query(Users.length).first
      val numRatings = Query(Ratings.length).first

      HashMap("numRappers" -> numRappers, "numUsers" -> numUsers, "numRatings" -> numRatings)
    }
  }

  def saveSession(sessionId : String, userId : Int){
    val now = new Timestamp(new Date().getTime)
    Database.forDataSource(dataSource) withSession {
      Sessions.forInsert insert(sessionId, userId, now)
    }
  }

  def retrieveSession(sessionId : String) : Option[User] = {
    Database.forDataSource(dataSource) withSession {
      val query = for{
        s <- Sessions if s.sessionId === sessionId
      } yield s.userId

      val userId = query.firstOption

      userId match{
        case Some( id : Int) => getUser(id)
        case None => None
      }
    }
  }

  def removeSession(sessionId : String){
    Database.forDataSource(dataSource) withSession {
      val query = for{
        s <- Sessions if s.sessionId === sessionId
      } yield s
      query.delete
    }
  }

  private def updateRating(rating : Rating, points : Int){
    val q = for { r <- Ratings if r.id === rating.id } yield {r.rating ~ r.updatedAt}
    q.update(points, new Timestamp(new Date().getTime))
  }

  private def createRating(userId : Int, rapperId : Int, points : Int) =
    Ratings.forInsert insert Rating(None, userId, rapperId, points, new Timestamp(new Date().getTime))
}