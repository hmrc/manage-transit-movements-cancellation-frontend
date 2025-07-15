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

import base.SpecBase
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.EitherValues
import play.api.libs.json.{JsError, JsNumber, Json}
import play.api.mvc.PathBindable

class DepartureIdSpec extends SpecBase with EitherValues {

  "Departure Id" - {
    "must bind from url" in {
      val pathBindable = implicitly[PathBindable[DepartureId]]
      val departureId  = DepartureId("12")

      val bind: Either[String, DepartureId] = pathBindable.bind("departureId", "12")
      bind.value mustEqual departureId
    }

    "unbind to path value" in {
      val pathBindable = implicitly[PathBindable[DepartureId]]
      val departureId  = DepartureId("12")

      val bindValue = pathBindable.unbind("departureId", departureId)
      bindValue mustEqual "12"
    }

    "must serialize and deserialize" in {
      val departureId = DepartureId("1")
      Json.toJson(departureId).validate[DepartureId].asOpt.value mustEqual departureId
    }

    "must return an error when not a JsString" in {
      val json = JsNumber(arbitrary[BigDecimal].sample.value)
      json.validate[DepartureId] mustBe a[JsError]
    }
  }
}
