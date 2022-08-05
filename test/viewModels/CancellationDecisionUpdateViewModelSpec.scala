/*
 * Copyright 2022 HM Revenue & Customs
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

package viewModels

import base.SpecBase
import generators.Generators
import models.messages.CancellationDecisionUpdate
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import java.time.LocalDate

class CancellationDecisionUpdateViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "CancellationDecisionUpdateViewModel" - {

    "when minimum data set" - {
      "must return 3 rows" in {
        val gen = for {
          mrn                <- Gen.alphaNumStr
          initiatedByCustoms <- Gen.oneOf(0, 1)
          decisionDate       <- arbitrary[LocalDate]
        } yield CancellationDecisionUpdate(mrn, None, initiatedByCustoms, None, decisionDate, None)

        forAll(gen) {
          message =>
            val viewModel = CancellationDecisionUpdateViewModel(message)
            viewModel.rows.length mustBe 3
        }
      }
    }

    "when maximum data set" - {
      "must return 5 rows" in {
        val gen = for {
          mrn                <- Gen.alphaNumStr
          initiatedByCustoms <- Gen.oneOf(0, 1)
          decision           <- Gen.oneOf(0, 1)
          decisionDate       <- arbitrary[LocalDate]
          justification      <- Gen.alphaNumStr
        } yield CancellationDecisionUpdate(mrn, None, initiatedByCustoms, Some(decision), decisionDate, Some(justification))

        forAll(gen) {
          message =>
            val viewModel = CancellationDecisionUpdateViewModel(message)
            viewModel.rows.length mustBe 5
        }
      }
    }
  }
}
