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

package controllers.actions

import com.google.inject.Singleton
import models.requests.{IdentifierRequest, OptionalDataRequest}
import play.api.mvc.ActionTransformer
import repositories.SessionRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRetrievalActionProviderImpl @Inject() (sessionRepository: SessionRepository)(implicit ec: ExecutionContext) extends DataRetrievalActionProvider {

  def apply(departureId: String): ActionTransformer[IdentifierRequest, OptionalDataRequest] =
    new DataRetrievalAction(departureId, sessionRepository)
}

trait DataRetrievalActionProvider {

  def apply(departureId: String): ActionTransformer[IdentifierRequest, OptionalDataRequest]
}

class DataRetrievalAction(
  departureId: String,
  sessionRepository: SessionRepository
)(implicit val executionContext: ExecutionContext)
    extends ActionTransformer[IdentifierRequest, OptionalDataRequest] {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] =
    sessionRepository.get(departureId).map {
      userAnswers =>
        OptionalDataRequest(request.request, request.eoriNumber, userAnswers)
    }
}
