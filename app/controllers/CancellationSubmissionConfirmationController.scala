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
import controllers.actions._
import models.LocalReferenceNumber
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.DepartureMessageService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CancellationSubmissionConfirmationView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CancellationSubmissionConfirmationController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  sessionRepository: SessionRepository,
  val controllerComponents: MessagesControllerComponents,
  view: CancellationSubmissionConfirmationView,
  messageService: DepartureMessageService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(departureId: String, lrn: LocalReferenceNumber): Action[AnyContent] = identify.async {
    implicit request =>
      (
        for {
          _ <- OptionT(messageService.getIE014(departureId))
          _ <- OptionT.liftF(sessionRepository.remove(departureId, request.eoriNumber))
        } yield Ok(view(lrn))
      ).getOrElse {
        logger.warn(s"No IE014 message found for departure ID $departureId")
        Redirect(controllers.routes.ErrorController.technicalDifficulties())
      }
  }
}
