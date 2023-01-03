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

import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import views.behaviours.SummaryListViewBehaviours
import views.html.CancellationDecisionUpdateView

class CancellationDecisionUpdateViewSpec extends SummaryListViewBehaviours with Generators {

  private val rows = listWithMaxLength[SummaryListRow]().sample.value

  override def summaryLists: Seq[SummaryList] = Seq(SummaryList(rows))

  private val rejected: Boolean = arbitrary[Boolean].sample.value

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[CancellationDecisionUpdateView].apply(prefix, rows, rejected)(fakeRequest, messages)

  override val prefix: String = Gen
    .oneOf(
      "cancellationDecision.cancellationRejected",
      "cancellationDecision.declarationCancelled"
    )
    .sample
    .value

  behave like pageWithTitle()

  behave like pageWithoutBackLink()

  behave like pageWithHeading()

  behave like pageWithSummaryLists()

  "when cancellation rejected" - {
    val view = injector.instanceOf[CancellationDecisionUpdateView].apply(prefix, rows, rejected = true)(fakeRequest, messages)
    val doc  = parseView(view)

    behave like pageWithPartialContent(doc, "p", "You must")
    behave like pageWithLink(
      doc,
      "contact",
      "contact the New Computerised Transit System helpdesk to get this fixed (opens in a new tab)",
      frontendAppConfig.nctsEnquiriesUrl
    )
  }
}
