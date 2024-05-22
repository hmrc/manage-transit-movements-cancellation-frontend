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

package models

import play.api.libs.json.{__, Reads}

sealed trait MessageType

object MessageType {

  case object DeclarationInvalidationRequest extends MessageType
  case object DepartureNotification extends MessageType
  case object AllocatedMRN extends MessageType
  case object GuaranteeRejected extends MessageType
  case object GoodsUnderControl extends MessageType
  case object DeclarationSent extends MessageType
  case object AmendmentAcceptance extends MessageType
  case class Other(status: String) extends MessageType

  implicit val reads: Reads[MessageType] =
    __.read[String].map {
      case "IE014"                     => DeclarationInvalidationRequest
      case "IE015"                     => DepartureNotification
      case "IE028"                     => AllocatedMRN
      case "IE055" | "IE060" | "IE928" => GuaranteeRejected
      case "IE004"                     => AmendmentAcceptance
      case x                           => Other(x)
    }
}
