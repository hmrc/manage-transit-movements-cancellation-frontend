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

package repositories

import models.{DepartureId, EoriNumber, UserAnswers}
import play.api.Configuration
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONSerializationPack
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.play.json.collection.Helpers.idWrites
import reactivemongo.play.json.collection.JSONCollection

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DefaultSessionRepository @Inject() (
  mongo: ReactiveMongoApi,
  config: Configuration
)(implicit ec: ExecutionContext)
    extends SessionRepository {

  private val collectionName: String = "user-answers"

  private val cacheTtl = config.get[Int]("mongodb.timeToLiveInSeconds")

  private def collection: Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](collectionName))

  private val lastUpdatedIndex = Index.apply(BSONSerializationPack)(
    key = Seq("lastUpdated" -> IndexType.Ascending),
    name = Some("user-answers-last-updated-index"),
    unique = true,
    background = false,
    dropDups = false,
    sparse = false,
    version = None,
    partialFilter = None,
    options = BSONDocument("expireAfterSeconds" -> cacheTtl),
    expireAfterSeconds = Some(cacheTtl),
    storageEngine = None,
    weights = None,
    defaultLanguage = None,
    languageOverride = None,
    textIndexVersion = None,
    sphereIndexVersion = None,
    bits = None,
    min = None,
    max = None,
    bucketSize = None,
    collation = None,
    wildcardProjection = None
  )

  val started: Future[Unit] =
    collection
      .flatMap {
        _.indexesManager.ensure(lastUpdatedIndex)
      }
      .map(
        _ => ()
      )

  override def get(departureId: DepartureId, eoriNumber: EoriNumber): Future[Option[UserAnswers]] =
    collection.flatMap(_.find(Json.obj("_id" -> departureId), None).one[UserAnswers])

  override def set(userAnswers: UserAnswers): Future[Boolean] = {

    val selector = Json.obj(
      "_id" -> userAnswers.id
    )

    val modifier = Json.obj(
      "$set" -> (userAnswers copy (lastUpdated = LocalDateTime.now))
    )

    collection.flatMap {
      _.update(ordered = false)
        .one(selector, modifier, upsert = true)
        .map {
          lastError =>
            lastError.ok
        }
    }
  }
}

trait SessionRepository {

  val started: Future[Unit]

  def get(departureId: DepartureId, eoriNumber: EoriNumber): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]
}
