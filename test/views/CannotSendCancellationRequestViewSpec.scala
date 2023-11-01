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

package views

import models.CustomsOffice
import play.twirl.api.HtmlFormat
import viewModels.CannotSendCancellationRequestViewModel
import views.behaviours.ViewBehaviours
import views.html.CannotSendCancellationRequestView

class CannotSendCancellationRequestViewSpec extends ViewBehaviours {

  override def view: HtmlFormat.Appendable =
    injector
      .instanceOf[CannotSendCancellationRequestView]
      .apply(lrn, departureId, CannotSendCancellationRequestViewModel("GB000060", None))(fakeRequest, messages)

  override val prefix: String = "cannotSendCancellationRequest"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithCaption(s"This declaration is LRN: $lrn")

  behave like pageWithHeading()

  behave like pageWithContent("p", s"This may be because the goods have already been released or the office of departure has rejected the declaration.")

  behave like pageWithLink(
    id = "viewStatus",
    expectedText = "View the status of this declaration",
    expectedHref = frontendAppConfig.manageTransitMovementsViewDeparturesUrl
  )

  "Customs office with no customsOffice record returned" - {
    val customsOfficeId = "id"
    val view = injector
      .instanceOf[CannotSendCancellationRequestView]
      .apply(lrn, departureId, CannotSendCancellationRequestViewModel(customsOfficeId, None))(fakeRequest, messages)

    val doc = parseView(view)

    behave like pageWithContent(
      doc,
      "p",
      s"If you have any questions, contact Customs office $customsOfficeId."
    )

  }

  "Customs office with a name and no telephone" - {
    val view = injector
      .instanceOf[CannotSendCancellationRequestView]
      .apply(lrn, departureId, CannotSendCancellationRequestViewModel("GB000060", Some(CustomsOffice("id", "OfficeName", "countryId", None))))(fakeRequest,
                                                                                                                                               messages
      )

    val doc = parseView(view)

    behave like pageWithContent(
      doc,
      "p",
      s"If you have any questions, contact Customs at OfficeName."
    )

  }

  "Customs office with no name and a telephone" - {
    val view = injector
      .instanceOf[CannotSendCancellationRequestView]
      .apply(lrn, departureId, CannotSendCancellationRequestViewModel("GB000060", Some(CustomsOffice("id", "", "countryId", Some("12234")))))(fakeRequest,
                                                                                                                                              messages
      )

    val doc = parseView(view)

    behave like pageWithContent(
      doc,
      "p",
      s"If you have any questions, contact Customs office id on 12234."
    )

  }

  "Customs office with no name and no telephone" - {
    val view = injector
      .instanceOf[CannotSendCancellationRequestView]
      .apply(lrn, departureId, CannotSendCancellationRequestViewModel("GB000060", Some(CustomsOffice("id", "", "countryId", None))))(fakeRequest, messages)

    val doc = parseView(view)

    behave like pageWithContent(
      doc,
      "p",
      s"If you have any questions, contact Customs office GB000060."
    )

  }

}
