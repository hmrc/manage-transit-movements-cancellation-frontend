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
import controllers.routes
import models.requests.IdentifierRequest
import models.{EoriNumber, LocalReferenceNumber}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.Results._
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DepartureMessageService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GetLRNActionSpec extends SpecBase with BeforeAndAfterEach with GuiceOneAppPerSuite {

  val mockMessageService: DepartureMessageService = mock[DepartureMessageService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset(mockMessageService)
  }

  private def fakeOkResult[A]: A => Future[Result] =
    _ => Future.successful(Ok)

  "GetLRNAction" - {
    "must return 200 LRN is available" in {

      when(mockMessageService.getLRNFromDeclarationMessage(any())(any(), any())).thenReturn(Future.successful(Some(LocalReferenceNumber("AB123"))))

      val getLRNActionProvider = new GetLRNActionProvider(mockMessageService)(implicitly)(departureId)

      val testRequest = IdentifierRequest(FakeRequest(GET, "/"), EoriNumber("CD123"))

      val result: Future[Result] = getLRNActionProvider.invokeBlock(testRequest, fakeOkResult)

      status(result) mustEqual OK
    }

    "must return 303 and redirect to technical difficulties when no LRN is available" in {

      when(mockMessageService.getLRNFromDeclarationMessage(any())(any(), any())).thenReturn(Future.successful(None))

      val getLRNActionProvider = new GetLRNActionProvider(mockMessageService)(implicitly)(departureId)

      val testRequest = IdentifierRequest(FakeRequest(GET, "/"), EoriNumber("CD123"))

      val result: Future[Result] = getLRNActionProvider.invokeBlock(testRequest, fakeOkResult)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ErrorController.technicalDifficulties().url
    }
  }
}
