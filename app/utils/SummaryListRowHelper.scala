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

package utils

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.html.components._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private[utils] class SummaryListRowHelper(implicit messages: Messages) {

  protected def formatAsText[T](value: T): Content = s"$value".toText

  protected def formatAsYesOrNo(value: Boolean): Content =
    if (value) {
      messages("site.yes").toText
    } else {
      messages("site.no").toText
    }

  protected def formatAsYesOrNo(value: Int): Content = formatAsYesOrNo(value == 1)

  protected def formatAsAcceptedOrRejected(value: Int): Content =
    if (value == 1) {
      messages("site.accepted").toText
    } else {
      messages("site.rejected").toText
    }

  protected def formatAsDate(value: LocalDate): Content = {
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    dateFormatter.format(value).toText
  }

  protected def buildRow(
    key: String,
    answer: Content
  ): SummaryListRow =
    SummaryListRow(
      key = Key(messages(key).toText),
      value = Value(answer)
    )

}
