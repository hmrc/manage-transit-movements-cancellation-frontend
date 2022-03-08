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

package models.response

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}
import utils.NodeSeqFormat

import scala.xml.NodeSeq

case class MRNAllocatedMessage(
  rootLevel: MRNAllocatedRootLevel,
  movementReferenceNumber: String,
  principalTraderDetails: PrincipalTraderDetails,
  customsOfficeReference: String
)

object MRNAllocatedMessage extends NodeSeqFormat {

  implicit val xmlReader: XmlReader[MRNAllocatedMessage] = (
    __.read[MRNAllocatedRootLevel],
    (__ \ "HEAHEA" \ "DocNumHEA5").read[String],
    (__ \ "TRAPRIPC1").read[PrincipalTraderDetails],
    (__ \ "CUSOFFDEPEPT" \ "RefNumEPT1").read[String]
  ).mapN(apply)

  implicit val reads: Reads[MRNAllocatedMessage] = (json: JsValue) =>
    for {
      mrnMessage <- (json \ "message")
        .validate[NodeSeq]
        .flatMap(
          one =>
            XmlReader.of[MRNAllocatedMessage].read(one).toOption match {
              case Some(value) => JsSuccess(value)
              case None =>
                JsError("MRNAllocatedMessage could not be parsed from the xml")
            }
        )
    } yield mrnMessage
}
