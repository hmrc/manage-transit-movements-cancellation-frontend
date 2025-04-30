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
import forms.CancellationReasonFormProvider
import generated.{CC015CType, CC028CType}
import generators.Generators
import models.AuditType.DeclarationInvalidationRequest
import models.Constants.commentMaxLength
import models.DepartureId
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.CancellationReasonPage
import play.api.data.Form
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.submission.{AuditService, SubmissionService}
import uk.gov.hmrc.http.HttpResponse
import views.html.CancellationReasonView

import scala.concurrent.Future

class CancellationReasonControllerSpec extends SpecBase with MockitoSugar with ScalaCheckPropertyChecks with Generators {

  private lazy val cancellationReasonRoute: String = routes.CancellationReasonController.onPageLoad(departureId, lrn).url
  private val form: Form[String]                   = new CancellationReasonFormProvider()()

  private val mockSubmissionService: SubmissionService = mock[SubmissionService]
  private val mockAuditService: AuditService           = mock[AuditService]

  private val validAnswer = "answer"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(
        bind[SubmissionService].toInstance(mockSubmissionService),
        bind[AuditService].toInstance(mockAuditService)
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDepartureMessageService)
    reset(mockSubmissionService)
    reset(mockAuditService)
  }

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

    "must submit IE014 when IE015 found and IE028 found" in {
      forAll(arbitrary[CC015CType], arbitrary[CC028CType]) {
        (ie015, ie028) =>
          beforeEach()

          cancellationStatusSubmittable(departureId, lrn)

          val userAnswers = emptyUserAnswers
          dataRetrievalWithData(userAnswers)

          when(mockDepartureMessageService.getIE015(any())(any(), any())).thenReturn(Future.successful(Some(ie015)))
          when(mockDepartureMessageService.getIE028(any())(any(), any())).thenReturn(Future.successful(Some(ie028)))
          when(mockSubmissionService.submit(any(), any(), any(), any(), any())(any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          val request = FakeRequest(POST, cancellationReasonRoute)
            .withFormUrlEncodedBody(("value", validAnswer))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.CancellationSubmissionConfirmationController.onPageLoad(departureId, lrn).url

          val auditedAnswers = userAnswers.setValue(CancellationReasonPage, validAnswer)
          verify(mockAuditService).audit(eqTo(DeclarationInvalidationRequest), eqTo(auditedAnswers))(any())
          verify(mockSubmissionService).submit(
            eqTo(eoriNumber),
            eqTo(ie015),
            eqTo(Some(ie028.TransitOperation.MRN)),
            eqTo(validAnswer),
            eqTo(DepartureId(departureId))
          )(any())
      }
    }

    "must submit IE014 when IE015 found and IE028 not found" in {
      forAll(arbitrary[CC015CType]) {
        ie015 =>
          beforeEach()

          cancellationStatusSubmittable(departureId, lrn)

          val userAnswers = emptyUserAnswers
          dataRetrievalWithData(userAnswers)

          when(mockDepartureMessageService.getIE015(any())(any(), any())).thenReturn(Future.successful(Some(ie015)))
          when(mockDepartureMessageService.getIE028(any())(any(), any())).thenReturn(Future.successful(None))
          when(mockSubmissionService.submit(any(), any(), any(), any(), any())(any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          val request = FakeRequest(POST, cancellationReasonRoute)
            .withFormUrlEncodedBody(("value", validAnswer))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.CancellationSubmissionConfirmationController.onPageLoad(departureId, lrn).url

          val auditedAnswers = userAnswers.setValue(CancellationReasonPage, validAnswer)
          verify(mockAuditService).audit(eqTo(DeclarationInvalidationRequest), eqTo(auditedAnswers))(any())
          verify(mockSubmissionService).submit(eqTo(eoriNumber), eqTo(ie015), eqTo(None), eqTo(validAnswer), eqTo(DepartureId(departureId)))(any())
      }
    }

    "must redirect to technical difficulties when cannot submit" in {
      forAll(arbitrary[CC015CType], Gen.choose(400: Int, 599: Int)) {
        (ie015, errorCode) =>
          beforeEach()

          cancellationStatusSubmittable(departureId, lrn)

          val userAnswers = emptyUserAnswers
          dataRetrievalWithData(userAnswers)

          when(mockDepartureMessageService.getIE015(any())(any(), any())).thenReturn(Future.successful(Some(ie015)))
          when(mockDepartureMessageService.getIE028(any())(any(), any())).thenReturn(Future.successful(None))
          when(mockSubmissionService.submit(any(), any(), any(), any(), any())(any())).thenReturn(Future.successful(HttpResponse(errorCode, "")))

          val request = FakeRequest(POST, cancellationReasonRoute)
            .withFormUrlEncodedBody(("value", validAnswer))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.ErrorController.technicalDifficulties().url

          verifyNoInteractions(mockAuditService)
          verify(mockSubmissionService).submit(eqTo(eoriNumber), eqTo(ie015), eqTo(None), eqTo(validAnswer), eqTo(DepartureId(departureId)))(any())
      }
    }

    "must redirect to the technicalDifficulties page when no ieo15Data found" in {
      cancellationStatusSubmittable(departureId, lrn)

      val userAnswers = emptyUserAnswers
      dataRetrievalWithData(userAnswers)

      when(mockDepartureMessageService.getIE015(any())(any(), any())).thenReturn(Future.successful(None))

      val request = FakeRequest(POST, cancellationReasonRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.technicalDifficulties().url

      verifyNoInteractions(mockAuditService)
      verifyNoInteractions(mockSubmissionService)
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
