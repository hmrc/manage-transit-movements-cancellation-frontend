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

import generated.*
import models.IE015.*

import scala.language.implicitConversions
import scala.util.Try
import scala.xml.Node

// Since we only need a couple of fields from the IE015,
// we have created a custom case class to only retrieve the fields we need
// rather than using scalaxb and storing the entire message in memory

case class IE015(
  transitOperation: TransitOperation,
  customsOfficeOfDeparture: CustomsOfficeOfDeparture,
  holderOfTheTransitProcedure: HolderOfTheTransitProcedure
)

object IE015 {

  implicit def reads(node: Node): IE015 = {
    val transitOperation            = TransitOperation.reads((node \ "TransitOperation").head)
    val customsOfficeOfDeparture    = CustomsOfficeOfDeparture.reads((node \ "CustomsOfficeOfDeparture").head)
    val holderOfTheTransitProcedure = HolderOfTheTransitProcedure.reads((node \ "HolderOfTheTransitProcedure").head)
    new IE015(transitOperation, customsOfficeOfDeparture, holderOfTheTransitProcedure)
  }

  case class TransitOperation(lrn: String)

  object TransitOperation {

    def reads(node: Node): TransitOperation = {
      val lrn = (node \ "LRN").text
      new TransitOperation(lrn)
    }
  }

  case class CustomsOfficeOfDeparture(referenceNumber: String) {

    def toScalaxb: CustomsOfficeOfDepartureType03 =
      CustomsOfficeOfDepartureType03(
        referenceNumber = referenceNumber
      )
  }

  object CustomsOfficeOfDeparture {

    def reads(node: Node): CustomsOfficeOfDeparture = {
      val referenceNumber = (node \ "referenceNumber").text
      new CustomsOfficeOfDeparture(referenceNumber)
    }
  }

  case class HolderOfTheTransitProcedure(
    identificationNumber: Option[String],
    tirHolderIdentificationNumber: Option[String],
    name: Option[String],
    address: Option[Address],
    contactPerson: Option[ContactPerson]
  ) {

    def toScalaxb: HolderOfTheTransitProcedureType02 =
      HolderOfTheTransitProcedureType02(
        identificationNumber = identificationNumber,
        TIRHolderIdentificationNumber = tirHolderIdentificationNumber,
        name = name,
        Address = address.map(_.toScalaxb),
        ContactPerson = contactPerson.map(_.toScalaxb)
      )
  }

  object HolderOfTheTransitProcedure {

    def reads(node: Node): HolderOfTheTransitProcedure = {
      val identificationNumber          = (node \ "identificationNumber").headOption.map(_.text)
      val TIRHolderIdentificationNumber = (node \ "TIRHolderIdentificationNumber").headOption.map(_.text)
      val name                          = (node \ "name").headOption.map(_.text)
      val address                       = Try(Address.reads((node \ "Address").head)).toOption
      val contactPerson                 = Try(ContactPerson.reads((node \ "ContactPerson").head)).toOption
      new HolderOfTheTransitProcedure(identificationNumber, TIRHolderIdentificationNumber, name, address, contactPerson)
    }
  }

  case class Address(
    streetAndNumber: String,
    postcode: Option[String],
    city: String,
    country: String
  ) {

    def toScalaxb: AddressType15 =
      AddressType15(
        streetAndNumber = streetAndNumber,
        postcode = postcode,
        city = city,
        country = country
      )
  }

  object Address {

    def reads(node: Node): Address = {
      val streetAndNumber = (node \ "streetAndNumber").text
      val postcode        = (node \ "postcode").headOption.map(_.text)
      val city            = (node \ "city").text
      val country         = (node \ "country").text
      new Address(streetAndNumber, postcode, city, country)
    }
  }

  case class ContactPerson(
    name: String,
    phoneNumber: String,
    eMailAddress: Option[String]
  ) {

    def toScalaxb: ContactPersonType04 =
      ContactPersonType04(
        name = name,
        phoneNumber = phoneNumber,
        eMailAddress = eMailAddress
      )
  }

  object ContactPerson {

    def reads(node: Node): ContactPerson = {
      val name         = (node \ "name").text
      val phoneNumber  = (node \ "phoneNumber").text
      val eMailAddress = (node \ "eMailAddress").headOption.map(_.text)
      new ContactPerson(name, phoneNumber, eMailAddress)
    }
  }
}
