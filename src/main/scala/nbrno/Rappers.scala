package nbrno

import unfiltered.filter.Plan
import unfiltered.request.Path
import unfiltered.response.ResponseString

object Rappers extends Plan {
  def intent: Plan.Intent = {
    case Path("/") => {
      ResponseString("Hello World")
    }
  }
}
