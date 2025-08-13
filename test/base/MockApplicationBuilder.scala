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

package base

import config.FrontendAppConfig
import connectors.DepartureMovementConnector
import controllers.actions.*
import models.requests.{IdentifierRequest, OptionalDataRequest}
import models.{LocalReferenceNumber, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, TestSuite}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.{bind, Injector}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, ActionTransformer, Result}
import repositories.SessionRepository
import services.DepartureMessageService

import scala.concurrent.{ExecutionContext, Future}

trait MockApplicationBuilder extends GuiceOneAppPerSuite with BeforeAndAfterEach with MockitoSugar {
  self: TestSuite & SpecBase =>

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  val lrn: LocalReferenceNumber = LocalReferenceNumber("lrn")

  val mockDataRetrievalActionProvider: DataRetrievalActionProvider         = mock[DataRetrievalActionProvider]
  val mockCheckCancellationStatusProvider: CheckCancellationStatusProvider = mock[CheckCancellationStatusProvider]

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  val mockDepartureMessageService: DepartureMessageService       = mock[DepartureMessageService]
  val mockDepartureMovementConnector: DepartureMovementConnector = mock[DepartureMovementConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDataRetrievalActionProvider)
    reset(mockCheckCancellationStatusProvider)
    reset(mockSessionRepository)
    reset(mockDepartureMessageService)
    when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
  }

  def dataRetrievalWithData(userAnswers: UserAnswers): Unit = dataRetrieval(Some(userAnswers))

  def dataRetrievalNoData(): Unit = dataRetrieval(None)

  private def dataRetrieval(userAnswers: Option[UserAnswers]): Unit = {
    val fakeDataRetrievalAction = new ActionTransformer[IdentifierRequest, OptionalDataRequest] {
      override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] =
        Future.successful(OptionalDataRequest(request.request, request.eoriNumber, userAnswers))

      override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global
    }

    when(mockDataRetrievalActionProvider.apply(any())).thenReturn(fakeDataRetrievalAction)
  }

  def cancellationStatusSubmittable(departureId: String, lrn: LocalReferenceNumber): Unit    = checkCancellationStatus(isSubmittable = true, departureId, lrn)
  def cancellationStatusNotSubmittable(departureId: String, lrn: LocalReferenceNumber): Unit = checkCancellationStatus(isSubmittable = false, departureId, lrn)

  private def checkCancellationStatus(isSubmittable: Boolean, departureId: String, lrn: LocalReferenceNumber): Unit = {
    val fakeCheckCancellationStatus = new ActionFilter[IdentifierRequest] {
      override protected def filter[A](request: IdentifierRequest[A]): Future[Option[Result]] =
        if (isSubmittable) {
          Future.successful(None)
        } else {
          Future.successful(Option(Redirect(controllers.routes.CannotSendCancellationRequestController.onPageLoad(departureId, lrn))))
        }

      override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global

    }

    when(mockCheckCancellationStatusProvider.apply(any(), any())).thenReturn(fakeCheckCancellationStatus)
  }

  override def fakeApplication(): Application =
    guiceApplicationBuilder()
      .build()

  def guiceApplicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[DataRetrievalActionProvider].toInstance(mockDataRetrievalActionProvider),
        bind[CheckCancellationStatusProvider].toInstance(mockCheckCancellationStatusProvider),
        bind[DepartureMessageService].toInstance(mockDepartureMessageService),
        bind[SessionRepository].toInstance(mockSessionRepository)
      )

}
