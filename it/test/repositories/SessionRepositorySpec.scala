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
import itbase.ItSpecBase
import models.{EoriNumber, LocalReferenceNumber, SensitiveFormats, UserAnswers}
import org.mongodb.scala.model.Filters
import play.api.libs.json.Json
import services.DateTimeService
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.concurrent.ExecutionContext.Implicits.global

class SessionRepositorySpec extends ItSpecBase with DefaultPlayMongoRepositorySupport[UserAnswers] {

  private val config: FrontendAppConfig        = app.injector.instanceOf[FrontendAppConfig]
  private val dateTimeService: DateTimeService = app.injector.instanceOf[DateTimeService]

  implicit private val sensitiveFormats: SensitiveFormats = app.injector.instanceOf[SensitiveFormats]

  override protected val repository = new SessionRepository(mongoComponent, config, dateTimeService)

  private lazy val userAnswers1 = UserAnswers("AB123", EoriNumber("EoriNumber1"), LocalReferenceNumber("AB123"), Json.obj(), dateTimeService.currentInstant)
  private lazy val userAnswers2 = UserAnswers("CD123", EoriNumber("EoriNumber2"), LocalReferenceNumber("AB123"), Json.obj(), dateTimeService.currentInstant)
  private lazy val userAnswers3 = UserAnswers("EF123", EoriNumber("EoriNumber3"), LocalReferenceNumber("AB123"), Json.obj(), dateTimeService.currentInstant)

  override def beforeEach(): Unit = {
    super.beforeEach()
    insert(userAnswers1).futureValue
    insert(userAnswers2).futureValue
  }

  private def findOne(userAnswers: UserAnswers): Option[UserAnswers] =
    find(Filters.eq("_id", userAnswers.id)).futureValue.headOption

  "SessionRepository" - {

    "get" - {

      "must return UserAnswers when given a DepartureId" in {

        val result = repository.get(userAnswers1.id).futureValue

        result.value.id `mustBe` userAnswers1.id
        result.value.eoriNumber `mustBe` userAnswers1.eoriNumber
        result.value.data `mustBe` userAnswers1.data
      }

      "must return None when no UserAnswers match DepartureId" in {

        val result = repository.get(userAnswers3.id).futureValue

        result `mustBe` None
      }
    }

    "set" - {

      "must create new document when given valid UserAnswers" in {

        findOne(userAnswers3) must not be defined

        val setResult = repository.set(userAnswers3).futureValue

        setResult `mustBe` true

        val getResult = findOne(userAnswers3).get

        getResult.id mustBe userAnswers3.id
        getResult.eoriNumber mustBe userAnswers3.eoriNumber
        getResult.data mustBe userAnswers3.data
      }

      "must update document when it already exists" in {

        val firstGet = findOne(userAnswers1).get

        val setResult = repository.set(userAnswers1.copy(data = Json.obj("foo" -> "bar"))).futureValue

        setResult `mustBe` true

        val secondGet = findOne(userAnswers1).get

        firstGet.id mustBe secondGet.id
        firstGet.eoriNumber mustBe secondGet.eoriNumber
        firstGet.data mustNot equal(secondGet.data)
        firstGet.lastUpdated `isBefore` secondGet.lastUpdated mustBe true
      }
    }

    "remove" - {

      "must remove document when given a valid LocalReferenceNumber and EoriNumber" in {

        repository.get(userAnswers1.id).futureValue `mustBe` defined

        repository.remove(userAnswers1.id, userAnswers1.eoriNumber).futureValue

        repository.get(userAnswers1.id).futureValue `must` not `be` defined
      }
    }
  }
}
