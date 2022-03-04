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

package models

import play.api.libs.json.{__, JsString, Reads, Writes}

case class EoriNumber(value: String)

object EoriNumber {
  implicit def reads: Reads[EoriNumber]   = __.read[String] map EoriNumber.apply
  implicit def writes: Writes[EoriNumber] = Writes(eori => JsString(eori.value))

  private val eoriPrefix = "GB"
  private val eoriRegex  = "[A-Z]{2}[^\n\r]{1,}"

  def prefixGBIfMissing(eoriNumber: String): String =
    if (!eoriNumber.matches(eoriRegex)) {
      s"$eoriPrefix$eoriNumber"
    } else eoriNumber
}
