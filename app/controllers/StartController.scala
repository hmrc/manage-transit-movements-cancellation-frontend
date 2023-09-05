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
import models.{LocalReferenceNumber, UserAnswers}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.TimeMachine

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class StartController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  sessionRepository: SessionRepository,
  val controllerComponents: MessagesControllerComponents,
  timeMachine: TimeMachine
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def start(departureId: String, lrn: LocalReferenceNumber): Action[AnyContent] = actions.getData(departureId).async {
    implicit request =>
      sessionRepository.set(
        request.userAnswers.getOrElse(
          UserAnswers(departureId, request.eoriNumber, lrn, Json.obj(), timeMachine.now())
        )
      ) map (
        _ => Redirect(routes.ConfirmCancellationController.onPageLoad(departureId, lrn))
      )
  }
}
