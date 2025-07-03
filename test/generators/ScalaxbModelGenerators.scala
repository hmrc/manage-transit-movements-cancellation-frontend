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

package generators

import generated.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import scalaxb.XMLCalendar

import java.time.LocalDateTime
import javax.xml.datatype.XMLGregorianCalendar

trait ScalaxbModelGenerators {
  self: Generators =>

  implicit lazy val arbitraryCC014CType: Arbitrary[CC014CType] =
    Arbitrary {
      for {
        messageSequence1            <- arbitrary[MESSAGESequence]
        transitOperation            <- arbitrary[TransitOperationType56]
        invalidation                <- arbitrary[InvalidationType02]
        customsOfficeOfDeparture    <- arbitrary[CustomsOfficeOfDepartureType05]
        holderOfTheTransitProcedure <- arbitrary[HolderOfTheTransitProcedureType23]
      } yield CC014CType(
        messageSequence1 = messageSequence1,
        TransitOperation = transitOperation,
        Invalidation = invalidation,
        CustomsOfficeOfDeparture = customsOfficeOfDeparture,
        HolderOfTheTransitProcedure = holderOfTheTransitProcedure,
        attributes = Map.empty
      )
    }

  implicit lazy val arbitraryHolderOfTheTransitProcedureType23: Arbitrary[HolderOfTheTransitProcedureType23] =
    Arbitrary {
      for {
        identificationNumber          <- Gen.option(nonEmptyString)
        tirHolderIdentificationNumber <- Gen.option(nonEmptyString)
        name                          <- Gen.option(nonEmptyString)
      } yield HolderOfTheTransitProcedureType23(
        identificationNumber = identificationNumber,
        TIRHolderIdentificationNumber = tirHolderIdentificationNumber,
        name = name,
        Address = None
      )
    }

  implicit lazy val arbitraryCustomsOfficeOfDestinationDeclaredType01: Arbitrary[CustomsOfficeOfDestinationDeclaredType01] =
    Arbitrary {
      for {
        referenceNumber <- nonEmptyString
      } yield CustomsOfficeOfDestinationDeclaredType01(
        referenceNumber = referenceNumber
      )
    }

  implicit lazy val arbitraryCustomsOfficeOfDepartureType05: Arbitrary[CustomsOfficeOfDepartureType05] =
    Arbitrary {
      for {
        referenceNumber <- nonEmptyString
      } yield CustomsOfficeOfDepartureType05(
        referenceNumber = referenceNumber
      )
    }

  implicit lazy val arbitraryCustomsOfficeOfDestinationActualType03: Arbitrary[CustomsOfficeOfDestinationActualType03] =
    Arbitrary {
      for {
        referenceNumber <- nonEmptyString
      } yield CustomsOfficeOfDestinationActualType03(
        referenceNumber = referenceNumber
      )
    }

  implicit lazy val arbitraryTransitOperationType56: Arbitrary[TransitOperationType56] =
    Arbitrary {
      for {
        lrn <- Gen.option(nonEmptyString)
        mrn <- Gen.option(nonEmptyString)
      } yield TransitOperationType56(
        LRN = lrn,
        MRN = mrn
      )
    }

  implicit lazy val arbitraryFlag: Arbitrary[Flag] =
    Arbitrary {
      for {
        bool <- arbitrary[Boolean]
      } yield if (bool) Number1 else Number0
    }

  implicit lazy val arbitraryMESSAGESequence: Arbitrary[MESSAGESequence] =
    Arbitrary {
      for {
        messageSender          <- nonEmptyString
        messageRecipient       <- nonEmptyString
        preparationDateAndTime <- arbitrary[XMLGregorianCalendar]
        messageIdentification  <- nonEmptyString
        messageType            <- arbitrary[MessageTypes]
        correlationIdentifier  <- Gen.option(nonEmptyString)
      } yield MESSAGESequence(
        messageSender = messageSender,
        messageRecipient = messageRecipient,
        preparationDateAndTime = preparationDateAndTime,
        messageIdentification = messageIdentification,
        messageType = messageType,
        correlationIdentifier = correlationIdentifier
      )
    }

  implicit lazy val arbitraryMessageTypes: Arbitrary[MessageTypes] =
    Arbitrary {
      Gen.oneOf(MessageTypes.values)
    }

  implicit lazy val arbitraryXMLGregorianCalendar: Arbitrary[XMLGregorianCalendar] =
    Arbitrary {
      XMLCalendar(LocalDateTime.now().toString)
    }

  implicit lazy val arbitraryInvalidationType02: Arbitrary[InvalidationType02] =
    Arbitrary {
      for {
        requestDateAndTime  <- Gen.option(arbitrary[XMLGregorianCalendar])
        decisionDateAndTime <- Gen.option(arbitrary[XMLGregorianCalendar])
        decision            <- Gen.option(arbitrary[Flag])
        initiatedByCustoms  <- arbitrary[Flag]
        justification       <- Gen.option(nonEmptyString)
      } yield InvalidationType02(
        requestDateAndTime = requestDateAndTime,
        decisionDateAndTime = decisionDateAndTime,
        decision = decision,
        initiatedByCustoms = initiatedByCustoms,
        justification = justification
      )
    }
}
