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

import forms.ConfirmCancellationFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.ConfirmCancellation

class ConfirmCancellationSpec extends YesNoViewBehaviours {

  override def form: Form[Boolean] = new ConfirmCancellationFormProvider()()

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[ConfirmCancellation].apply(form, departureId, lrn)(fakeRequest, messages)

  override val prefix: String = "confirmCancellation"

  behave like pageWithHeading

  behave like pageWithTitle

  behave like yesNoPage()

  behave like pageWithContinueButton()

  behave like pageWithBackLink()
}
