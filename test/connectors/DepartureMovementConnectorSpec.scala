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

import akka.util.Helpers.Requiring
import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import connectors.responses.{InvalidStatus, MalformedBody}
import generators.Generators
import helper.WireMockServerHandler
import models.DepartureStatus.DepartureSubmitted
import models.messages.{CancellationDecisionUpdate, CancellationRequest}
import models.response._
import org.scalacheck.Gen
import org.scalatest.EitherValues
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.HeaderNames
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers
import uk.gov.hmrc.http.{Authorization, HeaderCarrier}

import java.time.{LocalDate, LocalDateTime}
import scala.xml.NodeSeq

class DepartureMovementConnectorSpec extends SpecBase with WireMockServerHandler with ScalaCheckPropertyChecks with Generators with EitherValues {
  implicit val hc: HeaderCarrier = HeaderCarrier(Some(Authorization("BearerToken")))

  private lazy val connector: DepartureMovementConnector =
    app.injector.instanceOf[DepartureMovementConnector]

  private val startUrl = "transits-movements-trader-at-departure"

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(conf = "microservice.services.departures.port" -> server.port())
    .build()

  private val departuresResponseJson = Json.obj(
    "referenceNumber" -> "lrn",
    "status"          -> "DepartureSubmitted"
  )

  private val errorResponseCodes: Gen[Int] = Gen.chooseNum(400, 599)

  "DeparturesMovementConnector" - {
    "getDepartures" - {
      "must return a successful future response" in {
        val expectedResult =
          ResponseDeparture(
            lrn,
            DepartureSubmitted
          )

        server.stubFor(
          get(urlEqualTo(s"/$startUrl/movements/departures/${departureId.index}"))
            .withHeader("Channel", containing("web"))
            .willReturn(okJson(departuresResponseJson.toString()))
        )

        connector.getDeparture(departureId).futureValue mustBe Some(expectedResult)
      }

      "must return a None when an error response is returned from getDepartures" in {

        forAll(errorResponseCodes) {
          errorResponse =>
            server.stubFor(
              get(urlEqualTo(s"/$startUrl/movements/departures/${departureId.index}"))
                .withHeader("Channel", containing("web"))
                .willReturn(
                  aResponse()
                    .withStatus(errorResponse)
                )
            )
            connector.getDeparture(departureId).futureValue mustBe None
        }
      }
    }

    "getMessageSummary" - {
      "must return a successful future response" in {
        val messagesResponseJson = Json.obj(
          "departureId" -> 23,
          "messages"    -> Json.obj("IE015" -> "theFirstUrl", "IE028" -> "theSecondUrl")
        )

        val expectedResult = MessageSummary(
          departureId = 23,
          messages = Map(
            "IE015" -> "theFirstUrl",
            "IE028" -> "theSecondUrl"
          )
        )

        server.stubFor(
          get(urlEqualTo(s"/$startUrl/movements/departures/${departureId.index}/messages/summary"))
            .withHeader("Channel", containing("web"))
            .willReturn(okJson(messagesResponseJson.toString()))
        )

        connector.getMessageSummary(departureId).futureValue mustBe Right(expectedResult)
      }

      "must return Left MalformedBody if messages json is malformed" in {
        val messagesResponseJson = Json.obj(
          "depurId" -> 23,
          "mees"    -> Json.obj("IE015" -> "theFirstUrl", "IE028" -> "theSecondUrl")
        )

        server.stubFor(
          get(urlEqualTo(s"/$startUrl/movements/departures/${departureId.index}/messages/summary"))
            .withHeader("Channel", containing("web"))
            .willReturn(okJson(messagesResponseJson.toString()))
        )

        connector.getMessageSummary(departureId).futureValue mustBe Left(MalformedBody)
      }

      "must return an InvalidStatus when an error response is returned from getMessageSummary" in {

        forAll(errorResponseCodes) {
          errorResponse =>
            server.stubFor(
              get(urlEqualTo(s"/$startUrl/movements/departures/${departureId.index}/messages/summary"))
                .withHeader("Channel", containing("web"))
                .willReturn(
                  aResponse()
                    .withStatus(errorResponse)
                )
            )
            connector.getMessageSummary(departureId).futureValue mustBe Left(InvalidStatus(errorResponse))
        }
      }
    }
    "getMrnAllocatedMessage" - {
      val mrnAllocatedMessage = <CC028B>
        <SynIdeMES1>SynIdeMES1</SynIdeMES1>
        <SynVerNumMES2>SynVerNumMES2</SynVerNumMES2>
        <MesSenMES3>MesSenMES3</MesSenMES3>
        <SenIdeCodQuaMES4>SenIdeCodQuaMES4</SenIdeCodQuaMES4>
        <MesRecMES6>MesRecMES6</MesRecMES6>
        <RecIdeCodQuaMES7>RecIdeCodQuaMES7</RecIdeCodQuaMES7>
        <DatOfPreMES9>DatOfPreMES9</DatOfPreMES9>
        <TimOfPreMES10>TimOfPreMES10</TimOfPreMES10>
        <IntConRefMES11>IntConRefMES11</IntConRefMES11>
        <RecRefMES12>RecRefMES12</RecRefMES12>
        <RecRefQuaMES13>RecRefQuaMES13</RecRefQuaMES13>
        <AppRefMES14>AppRefMES14</AppRefMES14>
        <PriMES15>PriMES15</PriMES15>
        <AckReqMES16>AckReqMES16</AckReqMES16>
        <ComAgrIdMES17>ComAgrIdMES17</ComAgrIdMES17>
        <TesIndMES18>TesIndMES18</TesIndMES18>
        <MesIdeMES19>MesIdeMES19</MesIdeMES19>
        <MesTypMES20>CC014A</MesTypMES20>
        <ComAccRefMES21>ComAccRefMES21</ComAccRefMES21>
        <MesSeqNumMES22>MesSeqNumMES22</MesSeqNumMES22>
        <FirAndLasTraMES23>FirAndLasTraMES23</FirAndLasTraMES23>
        <HEAHEA>
          <RefNumHEA4>lrn</RefNumHEA4>
          <DocNumHEA5>mrn</DocNumHEA5>
          <AccDatHEA158>12122020</AccDatHEA158>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>name</NamPC17>
          <StrAndNumPC122>street</StrAndNumPC122>
          <PosCodPC123>xx11xx</PosCodPC123>
          <CitPC124>city</CitPC124>
          <CouPC125>GB</CouPC125>
          <NADLNGPC>EN</NADLNGPC>
          <TINPC159>eori</TINPC159>
          <HITPC126>holder tir</HITPC126>
        </TRAPRIPC1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>AB12345C</RefNumEPT1>
        </CUSOFFDEPEPT>
      </CC028B>

      "must return a successful future response" in {
        val messagesResponseJson = Json.obj(
          "dateTime"             -> LocalDateTime.now().toString,
          "messageType"          -> "IE028",
          "messageCorrelationId" -> 2,
          "message"              -> mrnAllocatedMessage.toString()
        )

        val expectedResult = MRNAllocatedMessage(
          MRNAllocatedRootLevel(
            "SynIdeMES1",
            "SynVerNumMES2",
            "MesSenMES3",
            Some("SenIdeCodQuaMES4"),
            "MesRecMES6",
            Some("RecIdeCodQuaMES7"),
            "DatOfPreMES9",
            "TimOfPreMES10",
            "IntConRefMES11",
            Some("RecRefMES12"),
            Some("RecRefQuaMES13"),
            Some("AppRefMES14"),
            Some("PriMES15"),
            Some("AckReqMES16"),
            Some("ComAgrIdMES17"),
            Some("TesIndMES18"),
            "MesIdeMES19",
            Some("ComAccRefMES21"),
            Some("MesSeqNumMES22"),
            Some("FirAndLasTraMES23")
          ),
          "mrn",
          PrincipalTraderDetails(Some("name"), Some("street"), Some("xx11xx"), Some("city"), Some("GB"), Some("EN"), Some("eori"), Some("holder tir")),
          "AB12345C"
        )

        val url = s"/$startUrl/movements/departures/${departureId.index}/messages/2"

        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Channel", containing("web"))
            .willReturn(okJson(messagesResponseJson.toString()))
        )

        connector.getMrnAllocatedMessage(departureId, url).futureValue mustBe Right(expectedResult)
      }

      "must return Left MalformedBody if messages json is malformed" in {
        val messagesResponseJson = Json.obj(
          "dateTime"             -> LocalDateTime.now().toString,
          "messageType"          -> "IE028",
          "messageCorrelationId" -> 2,
          "mage"                 -> mrnAllocatedMessage.toString()
        )

        val url = s"/$startUrl/movements/departures/${departureId.index}/messages/2"

        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Channel", containing("web"))
            .willReturn(okJson(messagesResponseJson.toString()))
        )

        connector.getMrnAllocatedMessage(departureId, url).futureValue mustBe Left(MalformedBody)
      }

      "must return an InvalidStatus when an error response is returned from getMrnAllocatedMessage" in {

        forAll(errorResponseCodes) {
          errorResponse =>
            val url = s"/$startUrl/movements/departures/${departureId.index}/messages/2"

            server.stubFor(
              get(urlEqualTo(url))
                .withHeader("Channel", containing("web"))
                .willReturn(
                  aResponse()
                    .withStatus(errorResponse)
                )
            )

            connector.getMrnAllocatedMessage(departureId, url).futureValue mustBe Left(InvalidStatus(errorResponse))
        }
      }
    }
    "submitCancellation" - {

      val cancellationRequest: CancellationRequest =
        CancellationRequest(
          MRNAllocatedRootLevel(
            "SynIdeMES1",
            "SynVerNumMES2",
            "MesSenMES3",
            Some("SenIdeCodQuaMES4"),
            "MesRecMES6",
            Some("RecIdeCodQuaMES7"),
            "DatOfPreMES9",
            "TimOfPreMES10",
            "IntConRefMES11",
            Some("RecRefMES12"),
            Some("RecRefQAMES12"),
            Some("AppRefMES14"),
            Some("PriMES15"),
            Some("AckReqMES16"),
            Some("ComAgrIdMES17"),
            Some("TesIndMES18"),
            "MesIdeMES19",
            Some("ComAckRef"),
            Some("MesSeqNum"),
            None
          ),
          "mrn",
          LocalDate.now(),
          "just cause",
          PrincipalTraderDetails(Some("name"), Some("street"), Some("xx11xx"), Some("city"), Some("GB"), Some("EN"), Some("eori"), Some("holder tir")),
          "123456"
        )

      "must return a successful future response" in {

        val request = cancellationRequest

        server.stubFor(
          post(urlEqualTo(s"/$startUrl/movements/departures/${departureId.index}/messages"))
            .withHeader("Channel", containing("web"))
            .withHeader(HeaderNames.CONTENT_TYPE, containing("application/xml"))
            .willReturn(status(Helpers.ACCEPTED))
        )

        connector.submitCancellation(departureId, request).futureValue.isRight mustBe true
      }

      "must return  InvalidStatus when an error response is returned from submitCancellation" in {

        val request = cancellationRequest

        forAll(errorResponseCodes) {
          errorResponse =>
            server.stubFor(
              post(urlEqualTo(s"/$startUrl/movements/departures/${departureId.index}/messages"))
                .withHeader("Channel", containing("web"))
                .withHeader(HeaderNames.CONTENT_TYPE, containing("application/xml"))
                .willReturn(
                  aResponse()
                    .withStatus(errorResponse)
                )
            )
            connector.submitCancellation(departureId, request).futureValue mustBe Left(InvalidStatus(errorResponse))
        }
      }
    }

    "getCancellationDecisionUpdateMessage" - {
      "must return valid 'cancellation decision update message'" in {
        val url = s"/transits-movements-trader-at-departure/movements/departures/${departureId.value}/messages/2"

        val xml: NodeSeq = <CC009A>
          <HEAHEA>
            <DocNumHEA5>19GB00006010021477</DocNumHEA5>
            <CanDecHEA93>1</CanDecHEA93>
            <DatOfCanReqHEA147>20190912</DatOfCanReqHEA147>
            <CanIniByCusHEA94>0</CanIniByCusHEA94>
            <DatOfCanDecHEA146>20190912</DatOfCanDecHEA146>
            <CanJusHEA248>ok thats fine</CanJusHEA248>
          </HEAHEA>
        </CC009A>

        val json = Json.obj("message" -> xml.toString())

        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Channel", containing("web"))
            .willReturn(
              okJson(json.toString)
            )
        )
        val expectedResult =
          Some(
            CancellationDecisionUpdate(
              "19GB00006010021477",
              Some(LocalDate.parse("2019-09-12")),
              0,
              Some(1),
              LocalDate.parse("2019-09-12"),
              Some("ok thats fine")
            )
          )

        connector.getCancellationDecisionUpdateMessage(url).futureValue mustBe expectedResult
      }

      "must return None for malformed input'" in {
        val url                   = s"/transits-movements-trader-at-departure/movements/departures/${departureId.value}/messages/2"
        val rejectionXml: NodeSeq = <CC009A>
          <FUNERRER1>
            <ErrTypER11>15</ErrTypER11>
            <ErrPoiER12>not valid</ErrPoiER12>
          </FUNERRER1>
        </CC009A>

        val json = Json.obj("message" -> rejectionXml.toString())

        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Channel", containing("web"))
            .willReturn(
              okJson(json.toString)
            )
        )
        connector.getCancellationDecisionUpdateMessage(url).futureValue mustBe None
      }

      "must return None when an error response is returned from getCancellationDecisionUpdateMessage" in {
        val url: String = "/transits-movements-trader-at-departure/movements/departures/1/messages/2"
        forAll(errorResponseCodes) {
          errorResponseCode =>
            server.stubFor(
              get(urlEqualTo(url))
                .withHeader("Channel", containing("web"))
                .willReturn(
                  aResponse()
                    .withStatus(errorResponseCode)
                )
            )
            connector.getCancellationDecisionUpdateMessage(url).futureValue mustBe None
        }
      }
    }
  }
}
