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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock.*
import generated.*
import itbase.{ItSpecBase, WireMockServerHandler}
import models.{DepartureMessages, MessageMetaData, MessageStatus, MessageType}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import scalaxb.XMLCalendar

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.xml.Node

class DepartureMovementConnectorSpec extends ItSpecBase with WireMockServerHandler {

  private lazy val connector: DepartureMovementConnector = app.injector.instanceOf[DepartureMovementConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.common-transit-convention-traders.port" -> server.port())

  "DeparturesMovementConnector" - {

    "getMessageMetaData" - {

      "must return Messages" in {

        val responseJson: JsValue = Json.parse("""
            |{
            |  "_links": {
            |    "self": {
            |      "href": "/customs/transits/movements/departures/6365135ba5e821ee/messages"
            |    },
            |    "departure": {
            |      "href": "/customs/transits/movements/departures/6365135ba5e821ee"
            |    }
            |  },
            |  "messages": [
            |    {
            |      "_links": {
            |        "self": {
            |          "href": "/customs/transits/movements/departures/6365135ba5e821ee/messages/634982098f02f00b"
            |        },
            |        "departure": {
            |          "href": "/customs/transits/movements/departures/6365135ba5e821ee"
            |        }
            |      },
            |      "id": "634982098f02f00b",
            |      "departureId": "6365135ba5e821ee",
            |      "received": "2022-11-11T15:32:51.459Z",
            |      "type": "IE015",
            |      "status": "Success"
            |    },
            |    {
            |      "_links": {
            |        "self": {
            |          "href": "/customs/transits/movements/departures/6365135ba5e821ee/messages/634982098f02f00a"
            |        },
            |        "departure": {
            |          "href": "/customs/transits/movements/departures/6365135ba5e821ee"
            |        }
            |      },
            |      "id": "634982098f02f00a",
            |      "departureId": "6365135ba5e821ee",
            |      "received": "2022-11-10T15:32:51.459Z",
            |      "type": "IE028",
            |      "status": "Success"
            |    }
            |  ]
            |}
            |""".stripMargin)

        val expectedResult = DepartureMessages(
          List(
            MessageMetaData(
              "634982098f02f00b",
              MessageType.DepartureNotification,
              LocalDateTime.parse("2022-11-11T15:32:51.459Z", DateTimeFormatter.ISO_DATE_TIME),
              MessageStatus.Success
            ),
            MessageMetaData(
              "634982098f02f00a",
              MessageType.AllocatedMRN,
              LocalDateTime.parse("2022-11-10T15:32:51.459Z", DateTimeFormatter.ISO_DATE_TIME),
              MessageStatus.Success
            )
          )
        )

        server.stubFor(
          get(urlEqualTo(s"/movements/departures/$departureId/messages"))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(responseJson.toString()))
        )

        connector.getMessageMetaData(departureId).futureValue mustBe expectedResult
      }
    }

    "getMessage" - {
      val messageId = "messageId"
      val url       = s"/movements/departures/$departureId/messages/$messageId/body"

      "must return message" - {
        "when IE015" in {
          val xml: Node =
            <ncts:CC015C xmlns:ncts="http://ncts.dgtaxud.ec">
              <messageSender>message sender</messageSender>
              <messageRecipient>NTA.GB</messageRecipient>
              <preparationDateAndTime>2022-01-22T07:43:36</preparationDateAndTime>
              <messageIdentification>messageId</messageIdentification>
              <messageType>CC015C</messageType>
              <TransitOperation>
                <LRN>HnVr</LRN>
                <declarationType>Pbg</declarationType>
                <additionalDeclarationType>A</additionalDeclarationType>
                <security>1</security>
                <reducedDatasetIndicator>1</reducedDatasetIndicator>
                <bindingItinerary>0</bindingItinerary>
              </TransitOperation>
              <CustomsOfficeOfDeparture>
                <referenceNumber>GB000060</referenceNumber>
              </CustomsOfficeOfDeparture>
              <CustomsOfficeOfDestinationDeclared>
                <referenceNumber>XI000142</referenceNumber>
              </CustomsOfficeOfDestinationDeclared>
              <HolderOfTheTransitProcedure>
                <identificationNumber>idNumber</identificationNumber>
              </HolderOfTheTransitProcedure>
              <Consignment>
                <grossMass>6430669292.48125</grossMass>
              </Consignment>
            </ncts:CC015C>

          val expectedResult = CC015CType(
            messageSequence1 = MESSAGESequence(
              messageSender = "message sender",
              messageRecipient = "NTA.GB",
              preparationDateAndTime = XMLCalendar("2022-01-22T07:43:36"),
              messageIdentification = "messageId",
              messageType = CC015C,
              correlationIdentifier = None
            ),
            TransitOperation = TransitOperationType06(
              LRN = "HnVr",
              declarationType = "Pbg",
              additionalDeclarationType = "A",
              security = "1",
              reducedDatasetIndicator = Number1,
              bindingItinerary = Number0
            ),
            CustomsOfficeOfDeparture = CustomsOfficeOfDepartureType03(
              referenceNumber = "GB000060"
            ),
            CustomsOfficeOfDestinationDeclared = CustomsOfficeOfDestinationDeclaredType01(
              referenceNumber = "XI000142"
            ),
            HolderOfTheTransitProcedure = HolderOfTheTransitProcedureType14(
              identificationNumber = Some("idNumber")
            ),
            Consignment = ConsignmentType20(
              grossMass = 6430669292.48125
            )
          )

          server.stubFor(
            get(urlEqualTo(url))
              .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+xml"))
              .willReturn(ok(xml.toString()))
          )

          val result = connector.getMessage[CC015CType](departureId, messageId).futureValue

          result mustBe expectedResult
        }

        "when IE028" in {
          val xml: Node =
            <ncts:CC028C xmlns:ncts="http://ncts.dgtaxud.ec">
              <messageSender>message sender</messageSender>
              <messageRecipient>NTA.GB</messageRecipient>
              <preparationDateAndTime>2022-12-25T07:36:28</preparationDateAndTime>
              <messageIdentification>messageId</messageIdentification>
              <messageType>CC028C</messageType>
              <TransitOperation>
                <LRN>LRN123</LRN>
                <MRN>3817-MRNAllocated2</MRN>
                <declarationAcceptanceDate>2022-12-25</declarationAcceptanceDate>
              </TransitOperation>
              <CustomsOfficeOfDeparture>
                <referenceNumber>GB000060</referenceNumber>
              </CustomsOfficeOfDeparture>
              <HolderOfTheTransitProcedure>
                <identificationNumber>Fzsisks</identificationNumber>
              </HolderOfTheTransitProcedure>
            </ncts:CC028C>

          val expectedResult = CC028CType(
            messageSequence1 = MESSAGESequence(
              messageSender = "message sender",
              messageRecipient = "NTA.GB",
              preparationDateAndTime = XMLCalendar("2022-12-25T07:36:28"),
              messageIdentification = "messageId",
              messageType = CC028C,
              correlationIdentifier = None
            ),
            TransitOperation = TransitOperationType11(
              LRN = "LRN123",
              MRN = "3817-MRNAllocated2",
              declarationAcceptanceDate = XMLCalendar("2022-12-25")
            ),
            CustomsOfficeOfDeparture = CustomsOfficeOfDepartureType03(
              referenceNumber = "GB000060"
            ),
            HolderOfTheTransitProcedure = HolderOfTheTransitProcedureType20(
              identificationNumber = Some("Fzsisks")
            )
          )

          server.stubFor(
            get(urlEqualTo(url))
              .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+xml"))
              .willReturn(ok(xml.toString()))
          )

          val result = connector.getMessage[CC028CType](departureId, messageId).futureValue

          result mustBe expectedResult
        }
      }
    }
  }
}
