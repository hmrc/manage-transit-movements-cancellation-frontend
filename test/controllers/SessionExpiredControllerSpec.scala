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
import play.api.test.FakeRequest
import play.api.test.Helpers._

class SessionExpiredControllerSpec extends SpecBase {

  "Session Expired Controller" - {

    "must return OK and the correct view for a GET" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, routes.SessionExpiredController.onPageLoad().url)

      val result = route(app, request).value

      status(result) mustEqual OK
    }

    "must redirect to a new page for a POST" in {
      val request =
        FakeRequest(POST, routes.SessionExpiredController.onSubmit().url)
          .withFormUrlEncodedBody()

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        s"http://localhost:9949/auth-login-stub/gg-sign-in?continue=http%3A%2F%2Flocalhost%3A9485%2Fmanage-transit-movements%2Fview-departure-declarations"
    }
  }
}
