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

import org.scalacheck.Gen
import play.twirl.api.HtmlFormat
import viewModels.CannotSendCancellationRequestViewModel
import views.behaviours.ViewBehaviours
import views.html.CannotSendCancellationRequestView

class CannotSendCancellationRequestViewSpec extends ViewBehaviours {

  private val paragraph = Gen.alphaNumStr.sample.value

  override def view: HtmlFormat.Appendable =
    injector
      .instanceOf[CannotSendCancellationRequestView]
      .apply(lrn, departureId, CannotSendCancellationRequestViewModel(paragraph))(fakeRequest, messages)

  override val prefix: String = "cannotSendCancellationRequest"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithCaption(s"This declaration is LRN: $lrn")

  behave like pageWithHeading()

  behave like pageWithContent("p", "This may be because the goods have already been released or the office of departure has rejected the declaration.")

  behave like pageWithLink(
    id = "viewStatus",
    expectedText = "View the status of this declaration",
    expectedHref = "http://localhost:9485/manage-transit-movements/view-departure-declarations"
  )

  behave like pageWithContent("p", paragraph)
}
