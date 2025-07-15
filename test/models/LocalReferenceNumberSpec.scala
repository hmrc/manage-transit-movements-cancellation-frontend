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

import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.EitherValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import play.api.libs.json.{JsString, Json}
import play.api.mvc.PathBindable

class LocalReferenceNumberSpec extends AnyFreeSpec with Generators with Matchers with EitherValues {

  "LocalReferenceNumber" - {

    "must bind from url" in {
      val pathBindable = implicitly[PathBindable[LocalReferenceNumber]]
      val departureId  = LocalReferenceNumber("AB123")

      val bind: Either[String, LocalReferenceNumber] = pathBindable.bind("localReferenceNumber", "AB123")
      bind.value mustEqual departureId
    }

    "must not bind from url when LRN is too long" in {

      forAll(stringsLongerThan(LocalReferenceNumber.maxLength)) {
        string =>
          val pathBindable = implicitly[PathBindable[LocalReferenceNumber]]

          val bind: Either[String, LocalReferenceNumber] = pathBindable.bind("localReferenceNumber", string)
          bind.left.value mustEqual "Invalid Local Reference Number"
      }
    }

    "must not bind from url when LRN has invalid chars" in {

      forAll(Gen.oneOf(Seq("!", "\"", "£", "%", "^", "&", ">", "<", "*", "(", ")"))) {
        string =>
          val pathBindable = implicitly[PathBindable[LocalReferenceNumber]]

          val bind: Either[String, LocalReferenceNumber] = pathBindable.bind("localReferenceNumber", string)
          bind.left.value mustEqual "Invalid Local Reference Number"
      }
    }

    "unbind to path value" in {
      val pathBindable = implicitly[PathBindable[LocalReferenceNumber]]
      val departureId  = LocalReferenceNumber("AB123")

      val bindValue = pathBindable.unbind("localReferenceNumber", departureId)
      bindValue mustEqual "AB123"
    }

    "must serialise" in {
      forAll(arbitrary[LocalReferenceNumber]) {
        lrn =>
          Json.toJson(lrn) mustEqual JsString(lrn.toString)
      }
    }

    "must treat .apply and .toString as dual" in {

      forAll(arbitrary[LocalReferenceNumber]) {
        lrn =>
          new LocalReferenceNumber(lrn.toString).value mustEqual lrn.toString
      }
    }
  }
}
