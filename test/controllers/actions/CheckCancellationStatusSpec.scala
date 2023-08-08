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

package controllers.actions

import base.SpecBase
import generators.Generators
import models.DepartureMessageType.{AllocatedMRN, DeclarationSent, DepartureNotification, GoodsUnderControl, GuaranteeRejected}
import models.requests.IdentifierRequest
import models.{DepartureMessageMetaData, DepartureMessageType, EoriNumber}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import play.api.mvc.Results._
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CheckCancellationStatusSpec extends SpecBase with BeforeAndAfterEach with Generators {

  private def fakeOkResult[A]: A => Future[Result] =
    _ => Future.successful(Ok)

  "ArrivalStatusAction" - {
    "must return None when one of the allowed statuses" in {
      val messageType: Gen[DepartureMessageType] = Gen.oneOf(DepartureNotification, AllocatedMRN, GuaranteeRejected, GoodsUnderControl, DeclarationSent)

      forAll(messageType) {
        messageType =>
          when(mockDepartureMessageService.getMessageMetaDataHead(any())(any(), any()))
            .thenReturn(Future.successful(Some(DepartureMessageMetaData(LocalDateTime.now(), messageType, ""))))

          val checkCancellationStatus = new CheckCancellationStatusProvider(mockDepartureMessageService)

          val testRequest = IdentifierRequest(FakeRequest(GET, "/"), EoriNumber("eori"))

          val result: Future[Result] = checkCancellationStatus.apply(departureId).invokeBlock(testRequest, fakeOkResult)

          status(result) mustEqual OK
      }

    }

    "must return 303 and redirect to CannotSendCancellationRequest when status is not one of the allowed" in {

      val messageTypeShouldNotBe: Seq[DepartureMessageType] = Seq(DepartureNotification, AllocatedMRN, GuaranteeRejected, GoodsUnderControl, DeclarationSent)

      val messageType = arbitrary[DepartureMessageType].retryUntil(!messageTypeShouldNotBe.contains(_)).sample.value

      when(mockDepartureMessageService.getMessageMetaDataHead(any())(any(), any()))
        .thenReturn(Future.successful(Some(DepartureMessageMetaData(LocalDateTime.now(), messageType, ""))))

      val checkCancellationStatus = new CheckCancellationStatusProvider(mockDepartureMessageService)

      val testRequest = IdentifierRequest(FakeRequest(GET, "/"), EoriNumber("eori"))

      val result: Future[Result] = checkCancellationStatus.apply(departureId).invokeBlock(testRequest, fakeOkResult)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe controllers.routes.ErrorController.internalServerError().url // TODO: Change to new error page
    }

    "must return 303 and redirect to error page when message returns none" in {

      when(mockDepartureMessageService.getMessageMetaDataHead(any())(any(), any()))
        .thenReturn(Future.successful(None))

      val checkCancellationStatus = new CheckCancellationStatusProvider(mockDepartureMessageService)

      val testRequest = IdentifierRequest(FakeRequest(GET, "/"), EoriNumber("eori"))

      val result: Future[Result] = checkCancellationStatus.apply(departureId).invokeBlock(testRequest, fakeOkResult)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe controllers.routes.ErrorController
        .technicalDifficulties()
        .url // TODO: Should this redirect to tech difficulties since call has failed to retrive a message?
    }
  }
}
