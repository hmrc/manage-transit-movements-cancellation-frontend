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

package views

import org.jsoup.nodes.Document
import play.api.libs.json.Json

class SessionExpiredViewSpec extends SingleViewSpec("session-expired.njk", hasSignOutLink = false) {

  "must have a Sign In button with the correct href" in {
    val doc: Document = renderDocument(
      Json.obj("signInUrl" -> "/manage-transit-movements/what-do-you-want-to-do")
    ).futureValue

    assertPageHasButtonWithHref(doc, "submit", "/manage-transit-movements/what-do-you-want-to-do")
  }

}
