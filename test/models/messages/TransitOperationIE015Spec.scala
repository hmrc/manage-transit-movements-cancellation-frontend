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

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class TransitOperationIE015Spec extends AnyFreeSpec with Matchers with OptionValues {

  "TransitOperationIE015" - {

    "toIE014TransitOperation" - {

      "must return transit operation IE014 with MRN" in {

        val transitOp      = TransitOperationIE015("LRN123", Some("MRN123"))
        val expectedResult = TransitOperationIE014(None, Some("MRN123"))

        transitOp.toIE014TransitOperation mustBe expectedResult
      }

      "must return transit operation IE014 without MRN" in {

        val transitOp      = TransitOperationIE015("LRN123", None)
        val expectedResult = TransitOperationIE014(Some("LRN123"), None)

        transitOp.toIE014TransitOperation mustBe expectedResult
      }
    }

  }

}
