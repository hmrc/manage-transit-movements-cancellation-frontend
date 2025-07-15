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

package views.behaviours

import org.jsoup.nodes.Document
import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat

trait QuestionViewBehaviours[T] extends ViewBehaviours with ErrorSummaryViewBehaviours[T] {

  def form: Form[T]

  def applyView(form: Form[T]): HtmlFormat.Appendable

  override def view: HtmlFormat.Appendable = applyView(form)

  val errorKey: String     = "value"
  val errorMessage: String = "error"

  private lazy val formWithError: Form[T]               = form.withError(FormError(errorKey, errorMessage))
  private lazy val viewWithError: HtmlFormat.Appendable = applyView(formWithError)
  lazy val docWithError: Document                       = parseView(viewWithError)

  "when there are form errors" - {
    "must render error prefix in title" in {
      val title = docWithError.title()
      title mustEqual s"Error: ${messages(s"$prefix.title")} - Departure declarations - Manage your transit movements - GOV.UK"
    }
  }
}
