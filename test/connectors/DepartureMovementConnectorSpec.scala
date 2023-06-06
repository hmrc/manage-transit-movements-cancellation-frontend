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

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{containing, get, okJson, urlEqualTo}
import generators.Generators
import helper.WireMockServerHandler
import models.messages._
import models.{DepartureMessageMetaData, DepartureMessageType, DepartureMessages, LocalReferenceNumber}
import org.scalatest.EitherValues
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext.Implicits.global

class DepartureMovementConnectorSpec extends SpecBase with WireMockServerHandler with ScalaCheckPropertyChecks with Generators with EitherValues {

  private lazy val connector: DepartureMovementConnector = app.injector.instanceOf[DepartureMovementConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.common-transit-convention-traders.port" -> server.port())

  "DeparturesMovementConnector" - {
    "getLRN" - {

      "must return LocalReferenceNumber" in {

        val ie015Body = Json.obj(
          "CC015" -> Json.obj(
            "TransitOperation" -> Json.obj(
              "LRN" -> "AB123"
            )
          )
        )

        val responseJson = Json.parse(
          s"""
             |{
             |  "_links": {
             |    "self": {
             |      "href": "/customs/transits/movements/departures/62f4ebbbf581d4aa/messages/62f4ebbb765ba8c2"
             |    },
             |    "departure": {
             |      "href": "/customs/transits/movements/departures/62f4ebbbf581d4aa"
             |    }
             |  },
             |  "id": "62f4ebbb765ba8c2",
             |  "departureId": "62f4ebbbf581d4aa",
             |  "received": "2022-08-11T11:44:59.83705",
             |  "type": "IE015",
             |  "status": "Success",
             |  "body": ${ie015Body.toString}
             |}
             |""".stripMargin
        )

        server.stubFor(
          get(urlEqualTo(s"/movements/departures/$departureId/messages/ab123"))
            .willReturn(okJson(responseJson.toString()))
        )

        val result = connector.getLRN(s"movements/departures/$departureId/messages/ab123").futureValue

        result mustBe Some(LocalReferenceNumber("AB123"))
      }

    }

    "getMessageMetaData" - {

      "must return Messages" in {

        val responseJson: JsValue = Json.parse("""
            {
                "_links": {
                    "self": {
                        "href": "/customs/transits/movements/departures/6365135ba5e821ee/messages"
                    },
                    "departure": {
                        "href": "/customs/transits/movements/departures/6365135ba5e821ee"
                    }
                },
                "messages": [
                    {
                        "_links": {
                            "self": {
                                "href": "/customs/transits/movements/departures/6365135ba5e821ee/message/634982098f02f00b"
                            },
                            "departure": {
                                "href": "/customs/transits/movements/departures/6365135ba5e821ee"
                            }
                        },
                        "id": "634982098f02f00a",
                        "departureId": "6365135ba5e821ee",
                        "received": "2022-11-11T15:32:51.459Z",
                        "type": "IE015",
                        "status": "Success"
                    },
                    {
                        "_links": {
                            "self": {
                                "href": "/customs/transits/movements/departures/6365135ba5e821ee/message/634982098f02f00a"
                            },
                            "departure": {
                                "href": "/customs/transits/movements/departures/6365135ba5e821ee"
                            }
                        },
                        "id": "634982098f02f00a",
                        "departureId": "6365135ba5e821ee",
                        "received": "2022-11-10T15:32:51.459Z",
                        "type": "IE028",
                        "status": "Success"
                    }
                ]
            }
            """)

        val expectedResult = DepartureMessages(
          List(
            DepartureMessageMetaData(
              LocalDateTime.parse("2022-11-11T15:32:51.459Z", DateTimeFormatter.ISO_DATE_TIME),
              DepartureMessageType.DepartureNotification,
              "movements/departures/6365135ba5e821ee/message/634982098f02f00b"
            ),
            DepartureMessageMetaData(
              LocalDateTime.parse("2022-11-10T15:32:51.459Z", DateTimeFormatter.ISO_DATE_TIME),
              DepartureMessageType.AllocatedMRN,
              "movements/departures/6365135ba5e821ee/message/634982098f02f00a"
            )
          )
        )

        server.stubFor(
          get(urlEqualTo(s"/movements/departures/$departureId/messages"))
            .willReturn(okJson(responseJson.toString()))
        )

        connector.getMessageMetaData(departureId).futureValue mustBe Some(expectedResult)

      }
    }

    "getIE015data" - {

      "must return messages" in {
        val dateTime     = LocalDateTime.now
        val responseJson = Json.parse(s"""

    {
            "body": {
                "n1:CC015C": {
                    "messageSender": "message sender",
                    "messageRecipient": "message recipient",
                    "preparationDateAndTime": "$dateTime",
                    "messageIdentification": "messageId",
                    "messageType": "CC015C",
                    "TransitOperation": {
                        "LRN": "LRN",
                        "declarationType": "Pbg",
                        "additionalDeclarationType": "O",
                        "security": 8,
                        "reducedDatasetIndicator": 1,
                        "bindingItinerary": 0
                    },
                    "CustomsOfficeOfDeparture": {
                        "referenceNumber": "GB000060"
                    },
                    "CustomsOfficeOfDestinationDeclared": {
                        "referenceNumber": "GB000060"
                    },
                    "HolderOfTheTransitProcedure": {
                        "identificationNumber": "idNumber"
                    },
                    "Guarantee": {
                        "sequenceNumber": 48711,
                        "guaranteeType": 1,
                        "otherGuaranteeReference": "1qJMA6MbhnnrOJJjHBHX"
                    },
                    "Consignment": {
                        "grossMass": 6430669292.48125,
                        "HouseConsignment": {
                            "sequenceNumber": 48711,
                            "grossMass": 6430669292.48125,
                            "ConsignmentItem": {
                                "goodsItemNumber": 18914,
                                "declarationGoodsItemNumber": 1458,
                                "Commodity": {
                                    "descriptionOfGoods": "ZMyM5HTSTnLqT5FT9aHXwScqXKC1VitlWeO5gs91cVXBXOB8xBdXG5aGhG9VFjjDGiraIETFfbQWeA7VUokO7ngDOrKZ23ccKKMA6C3GpXciUTt9nS2pzCFFFeg4BXdkIe"
                                },
                                "Packaging": {
                                    "sequenceNumber": 48711,
                                    "typeOfPackages": "Oi"
                                }
                            }
                        }
                    }
                }
            }
        }

    """)

        server.stubFor(
          get(urlEqualTo(s"/$departureId"))
            .withHeader("Accept", containing("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(responseJson.toString()))
        )

        val result: Option[IE015Data] = connector.getIE015(departureId).futureValue

        val expectedResult = Some(
          IE015Data(
            IE015MessageData(
              "message sender",
              "message recipient",
              dateTime,
              "messageId",
              TransitOperation(None, Some("LRN")),
              CustomsOfficeOfDeparture("GB000060"),
              HolderOfTheTransitProcedure("idNumber", None, None, None, None)
            )
          )
        )

        result mustBe expectedResult

      }
    }

  }

}
