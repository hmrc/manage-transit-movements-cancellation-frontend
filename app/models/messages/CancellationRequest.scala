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

import models.response.{MRNAllocatedMessage, MRNAllocatedRootLevel, PrincipalTraderDetails}

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.xml.NodeSeq

case class CancellationRequest(
  rootLevel: MRNAllocatedRootLevel,
  movementReferenceNumber: String,
  dateOfCancellation: LocalDate,
  cancellationReason: String,
  principal: PrincipalTraderDetails,
  departureOffice: String
) {

  private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
  def toXml: NodeSeq                           = <CC014A>
    {rootLevel.toXml}
    <HEAHEA>
      <DocNumHEA5>{movementReferenceNumber}</DocNumHEA5>
      <DatOfCanReqHEA147>{dateOfCancellation.format(dateFormatter)}</DatOfCanReqHEA147>
      <CanReaHEA250>{cancellationReason}</CanReaHEA250>
    </HEAHEA>
    {principal.toXml}
    <CUSOFFDEPEPT>
      <RefNumEPT1>{departureOffice}</RefNumEPT1>
    </CUSOFFDEPEPT>
  </CC014A>
}

object CancellationRequest {

  def apply(cancellationReason: String, dateOfCancellation: LocalDate, mrnAllocatedMessage: MRNAllocatedMessage): CancellationRequest =
    new CancellationRequest(
      rootLevel = mrnAllocatedMessage.rootLevel,
      movementReferenceNumber = mrnAllocatedMessage.movementReferenceNumber,
      dateOfCancellation = dateOfCancellation,
      cancellationReason = cancellationReason,
      principal = mrnAllocatedMessage.principalTraderDetails,
      departureOffice = mrnAllocatedMessage.customsOfficeReference
    )
}
