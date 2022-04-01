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

trait YesNoViewBehaviours extends QuestionViewBehaviours[Boolean] {

  def yesNoPage(hintTextPrefix: Option[String] = None, args: Seq[String] = Nil): Unit =
    "page with a Yes/No question" - {
      "when rendered" - {

        "must contain a legend for the question" in {
          val legends = getElementsByTag(doc, "legend")
          legends.size mustBe 1
          assertElementIncludesText(legends.first(), messages(s"$prefix.heading", args: _*))

          hintTextPrefix.map {
            prefix =>
              val hint = getElementByClass(doc, "govuk-hint")
              assertElementIncludesText(hint, messages(s"$prefix.hint"))
          }
        }

        "must contain an input for the value" in {
          assertRenderedById(doc, "value-yes")
          assertRenderedById(doc, "value-no")
        }

        "must have no values checked when rendered with no form" in {
          assert(!doc.getElementById("value-yes").hasAttr("checked"))
          assert(!doc.getElementById("value-no").hasAttr("checked"))
        }

        "must not render an error summary" in {
          assertNotRenderedById(doc, "error-summary_header")
        }
      }

      "when rendered with a value of true" - {
        behave like answeredYesNoPage(answer = true)
      }

      "when rendered with a value of false" - {
        behave like answeredYesNoPage(answer = false)
      }

      "when rendered with an error" - {

        "must show an error summary" in {
          assertRenderedById(docWithError, "error-summary-title")
        }

        "must show an error in the value field's label" in {
          val errorSpan = docWithError.getElementsByClass("govuk-error-message").first
          assertElementContainsText(errorSpan, s"${messages("error.title.prefix")} ${messages(errorMessage)}")
        }
      }
    }

  private def answeredYesNoPage(answer: Boolean): Unit = {

    val doc = parseView(applyView(form.fill(answer)))

    "must have only the correct value checked" in {
      assert(doc.getElementById("value-yes").hasAttr("checked") == answer)
      assert(doc.getElementById("value-no").hasAttr("checked") != answer)
    }

    "must not render an error summary" in {
      assertNotRenderedById(doc, "error-summary_header")
    }
  }
}
