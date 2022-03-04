package pages

import models.$className$
import pages.behaviours.PageBehaviours

class $className$PageSpec extends PageBehaviours {

  "$className$Page" - {

    beRetrievable[$className$]($className$Page(departureId))

    beSettable[$className$]($className$Page(departureId))

    beRemovable[$className$]($className$Page(departureId))
  }
}
