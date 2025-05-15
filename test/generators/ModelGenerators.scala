/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package generators

import models.*
import org.scalacheck.Arbitrary

trait ModelGenerators extends UserAnswersEntryGenerators {
  self: Generators =>

  implicit lazy val arbitraryDepartureId: Arbitrary[DepartureId] =
    Arbitrary {
      for {
        departureId <- stringsWithMaxLength(17)
      } yield new DepartureId(departureId)
    }

  implicit lazy val arbitraryLocalReferenceNumber: Arbitrary[LocalReferenceNumber] =
    Arbitrary {
      for {
        lrn <- alphaNumericWithMaxLength(22)
      } yield new LocalReferenceNumber(lrn)
    }

  implicit lazy val arbitraryEoriNumber: Arbitrary[EoriNumber] =
    Arbitrary {
      for {
        number <- stringsWithMaxLength(17)
      } yield EoriNumber(number)
    }

  implicit lazy val arbitraryIE015: Arbitrary[IE015] = {
    import models.IE015.*
    Arbitrary {
      for {
        lrn                  <- nonEmptyString
        referenceNumber      <- nonEmptyString
        identificationNumber <- nonEmptyString
      } yield IE015(
        transitOperation = TransitOperation(
          lrn
        ),
        customsOfficeOfDeparture = CustomsOfficeOfDeparture(
          referenceNumber
        ),
        holderOfTheTransitProcedure = HolderOfTheTransitProcedure(
          identificationNumber = Some(identificationNumber),
          tirHolderIdentificationNumber = None,
          name = None,
          address = None,
          contactPerson = None
        )
      )
    }
  }

  implicit lazy val arbitraryIE028: Arbitrary[IE028] = {
    import models.IE028.*
    Arbitrary {
      for {
        mrn <- nonEmptyString
      } yield IE028(
        transitOperation = TransitOperation(
          mrn = mrn
        )
      )
    }
  }
}
