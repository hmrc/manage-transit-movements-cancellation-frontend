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

package viewModels

import models.messages.CancellationDecisionUpdate
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.CancellationDecisionUpdateHelper

import javax.inject.Inject

case class CancellationDecisionUpdateViewModel(
  rows: Seq[SummaryListRow]
)

object CancellationDecisionUpdateViewModel {

  def apply(message: CancellationDecisionUpdate)(implicit messages: Messages): CancellationDecisionUpdateViewModel = {

    val rows = {
      val helper = new CancellationDecisionUpdateHelper(message)

      Seq(
        helper.mrn,
        helper.initiatedByCustoms,
        helper.cancellationDecision,
        helper.cancellationDecisionDate,
        helper.cancellationJustification
      ).flatten
    }

    new CancellationDecisionUpdateViewModel(rows)
  }

  class CancellationDecisionUpdateViewModelProvider @Inject() () {

    def apply(message: CancellationDecisionUpdate)(implicit messages: Messages): CancellationDecisionUpdateViewModel =
      CancellationDecisionUpdateViewModel(message)
  }
}
