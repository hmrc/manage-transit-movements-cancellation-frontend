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

package controllers

import config.FrontendAppConfig
import connectors.DepartureMovementConnector
import controllers.actions._
import models.DepartureId
import models.response.ResponseDeparture
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.{CanNotCancel, CancellationSubmissionConfirmation}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CancellationSubmissionConfirmationController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  departureMovementConnector: DepartureMovementConnector,
  val controllerComponents: MessagesControllerComponents,
  appConfig: FrontendAppConfig,
  confirmationView: CancellationSubmissionConfirmation,
  canNotCancelView: CanNotCancel
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(departureId: DepartureId): Action[AnyContent] = identify.async {
    implicit request =>
      val departureListUrl = s"${appConfig.manageTransitMovementsViewDeparturesUrl}"

      departureMovementConnector.getDeparture(departureId).map {
        case Some(responseDeparture: ResponseDeparture) =>
          Ok(confirmationView(departureListUrl, responseDeparture.localReferenceNumber))
        case None =>
          NotFound(canNotCancelView(departureListUrl))
      }
  }
}
