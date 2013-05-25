package nbrno

import unfiltered.jetty.Http

object Main extends App {
  Http(8080).filter(Rappers).run()
}