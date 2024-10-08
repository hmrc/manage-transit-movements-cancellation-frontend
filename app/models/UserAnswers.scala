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
import queries.{Gettable, Settable}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import scala.util.{Failure, Success, Try}

final case class UserAnswers(
  id: String,
  eoriNumber: EoriNumber,
  lrn: LocalReferenceNumber,
  data: JsObject,
  lastUpdated: Instant
) {

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).getOrElse(None)

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Try[UserAnswers] = {

    val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy(data = d)
        page.cleanup(Some(value), updatedAnswers)
    }
  }

  def remove[A](page: Settable[A]): Try[UserAnswers] = {

    val updatedData = data.removeObject(page.path) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_) =>
        Success(data)
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy(data = d)
        page.cleanup(None, updatedAnswers)
    }
  }
}

object UserAnswers {

  import play.api.libs.functional.syntax._

  implicit def reads(implicit sensitiveFormats: SensitiveFormats): Reads[UserAnswers] = (
    (__ \ "_id").read[String] and
      (__ \ "eoriNumber").read[EoriNumber] and
      (__ \ "lrn").read[LocalReferenceNumber] and
      (__ \ "data").read[JsObject](sensitiveFormats.jsObjectReads) and
      (__ \ "lastUpdated").read(MongoJavatimeFormats.instantReads)
  )(UserAnswers.apply)

  implicit def writes(implicit sensitiveFormats: SensitiveFormats): OWrites[UserAnswers] =
    writes(sensitiveFormats.jsObjectWrites)

  val auditWrites: OWrites[UserAnswers] =
    writes(SensitiveFormats.nonSensitiveJsObjectWrites)

  private def writes(jsObjectWrites: Writes[JsObject]): OWrites[UserAnswers] = (
    (__ \ "_id").write[String] and
      (__ \ "eoriNumber").write[EoriNumber] and
      (__ \ "lrn").write[LocalReferenceNumber] and
      (__ \ "data").write[JsObject](jsObjectWrites) and
      (__ \ "lastUpdated").write(MongoJavatimeFormats.instantWrites)
  )(
    ua => Tuple.fromProductTyped(ua)
  )

  implicit def format(implicit sensitiveFormats: SensitiveFormats): Format[UserAnswers] =
    Format(reads, writes)
}
