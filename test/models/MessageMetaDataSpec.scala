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
import models.MessageType.DepartureNotification
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

class MessageMetaDataSpec extends SpecBase {

  "reads" - {
    "must deserialise" in {
      val json: JsValue = Json.parse("""
          |{
          |  "_links": {
          |    "self": {
          |      "href": "/customs/transits/movements/departures/6365135ba5e821ee/messages/634982098f02f00b"
          |    },
          |    "departure": {
          |      "href": "/customs/transits/movements/departures/6365135ba5e821ee"
          |    }
          |  },
          |  "id": "634982098f02f00b",
          |  "departureId": "6365135ba5e821ee",
          |  "received": "2022-11-11T15:32:51.459Z",
          |  "type": "IE015",
          |  "status": "Success"
          |}
          |""".stripMargin)

      val result = json.as[MessageMetaData]

      result mustBe MessageMetaData(id = "634982098f02f00b",
                                    messageType = DepartureNotification,
                                    received = LocalDateTime.of(2022: Int, 11: Int, 11: Int, 15: Int, 32: Int, 51: Int, 459000000: Int)
      )
    }
  }
}
