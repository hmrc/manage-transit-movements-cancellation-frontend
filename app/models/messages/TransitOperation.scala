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

package models.messages

import play.api.libs.json.{Json, OFormat}

trait TransitOperation

case class TransitOperationIE015(LRN: String, MRN: Option[String]) extends TransitOperation {

  def toIE014TransitOperation: TransitOperationIE014 =
    if (MRN.isDefined) {
      TransitOperationIE014(None, MRN)
    } else {
      TransitOperationIE014(Some(LRN), None)
    }

}

object TransitOperationIE015 {
  implicit val formats: OFormat[TransitOperationIE015] = Json.format[TransitOperationIE015]
}

case class TransitOperationIE028(MRN: String) extends TransitOperation

object TransitOperationIE028 {
  implicit val formats: OFormat[TransitOperationIE028] = Json.format[TransitOperationIE028]
}

case class TransitOperationIE014(LRN: Option[String], MRN: Option[String]) extends TransitOperation

object TransitOperationIE014 {

  implicit val formats: OFormat[TransitOperationIE014] = Json.format[TransitOperationIE014]
}
