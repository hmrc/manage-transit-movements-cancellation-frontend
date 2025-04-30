/*
 * Copyright 2025 HM Revenue & Customs
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

package models

import base.SpecBase
import models.IE028.*

class IE028Spec extends SpecBase {

  "TransitOperation" - {
    "must deserialise" in {
      val xml = <TransitOperation>
        <LRN>GB8dd47c1f2f001h2ce671</LRN>
        <MRN>25GB0002466YBYPKJ0</MRN>
        <declarationAcceptanceDate>2025-01-30</declarationAcceptanceDate>
      </TransitOperation>

      val result = TransitOperation.reads(xml)

      result mustEqual TransitOperation(
        mrn = "25GB0002466YBYPKJ0"
      )
    }
  }

  "IE028" - {
    "must deserialise" in {
      val xml = <ncts:CC028C PhaseID="NCTS5.0" xmlns:ncts="http://ncts.dgtaxud.ec">
        <messageSender>token</messageSender>
        <messageRecipient>FdOcminxBxSLGm1rRUn0q96S1</messageRecipient>
        <preparationDateAndTime>{{currentDateTime}}</preparationDateAndTime>
        <messageIdentification>6Onxa3En</messageIdentification>
        <messageType>CC028C</messageType>
        <correlationIdentifier>co-id-1</correlationIdentifier>
        <TransitOperation>
          <LRN>GB8dd47c1f2f001h2ce671</LRN>
          <MRN>25GB0002466YBYPKJ0</MRN>
          <declarationAcceptanceDate>2025-01-30</declarationAcceptanceDate>
        </TransitOperation>
        <CustomsOfficeOfDeparture>
          <referenceNumber>GB000246</referenceNumber>
        </CustomsOfficeOfDeparture>
        <HolderOfTheTransitProcedure>
          <identificationNumber>GB201909015000</identificationNumber>
        </HolderOfTheTransitProcedure>
      </ncts:CC028C>

      val result = IE028.reads(xml)

      result mustEqual IE028(
        TransitOperation(
          mrn = "25GB0002466YBYPKJ0"
        )
      )
    }
  }
}
