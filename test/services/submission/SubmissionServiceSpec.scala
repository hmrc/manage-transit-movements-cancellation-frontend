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
import generated.*
import generators.Generators
import models.IE015
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running
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

  "transform" - {
    "must return HolderOfTransitProcedureType23" in {
      forAll(arbitrary[IE015], nonEmptyString, nonEmptyString) {
        (ie015, mrn, justification) =>
          val result = service.transform(eoriNumber, ie015, Some(mrn), justification)
          result.HolderOfTheTransitProcedure mustEqual ie015.holderOfTheTransitProcedure.toScalaxb
      }
    }
  }

  "attributes" - {
    "must assign phase ID" - {
      "when phase6 disabled" in {
        val app = super.guiceApplicationBuilder().configure("feature-flags.phase-6-enabled" -> "false").build()
        running(app) {
          val service = app.injector.instanceOf[SubmissionService]
          val result  = service.attributes
          result.keys.size mustEqual 1
          result.get("@PhaseID").value.value.toString mustEqual "NCTS5.1"
        }
      }

      "when phase6 enabled" in {
        val app = super.guiceApplicationBuilder().configure("feature-flags.phase-6-enabled" -> "true").build()
        running(app) {
          val service = app.injector.instanceOf[SubmissionService]
          val result  = service.attributes
          result.keys.size mustEqual 1
          result.get("@PhaseID").value.value.toString mustEqual "NCTS6"
        }
      }
    }
  }

  "messageSequence" - {
    "must create message sequence" - {
      "when GB office of destination" in {
        val result = service.messageSequence(eoriNumber, "GB00001")

        result mustEqual MESSAGESequence(
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

        result mustEqual MESSAGESequence(
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
      "when mrn defined " in {
        val mrn = "MRN123"
        val lrn = "LRN123"

        val result = service.transitOperation(Some(lrn), Some(mrn))

        result mustEqual TransitOperationType56(
          LRN = None,
          MRN = Some(mrn)
        )
      }

      "when mrn undefined " in {
        val lrn = "LRN123"

        val result = service.transitOperation(Some(lrn), None)

        result mustEqual TransitOperationType56(
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

      result mustEqual InvalidationType02(
        requestDateAndTime = Some(XMLCalendar("2020-01-01T09:30:00")),
        decisionDateAndTime = None,
        decision = None,
        initiatedByCustoms = Number0,
        justification = Some(justification)
      )
    }
  }
}
