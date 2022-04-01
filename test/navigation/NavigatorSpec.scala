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

import base.SpecBase
import controllers.routes
import generators.Generators
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs.GET

class NavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val navigator: Navigator = new Navigator(frontendAppConfig)

  "Navigator" - {
    "must go from a page that doesn't exist in the route map to Index" in {

      case object UnknownPage extends Page

      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(UnknownPage, answers, departureId)
            .mustBe(routes.ConfirmCancellationController.onPageLoad(departureId))
      }
    }

    "Must go from ConfirmCancellationPage to CancellationReason page when user selects yes" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .setValue(ConfirmCancellationPage(departureId), true)
          navigator
            .nextPage(ConfirmCancellationPage(departureId), updatedAnswers, departureId)
            .mustBe(routes.CancellationReasonController.onPageLoad(departureId))
      }
    }

    "Must go from ConfirmCancellationPage to Declaration view when user selects no" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .setValue(ConfirmCancellationPage(departureId), false)
          navigator
            .nextPage(ConfirmCancellationPage(departureId), updatedAnswers, departureId)
            .mustBe(Call(GET, frontendAppConfig.manageTransitMovementsViewDeparturesUrl))
      }
    }

    "Must go from CancellationReason page to Confirmation page " in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .setValue(CancellationReasonPage(departureId), "Test reason")
          navigator
            .nextPage(CancellationReasonPage(departureId), updatedAnswers, departureId)
            .mustBe(routes.CancellationSubmissionConfirmationController.onPageLoad(departureId))
      }
    }
  }
}
