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

trait CharacterCountViewBehaviours extends QuestionViewBehaviours[String] {

  def pageWithCharacterCount(): Unit =
    "page with a character count question" - {

      "when rendered" - {

        "must contain a textarea" in {
          assert(getElementsByTag(doc, "textarea").size > 0)
        }

        "must bind a data module" in {
          val module = doc.getElementsByAttribute("data-module").first()
          module.`val`() mustBe "govuk-character-count"
        }

        "must not render an error summary" in {
          assertNotRenderedById(doc, "error-summary_header")
        }
      }
//
//      "when rendered with a value" - {
//        behave like answeredYesNoPage(answer = false)
//      }

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

}
