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

package models

import base.SpecBase
import play.api.libs.json.{JsString, Json}

class DepartureStatusSpec extends SpecBase {

  "DepartureStatus" - {

    "must serialise" - {

      "to an DepartureSubmitted" in {

        val json = JsString("PositiveAcknowledgement")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.PositiveAcknowledgement
      }

      "to an MrnAllocated" in {

        val json = JsString("MrnAllocated")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.MrnAllocated
      }

      "to a DepartureRejected" in {

        val json = JsString("DepartureRejected")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.DepartureRejected
      }

      "to an ControlDecisionNotification" in {

        val json = JsString("ControlDecisionNotification")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.ControlDecisionNotification
      }

      "to an NoReleaseForTransit" in {

        val json = JsString("NoReleaseForTransit")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.NoReleaseForTransit
      }

      "to an ReleaseForTransit" in {

        val json = JsString("ReleaseForTransit")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.ReleaseForTransit
      }

      "to an DeclarationCancellationRequest" in {

        val json = JsString("DeclarationCancellationRequest")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.DeclarationCancellationRequest
      }

      "to an CancellationDecision" in {

        val json = JsString("CancellationDecision")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.CancellationDecision
      }

      "to an WriteOffNotification" in {

        val json = JsString("WriteOffNotification")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.WriteOffNotification
      }

      "to an GuaranteeNotValid" in {

        val json = JsString("GuaranteeNotValid")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.GuaranteeNotValid
      }

      "to an DepartureSubmittedNegativeAcknowledgement" in {

        val json = JsString("DepartureSubmittedNegativeAcknowledgement")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.DepartureSubmittedNegativeAcknowledgement
      }

      "to an DeclarationCancellationRequestNegativeAcknowledgement" in {

        val json = JsString("DeclarationCancellationRequestNegativeAcknowledgement")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.DeclarationCancellationRequestNegativeAcknowledgement
      }

    }

    "must deserialise" in {

      DepartureStatus.values.map(
        departureStatus => Json.toJson(departureStatus) mustBe JsString(departureStatus.toString)
      )
    }
  }
}
