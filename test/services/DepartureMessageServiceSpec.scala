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
import generated.{CC015CType, CC028CType}
import generators.Generators
import models.DepartureMessageType.{AllocatedMRN, DepartureNotification}
import models.{DepartureMessageMetaData, DepartureMessages}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DepartureMessageServiceSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val mockConnector = mock[DepartureMovementConnector]
  private val service       = new DepartureMessageService(mockConnector)

  private val departureMessageMetaData1: DepartureMessageMetaData =
    DepartureMessageMetaData(LocalDateTime.now(), DepartureNotification, "path/url")

  private val departureMessageMetaData2: DepartureMessageMetaData =
    DepartureMessageMetaData(LocalDateTime.now().minusDays(1), DepartureNotification, "path/url")

  private val departureMessageMetaData3: DepartureMessageMetaData =
    DepartureMessageMetaData(LocalDateTime.now().minusDays(2), AllocatedMRN, "path/url")

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
      forAll(arbitrary[CC015CType]) {
        ie015 =>
          beforeEach()

          when(mockConnector.getMessageMetaData(any())(any())).thenReturn(Future.successful(Some(departureMessages)))
          when(mockConnector.getMessage[CC015CType](any())(any(), any())).thenReturn(Future.successful(ie015))

          service.getIE015(departureId).futureValue mustBe Some(ie015)

          verify(mockConnector).getMessage[CC015CType](any())(any(), any())
          verify(mockConnector).getMessageMetaData(any())(any())
      }
    }

    "getIE015FromDeclarationMessage returns none when getMessageMetaData connector fails" in {
      when(mockConnector.getMessageMetaData(any())(any())).thenReturn(Future.successful(None))
      service.getIE015(departureId).futureValue mustBe None
      verify(mockConnector).getMessageMetaData(any())(any())
    }

    "getIE028FromDeclarationMessage success" in {
      forAll(arbitrary[CC028CType]) {
        ie028 =>
          beforeEach()

          when(mockConnector.getMessageMetaData(any())(any())).thenReturn(Future.successful(Some(mrnAllocatedMessages)))
          when(mockConnector.getMessage[CC028CType](any())(any(), any())).thenReturn(Future.successful(ie028))

          service.getIE028(departureId).futureValue mustBe Some(ie028)

          verify(mockConnector).getMessage[CC028CType](any())(any(), any())
          verify(mockConnector).getMessageMetaData(any())(any())
      }
    }

    "getIE028FromDeclarationMessage returns none when getMessageMetaData connector fails" in {
      when(mockConnector.getMessageMetaData(any())(any())).thenReturn(Future.successful(None))
      service.getIE028(departureId).futureValue mustBe None
      verify(mockConnector).getMessageMetaData(any())(any())
    }
  }
}
