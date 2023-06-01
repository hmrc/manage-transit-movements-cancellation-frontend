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

import connectors.ApiConnector
import controllers.actions._
import forms.CancellationReasonFormProvider
import models.Constants.commentMaxLength
import models.DepartureId
import models.messages.{IE014Data, IE014MessageData, IE015Data, Invalidation}
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
  sessionRepository: SessionRepository,
  apiConnector: ApiConnector,
  navigator: Navigator,
  formProvider: CancellationReasonFormProvider,
  departureMessageService: DepartureMessageService,
  val controllerComponents: MessagesControllerComponents,
  view: CancellationReasonView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider()

  def onPageLoad(departureId: String): Action[AnyContent] = actions.requireData(departureId) {
    implicit request =>
      val preparedForm = request.userAnswers.get(CancellationReasonPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, departureId, request.lrn, commentMaxLength))
  }

  def onSubmit(departureId: String): Action[AnyContent] = actions.requireData(departureId).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, departureId, request.lrn, commentMaxLength))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(CancellationReasonPage, value))
              _              <- sessionRepository.set(updatedAnswers)
              ie015Data      <- departureMessageService.getIE015FromDeclarationMessage(departureId)
              ie014Data = IE015Data.fromIE015Data(messageData = ie015Data.get.data, reason = value)
              result <- apiConnector.submit(ie014Data, DepartureId(departureId))
            } yield result match {
              case Left(BadRequest) => Redirect(controllers.routes.ErrorController.badRequest())
              case Left(_)          => Redirect(controllers.routes.ErrorController.technicalDifficulties())
              case Right(x)         => Redirect(navigator.nextPage(CancellationReasonPage, updatedAnswers, departureId))
            }
        )
  }
}
