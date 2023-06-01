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

import play.api.libs.json.{Json, OFormat, OWrites}
import utils.Format.dateTimeFormatIE014

import java.time.LocalDateTime

case class IE014MessageData(preparationDateAndTime: LocalDateTime,
                            TransitOperation: TransitOperation,
                            CustomsOfficeOfDeparture: CustomsOfficeOfDeparture,
                            HolderOfTheTransitProcedure: HolderOfTheTransitProcedure,
                            Invalidation: Invalidation
)

object IE014MessageData {

  implicit val writes: OWrites[IE014MessageData] = OWrites {
    messageData =>
      Json.obj(
        "messageSender"               -> "NCTS", // TODO double check this
        "messageRecipient"            -> "NCTS", // TODO double check this
        "preparationDateAndTime"      -> messageData.preparationDateAndTime.format(dateTimeFormatIE014),
        "messageIdentification"       -> "CC014C", // TODO double check this
        "messageType"                 -> "CC014C",
        "@PhaseID"                    -> "NCTS5.0",
        "TransitOperation"            -> Json.toJsObject(messageData.TransitOperation),
        "CustomsOfficeOfDeparture"    -> Json.toJsObject(messageData.CustomsOfficeOfDeparture),
        "HolderOfTheTransitProcedure" -> Json.toJsObject(messageData.HolderOfTheTransitProcedure),
        "Invalidation"                -> Json.toJsObject(messageData.Invalidation)
      )
  }
}
