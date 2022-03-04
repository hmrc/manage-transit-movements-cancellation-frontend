package pages

import models.{$className$, DepartureId}
import pages.behaviours.PageBehaviours

class $className$PageSpec(departure: DepartureId) extends PageBehaviours {

  "$className$Page" - {

    beRetrievable[$className$]($className$Page(departure))

    beSettable[$className$]($className$Page(departure))

    beRemovable[$className$]($className$Page(departure))
  }
}
