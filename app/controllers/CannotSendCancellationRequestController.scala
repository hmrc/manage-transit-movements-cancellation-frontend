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

package controllers

import controllers.actions._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{DepartureMessageService, ReferenceDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.CannotSendCancellationRequestViewModel
import views.html.CannotSendCancellationRequestView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CannotSendCancellationRequestController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  referenceDataService: ReferenceDataService,
  departureMessageService: DepartureMessageService,
  view: CannotSendCancellationRequestView
)(implicit executionContext: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(departureId: String): Action[AnyContent] = actions
    .requireData(departureId)
    .async {
      implicit request =>
        departureMessageService.getIE015FromDeclarationMessage(departureId).flatMap {
          case Some(ie015) =>
            val customsOfficeRefNumber = ie015.data.CustomsOfficeOfDeparture.referenceNumber

            referenceDataService.getCustomsOfficeByCode(customsOfficeRefNumber).map {
              customsOffice =>
                Ok(view(request.lrn, departureId, CannotSendCancellationRequestViewModel(customsOfficeRefNumber, customsOffice)))
            }
          case _ => Future.successful(Redirect(controllers.routes.ErrorController.technicalDifficulties()))
        }
    }
}
