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

import play.api.libs.json._
import play.api.mvc.{JavascriptLiteral, PathBindable}

final case class DepartureId(value: String)

object DepartureId {

  implicit val departureIdWrites: Writes[DepartureId] = (departureId: DepartureId) => JsString(departureId.value)

  implicit val departureIdReads: Reads[DepartureId] = {
    case JsString(value) => JsSuccess(DepartureId(value))
    case e               => JsError(s"Error in deserialization of Json value to an DepartureId, expected JsString got ${e.getClass}")
  }

  implicit lazy val pathBindable: PathBindable[DepartureId] = new PathBindable[DepartureId] {

    override def bind(key: String, value: String): Either[String, DepartureId] =
      implicitly[PathBindable[String]].bind(key, value).map(DepartureId(_))

    override def unbind(key: String, departureId: DepartureId): String = departureId.value
  }

  implicit val departureIdJSLBinder: JavascriptLiteral[DepartureId] = (value: DepartureId) => s"""'${value.toString}'"""

}
