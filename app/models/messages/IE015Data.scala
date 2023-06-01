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

import play.api.libs.json.{__, Json, OWrites, Reads}

import java.time.LocalDateTime

case class IE015Data(data: IE015MessageData)

object IE015Data {

  def fromIE015Data(messageData: IE015MessageData, preparationDateAndTime: LocalDateTime = LocalDateTime.now(), reason: String): IE014Data =
    IE014Data(
      IE014MessageData(
        messageSender = messageData.messageSender,
        messageRecipient = messageData.messageRecipient,
        preparationDateAndTime = preparationDateAndTime,
        TransitOperation = messageData.TransitOperation,
        CustomsOfficeOfDeparture = CustomsOfficeOfDeparture(messageData.CustomsOfficeOfDeparture.referenceNumber),
        HolderOfTheTransitProcedure = messageData.HolderOfTheTransitProcedure,
        Invalidation = Invalidation(justification = reason)
      )
    )

  implicit val reads: Reads[IE015Data]    = (__ \ "body" \ "n1:CC015C").read[IE015MessageData].map(IE015Data.apply)
  implicit val writes: OWrites[IE015Data] = Json.writes[IE015Data]
}
