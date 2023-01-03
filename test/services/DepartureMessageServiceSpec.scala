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
import connectors.responses.InvalidStatus
import generators.Generators
import models.messages.CancellationDecisionUpdate
import models.response.MessageSummary
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import play.api.test.Helpers
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DepartureMessageServiceSpec extends SpecBase with Generators with BeforeAndAfterEach {

  private val mockConnector = mock[DepartureMovementConnector]

  implicit private val hc: HeaderCarrier = HeaderCarrier()

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockConnector)
  }

  "DepartureMessageService" - {

    "cancellationDecisionUpdateMessage" - {
      "when unable to retrieve message summary" - {
        "must return None" in {
          when(mockConnector.getMessageSummary(any())(any()))
            .thenReturn(Future.successful(Left(InvalidStatus(Helpers.INTERNAL_SERVER_ERROR))))

          val service = new DepartureMessageService(mockConnector)

          whenReady(service.cancellationDecisionUpdateMessage(departureId)) {
            result =>
              result mustBe None

              verify(mockConnector).getMessageSummary(eqTo(departureId))(any())
          }
        }
      }

      "when cancellation decision update (IE009) field undefined in message summary" - {
        "must return None" in {
          val summary = MessageSummary(departureId.index, Map())

          when(mockConnector.getMessageSummary(any())(any()))
            .thenReturn(Future.successful(Right(summary)))

          val service = new DepartureMessageService(mockConnector)

          whenReady(service.cancellationDecisionUpdateMessage(departureId)) {
            result =>
              result mustBe None

              verify(mockConnector).getMessageSummary(eqTo(departureId))(any())
          }
        }
      }

      "when cancellation decision update (IE009) field defined in message summary" - {
        "must return message" in {
          val location = Gen.alphaNumStr.sample.value
          val message  = arbitrary[CancellationDecisionUpdate].sample.value
          val summary  = MessageSummary(departureId.index, Map("IE009" -> location))

          when(mockConnector.getMessageSummary(any())(any()))
            .thenReturn(Future.successful(Right(summary)))

          when(mockConnector.getCancellationDecisionUpdateMessage(any())(any()))
            .thenReturn(Future.successful(Some(message)))

          val service = new DepartureMessageService(mockConnector)

          whenReady(service.cancellationDecisionUpdateMessage(departureId)) {
            result =>
              result mustBe Some(message)

              verify(mockConnector).getMessageSummary(eqTo(departureId))(any())
              verify(mockConnector).getCancellationDecisionUpdateMessage(eqTo(location))(any())
          }
        }
      }
    }
  }
}
