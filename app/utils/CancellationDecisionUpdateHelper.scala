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

package utils

import models.messages.CancellationDecisionUpdate
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow

class CancellationDecisionUpdateHelper(message: CancellationDecisionUpdate)(implicit messages: Messages) extends SummaryListRowHelper {

  def mrn: Option[SummaryListRow] =
    Some(buildRow("cancellationDecisionUpdate.mrn", formatAsText(message.mrn)))

  def initiatedByCustoms: Option[SummaryListRow] =
    Some(buildRow("cancellationDecisionUpdate.initiatedByCustoms", formatAsYesOrNo(message.cancellationInitiatedBy)))

  def cancellationDecision: Option[SummaryListRow] =
    message.cancellationDecision map {
      value =>
        buildRow("cancellationDecisionUpdate.cancellationDecision", formatAsAcceptedOrRejected(value))
    }

  def cancellationDecisionDate: Option[SummaryListRow] =
    Some(buildRow("cancellationDecisionUpdate.cancellationDecisionDate", formatAsDate(message.cancellationDecisionDate)))

  def cancellationJustification: Option[SummaryListRow] =
    message.cancellationJustification.map {
      value =>
        buildRow("cancellationDecisionUpdate.cancellationJustification", formatAsText(value))
    }
}
