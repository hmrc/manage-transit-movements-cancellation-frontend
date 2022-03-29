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

import scala.collection.JavaConverters._

trait ViewBehaviours extends SpecBase with ViewSpecAssertions {

  def view: HtmlFormat.Appendable

  def parseView(view: HtmlFormat.Appendable): Document = Jsoup.parse(view.toString())

  lazy val doc: Document = parseView(view)

  val prefix: String

  val hasSignOutLink: Boolean = true

  if (hasSignOutLink) {
    "must render sign out link in header" in {
      val link = getElementByClass(doc, "hmrc-sign-out-nav__link")
      assertElementContainsText(link, "Sign out")
      assertElementContainsHref(link,
                                "http://localhost:9553/bas-gateway/sign-out-without-state?continue=http://localhost:9514/feedback/manage-transit-departures"
      )
    }
  } else {
    "must not render sign out link in header" in {
      assertElementDoesNotExist(doc, "hmrc-sign-out-nav__link")
    }
  }

  "must render service name link in header" in {
    val link = getElementByClass(doc, "hmrc-header__service-name--linked")
    assertElementContainsText(link, "Manage your transit movements")
    assertElementContainsHref(link, "http://localhost:10122/manage-transit-movements/cancellation")
  }

  "must append service to feedback link" in {
    val link = getElementBySelector(doc, ".govuk-phase-banner__text > .govuk-link")
    getElementHref(link) must endWith("?service=CTCTraders")
  }

  "must render title" in {
    val title = doc.title()
    title mustBe s"${messages(s"$prefix.title")} - Manage your transit movements - GOV.UK"
  }

  "must render accessibility statement link" in {
    val link = doc
      .select(".govuk-footer__inline-list-item > .govuk-footer__link")
      .asScala
      .find(_.text() == "Accessibility statement")
      .get

    getElementHref(link) must include("http://localhost:12346/accessibility-statement/manage-transit-movements?referrerUrl=")
  }

  "must not render language toggle" in {
    assertElementDoesNotExist(doc, "hmrc-language-select")
  }

  def pageWithHeading(): Unit =
    "must render heading" in {
      val heading = getElementByTag(doc, "h1")
      assertElementIncludesText(heading, messages(s"$prefix.heading"))
    }

  def pageWithCaption(expectedText: String): Unit =
    "must render caption" in {
      val caption = getElementByClass(doc, "govuk-caption-xl")
      assertElementContainsText(caption, expectedText)
    }

  def pageWithHint(expectedText: String): Unit =
    "must render hint" in {
      val hint = getElementByClass(doc, "govuk-hint")
      assertElementContainsText(hint, expectedText)
    }

  def pageWithContinueButton(): Unit =
    "must render continue button" in {
      val button = getElementByClass(doc, "govuk-button")
      assertElementContainsText(button, "Continue")
      assertElementContainsId(button, "submit")
    }

  def pageWithSubmitButton(expectedHref: String): Unit =
    "must render a submit button" in {
      val button = getElementByClass(doc, "govuk-button")
      assertElementContainsText(button, "Submit")
      assertElementContainsId(button, "submit")
    }

  def pageWithButton(expectedText: String, expectedHref: String): Unit =
    s"must render $expectedText button" in {
      val button = getElementByClass(doc, "govuk-button")
      assertElementContainsText(button, expectedText)
      assertElementContainsHref(button, expectedHref)
    }

  def pageWithLink(id: String, expectedText: String, expectedHref: String): Unit =
    s"must render link with id $id" in {
      val link = getElementById(doc, id)
      assertElementContainsText(link, expectedText)
      assertElementContainsHref(link, expectedHref)
    }

  def pageWithBackLink(): Unit =
    "must render back link" in {
      val link = getElementByClass(doc, "govuk-back-link")
      assertElementContainsText(link, "Back")
      assertElementContainsHref(link, "javascript:history.back()")
    }

  def pageWithoutBackLink(): Unit =
    "must not render back link" in {
      assertElementDoesNotExist(doc, "govuk-back-link")
    }

  def pageWithContent(tag: String, expectedText: String): Unit =
    s"must render $tag with text $expectedText" in {
      val elements = getElementsByTag(doc, tag)
      assertElementExists(elements, _.text() == expectedText)
    }

}
