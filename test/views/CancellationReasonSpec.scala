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

import forms.CancellationReasonFormProvider
import models.Constants.commentMaxLength
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.base.FormViewSpec
import views.html.CancellationReason

class CancellationReasonSpec extends FormViewSpec[String] {

  override def form: Form[String] = new CancellationReasonFormProvider()()

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[CancellationReason].apply(form, departureId, lrn, commentMaxLength)(fakeRequest, messages)

  override val prefix: String = "cancellationReason"

  behave like pageWithHeading

  behave like pageWithTitle

  "must render caption" in {
    assertPageContainsCaption(doc, s"The local reference number is $lrn")
  }

  "must render hint" in {
    assertPageContainsHint(doc, s"You can enter up to $commentMaxLength characters")
  }

  "must render continue button" in {
    assertPageContainsSubmitButton(doc, "Continue")
  }
}
