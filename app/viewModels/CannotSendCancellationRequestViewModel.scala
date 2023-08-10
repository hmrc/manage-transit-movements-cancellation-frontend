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

import models.CustomsOffice
import play.api.i18n.Messages

case class CannotSendCancellationRequestViewModel(customsOfficeId: String, customsOffice: Option[CustomsOffice]) {

  // scalastyle:off cyclomatic.complexity
  def customsOfficeMessage(implicit messages: Messages): String = customsOffice match {
    case Some(CustomsOffice(_, name, _, Some(phone))) if name.nonEmpty && phone.nonEmpty =>
      messages("cannotSendCancellationRequest.nameExistsAndPhoneExist", name, phone)
    case Some(CustomsOffice(_, name, _, Some(phone))) if name.nonEmpty && phone.isEmpty =>
      messages("cannotSendCancellationRequest.NameExists", name)
    case Some(CustomsOffice(_, name, _, None)) if name.nonEmpty =>
      messages("cannotSendCancellationRequest.NameExists", name)
    case Some(CustomsOffice(id, "", _, Some(phone))) if phone.nonEmpty =>
      messages("cannotSendCancellationRequest.PhoneExists", id, phone)
    case _ => messages("cannotSendCancellationRequest.NoneExist", customsOfficeId)
  }
  // scalastyle:on cyclomatic.complexity

}
