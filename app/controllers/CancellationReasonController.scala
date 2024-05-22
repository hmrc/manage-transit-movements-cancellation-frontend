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
import forms.CancellationReasonFormProvider
import logging.Logging
import models.AuditType.DeclarationInvalidationRequest
import models.Constants.commentMaxLength
import models.{DepartureId, LocalReferenceNumber}
import pages.CancellationReasonPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.DepartureMessageService
import services.submission.{AuditService, SubmissionService}
import uk.gov.hmrc.http.HttpReads.is2xx
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CancellationReasonView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CancellationReasonController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  formProvider: CancellationReasonFormProvider,
  departureMessageService: DepartureMessageService,
  val controllerComponents: MessagesControllerComponents,
  view: CancellationReasonView,
  auditService: AuditService,
  submissionService: SubmissionService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

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
                userAnswers <- OptionT.fromOption[Future](request.userAnswers.set(CancellationReasonPage, value).toOption)
                ie015       <- OptionT(departureMessageService.getIE015(departureId))
                ie028       <- OptionT.liftF(departureMessageService.getIE028(departureId))
                mrn = ie028.map(_.TransitOperation.MRN)
                response <- OptionT.liftF(submissionService.submit(userAnswers.eoriNumber, ie015, mrn, value, DepartureId(departureId)))
              } yield response.status match {
                case x if is2xx(x) =>
                  auditService.audit(DeclarationInvalidationRequest, userAnswers)
                  Redirect(controllers.routes.CancellationSubmissionConfirmationController.onPageLoad(departureId, lrn))
                case x =>
                  logger.error(s"Error submitting IE014: $x")
                  Redirect(routes.ErrorController.technicalDifficulties())
              }
            ).getOrElse(
              Redirect(controllers.routes.ErrorController.technicalDifficulties())
            )
        )
  }
}
