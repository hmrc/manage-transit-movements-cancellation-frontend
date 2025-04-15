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

package services.submission

import connectors.ApiConnector
import generated._
import models.{DepartureId, EoriNumber}
import scalaxb.DataRecord
import scalaxb.`package`.toXML
import services.DateTimeService
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import javax.inject.Inject
import scala.concurrent.Future
import scala.xml.{NamespaceBinding, NodeSeq}

class SubmissionService @Inject() (
  dateTimeService: DateTimeService,
  messageIdentificationService: MessageIdentificationService,
  connector: ApiConnector
) {

  private val scope: NamespaceBinding = scalaxb.toScope(Some("ncts") -> "http://ncts.dgtaxud.ec")

  def submit(
    eoriNumber: EoriNumber,
    ie015: CC015CType,
    mrn: Option[String],
    justification: String,
    departureId: DepartureId
  )(implicit hc: HeaderCarrier): Future[HttpResponse] =
    connector.submit(buildXml(eoriNumber, ie015, mrn, justification), departureId)

  private def buildXml(eoriNumber: EoriNumber, ie015: CC015CType, mrn: Option[String], justification: String): NodeSeq =
    toXML(transform(eoriNumber, ie015, mrn, justification), s"ncts:${CC014C.toString}", scope)

  def transform(eoriNumber: EoriNumber, ie015: CC015CType, mrn: Option[String], justification: String): CC014CType = {
    val officeOfDeparture           = ie015.CustomsOfficeOfDeparture
    val holderOfTheTransitProcedure = ie015.HolderOfTheTransitProcedure
    CC014CType(
      messageSequence1 = messageSequence(eoriNumber, officeOfDeparture.referenceNumber),
      TransitOperation = transitOperation(Option(ie015.TransitOperation.LRN), mrn),
      Invalidation = invalidation(justification),
      CustomsOfficeOfDeparture = officeOfDeparture,
      HolderOfTheTransitProcedure = holderOfTheTransitProcedure,
      attributes = attributes
    )
  }

  // TODO: If phase 5 set id to NCTS5u461 otherwise set to NCTS6
  def attributes: Map[String, DataRecord[?]] =
    Map("@PhaseID" -> DataRecord(PhaseIDtype.fromString(NCTS5u461.toString, scope)))

  def messageSequence(eoriNumber: EoriNumber, officeOfDeparture: String): MESSAGESequence =
    MESSAGESequence(
      messageSender = eoriNumber.value,
      messageRecipient = s"NTA.${officeOfDeparture.take(2)}",
      preparationDateAndTime = dateTimeService.currentDateTime,
      messageIdentification = messageIdentificationService.randomIdentifier,
      messageType = CC014C,
      correlationIdentifier = None
    )

  def transitOperation(lrn: Option[String], mrn: Option[String]): TransitOperationType56 =
    TransitOperationType56(
      LRN = if (mrn.isDefined) None else lrn,
      MRN = mrn
    )

  def invalidation(justification: String): InvalidationType02 =
    InvalidationType02(
      requestDateAndTime = Some(dateTimeService.currentDateTime),
      decisionDateAndTime = None,
      decision = None,
      initiatedByCustoms = Number0,
      justification = Some(justification)
    )
}
