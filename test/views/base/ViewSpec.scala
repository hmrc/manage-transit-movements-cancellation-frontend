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

package views.base

import base.SpecBase
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat

trait ViewSpec extends SpecBase with ViewSpecAssertions {

  def view: HtmlFormat.Appendable

  def parseView(view: HtmlFormat.Appendable): Document = Jsoup.parse(view.toString())

  lazy val doc: Document = parseView(view)

  val prefix: String

  val hasSignOutLink: Boolean = true

  "must render heading" in {
    assertPageContainsHeading(doc, messages(s"$prefix.heading"))
  }

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
}
