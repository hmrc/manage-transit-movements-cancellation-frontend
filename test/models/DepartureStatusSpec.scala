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

        val json = JsString("DepartureSubmitted")

        json.validate[DepartureStatus].asOpt.value mustBe DepartureStatus.DepartureSubmitted
      }

    }

    "must deserialise" in {

      Json.toJson(DepartureStatus.values) mustBe JsString(DepartureStatus.values.toString)

    }
  }
}
