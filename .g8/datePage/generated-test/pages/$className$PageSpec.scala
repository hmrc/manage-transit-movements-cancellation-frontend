package pages

import java.time.LocalDate

import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours
import models.DepartureId

class $className$PageSpec(departure: DepartureId) extends PageBehaviours {

  "$className$Page" - {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate]($className$Page(departure))

    beSettable[LocalDate]($className$Page(departure))

    beRemovable[LocalDate]($className$Page(departure))
  }
}
