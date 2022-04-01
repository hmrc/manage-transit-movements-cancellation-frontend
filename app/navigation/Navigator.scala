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

  protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case ConfirmCancellationPage(departureId) =>
      ua => confirmCancellationRoute(ua, departureId)
    case CancellationReasonPage(departureId) =>
      _ => Some(routes.CancellationSubmissionConfirmationController.onPageLoad(departureId))
  }

  def confirmCancellationRoute(ua: UserAnswers, departureId: DepartureId): Option[Call] =
    ua.get(ConfirmCancellationPage(departureId)) map {
      case true  => routes.CancellationReasonController.onPageLoad(departureId)
      case false => Call(GET, appConfig.manageTransitMovementsViewDeparturesUrl)
    }

  private def handleCall(userAnswers: UserAnswers, call: UserAnswers => Option[Call]) =
    call(userAnswers) match {
      case Some(onwardRoute) => onwardRoute
      case None              => routes.SessionExpiredController.onPageLoad()
    }

  def nextPage(page: Page, userAnswers: UserAnswers, departureId: DepartureId): Call =
    normalRoutes.lift(page) match {
      case None       => routes.ConfirmCancellationController.onPageLoad(departureId)
      case Some(call) => handleCall(userAnswers, call)
    }
}
