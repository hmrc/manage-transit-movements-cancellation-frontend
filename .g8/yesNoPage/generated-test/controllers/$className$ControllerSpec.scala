package controllers

import base.{MockNunjucksRendererApp, SpecBase}
import controllers.actions.FakeDataRetrievalAction
import forms.$className$FormProvider
import matchers.JsonMatchers
import models.{NormalMode, UserAnswers, LocalReferenceNumber}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import pages.$className$Page
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import config.FrontendAppConfig
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import navigation.{FakeNavigator, Navigator}


import scala.concurrent.Future

class $className$ControllerSpec extends SpecBase with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")
  private val formProvider = new $className$FormProvider()
  private val form = formProvider()
  private val template = "$className;format="decap"$.njk"
  private val mockFrontendAppConfig = mock[FrontendAppConfig]

  lazy val $className;format="decap"$Route = routes.$className$Controller.onPageLoad(departureId, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).toInstance(new FakeNavigator(onwardRoute, mockFrontendAppConfig)))

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {

      checkCancellationStatus()

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(GET,  $className;format="decap"$Route)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"        -> form,
        "lrn"         -> LocalReferenceNumber(""),
        "departureId" -> departureId,
        "radios"      -> Radios.yesNo(form("value"))
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      checkCancellationStatus()

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(departureId, eoriNumber).set($className$Page(departureId), true).success.value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET,  $className;format="decap"$Route)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      templateCaptor.getValue mustEqual template

    }

    "must redirect to the next page when valid data is submitted and user selects Yes" in {

      checkCancellationStatus()

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST,  $className;format="decap"$Route)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must redirect to the next page when valid data is submitted and user selects No" in {

      checkCancellationStatus()
      when(mockSessionRepository.set(any())) thenReturn Future.successful(false)

      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST,  $className;format="decap"$Route)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(POST,  $className;format="decap"$Route).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject] = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "lrn"         -> LocalReferenceNumber(""),
        "departureId" -> departureId,
        "radios" -> Radios.yesNo(form("value"))
      )
      println ("jsoCaptor.getvalue = " + jsonCaptor.getValue)

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }

  }
}
