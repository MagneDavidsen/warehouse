package nbrno

import unfiltered.filter.Plan
import unfiltered.request.Path
import unfiltered.response.ResponseString

object RappersPlan extends Plan {

  def intent = {
    case Path("/") => {
      ResponseString(DatabaseHandler.getRappers)
    }
  }
}

