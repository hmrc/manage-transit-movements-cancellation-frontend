/*
 * Copyright 2024 HM Revenue & Customs
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
import models.MessageType._
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.JsString

class MessageTypeSpec extends SpecBase with ScalaCheckPropertyChecks {

  "must deserialise" - {
    "when IE015" in {
      val json = JsString("IE015")
      json.validate[MessageType].get mustEqual DepartureNotification
    }

    "when IE028" in {
      val json = JsString("IE028")
      json.validate[MessageType].get mustEqual AllocatedMRN
    }

    "when IE055" in {
      val json = JsString("IE055")
      json.validate[MessageType].get mustEqual GuaranteeRejected
    }

    "when IE060" in {
      val json = JsString("IE060")
      json.validate[MessageType].get mustEqual GuaranteeRejected
    }

    "when IE928" in {
      val json = JsString("IE928")
      json.validate[MessageType].get mustEqual GuaranteeRejected
    }

    "when IE004" in {
      val json = JsString("IE004")
      json.validate[MessageType].get mustEqual AmendmentAcceptance
    }

    "when something else" in {
      forAll(Gen.alphaNumStr) {
        code =>
          val json = JsString(code)
          json.validate[MessageType].get mustEqual Other(code)
      }
    }
  }
}
