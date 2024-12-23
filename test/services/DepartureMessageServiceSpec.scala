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
import generated._
import generators.Generators
import models.MessageType._
import models.{DepartureMessages, MessageMetaData}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models.MessageStatus

class DepartureMessageServiceSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val mockConnector = mock[DepartureMovementConnector]
  private val service       = new DepartureMessageService(mockConnector)

  private val message1: MessageMetaData =
    MessageMetaData("message1Id", DepartureNotification, LocalDateTime.now(), MessageStatus.Success)

  private val message2: MessageMetaData =
    MessageMetaData("message2Id", DepartureNotification, LocalDateTime.now().minusDays(1), MessageStatus.Success)

  private val message3: MessageMetaData =
    MessageMetaData("message3Id", AllocatedMRN, LocalDateTime.now().plusDays(1), MessageStatus.Success)

  private val message4: MessageMetaData =
    MessageMetaData("message4Id", AllocatedMRN, LocalDateTime.now().plusDays(2), MessageStatus.Success)

  private val message5: MessageMetaData =
    MessageMetaData("message5Id", DeclarationInvalidationRequest, LocalDateTime.now().plusDays(3), MessageStatus.Success)

  private val message6: MessageMetaData =
    MessageMetaData("message6Id", InvalidationDecision, LocalDateTime.now().plusDays(3), MessageStatus.Success)

  private val departureMessages: DepartureMessages =
    DepartureMessages(List(message1, message2, message3, message4, message5, message6))

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[DepartureMovementConnector].toInstance(mockConnector))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockConnector)
  }

  "DepartureMessageService" - {

    "getMessageMetaDataHead" - {
      "must return latest message" - {
        "when all statuses are success" in {
          when(mockConnector.getMessageMetaData(any())(any())).thenReturn(Future.successful(departureMessages))

          service.getMessageMetaDataHead(departureId).futureValue mustBe Some(message6)

          verify(mockConnector).getMessageMetaData(eqTo(departureId))(any())
        }

        "when latest status is Failed" in {
          val message1 = MessageMetaData("message1Id", DepartureNotification, LocalDateTime.now(), MessageStatus.Success)
          val message2 = MessageMetaData("message2Id", DeclarationInvalidationRequest, LocalDateTime.now(), MessageStatus.Failed)
          val messages = DepartureMessages(List(message1, message2))

          when(mockConnector.getMessageMetaData(any())(any())).thenReturn(Future.successful(messages))

          service.getMessageMetaDataHead(departureId).futureValue mustBe Some(message1)

          verify(mockConnector).getMessageMetaData(eqTo(departureId))(any())
        }
      }
    }

    "getIE014" - {
      "when success" in {
        forAll(arbitrary[CC014CType]) {
          ie014 =>
            beforeEach()

            when(mockConnector.getMessageMetaData(any())(any())).thenReturn(Future.successful(departureMessages))
            when(mockConnector.getMessage[CC014CType](any(), any())(any(), any())).thenReturn(Future.successful(ie014))

            service.getIE014(departureId).futureValue mustBe Some(ie014)

            verify(mockConnector).getMessageMetaData(eqTo(departureId))(any())
            verify(mockConnector).getMessage[CC014CType](eqTo(departureId), eqTo("message5Id"))(any(), any())
        }
      }
    }

    "getIE015" - {
      "when success" in {
        forAll(arbitrary[CC015CType]) {
          ie015 =>
            beforeEach()

            when(mockConnector.getMessageMetaData(any())(any())).thenReturn(Future.successful(departureMessages))
            when(mockConnector.getMessage[CC015CType](any(), any())(any(), any())).thenReturn(Future.successful(ie015))

            service.getIE015(departureId).futureValue mustBe Some(ie015)

            verify(mockConnector).getMessageMetaData(eqTo(departureId))(any())
            verify(mockConnector).getMessage[CC015CType](eqTo(departureId), eqTo("message1Id"))(any(), any())
        }
      }
    }

    "getIE028" - {
      "when success" in {
        forAll(arbitrary[CC028CType]) {
          ie028 =>
            beforeEach()

            when(mockConnector.getMessageMetaData(any())(any())).thenReturn(Future.successful(departureMessages))
            when(mockConnector.getMessage[CC028CType](any(), any())(any(), any())).thenReturn(Future.successful(ie028))

            service.getIE028(departureId).futureValue mustBe Some(ie028)

            verify(mockConnector).getMessageMetaData(eqTo(departureId))(any())
            verify(mockConnector).getMessage[CC028CType](eqTo(departureId), eqTo("message4Id"))(any(), any())
        }
      }
    }
  }
}
