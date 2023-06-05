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
import models.DepartureMessageMetaData
import models.DepartureMessageType.DepartureNotification
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach

import java.time.LocalDateTime
import scala.concurrent.Future

class DepartureMessageServiceSpec extends SpecBase with Generators with BeforeAndAfterEach {

  private val mockConnector = mock[DepartureMovementConnector]
  private val service = new DepartureMessageService(mockConnector)

  private val departureMessage = DepartureMessageMetaData(LocalDateTime.now(), DepartureNotification, "path/url")

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockConnector)
  }

  "DepartureMessageService" - {
    "getDepartureNotificationMetaData" in {
      val date = LocalDateTime.now
      val departureNotificationMetaData: DepartureMessageMetaData = DepartureMessageMetaData(date, DepartureNotification, "path/url")

      when(mockConnector.getMessageMetaData(departureId)(any(), any())).thenReturn(Future.successful(departureNotificationMetaData)))
      service.getLRNFromDeclarationMessage(departureId)(_,_).futureValue mustBe Some(departureMessage)
    }
  }
}

