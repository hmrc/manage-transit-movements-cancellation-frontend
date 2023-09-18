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

package services

import base.SpecBase
import connectors.DepartureMovementConnector
import generators.Generators
import models.DepartureMessageType.{AllocatedMRN, DepartureNotification}
import models.messages.{CustomsOfficeOfDeparture, HolderOfTheTransitProcedure, IE015Data, IE015MessageData, TransitOperationIE015}
import models.{DepartureMessageMetaData, DepartureMessages}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DepartureMessageServiceSpec extends SpecBase with Generators with BeforeAndAfterEach {

  private val mockConnector                                       = mock[DepartureMovementConnector]
  private val service                                             = new DepartureMessageService(mockConnector)
  private val departureMessageMetaData1: DepartureMessageMetaData = DepartureMessageMetaData(LocalDateTime.now(), DepartureNotification, "path/url")

  private val departureMessageMetaData2: DepartureMessageMetaData =
    DepartureMessageMetaData(LocalDateTime.now().minusDays(1), DepartureNotification, "path/url")
  private val departureMessageMetaData3: DepartureMessageMetaData = DepartureMessageMetaData(LocalDateTime.now().minusDays(2), AllocatedMRN, "path/url")

  private val departureMessages: DepartureMessages = DepartureMessages(List(departureMessageMetaData1, departureMessageMetaData2))

  private val mrnAllocatedMessages: DepartureMessages = DepartureMessages(List(departureMessageMetaData1, departureMessageMetaData3))

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[DepartureMovementConnector].toInstance(mockConnector))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockConnector)
  }

  "DepartureMessageService" - {
    "getIE015FromDeclarationMessage success" in {

      when(mockConnector.getMessageMetaData(any())(any(), any())).thenReturn(Future.successful(Some(departureMessages)))
      when(mockConnector.getIE015(any())(any())).thenReturn(Future.successful(ie015Data))
      service.getIE015FromDeclarationMessage(departureId).futureValue mustBe Some(ie015Data)
      verify(mockConnector).getIE015(any())(any())
      verify(mockConnector).getMessageMetaData(any())(any(), any())

    }

    "getIE015FromDeclarationMessage returns none when getMessageMetaData connector fails" in {

      when(mockConnector.getMessageMetaData(any())(any(), any())).thenReturn(Future.successful(None))
      service.getIE015FromDeclarationMessage(departureId).futureValue mustBe None
      verify(mockConnector).getMessageMetaData(any())(any(), any())

    }

    "getIE028FromDeclarationMessage success" in {

      when(mockConnector.getMessageMetaData(any())(any(), any())).thenReturn(Future.successful(Some(mrnAllocatedMessages)))
      when(mockConnector.getIE028(any())(any())).thenReturn(Future.successful(ie028Data))
      service.getIE028FromDeclarationMessage(departureId).futureValue mustBe Some(ie028Data)
      verify(mockConnector).getIE028(any())(any())
      verify(mockConnector).getMessageMetaData(any())(any(), any())

    }

    "getIE028FromDeclarationMessage returns none when getMessageMetaData connector fails" in {

      when(mockConnector.getMessageMetaData(any())(any(), any())).thenReturn(Future.successful(None))
      service.getIE028FromDeclarationMessage(departureId).futureValue mustBe None
      verify(mockConnector).getMessageMetaData(any())(any(), any())

    }

    "mrnAllocatedIE015" - {

      "must return IE015 when MRN has been allocated" in {

        val expectedIE015 = IE015Data(
          IE015MessageData(
            "sender",
            "recipient",
            ie015Data.data.preparationDateAndTime,
            "messageId",
            TransitOperationIE015("LRNAB123", Some("MRN123")),
            CustomsOfficeOfDeparture("AB123"),
            HolderOfTheTransitProcedure = HolderOfTheTransitProcedure("123")
          )
        )

        when(mockConnector.getMessageMetaData(any())(any(), any())).thenReturn(Future.successful(Some(departureMessages)))
        when(mockConnector.getIE015(any())(any())).thenReturn(Future.successful(ie015Data))

        when(mockConnector.getMessageMetaData(any())(any(), any())).thenReturn(Future.successful(Some(mrnAllocatedMessages)))
        when(mockConnector.getIE028(any())(any())).thenReturn(Future.successful(ie028Data))

        service.mrnAllocatedIE015(departureId).futureValue mustBe Some(expectedIE015)

      }

      "must return IE015 when MRN has not been allocated" in {

        when(mockConnector.getMessageMetaData(any())(any(), any())).thenReturn(Future.successful(Some(departureMessages)))
        when(mockConnector.getIE015(any())(any())).thenReturn(Future.successful(ie015Data))

        service.mrnAllocatedIE015(departureId).futureValue mustBe Some(ie015Data)
      }

      "must return None when IE015 is not defined" in {

        when(mockConnector.getMessageMetaData(any())(any(), any())).thenReturn(Future.successful(None))

        service.mrnAllocatedIE015(departureId).futureValue mustBe None
      }
    }
  }
}
