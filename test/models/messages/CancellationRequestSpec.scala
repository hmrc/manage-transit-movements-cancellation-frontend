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

package models.messages

import models.response.{MRNAllocatedRootLevel, PrincipalTraderDetails}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import java.time.LocalDate
import scala.xml.Utility.trim

class CancellationRequestSpec extends AnyFreeSpec with Matchers {

  "toXml" - {
    val date = LocalDate.of(2018, 4, 22)

    "convert to XML with all fields" in {
      val requestXml =
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
          date,
          "just cause",
          PrincipalTraderDetails(Some("name"), Some("street"), Some("xx11xx"), Some("city"), Some("GB"), Some("EN"), Some("eori"), Some("holder tir")),
          "123456"
        ).toXml.map(trim)

      val expectedXml = <CC014A>
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
        <MesTypMES20>GB014A</MesTypMES20>
        <ComAccRefMES21>ComAccRefMES21</ComAccRefMES21>
        <MesSeqNumMES22>MesSeqNumMES22</MesSeqNumMES22>
        <FirAndLasTraMES23>FirAndLasTraMES23</FirAndLasTraMES23>
        <HEAHEA>
          <DocNumHEA5>mrn</DocNumHEA5>
          <DatOfCanReqHEA147>20180422</DatOfCanReqHEA147>
          <CanReaHEA250>just cause</CanReaHEA250>
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
          <RefNumEPT1>123456</RefNumEPT1>
        </CUSOFFDEPEPT>
      </CC014A>.map(trim)

      requestXml mustBe expectedXml
    }
    "convert to XML with mandatory fields" in {
      val requestXml =
        CancellationRequest(
          MRNAllocatedRootLevel(
            "SynIdeMES1",
            "SynVerNumMES2",
            "MesSenMES3",
            None,
            "MesRecMES6",
            None,
            "DatOfPreMES9",
            "TimOfPreMES10",
            "IntConRefMES11",
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            "MesIdeMES19",
            None,
            None,
            None
          ),
          "mrn",
          date,
          "just cause",
          PrincipalTraderDetails(Some("name"), Some("street"), Some("xx11xx"), Some("city"), Some("GB"), None, None, Some("holder tir")),
          "123456"
        ).toXml.map(trim)

      val expectedXml = <CC014A>
        <SynIdeMES1>SynIdeMES1</SynIdeMES1>
        <SynVerNumMES2>SynVerNumMES2</SynVerNumMES2>
        <MesSenMES3>MesSenMES3</MesSenMES3>
        <MesRecMES6>MesRecMES6</MesRecMES6>
        <DatOfPreMES9>DatOfPreMES9</DatOfPreMES9>
        <TimOfPreMES10>TimOfPreMES10</TimOfPreMES10>
        <IntConRefMES11>IntConRefMES11</IntConRefMES11>
        <MesIdeMES19>MesIdeMES19</MesIdeMES19>
        <MesTypMES20>GB014A</MesTypMES20>
        <HEAHEA>
          <DocNumHEA5>mrn</DocNumHEA5>
          <DatOfCanReqHEA147>20180422</DatOfCanReqHEA147>
          <CanReaHEA250>just cause</CanReaHEA250>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>name</NamPC17>
          <StrAndNumPC122>street</StrAndNumPC122>
          <PosCodPC123>xx11xx</PosCodPC123>
          <CitPC124>city</CitPC124>
          <CouPC125>GB</CouPC125>
          <HITPC126>holder tir</HITPC126>
        </TRAPRIPC1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>123456</RefNumEPT1>
        </CUSOFFDEPEPT>
      </CC014A>.map(trim)

      requestXml mustBe expectedXml
    }
  }
}
