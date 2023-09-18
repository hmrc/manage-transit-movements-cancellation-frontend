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

import cats.data.OptionT
import connectors.ApiConnector
import controllers.actions._
import forms.CancellationReasonFormProvider
import models.Constants.commentMaxLength
import models.messages.IE015Data
import models.{DepartureId, LocalReferenceNumber}
import navigation.Navigator
import pages.CancellationReasonPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.DepartureMessageService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CancellationReasonView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CancellationReasonController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  apiConnector: ApiConnector,
  formProvider: CancellationReasonFormProvider,
  departureMessageService: DepartureMessageService,
  sessionRepository: SessionRepository,
  val controllerComponents: MessagesControllerComponents,
  view: CancellationReasonView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider()

  def onPageLoad(departureId: String, lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireDataAndCheckCancellationStatus(departureId, lrn) {
    implicit request =>
      Ok(view(form, departureId, lrn, commentMaxLength))
  }

  def onSubmit(departureId: String, lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireDataAndCheckCancellationStatus(departureId, lrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, departureId, lrn, commentMaxLength))),
          value =>
            (
              for {
                ie015Data    <- OptionT(departureMessageService.mrnAllocatedIE015(departureId))
                ie014Data    <- OptionT.pure[Future](IE015Data.toIE014(ie015Data, value.trim))
                _            <- OptionT.liftF(sessionRepository.remove(departureId, request.eoriNumber))
                hasSubmitted <- OptionT.liftF(apiConnector.submit(ie014Data, DepartureId(departureId)))
              } yield hasSubmitted match {
                case true  => Redirect(controllers.routes.CancellationSubmissionConfirmationController.onPageLoad(lrn))
                case false => Redirect(controllers.routes.ErrorController.technicalDifficulties())
              }
            ).getOrElse(
              Redirect(controllers.routes.ErrorController.technicalDifficulties())
            )
        )
  }
}
