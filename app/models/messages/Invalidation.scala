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

import play.api.libs.json.{Json, OWrites}
import utils.Format.dateTimeFormatIE014

import java.time.LocalDateTime

case class Invalidation(requestDateAndTime: LocalDateTime = LocalDateTime.now(), initiatedByCustoms: String = "0", justification: String)

object Invalidation {

  implicit val writes: OWrites[Invalidation] = OWrites {
    invalidation =>
      Json.obj(
        "requestDateAndTime" -> invalidation.requestDateAndTime.format(dateTimeFormatIE014),
        "initiatedByCustoms" -> invalidation.initiatedByCustoms,
        "justification"      -> invalidation.justification
      )
  }

}
