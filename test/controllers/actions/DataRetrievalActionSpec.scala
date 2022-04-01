/*
 * Copyright 2022 HM Revenue & Customs
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
import models.requests.{AuthorisedRequest, OptionalDataRequest}
import models.{DepartureId, EoriNumber, LocalReferenceNumber, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContent, Request, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with GuiceOneAppPerSuite with Generators {

  val sessionRepository: SessionRepository = mock[SessionRepository]

  override lazy val app: Application = {

    import play.api.inject._

    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionRepository].toInstance(sessionRepository)
      )
      .build()
  }

  def harness(departureId: DepartureId, f: OptionalDataRequest[AnyContent] => Unit): Unit = {

    lazy val actionProvider = app.injector.instanceOf[DataRetrievalActionProviderImpl]

    actionProvider(departureId)
      .invokeBlock(
        AuthorisedRequest(FakeRequest(GET, "/").asInstanceOf[Request[AnyContent]], EoriNumber(""), LocalReferenceNumber("")),
        {
          request: OptionalDataRequest[AnyContent] =>
            f(request)
            Future.successful(Results.Ok)
        }
      )
      .futureValue
  }

  "a data retrieval action" - {

    "must return an OptionalDataRequest with an empty UserAnswers" - {

      "where there are no existing answers for this LRN" in {

        when(sessionRepository.get(any())) thenReturn Future.successful(None)

        harness(departureId, request => request.userAnswers must not be defined)
      }
    }

    "must return an OptionalDataRequest with some defined UserAnswers" - {

      "when there are existing answers for this LRN" in {

        when(sessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))

        harness(departureId, request => request.userAnswers mustBe defined)
      }
    }
  }
}
