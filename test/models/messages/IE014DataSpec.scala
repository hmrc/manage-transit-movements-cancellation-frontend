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

package models.messages

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsValue, Json}
import utils.Format.dateTimeFormatIE014

import java.time.LocalDateTime

class IE014DataSpec extends AnyFreeSpec with Matchers with OptionValues {

  "IE014DataSpec" - {

    "must serialize" in {

      val dateTimeNow = LocalDateTime.now()

      val ie014Data = IE014Data(
        IE014MessageData(
          "NCTS",
          dateTimeNow,
          TransitOperation(Some("AB123"), Some("CD123")),
          CustomsOfficeOfDeparture("GB123"),
          HolderOfTheTransitProcedure("idNo"),
          Invalidation(justification = "reason for cancellation")
        )
      )

      val expectedResult: JsValue = Json.parse(
        s"""
           |{
           |   "n1:CC014C" : {
           |       "messageSender" : "NCTS",
           |       "messageRecipient" : "NTA.GB",
           |       "preparationDateAndTime" : "${ie014Data.`n1:CC014C`.preparationDateAndTime.format(dateTimeFormatIE014)}",
           |       "messageIdentification" : "CC014C",
           |       "messageType" : "CC014C",
           |       "@PhaseID" : "NCTS5.0",
           |       "TransitOperation" : {
           |           "MRN" : "AB123",
           |           "LRN" : "CD123"
           |       },
           |       "CustomsOfficeOfDeparture" : {
           |           "referenceNumber" : "GB123"
           |       },
           |       "HolderOfTheTransitProcedure" : {
           |           "identificationNumber" : "idNo"
           |       },
           |       "Invalidation" : {
           |         "requestDateAndTime" : "${ie014Data.`n1:CC014C`.Invalidation.requestDateAndTime.format(dateTimeFormatIE014)}",
           |         "decisionDateAndTime" : "${ie014Data.`n1:CC014C`.Invalidation.decisionDateAndTime.format(dateTimeFormatIE014)}",
           |         "decision" : "0",
           |         "initiatedByCustoms" : "0",
           |         "justification" : "reason for cancellation"
           |       }
           |   }
           |}
           |""".stripMargin
      )

      Json.toJsObject(ie014Data) mustBe expectedResult
    }
  }

}
