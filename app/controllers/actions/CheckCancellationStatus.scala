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

package controllers.actions

import connectors.DepartureMovementConnector
import models.DepartureStatus._
import models.requests.{AuthorisedRequest, IdentifierRequest}
import models.response.ResponseDeparture
import models.{DepartureId, DepartureStatus}
import play.api.mvc.Results._
import play.api.mvc.{ActionRefiner, Result}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckCancellationStatusProvider @Inject() (
  departureMovementConnector: DepartureMovementConnector
)(implicit ec: ExecutionContext) {

  def apply(departureId: DepartureId): ActionRefiner[IdentifierRequest, AuthorisedRequest] =
    new CancellationStatusAction(departureId, departureMovementConnector)

}

class CancellationStatusAction(
  departureId: DepartureId,
  departureMovementConnector: DepartureMovementConnector
)(implicit protected val executionContext: ExecutionContext)
    extends ActionRefiner[IdentifierRequest, AuthorisedRequest] {

  final val validStatus: Seq[DepartureStatus] =
    Seq(GuaranteeNotValid, MrnAllocated, NoReleaseForTransit, ControlDecisionNotification, DeclarationCancellationRequestNegativeAcknowledgement)

  override protected def refine[A](request: IdentifierRequest[A]): Future[Either[Result, AuthorisedRequest[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    departureMovementConnector.getDeparture(departureId).map {
      case Some(responseDeparture: ResponseDeparture) if !validStatus.contains(responseDeparture.status) =>
        Left(Redirect(controllers.routes.CanNotCancelController.onPageLoad()))

      case Some(responseDeparture: ResponseDeparture) =>
        Right(AuthorisedRequest(request.request, request.eoriNumber, responseDeparture.localReferenceNumber))

      case None =>
        Left(Redirect(controllers.routes.DeclarationNotFoundController.onPageLoad()))

    }
  }
}
