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

import controllers.actions._
import forms.ConfirmCancellationFormProvider
import models.{DepartureId, Mode, UserAnswers}
import navigation.Navigator
import pages.ConfirmCancellationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ConfirmCancellation

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmCancellationController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  sessionRepository: SessionRepository,
  formProvider: ConfirmCancellationFormProvider,
  checkCancellationStatus: CheckCancellationStatusProvider,
  navigator: Navigator,
  getData: DataRetrievalActionProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmCancellation
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider()

  def onPageLoad(departureId: DepartureId): Action[AnyContent] =
    (identify andThen checkCancellationStatus(departureId) andThen getData(departureId)).async {
      implicit request =>
        Future.successful(Ok(view(form, departureId, request.lrn)))
    }

  def onSubmit(departureId: DepartureId, mode: Mode): Action[AnyContent] =
    (identify andThen checkCancellationStatus(departureId) andThen getData(departureId)).async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, departureId, request.lrn))),
            value => {
              val userAnswers = request.userAnswers match {
                case Some(value) => value
                case None        => UserAnswers(departureId, request.eoriNumber)
              }
              for {
                updatedAnswers <- Future.fromTry(userAnswers.set(ConfirmCancellationPage(departureId), value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(ConfirmCancellationPage(departureId), mode, updatedAnswers, departureId))
            }
          )
    }
}
