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

package utils

import base.SpecBase
import generators.Generators
import models.messages.CancellationDecisionUpdate
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._

import java.time.LocalDate

class CancellationDecisionUpdateHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "mrn" - {
    "must return row" in {
      forAll(arbitrary[CancellationDecisionUpdate]) {
        message =>
          val helper = new CancellationDecisionUpdateHelper(message)
          helper.mrn mustBe Some(
            SummaryListRow(
              key = "Movement reference number".toKey,
              value = Value(message.mrn.toText)
            )
          )
      }
    }
  }

  "initiatedByCustoms" - {
    "must return row" - {
      "when initiated by customs" in {
        val gen = for {
          message <- arbitrary[CancellationDecisionUpdate]
        } yield message.copy(cancellationInitiatedBy = 1)
        forAll(gen) {
          message =>
            val helper = new CancellationDecisionUpdateHelper(message)
            helper.initiatedByCustoms mustBe Some(
              SummaryListRow(
                key = "Initiated by customs?".toKey,
                value = Value("Yes".toText)
              )
            )
        }
      }

      "when not initiated by customs" in {
        val gen = for {
          message <- arbitrary[CancellationDecisionUpdate]
        } yield message.copy(cancellationInitiatedBy = 0)
        forAll(gen) {
          message =>
            val helper = new CancellationDecisionUpdateHelper(message)
            helper.initiatedByCustoms mustBe Some(
              SummaryListRow(
                key = "Initiated by customs?".toKey,
                value = Value("No".toText)
              )
            )
        }
      }
    }
  }

  "cancellationDecision" - {
    "must not return row" - {
      "when decision undefined" in {
        val gen = for {
          message <- arbitrary[CancellationDecisionUpdate]
        } yield message.copy(cancellationDecision = None)
        forAll(gen) {
          message =>
            val helper = new CancellationDecisionUpdateHelper(message)
            helper.cancellationDecision mustBe None
        }
      }
    }

    "must return row" - {
      "when accepted" in {
        val gen = for {
          message <- arbitrary[CancellationDecisionUpdate]
        } yield message.copy(cancellationDecision = Some(1))
        forAll(gen) {
          message =>
            val helper = new CancellationDecisionUpdateHelper(message)
            helper.cancellationDecision mustBe Some(
              SummaryListRow(
                key = "Status".toKey,
                value = Value("Accepted".toText)
              )
            )
        }
      }

      "when rejected" in {
        val gen = for {
          message <- arbitrary[CancellationDecisionUpdate]
        } yield message.copy(cancellationDecision = Some(0))
        forAll(gen) {
          message =>
            val helper = new CancellationDecisionUpdateHelper(message)
            helper.cancellationDecision mustBe Some(
              SummaryListRow(
                key = "Status".toKey,
                value = Value("Rejected".toText)
              )
            )
        }
      }
    }
  }

  "cancellationDecisionDate" - {
    "must return row" in {
      val gen = for {
        date    <- Gen.const(LocalDate.of(2000: Int, 1: Int, 1: Int))
        message <- arbitrary[CancellationDecisionUpdate]
      } yield message.copy(cancellationDecisionDate = date)
      forAll(gen) {
        message =>
          val helper = new CancellationDecisionUpdateHelper(message)
          helper.cancellationDecisionDate mustBe Some(
            SummaryListRow(
              key = "Date of decision".toKey,
              value = Value("1 January 2000".toText)
            )
          )
      }
    }
  }

  "cancellationJustification" - {
    "must not return row" - {
      "when justification undefined" in {
        val gen = for {
          message <- arbitrary[CancellationDecisionUpdate]
        } yield message.copy(cancellationJustification = None)
        forAll(gen) {
          message =>
            val helper = new CancellationDecisionUpdateHelper(message)
            helper.cancellationJustification mustBe None
        }
      }
    }

    "must return row" - {
      "when justification defined" in {
        val gen = for {
          justification <- Gen.alphaNumStr
          message       <- arbitrary[CancellationDecisionUpdate]
        } yield message.copy(cancellationJustification = Some(justification))
        forAll(gen) {
          message =>
            val helper = new CancellationDecisionUpdateHelper(message)
            helper.cancellationJustification mustBe Some(
              SummaryListRow(
                key = "Reason".toKey,
                value = Value(message.cancellationJustification.get.toText)
              )
            )
        }
      }
    }
  }
}
