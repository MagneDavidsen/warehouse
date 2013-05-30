package nbrno

import unfiltered.jetty.Http
import scala.util.Properties
import java.net.URL

object Server extends App {
  Http(Properties.envOrElse("PORT", "8081").toInt).resources(new URL(getClass().getResource("/www/"), "."))
    .filter(RappersPlan).filter(SignupPlan).run()
}