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

import play.api.libs.json.{JsError, JsString, JsSuccess, Reads}

sealed trait DepartureStatus

object DepartureStatus extends Enumerable.Implicits {

  case object DepartureSubmitted extends DepartureStatus

  private case object OtherStatus extends DepartureStatus

  val values: DepartureStatus = DepartureSubmitted

  implicit val enumerable: Enumerable[DepartureStatus] =
    Enumerable(
      values.toString -> values
    )

  implicit val reads: Reads[DepartureStatus] =
    Reads {
      case JsString(str) =>
        enumerable
          .withName(str)
          .map {
            s =>
              JsSuccess(s)
          }
          .getOrElse(JsSuccess(OtherStatus))
      case _ =>
        JsError("error.invalid")
    }
}
