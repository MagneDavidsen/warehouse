package warehouse

import org.json4s.DefaultFormats
import org.json4s.native.Serialization.read
import org.json4s.native.Serialization.write
import org.slf4j.{Logger, LoggerFactory}
import unfiltered.filter.Plan
import unfiltered.request._
import unfiltered.response.{ResponseString, _}
import warehouse.domain.Item

trait PlanComponent {
  this: DatabaseHandlerComponent =>
  val itemsPlan: ItemsPlan


  class ItemsPlan extends Plan {
    implicit val formats = DefaultFormats
    val logger: Logger = LoggerFactory.getLogger("warehouse.ItemsPlan")

    def intent = {
      case req@Path("/api/items") =>
        val body: String = Body.string(req)
        logger.info("RequestBody: " ++ body)
        req match {
          case POST(_) => req match {
            case RequestContentType("application/json") => req match {
              case Accepts.Json(_) =>
                Ok ~> JsonContent ~> {
                  val item: Item = read[Item](body)
                  val registeredItem: Item = databaseHandler.createItem(item)
                  Created ~> ResponseString("Record created")
                }
              case _ => NotAcceptable
            }
            case _ => UnsupportedMediaType
          }

          case GET(_) => req match {
              case Accepts.Json(_) =>
                val items = databaseHandler.getItems();
                Ok ~> JsonContent ~> ResponseString(write(items))
              case _ => NotAcceptable
            }

          case _ => MethodNotAllowed
        }
    }
  }
}
