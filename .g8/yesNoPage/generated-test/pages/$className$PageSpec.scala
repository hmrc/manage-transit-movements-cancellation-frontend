package pages

import pages.behaviours.PageBehaviours

class $className$PageSpec extends PageBehaviours {

  "$className$Page" - {

    beRetrievable[Boolean]($className$Page(departureId))

    beSettable[Boolean]($className$Page(departureId))

    beRemovable[Boolean]($className$Page(departureId))
  }
}
