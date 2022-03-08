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

package controllers.actions

import com.google.inject.Singleton
import models.DepartureId
import models.requests.{AuthorisedRequest, OptionalDataRequest}
import play.api.mvc.ActionTransformer
import repositories.SessionRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRetrievalActionProviderImpl @Inject() (sessionRepository: SessionRepository, ec: ExecutionContext) extends DataRetrievalActionProvider {

  def apply(departureId: DepartureId): ActionTransformer[AuthorisedRequest, OptionalDataRequest] =
    new DataRetrievalAction(departureId, ec, sessionRepository)
}

trait DataRetrievalActionProvider {

  def apply(departureId: DepartureId): ActionTransformer[AuthorisedRequest, OptionalDataRequest]
}

class DataRetrievalAction(
  departureId: DepartureId,
  implicit protected val executionContext: ExecutionContext,
  sessionRepository: SessionRepository
) extends ActionTransformer[AuthorisedRequest, OptionalDataRequest] {

  override protected def transform[A](request: AuthorisedRequest[A]): Future[OptionalDataRequest[A]] =
    sessionRepository.get(departureId, request.eoriNumber).map {
      userAnswers =>
        OptionalDataRequest(request.request, request.eoriNumber, request.lrn, userAnswers)
    }
}
