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

package base

import controllers.actions._
import models.requests.{AuthorisedRequest, IdentifierRequest, OptionalDataRequest}
import models.{LocalReferenceNumber, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.{BeforeAndAfterEach, TestSuite}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{ActionRefiner, ActionTransformer, Result}
import repositories.SessionRepository
import services.CancellationSubmissionService

import scala.concurrent.{ExecutionContext, Future}

trait MockApplicationBuilder extends GuiceOneAppPerSuite with BeforeAndAfterEach with MockitoSugar {
  self: TestSuite =>

  val lrn: LocalReferenceNumber = LocalReferenceNumber("lrn")

  val mockSubmissionService: CancellationSubmissionService = mock[CancellationSubmissionService]

  val mockDataRetrievalActionProvider: DataRetrievalActionProvider = mock[DataRetrievalActionProvider]

  val mockCheckCancellationStatusProvider: CheckCancellationStatusProvider = mock[CheckCancellationStatusProvider]

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  override def beforeEach(): Unit = {
    super.beforeEach()

    Mockito.reset(
      mockDataRetrievalActionProvider,
      mockCheckCancellationStatusProvider,
      mockSessionRepository
    )

    when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
  }

  def dataRetrievalWithData(userAnswers: UserAnswers): Unit = dataRetrieval(Some(userAnswers))

  def dataRetrievalNoData(): Unit = dataRetrieval(None)

  private def dataRetrieval(userAnswers: Option[UserAnswers]): Unit = {
    val fakeDataRetrievalAction = new ActionTransformer[AuthorisedRequest, OptionalDataRequest] {
      override protected def transform[A](request: AuthorisedRequest[A]): Future[OptionalDataRequest[A]] =
        Future.successful(OptionalDataRequest(request.request, request.eoriNumber, lrn, userAnswers))

      override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global
    }

    when(mockDataRetrievalActionProvider.apply(any())).thenReturn(fakeDataRetrievalAction)
  }

  def checkCancellationStatus(): Unit = {
    val fakeCancellationStatusAction = new ActionRefiner[IdentifierRequest, AuthorisedRequest] {
      override protected def refine[A](request: IdentifierRequest[A]): Future[Either[Result, AuthorisedRequest[A]]] =
        Future.successful(Right(AuthorisedRequest(request.request, request.eoriNumber, lrn)))

      override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global
    }

    when(mockCheckCancellationStatusProvider.apply(any())).thenReturn(fakeCancellationStatusAction)
  }

  override def fakeApplication(): Application =
    guiceApplicationBuilder()
      .build()

  // Override to provide custom binding
  def guiceApplicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[DataRetrievalActionProvider].toInstance(mockDataRetrievalActionProvider),
        bind[CheckCancellationStatusProvider].toInstance(mockCheckCancellationStatusProvider),
        bind[CancellationSubmissionService].toInstance(mockSubmissionService),
        bind[SessionRepository].toInstance(mockSessionRepository)
      )

}
