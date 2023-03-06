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

package repositories

import config.FrontendAppConfig
import models.{DepartureId, EoriNumber, UserAnswers}
import org.mongodb.scala.model.Filters
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import utils.TimeMachine

import scala.concurrent.ExecutionContext.Implicits.global

class SessionRepositorySpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with BeforeAndAfterEach
    with GuiceOneAppPerSuite
    with OptionValues
    with DefaultPlayMongoRepositorySupport[UserAnswers] {

  private val config: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]
  private val timeMachine: TimeMachine  = app.injector.instanceOf[TimeMachine]

  override protected val repository = new SessionRepository(mongoComponent, config, timeMachine)

  private lazy val userAnswers1 = UserAnswers(DepartureId(0), EoriNumber("EoriNumber1"), Json.obj(), timeMachine.now())
  private lazy val userAnswers2 = UserAnswers(DepartureId(1), EoriNumber("EoriNumber2"), Json.obj(), timeMachine.now())
  private lazy val userAnswers3 = UserAnswers(DepartureId(2), EoriNumber("EoriNumber3"), Json.obj(), timeMachine.now())

  override def beforeEach(): Unit = {
    super.beforeEach()
    insert(userAnswers1).futureValue
    insert(userAnswers2).futureValue
  }

  private def findOne(userAnswers: UserAnswers): Option[UserAnswers] =
    find(Filters.eq("_id", userAnswers.id.index)).futureValue.headOption

  "SessionRepository" - {

    "get" - {

      "must return UserAnswers when given a DepartureId" in {

        val result = repository.get(userAnswers1.id).futureValue

        result.value.id mustBe userAnswers1.id
        result.value.eoriNumber mustBe userAnswers1.eoriNumber
        result.value.data mustBe userAnswers1.data
      }

      "must return None when no UserAnswers match DepartureId" in {

        val result = repository.get(userAnswers3.id).futureValue

        result mustBe None
      }
    }

    "set" - {

      "must create new document when given valid UserAnswers" in {

        findOne(userAnswers3) must not be defined

        val setResult = repository.set(userAnswers3).futureValue

        setResult mustBe true

        val getResult = findOne(userAnswers3).get

        getResult.id mustBe userAnswers3.id
        getResult.eoriNumber mustBe userAnswers3.eoriNumber
        getResult.data mustBe userAnswers3.data
      }

      "must update document when it already exists" in {

        val firstGet = findOne(userAnswers1).get

        val setResult = repository.set(userAnswers1.copy(data = Json.obj("foo" -> "bar"))).futureValue

        setResult mustBe true

        val secondGet = findOne(userAnswers1).get

        firstGet.id mustBe secondGet.id
        firstGet.eoriNumber mustBe secondGet.eoriNumber
        firstGet.data mustNot equal(secondGet.data)
        firstGet.lastUpdated isBefore secondGet.lastUpdated mustBe true
      }
    }
  }
}
