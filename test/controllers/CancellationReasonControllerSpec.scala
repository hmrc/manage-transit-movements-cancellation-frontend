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

package controllers

import base.SpecBase
import connectors.ApiConnector
import forms.CancellationReasonFormProvider
import matchers.JsonMatchers
import models.AuditType.DeclarationInvalidationRequest
import models.Constants.commentMaxLength
import models.messages._
import models.{DepartureMessageMetaData, DepartureMessageType, DepartureMessages}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.CancellationReasonPage
import play.api.data.Form
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AuditService
import views.html.CancellationReasonView

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.Future

class CancellationReasonControllerSpec extends SpecBase with MockitoSugar with JsonMatchers {

  private lazy val cancellationReasonRoute: String = routes.CancellationReasonController.onPageLoad(departureId, lrn).url
  private val form: Form[String]                   = new CancellationReasonFormProvider()()

  private val mockApiConnector: ApiConnector = mock[ApiConnector]
  private val mockAuditService: AuditService = mock[AuditService]

  private val validAnswer = "answer"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(
        bind[ApiConnector].toInstance(mockApiConnector),
        bind[AuditService].toInstance(mockAuditService)
      )

  "CancellationReason Controller" - {

    "must redirect to CannotSendCancellationRequest page when cancellation status is not submittable on a GET" in {

      cancellationStatusNotSubmittable(departureId, lrn)

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(GET, cancellationReasonRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.CannotSendCancellationRequestController.onPageLoad(departureId, lrn).url
    }

    "must redirect to CannotSendCancellationRequest page when cancellation status is not submittable on a POST" in {

      cancellationStatusNotSubmittable(departureId, lrn)

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, cancellationReasonRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.CannotSendCancellationRequestController.onPageLoad(departureId, lrn).url
    }

    "must return OK and the correct view for a GET" in {

      cancellationStatusSubmittable(departureId, lrn)

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(GET, cancellationReasonRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[CancellationReasonView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, departureId, lrn, commentMaxLength)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      cancellationStatusSubmittable(departureId, lrn)

      val date = LocalDateTime.now

      val ie015Data: IE015Data = IE015Data(
        IE015MessageData(
          "sender",
          "recipient",
          date,
          "CC015",
          TransitOperationIE015("LRNAB123", Some("MRNCD3232")),
          CustomsOfficeOfDeparture("AB123"),
          HolderOfTheTransitProcedure = HolderOfTheTransitProcedure("123")
        )
      )
      val messages = DepartureMessages(
        List(
          DepartureMessageMetaData(
            LocalDateTime.parse(s"$date", DateTimeFormatter.ISO_DATE_TIME),
            DepartureMessageType.DepartureNotification,
            "movements/departures/6365135ba5e821ee/message/634982098f02f00b"
          )
        )
      )

      val userAnswers = emptyUserAnswers
      dataRetrievalWithData(userAnswers)

      when(mockDepartureMessageService.mrnAllocatedIE015(any())(any(), any())).thenReturn(Future.successful(Some(ie015Data)))
      when(mockDepartureMovementConnector.getMessageMetaData(any())(any(), any())).thenReturn(Future.successful(Some(messages)))
      when(mockSessionRepository.remove(any(), any())).thenReturn(Future.successful(true))
      when(mockApiConnector.submit(any(), any())(any())).thenReturn(Future.successful(true))

      val request = FakeRequest(POST, cancellationReasonRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.CancellationSubmissionConfirmationController.onPageLoad(lrn).url

      val auditedAnswers = userAnswers.setValue(CancellationReasonPage, validAnswer)
      verify(mockAuditService).audit(eqTo(DeclarationInvalidationRequest), eqTo(auditedAnswers))(any())
    }

    "must redirect to technical difficulties when cannot submit" in {

      cancellationStatusSubmittable(departureId, lrn)

      val date = LocalDateTime.now

      val ie015Data: IE015Data = IE015Data(
        IE015MessageData(
          "sender",
          "recipient",
          date,
          "CC015",
          TransitOperationIE015("LRNAB123", Some("MRNCD3232")),
          CustomsOfficeOfDeparture("AB123"),
          HolderOfTheTransitProcedure = HolderOfTheTransitProcedure("123")
        )
      )
      val messages = DepartureMessages(
        List(
          DepartureMessageMetaData(
            LocalDateTime.parse(s"$date", DateTimeFormatter.ISO_DATE_TIME),
            DepartureMessageType.DepartureNotification,
            "movements/departures/6365135ba5e821ee/message/634982098f02f00b"
          )
        )
      )

      dataRetrievalWithData(emptyUserAnswers)

      when(mockDepartureMessageService.mrnAllocatedIE015(any())(any(), any())).thenReturn(Future.successful(Some(ie015Data)))
      when(mockDepartureMovementConnector.getMessageMetaData(any())(any(), any())).thenReturn(Future.successful(Some(messages)))
      when(mockSessionRepository.remove(any(), any())).thenReturn(Future.successful(true))
      when(mockApiConnector.submit(any(), any())(any())).thenReturn(Future.successful(false))

      val request = FakeRequest(POST, cancellationReasonRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.technicalDifficulties().url
    }

    "must redirect to the technicalDifficulties page when no ieo15Data found" in {

      cancellationStatusSubmittable(departureId, lrn)

      val date = LocalDateTime.now

      val messages = DepartureMessages(
        List(
          DepartureMessageMetaData(
            LocalDateTime.parse(s"$date", DateTimeFormatter.ISO_DATE_TIME),
            DepartureMessageType.DepartureNotification,
            "movements/departures/6365135ba5e821ee/message/634982098f02f00b"
          )
        )
      )

      dataRetrievalWithData(emptyUserAnswers)

      when(mockDepartureMessageService.mrnAllocatedIE015(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockDepartureMovementConnector.getMessageMetaData(any())(any(), any())).thenReturn(Future.successful(Some(messages)))
      when(mockApiConnector.submit(any(), any())(any())).thenReturn(Future.successful(true))

      val request = FakeRequest(POST, cancellationReasonRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.technicalDifficulties().url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      cancellationStatusSubmittable(departureId, lrn)

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, cancellationReasonRoute).withFormUrlEncodedBody(("value", ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      cancellationStatusSubmittable(departureId, lrn)

      dataRetrievalNoData()

      val request = FakeRequest(GET, cancellationReasonRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      cancellationStatusSubmittable(departureId, lrn)

      dataRetrievalNoData()

      val request = FakeRequest(POST, cancellationReasonRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
