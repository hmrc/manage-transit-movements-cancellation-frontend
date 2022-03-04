package controllers

import config.FrontendAppConfig
import forms.$className$FormProvider
import models.{$className$, LocalReferenceNumber, NormalMode}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.$className$Page
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport
import base.{MockNunjucksRendererApp, SpecBase}
import matchers.JsonMatchers

import scala.concurrent.Future

class $className$ControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  lazy val $className;format="decap"$Route = routes.$className$Controller.onPageLoad(departureId, NormalMode).url

  private val formProvider = new $className$FormProvider()
  private val form = formProvider()
  private val template = "$className;format="decap"$.njk"
  private val mockFrontendAppConfig = mock[FrontendAppConfig]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).toInstance(new FakeNavigator(onwardRoute, mockFrontendAppConfig)))

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {
      checkCancellationStatus()

      dataRetrievalWithData(emptyUserAnswers)
      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val request = FakeRequest(GET, $className;format="decap"$Route)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> form,
        "mode"       -> NormalMode,
        "departureId"        -> departureId,
        "lrn"    -> LocalReferenceNumber(""),
        "checkboxes" -> $className$.checkboxes(form)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      checkCancellationStatus()

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val userAnswers = emptyUserAnswers.set($className$Page, $className$.values.toSet).success.value
      dataRetrievalWithData(userAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.fill($className$.values.toSet)

      val expectedJson = Json.obj(
        "form"       -> filledForm,
        "mode"       -> NormalMode,
        "departureId"        -> departureId,
        "lrn"    -> LocalReferenceNumber(""),
        "checkboxes" -> $className$.checkboxes(filledForm)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted" in {
      checkCancellationStatus()

      dataRetrievalWithData(emptyUserAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, $className;format="decap"$Route)
          .withFormUrlEncodedBody(("value[0]", $className$.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      checkCancellationStatus()

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request =  FakeRequest(POST, $className;format="decap"$Route).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> boundForm,
        "mode"       -> NormalMode,
        "departureId"        -> departureId,
        "lrn"    -> LocalReferenceNumber(""),
        "checkboxes" -> $className$.checkboxes(boundForm)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      checkCancellationStatus()

      dataRetrievalNoData()
      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      checkCancellationStatus()

      dataRetrievalNoData()
      val request = FakeRequest(POST, $className;format="decap"$Route).withFormUrlEncodedBody(("value[0]", $className$.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
