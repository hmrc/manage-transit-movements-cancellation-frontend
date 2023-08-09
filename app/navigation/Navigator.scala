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

package navigation

import com.google.inject.{Inject, Singleton}
import config.FrontendAppConfig
import controllers.routes
import models._
import pages._
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs.GET

@Singleton
class Navigator @Inject() (val appConfig: FrontendAppConfig) {

  protected def normalRoutes(departureId: String, lrn: LocalReferenceNumber): PartialFunction[Page, UserAnswers => Option[Call]] = {

    case ConfirmCancellationPage =>
      ua => confirmCancellationRoute(ua, departureId, lrn)
    case CancellationReasonPage =>
      _ => Some(routes.CancellationSubmissionConfirmationController.onPageLoad(lrn))
  }

  def confirmCancellationRoute(ua: UserAnswers, departureId: String, lrn: LocalReferenceNumber): Option[Call] =
    ua.get(ConfirmCancellationPage) map {
      case true =>
        routes.CancellationReasonController.onPageLoad(departureId, lrn)
      case false =>
        Call(GET, appConfig.manageTransitMovementsViewDeparturesUrl)
    }

  private def handleCall(userAnswers: UserAnswers, call: UserAnswers => Option[Call]) =
    call(userAnswers) match {
      case Some(onwardRoute) => onwardRoute
      case None              => routes.SessionExpiredController.onPageLoad()
    }

  def nextPage(page: Page, userAnswers: UserAnswers, departureId: String, lrn: LocalReferenceNumber): Call =
    normalRoutes(departureId, lrn).lift(page) match {
      case None       => routes.ConfirmCancellationController.onPageLoad(departureId, lrn)
      case Some(call) => handleCall(userAnswers, call)
    }
}
