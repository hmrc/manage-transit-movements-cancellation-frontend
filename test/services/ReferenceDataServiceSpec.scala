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

package services

import base.SpecBase
import connectors.ReferenceDataConnector
import models.CustomsOffice
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataServiceSpec extends SpecBase {

  private val mockConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private val customsOffice = CustomsOffice("ID1", "NAME001", "GB", None)

  "ReferenceDataService" - {

    "getCustomsOfficeByCode should" - {
      "return a customsOffice" in {

        when(mockConnector.getCustomsOffice(any())(any(), any())).thenReturn(Future.successful(Right(customsOffice)))

        val service = new ReferenceDataServiceImpl(mockConnector)

        service.getCustomsOfficeByCode("GB00001").futureValue mustEqual customsOffice

        verify(mockConnector).getCustomsOffice(any())(any(), any())
      }
    }
  }
}
