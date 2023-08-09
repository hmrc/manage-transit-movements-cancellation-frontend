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
import forms.ConfirmCancellationFormProvider
import models.LocalReferenceNumber
import navigation.Navigator
import pages.ConfirmCancellationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ConfirmCancellationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmCancellationController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  formProvider: ConfirmCancellationFormProvider,
  navigator: Navigator,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmCancellationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider()

  def onPageLoad(departureId: String, lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(departureId) {
    implicit request =>
      val preparedForm = request.userAnswers.get(ConfirmCancellationPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, departureId, lrn))
  }

  def onSubmit(departureId: String, lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(departureId).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, departureId, lrn))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ConfirmCancellationPage, value))
            } yield Redirect(navigator.nextPage(ConfirmCancellationPage, updatedAnswers, departureId, lrn))
        )
  }
}
