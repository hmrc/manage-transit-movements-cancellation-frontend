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

package views.behaviours

import base.SpecBase
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import views.base.ViewSpecAssertions

trait ViewBehaviours extends ViewSpec {

  def pageWithTitle(): Unit =
    "must render title" in {
      val title = doc.title()
      title mustBe s"${messages(s"$prefix.title")} - Manage your transit movements - GOV.UK"
    }

  def pageWithHeading(): Unit =
    "must render heading" in {
      assertPageContainsHeading(doc, messages(s"$prefix.heading"))
    }

  def pageWithCaption(expectedText: String): Unit =
    "must render caption" in {
      assertPageContainsCaption(doc, expectedText)
    }

  def pageWithHint(expectedText: String): Unit =
    "must render hint" in {
      assertPageContainsHint(doc, expectedText)
    }

  def pageWithContinueButton(): Unit =
    "must render continue button" in {
      assertPageContainsSubmitButton(doc, "Continue")
    }

  def pageWithLink(id: String, expectedText: String, expectedHref: String): Unit =
    s"must render link with id $id" in {
      assertPageContainsLink(doc, id, expectedText, expectedHref)
    }

  def pageWithBackLink(): Unit =
    "must render back link" in {
      assertPageContainsBackLink(doc)
    }

  def pageWithoutBackLink(): Unit =
    "must not render back link" in {
      assertPageDoesNotContainBackLink(doc)
    }

  def pageWithContent(tag: String, expectedText: String): Unit =
    s"must render $tag with text $expectedText" in {
      assertPageContainsTagWithExpectedText(doc, tag, expectedText)
    }

}

private[behaviours] trait ViewSpec extends SpecBase with ViewSpecAssertions {

  def view: HtmlFormat.Appendable

  def parseView(view: HtmlFormat.Appendable): Document = Jsoup.parse(view.toString())

  lazy val doc: Document = parseView(view)

  val prefix: String

  val hasSignOutLink: Boolean = true

  if (hasSignOutLink) {
    "must render sign out link in header" in {
      assertPageHasSignOutLink(
        doc = doc,
        expectedText = "Sign out",
        expectedHref = "http://localhost:9553/bas-gateway/sign-out-without-state?continue=http://localhost:9514/feedback/manage-transit-departures"
      )
    }
  } else {
    "must not render sign out link in header" in {
      assertPageHasNoSignOutLink(doc)
    }
  }

  "must render service name link in header" in {
    val link = doc.getElementsByClass("hmrc-header__service-name--linked")
    link.text() mustBe "Manage your transit movements"
    link.attr("href") mustBe "http://localhost:10122/manage-transit-movements/cancellation"
  }

  "must append service to feedback link" in {
    val link = doc.getElementsByClass("govuk-phase-banner__text").first().getElementsByClass("govuk-link").first()
    link.attr("href") must include("?service=CTCTraders")
  }

  // TODO - test that accessibility link href is correct
  // TODO - rename views to add View
}
