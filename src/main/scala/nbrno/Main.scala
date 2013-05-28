package nbrno

import unfiltered.jetty.Http
import scala.util.Properties

object Main extends App {
  Http(Properties.envOrElse("PORT", "8080").toInt).filter(RappersPlan).filter(SignupPlan).run()
}