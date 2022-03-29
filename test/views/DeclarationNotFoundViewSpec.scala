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

package views

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.DeclarationNotFoundView

class DeclarationNotFoundViewSpec extends ViewBehaviours {

  val departuresUrl = "departuresUrl"
  val contactUrl    = "https://www.gov.uk/new-computerised-transit-system"

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[DeclarationNotFoundView].apply(departuresUrl)(fakeRequest, messages)

  override val prefix: String = "canNotCancel"

  behave like pageWithBackLink

  behave like pageWithHeading

  behave like pageWithPartialContent(
    "p",
    "There is no departure declaration with these details. If you still need to cancel this departure declaration, you must "
  )

  behave like pageWithLink(
    "contact",
    "contact the New Computerised Transit System (NCTS) Helpdesk (opens in a new tab)",
    contactUrl
  )

  behave like pageWithLink(
    "manage-transit-movements",
    "Back to departure declarations",
    departuresUrl
  )
}
