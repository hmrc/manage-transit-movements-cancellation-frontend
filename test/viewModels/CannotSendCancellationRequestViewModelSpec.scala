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

package viewModels

import base.SpecBase
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import models.CustomsOffice

class CannotSendCancellationRequestViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "CannotSendCancellationRequestViewModel" - {

    "must render correct paragraph" - {

      "When Customs office name and telephone exists" in {

        val customsOffice     = CustomsOffice("ID001", "Dover", "GB", Some("00443243543"))
        val viewModelProvider = CannotSendCancellationRequestViewModel("ID001", Some(customsOffice))

        val result: String = viewModelProvider.customsOfficeMessage

        result mustBe "If you have any questions, contact Customs at Dover on 00443243543."
      }
      "When Customs Office name available and telephone does not exist" in {

        val customsOffice     = CustomsOffice("ID001", "Dover", "GB", Some(""))
        val viewModelProvider = CannotSendCancellationRequestViewModel("ID001", Some(customsOffice))

        val result: String = viewModelProvider.customsOfficeMessage

        result mustBe "If you have any questions, contact Customs at Dover."
      }
      "When Customs Office name not available and telephone exists" in {

        val customsOffice     = CustomsOffice("ID001", "", "GB", Some("00443243543"))
        val viewModelProvider = CannotSendCancellationRequestViewModel("ID001", Some(customsOffice))

        val result: String = viewModelProvider.customsOfficeMessage

        result mustBe "If you have any questions, contact Customs office ID001 on 00443243543."
      }
      "When Customs Office name available and telephone is None" in {

        val customsOffice     = CustomsOffice("ID001", "Dover", "GB", None)
        val viewModelProvider = CannotSendCancellationRequestViewModel("ID001", Some(customsOffice))

        val result: String = viewModelProvider.customsOfficeMessage

        result mustBe "If you have any questions, contact Customs at Dover."
      }
      "When Customs Office name not available and telephone does not exist" in {

        val customsOffice     = CustomsOffice("ID001", "", "GB", Some(""))
        val viewModelProvider = CannotSendCancellationRequestViewModel("ID001", Some(customsOffice))

        val result: String = viewModelProvider.customsOfficeMessage

        result mustBe "If you have any questions, contact Customs office ID001."
      }
      "When Customs Office name not available and telephone is None" in {

        val customsOffice     = CustomsOffice("ID001", "", "GB", None)
        val viewModelProvider = CannotSendCancellationRequestViewModel("ID001", Some(customsOffice))

        val result: String = viewModelProvider.customsOfficeMessage

        result mustBe "If you have any questions, contact Customs office ID001."
      }
      "When Customs Office not fetched from reference data service" in {

        val viewModelProvider = CannotSendCancellationRequestViewModel("ID001", None)

        val result: String = viewModelProvider.customsOfficeMessage

        result mustBe "If you have any questions, contact Customs office ID001."
      }

    }

  }
}
