package nbrno

import unfiltered.filter.Plan
import unfiltered.request.{POST, Path}
import unfiltered.response.{MethodNotAllowed, ResponseString}

import nbrno.domain.User
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.{read, write}

object RappersPlan extends Plan {

  def intent = {
    case Path("/") => {
      ResponseString(DatabaseHandler.getRappers)
    }
  }
}

object SignupPlan extends Plan {
  implicit val formats = DefaultFormats
  def intent = {
    case req @ Path("/api/signup") => req match{
      case POST(_) => {
        val newUser : User  = DatabaseHandler.createUser(
          req.parameterValues("username").head,
          req.parameterValues("email").head,
          req.parameterValues("password").head)

        ResponseString(write(newUser))
      }
      case _ => MethodNotAllowed
    }
  }
}

