package nbrno

import unfiltered.filter.Plan
import unfiltered.request._
import unfiltered.response._
import nbrno.domain.{SignupUser, User}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.{read, write}
import unfiltered.response.ResponseString
import org.slf4j.impl.StaticLoggerBinder
import unfiltered.Cookie

object RappersPlan extends Plan {
  implicit val formats = DefaultFormats

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
      case POST(_) => req match {
        case RequestContentType("application/json;charset=UTF-8") => req match {
          case Accepts.Json(_) =>
            Ok ~> JsonContent ~> {
              val user: SignupUser = read[SignupUser](Body.string(req))
              if (DatabaseHandler.availableUsername(user.username)) {
                DatabaseHandler.createUser(user.username, "", user.password)
                val sessionId : String = nbrno.Server.addUsernameToSessionStore(user.username)
                Ok ~> SetCookies(Cookie("SESSION_ID", sessionId))
              }
              else BadRequest ~> ResponseString("Username not available")
            }
          case _ => NotAcceptable
        }
        case _ => UnsupportedMediaType
      }
      case _ => MethodNotAllowed
    }

    case req@Path("/api/login") => req match {
      case POST(_) => req match {
        case RequestContentType("application/json;charset=UTF-8") => req match {
          case Accepts.Json(_) =>
            Ok ~> JsonContent ~> {
              val user: SignupUser = read[SignupUser](Body.string(req))
              if (DatabaseHandler.validateUser(user.username, user.password)) {
                val sessionId : String = nbrno.Server.addUsernameToSessionStore(user.username)
                Ok ~> SetCookies(Cookie("SESSION_ID", sessionId))
              }
              else Unauthorized ~> ResponseString("Wrong username or password")
            }
          case _ => NotAcceptable
        }
        case _ => UnsupportedMediaType
      }
      case _ => MethodNotAllowed
    }

    case req@Path("/api/cookies") => req match {
      case GET(_) & Cookies(cookies) => {
        val s: String = cookies("SESSION_ID").get.value
        ResponseString(s)
      }
      case _ => MethodNotAllowed
    }
  }
}


