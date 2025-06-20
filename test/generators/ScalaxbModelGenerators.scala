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

import generated._
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
        transitOperation            <- arbitrary[TransitOperationType05]
        invalidation                <- arbitrary[InvalidationType02]
        customsOfficeOfDeparture    <- arbitrary[CustomsOfficeOfDepartureType03]
        holderOfTheTransitProcedure <- arbitrary[HolderOfTheTransitProcedureType02]
      } yield CC014CType(
        messageSequence1 = messageSequence1,
        TransitOperation = transitOperation,
        Invalidation = invalidation,
        CustomsOfficeOfDeparture = customsOfficeOfDeparture,
        HolderOfTheTransitProcedure = holderOfTheTransitProcedure,
        attributes = Map.empty
      )
    }

  implicit lazy val arbitraryConsignmentItemType03: Arbitrary[CUSTOM_ConsignmentItemType03] =
    Arbitrary {
      for {
        goodsItemNumber            <- arbitrary[BigInt]
        declarationGoodsItemNumber <- arbitrary[BigInt]
        commodity                  <- arbitrary[CUSTOM_CommodityType08]
      } yield CUSTOM_ConsignmentItemType03(
        goodsItemNumber = goodsItemNumber,
        declarationGoodsItemNumber = declarationGoodsItemNumber,
        declarationType = None,
        countryOfDispatch = None,
        countryOfDestination = None,
        referenceNumberUCR = None,
        Consignee = None,
        AdditionalSupplyChainActor = Nil,
        Commodity = commodity,
        Packaging = Nil,
        PreviousDocument = Nil,
        SupportingDocument = Nil,
        TransportDocument = Nil,
        AdditionalReference = Nil,
        AdditionalInformation = Nil,
        TransportCharges = None
      )
    }

  implicit lazy val arbitraryConsignmentItemType04: Arbitrary[CUSTOM_ConsignmentItemType04] =
    Arbitrary {
      for {
        goodsItemNumber            <- arbitrary[BigInt]
        declarationGoodsItemNumber <- arbitrary[BigInt]
        commodity                  <- arbitrary[CUSTOM_CommodityType08]
      } yield CUSTOM_ConsignmentItemType04(
        goodsItemNumber = goodsItemNumber,
        declarationGoodsItemNumber = declarationGoodsItemNumber,
        declarationType = None,
        countryOfDestination = None,
        Consignee = None,
        Commodity = commodity,
        Packaging = Nil,
        PreviousDocument = Nil,
        SupportingDocument = Nil,
        TransportDocument = Nil,
        AdditionalReference = Nil,
        AdditionalInformation = Nil
      )
    }

  implicit lazy val arbitraryCommodityType08: Arbitrary[CUSTOM_CommodityType08] =
    Arbitrary {
      for {
        descriptionOfGoods <- nonEmptyString
      } yield CUSTOM_CommodityType08(
        descriptionOfGoods = descriptionOfGoods,
        cusCode = None,
        CommodityCode = None,
        DangerousGoods = Nil,
        GoodsMeasure = None
      )
    }

  implicit lazy val arbitraryHolderOfTheTransitProcedureType02: Arbitrary[HolderOfTheTransitProcedureType02] =
    Arbitrary {
      for {
        identificationNumber          <- Gen.option(nonEmptyString)
        tirHolderIdentificationNumber <- Gen.option(nonEmptyString)
        name                          <- Gen.option(nonEmptyString)
      } yield HolderOfTheTransitProcedureType02(
        identificationNumber = identificationNumber,
        TIRHolderIdentificationNumber = tirHolderIdentificationNumber,
        name = name,
        Address = None,
        ContactPerson = None
      )
    }

  implicit lazy val arbitraryHolderOfTheTransitProcedureType05: Arbitrary[HolderOfTheTransitProcedureType05] =
    Arbitrary {
      for {
        identificationNumber          <- Gen.option(nonEmptyString)
        tirHolderIdentificationNumber <- Gen.option(nonEmptyString)
        name                          <- Gen.option(nonEmptyString)
      } yield HolderOfTheTransitProcedureType05(
        identificationNumber = identificationNumber,
        TIRHolderIdentificationNumber = tirHolderIdentificationNumber,
        name = name,
        Address = None,
        ContactPerson = None
      )
    }

  implicit lazy val arbitraryHolderOfTheTransitProcedureType14: Arbitrary[HolderOfTheTransitProcedureType14] =
    Arbitrary {
      for {
        identificationNumber          <- Gen.option(nonEmptyString)
        tirHolderIdentificationNumber <- Gen.option(nonEmptyString)
        name                          <- Gen.option(nonEmptyString)
      } yield HolderOfTheTransitProcedureType14(
        identificationNumber = identificationNumber,
        TIRHolderIdentificationNumber = tirHolderIdentificationNumber,
        name = name,
        Address = None,
        ContactPerson = None
      )
    }

  implicit lazy val arbitraryHolderOfTheTransitProcedureType20: Arbitrary[HolderOfTheTransitProcedureType20] =
    Arbitrary {
      for {
        identificationNumber          <- Gen.option(nonEmptyString)
        tirHolderIdentificationNumber <- Gen.option(nonEmptyString)
        name                          <- Gen.option(nonEmptyString)
      } yield HolderOfTheTransitProcedureType20(
        identificationNumber = identificationNumber,
        TIRHolderIdentificationNumber = tirHolderIdentificationNumber,
        name = name,
        Address = None
      )
    }

  implicit lazy val arbitraryConsignmentType04: Arbitrary[CUSTOM_ConsignmentType04] =
    Arbitrary {
      for {
        containerIndicator <- arbitrary[Flag]
        grossMass          <- arbitrary[BigDecimal]
      } yield CUSTOM_ConsignmentType04(
        countryOfDispatch = None,
        countryOfDestination = None,
        containerIndicator = containerIndicator,
        inlandModeOfTransport = None,
        modeOfTransportAtTheBorder = None,
        grossMass = grossMass,
        referenceNumberUCR = None,
        Carrier = None,
        Consignor = None,
        Consignee = None,
        AdditionalSupplyChainActor = Nil,
        TransportEquipment = Nil,
        LocationOfGoods = None,
        DepartureTransportMeans = Nil,
        CountryOfRoutingOfConsignment = Nil,
        ActiveBorderTransportMeans = Nil,
        PlaceOfLoading = None,
        PlaceOfUnloading = None,
        PreviousDocument = Nil,
        SupportingDocument = Nil,
        TransportDocument = Nil,
        AdditionalReference = Nil,
        AdditionalInformation = Nil,
        TransportCharges = None,
        HouseConsignment = Nil
      )
    }

  implicit lazy val arbitraryConsignmentType20: Arbitrary[ConsignmentType20] =
    Arbitrary {
      for {
        grossMass <- arbitrary[BigDecimal]
      } yield ConsignmentType20(
        countryOfDispatch = None,
        countryOfDestination = None,
        containerIndicator = None,
        inlandModeOfTransport = None,
        modeOfTransportAtTheBorder = None,
        grossMass = grossMass,
        referenceNumberUCR = None,
        Carrier = None,
        Consignor = None,
        Consignee = None,
        AdditionalSupplyChainActor = Nil,
        TransportEquipment = Nil,
        LocationOfGoods = None,
        DepartureTransportMeans = Nil,
        CountryOfRoutingOfConsignment = Nil,
        ActiveBorderTransportMeans = Nil,
        PlaceOfLoading = None,
        PlaceOfUnloading = None,
        PreviousDocument = Nil,
        SupportingDocument = Nil,
        TransportDocument = Nil,
        AdditionalReference = Nil,
        AdditionalInformation = Nil,
        TransportCharges = None,
        HouseConsignment = Nil
      )
    }

  implicit lazy val arbitraryTraderAtDestinationType03: Arbitrary[TraderAtDestinationType03] =
    Arbitrary {
      for {
        identificationNumber <- nonEmptyString
      } yield TraderAtDestinationType03(
        identificationNumber = identificationNumber
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

  implicit lazy val arbitraryCustomsOfficeOfDepartureType03: Arbitrary[CustomsOfficeOfDepartureType03] =
    Arbitrary {
      for {
        referenceNumber <- nonEmptyString
      } yield CustomsOfficeOfDepartureType03(
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

  implicit lazy val arbitraryTransitOperationType05: Arbitrary[TransitOperationType05] =
    Arbitrary {
      for {
        lrn <- Gen.option(nonEmptyString)
        mrn <- Gen.option(nonEmptyString)
      } yield TransitOperationType05(
        LRN = lrn,
        MRN = mrn
      )
    }

  implicit lazy val arbitraryTransitOperationType06: Arbitrary[TransitOperationType06] =
    Arbitrary {
      for {
        lrn                       <- nonEmptyString
        declarationType           <- nonEmptyString
        additionalDeclarationType <- nonEmptyString
        security                  <- nonEmptyString
        reducedDatasetIndicator   <- arbitrary[Flag]
        bindingItinerary          <- arbitrary[Flag]
      } yield TransitOperationType06(
        LRN = lrn,
        declarationType = declarationType,
        additionalDeclarationType = additionalDeclarationType,
        TIRCarnetNumber = None,
        presentationOfTheGoodsDateAndTime = None,
        security = security,
        reducedDatasetIndicator = reducedDatasetIndicator,
        specificCircumstanceIndicator = None,
        communicationLanguageAtDeparture = None,
        bindingItinerary = bindingItinerary,
        limitDate = None
      )
    }

  implicit lazy val arbitraryTransitOperationType11: Arbitrary[TransitOperationType11] =
    Arbitrary {
      for {
        lrn                       <- nonEmptyString
        mrn                       <- nonEmptyString
        declarationAcceptanceDate <- arbitrary[XMLGregorianCalendar]
      } yield TransitOperationType11(
        LRN = lrn,
        MRN = mrn,
        declarationAcceptanceDate = declarationAcceptanceDate
      )
    }

  implicit lazy val arbitraryTransitOperationType12: Arbitrary[TransitOperationType12] =
    Arbitrary {
      for {
        lrn                       <- nonEmptyString
        mrn                       <- nonEmptyString
        declarationType           <- nonEmptyString
        additionalDeclarationType <- nonEmptyString
        declarationAcceptanceDate <- arbitrary[XMLGregorianCalendar]
        releaseDate               <- arbitrary[XMLGregorianCalendar]
        security                  <- nonEmptyString
        reducedDatasetIndicator   <- arbitrary[Flag]
        bindingItinerary          <- arbitrary[Flag]
      } yield TransitOperationType12(
        LRN = lrn,
        MRN = mrn,
        declarationType = declarationType,
        additionalDeclarationType = additionalDeclarationType,
        TIRCarnetNumber = None,
        declarationAcceptanceDate = declarationAcceptanceDate,
        releaseDate = releaseDate,
        security = security,
        reducedDatasetIndicator = reducedDatasetIndicator,
        specificCircumstanceIndicator = None,
        communicationLanguageAtDeparture = None,
        bindingItinerary = bindingItinerary
      )
    }

  implicit lazy val arbitraryTransitOperationType14: Arbitrary[TransitOperationType14] =
    Arbitrary {
      for {
        mrn                     <- nonEmptyString
        security                <- nonEmptyString
        reducedDatasetIndicator <- arbitrary[Flag]
      } yield TransitOperationType14(
        MRN = mrn,
        declarationType = None,
        declarationAcceptanceDate = None,
        security = security,
        reducedDatasetIndicator = reducedDatasetIndicator
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

  implicit lazy val arbitraryTransportChargesType: Arbitrary[TransportChargesType] =
    Arbitrary {
      for {
        methodOfPayment <- nonEmptyString
      } yield TransportChargesType(
        methodOfPayment = methodOfPayment
      )
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
