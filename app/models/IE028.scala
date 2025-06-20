/*
 * Copyright 2025 HM Revenue & Customs
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

import models.IE028.TransitOperation

import scala.language.implicitConversions
import scala.xml.Node

// Since we only need a couple of fields from the IE028,
// we have created a custom case class to only retrieve the fields we need
// rather than using scalaxb and storing the entire message in memory

case class IE028(
  transitOperation: TransitOperation
)

object IE028 {

  implicit def reads(node: Node): IE028 = {
    val transitOperation = TransitOperation.reads((node \ "TransitOperation").head)
    new IE028(transitOperation)
  }

  case class TransitOperation(mrn: String)

  object TransitOperation {

    def reads(node: Node): TransitOperation = {
      val mrn = (node \ "MRN").text
      new TransitOperation(mrn)
    }
  }
}
