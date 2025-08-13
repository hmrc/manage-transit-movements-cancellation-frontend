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
import models.EoriNumber
import models.requests.{IdentifierRequest, OptionalDataRequest}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import play.api.mvc.{AnyContent, Request, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with Generators {

  private val mockSessionRepository: SessionRepository = mock[SessionRepository]

  def harness(departureId: String, f: OptionalDataRequest[AnyContent] => Unit): Unit = {

    lazy val actionProvider = new DataRetrievalActionProviderImpl(mockSessionRepository)

    actionProvider(departureId)
      .invokeBlock(
        IdentifierRequest(FakeRequest(GET, "/").asInstanceOf[Request[AnyContent]], EoriNumber("")),
        {
          (request: OptionalDataRequest[AnyContent]) =>
            f(request)
            Future.successful(Results.Ok)
        }
      )
      .futureValue
  }

  "a data retrieval action" - {

    "must return an OptionalDataRequest with an empty UserAnswers" - {

      "where there are no existing answers for this LRN" in {

        when(mockSessionRepository.get(any())) `thenReturn` Future.successful(None)

        harness(departureId, request => request.userAnswers must not be defined)
      }
    }

    "must return an OptionalDataRequest with some defined UserAnswers" - {

      "when there are existing answers for this LRN" in {

        when(mockSessionRepository.get(any())) `thenReturn` Future.successful(Some(emptyUserAnswers))

        harness(departureId, request => request.userAnswers mustBe defined)
      }
    }
  }
}
