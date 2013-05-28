package nbrno

import unfiltered.filter.Plan
import unfiltered.request._
import unfiltered.response._
import nbrno.domain.{SignupUser, User}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.{read, write}
import unfiltered.response.ResponseString

object RappersPlan extends Plan {

  def intent = {
    case GET(_) & Path("/api/rappers") => {
      ResponseString(write(DatabaseHandler.getRappers))
    }
  }
}


object SignupPlan extends Plan {
  implicit val formats = DefaultFormats

  def intent = {
    case req@Path("/api/signup") => req match {
      case POST(_) & RequestContentType(ct) => ct match {
        case "application/json" => {
          val user: SignupUser = read(Body.string(req))
          val newUser: User = DatabaseHandler.createUser(user.username, "", user.password)
          ResponseString(write(newUser))
        }
      }
      case _ => BadRequest ~> ResponseString("Invalid JSON data")
    }
    case _ => MethodNotAllowed ~> ResponseString("method MUST be POST")
  }
}


