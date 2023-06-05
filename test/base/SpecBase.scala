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
import models.messages._
import models.{EoriNumber, UserAnswers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues, TryValues}
import pages.QuestionPage
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

import java.time.{Instant, LocalDateTime}

trait SpecBase
    extends AnyFreeSpec
    with Matchers
    with OptionValues
    with TryValues
    with ScalaFutures
    with IntegrationPatience
    with BeforeAndAfterEach
    with MockApplicationBuilder {

  val departureId: String = "AB123"

  val eoriNumber: EoriNumber = EoriNumber("eoriNumber")

  def emptyUserAnswers: UserAnswers = UserAnswers(departureId, eoriNumber, Json.obj(), Instant.now())

  val ie014Data: IE014Data = IE014Data(
    IE014MessageData(
      "sender",
      "recipient",
      LocalDateTime.now(),
      TransitOperation(Some("MRNCD3232"), Some("LRNAB123")),
      CustomsOfficeOfDeparture("AB123"),
      HolderOfTheTransitProcedure = HolderOfTheTransitProcedure("123"),
      Invalidation(justification = "Hello this is a rejection.")
    )
  )
  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  implicit class RichUserAnswers(userAnswers: UserAnswers) {

    def getValue[T](page: QuestionPage[T])(implicit rds: Reads[T]): T =
      userAnswers.get(page).value

    def setValue[T](page: QuestionPage[T], value: T)(implicit wts: Writes[T]): UserAnswers =
      userAnswers.set(page, value).success.value

    def removeValue(page: QuestionPage[_]): UserAnswers =
      userAnswers.remove(page).success.value
  }

}
