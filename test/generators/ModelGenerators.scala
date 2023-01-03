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

import models._
import models.messages.CancellationDecisionUpdate
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

import java.time.LocalDate

trait ModelGenerators extends UserAnswersEntryGenerators {
  self: Generators =>

  implicit lazy val arbitraryDepartureId: Arbitrary[DepartureId] =
    Arbitrary {
      for {
        departureId <- intsBelowValue(1000000)
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

  implicit lazy val arbitraryCancellationDecisionUpdate: Arbitrary[CancellationDecisionUpdate] =
    Arbitrary {
      for {
        mrn                       <- Gen.alphaNumStr
        cancellationRequestDate   <- arbitrary[Option[LocalDate]]
        cancellationInitiatedBy   <- Gen.oneOf(0, 1)
        cancellationDecision      <- Gen.option(Gen.oneOf(0, 1))
        cancellationDecisionDate  <- arbitrary[LocalDate]
        cancellationJustification <- Gen.option(Gen.alphaNumStr)
      } yield CancellationDecisionUpdate(
        mrn,
        cancellationRequestDate,
        cancellationInitiatedBy,
        cancellationDecision,
        cancellationDecisionDate,
        cancellationJustification
      )
    }
}
