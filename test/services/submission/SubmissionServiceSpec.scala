/*
 * Copyright 2024 HM Revenue & Customs
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

package services.submission

import base.{MockApplicationBuilder, SpecBase}
import generated._
import generators.Generators
import org.mockito.Mockito.{reset, when}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import scalaxb.XMLCalendar
import services.DateTimeService

import java.time.LocalDateTime

class SubmissionServiceSpec extends SpecBase with MockApplicationBuilder with ScalaCheckPropertyChecks with Generators {

  private val service = app.injector.instanceOf[SubmissionService]

  private lazy val mockDateTimeService              = mock[DateTimeService]
  private lazy val mockMessageIdentificationService = mock[MessageIdentificationService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(
        bind[DateTimeService].toInstance(mockDateTimeService),
        bind[MessageIdentificationService].toInstance(mockMessageIdentificationService)
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDateTimeService)
    reset(mockMessageIdentificationService)

    when(mockDateTimeService.currentDateTime)
      .thenReturn(LocalDateTime.of(2020, 1, 1, 9, 30, 0))

    when(mockMessageIdentificationService.randomIdentifier)
      .thenReturn("foo")
  }

  "attributes" - {
    "must assign phase ID" in {
      val result = service.attributes
      result.keys.size mustBe 1
      result.get("@PhaseID").value.value.toString mustBe "NCTS5.1"
    }
  }

  "messageSequence" - {
    "must create message sequence" - {
      "when GB office of destination" in {
        val result = service.messageSequence(eoriNumber, "GB00001")

        result mustBe MESSAGESequence(
          messageSender = eoriNumber.value,
          messageRecipient = "NTA.GB",
          preparationDateAndTime = XMLCalendar("2020-01-01T09:30:00"),
          messageIdentification = "foo",
          messageType = CC014C,
          correlationIdentifier = None
        )
      }

      "when XI office of destination" in {
        val result = service.messageSequence(eoriNumber, "XI00001")

        result mustBe MESSAGESequence(
          messageSender = eoriNumber.value,
          messageRecipient = "NTA.XI",
          preparationDateAndTime = XMLCalendar("2020-01-01T09:30:00"),
          messageIdentification = "foo",
          messageType = CC014C,
          correlationIdentifier = None
        )
      }
    }
  }

  "transitOperation" - {
    "must create transit operation" - {
      val lrn = "LRN123"

      "when mrn defined" in {
        val mrn = "MRN123"

        val result = service.transitOperation(lrn, Some(mrn))

        result mustBe TransitOperationType05(
          LRN = None,
          MRN = Some(mrn)
        )
      }

      "when mrn undefined" in {
        val result = service.transitOperation(lrn, None)

        result mustBe TransitOperationType05(
          LRN = Some(lrn),
          MRN = None
        )
      }
    }
  }

  "invalidation" - {
    "must create invalidation" in {
      val justification = "There was a problem"

      val result = service.invalidation(justification)

      result mustBe InvalidationType02(
        requestDateAndTime = Some(XMLCalendar("2020-01-01T09:30:00")),
        decisionDateAndTime = None,
        decision = None,
        initiatedByCustoms = Number0,
        justification = Some(justification)
      )
    }
  }

  "holderOfTransit" - {
    "must create holder of transit" - {
      "when address and contact person defined" in {
        val ie015 = HolderOfTheTransitProcedureType14(
          identificationNumber = Some("in"),
          TIRHolderIdentificationNumber = Some("tirhin"),
          name = Some("hotn"),
          Address = Some(
            AddressType17(
              streetAndNumber = "san",
              postcode = Some("pc"),
              city = "c",
              country = "GB"
            )
          ),
          ContactPerson = Some(
            ContactPersonType05(
              name = "cpn",
              phoneNumber = "cppn",
              eMailAddress = Some("cpea")
            )
          )
        )

        val result = service.holderOfTransit(ie015)

        result mustBe HolderOfTheTransitProcedureType02(
          identificationNumber = Some("in"),
          TIRHolderIdentificationNumber = Some("tirhin"),
          name = Some("hotn"),
          Address = Some(
            AddressType15(
              streetAndNumber = "san",
              postcode = Some("pc"),
              city = "c",
              country = "GB"
            )
          ),
          ContactPerson = Some(
            ContactPersonType04(
              name = "cpn",
              phoneNumber = "cppn",
              eMailAddress = Some("cpea")
            )
          )
        )
      }

      "when address and contact person undefined" in {
        val ie015 = HolderOfTheTransitProcedureType14(
          identificationNumber = Some("in"),
          TIRHolderIdentificationNumber = Some("tirhin"),
          name = Some("hotn"),
          Address = None,
          ContactPerson = None
        )

        val result = service.holderOfTransit(ie015)

        result mustBe HolderOfTheTransitProcedureType02(
          identificationNumber = Some("in"),
          TIRHolderIdentificationNumber = Some("tirhin"),
          name = Some("hotn"),
          Address = None,
          ContactPerson = None
        )
      }
    }
  }
}
