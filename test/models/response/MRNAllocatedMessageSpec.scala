/*
 * Copyright 2022 HM Revenue & Customs
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

package models.response

import com.lucidchart.open.xtract.XmlReader
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json

import java.time.LocalDateTime

class MRNAllocatedMessageSpec extends AnyFreeSpec with Matchers with OptionValues {

  "MRNAllocatedMessage" - {
    "Parse from XML" - {
      "when only mandatory fields are present" in {
        val xml = <CC028B>
          <SynIdeMES1>SynIdeMES1</SynIdeMES1>
          <SynVerNumMES2>SynVerNumMES2</SynVerNumMES2>
          <MesSenMES3>MesSenMES3</MesSenMES3>
          <MesRecMES6>MesRecMES6</MesRecMES6>
          <DatOfPreMES9>DatOfPreMES9</DatOfPreMES9>
          <TimOfPreMES10>TimOfPreMES10</TimOfPreMES10>
          <IntConRefMES11>IntConRefMES11</IntConRefMES11>
          <MesIdeMES19>MesIdeMES19</MesIdeMES19>
          <MesTypMES20>MesTypMES20</MesTypMES20>
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
            <HITPC126>holder tir</HITPC126>
          </TRAPRIPC1>
          <CUSOFFDEPEPT>
            <RefNumEPT1>AB12345C</RefNumEPT1>
          </CUSOFFDEPEPT>
        </CC028B>

        val expectedModel =  MRNAllocatedMessage(
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
          PrincipalTraderDetails(Some("name"), Some("street"), Some("xx11xx"), Some("city"), Some("GB"), None, None, Some("holder tir")),
          "AB12345C"
        )

        XmlReader.of[MRNAllocatedMessage].read(xml).toOption.value mustBe expectedModel
      }
      "when all fields present" in {
        val xml = <CC028B>
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
          <MesTypMES20>MesTypMES20</MesTypMES20>
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

        val expectedModel =  MRNAllocatedMessage(
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

        XmlReader.of[MRNAllocatedMessage].read(xml).toOption.value mustBe expectedModel
      }
    }
    "Fail to parse" - {
      "when mandatory fields missing" in {
        val xml = <CC028B>
          <SynIdeMES1>SynIdeMES1</SynIdeMES1>
          <SynVerNumMES2>SynVerNumMES2</SynVerNumMES2>
          <MesSenMES3>MesSenMES3</MesSenMES3>
          <IntConRefMES11>IntConRefMES11</IntConRefMES11>
          <AppRefMES14>AppRefMES14</AppRefMES14>
          <MesIdeMES19>MesIdeMES19</MesIdeMES19>
          <MesTypMES20>MesTypMES20</MesTypMES20>
          <HEAHEA>
            <RefNumHEA4>lrn</RefNumHEA4>
            <DocNumHEA5>mrn</DocNumHEA5>
            <AccDatHEA158>12122020</AccDatHEA158>
          </HEAHEA>
          <TRAPRIPC1>
            <NamPC17>name</NamPC17>
            <StrAndNumPC122>street</StrAndNumPC122>
            <HITPC126>holder tir</HITPC126>
          </TRAPRIPC1>
          <CUSOFFDEPEPT>
          </CUSOFFDEPEPT>
        </CC028B>

        XmlReader.of[MRNAllocatedMessage].read(xml).toOption mustBe None
      }
    }
    "Json Parsing" - {
      "should succeed if message is valid" in {
        val xml = <CC028B>
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
          <MesTypMES20>MesTypMES20</MesTypMES20>
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

        val expectedModel =  MRNAllocatedMessage(
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

        Json.obj(
          "dateTime" -> LocalDateTime.now().toString,
          "messageType" -> "IE028",
          "messageCorrelationId" -> 2,
          "message" -> xml.toString()
        ).asOpt[MRNAllocatedMessage].value mustBe expectedModel
      }
      "should fail if json fields are malformed" in {
        val xml = <CC028B>
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
          <MesTypMES20>MesTypMES20</MesTypMES20>
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


        Json.obj(
          "dateTime" -> LocalDateTime.now().toString,
          "messageType" -> "IE028",
          "messageCorrelationId" -> 2,
          "meage" -> xml.toString()
        ).asOpt[MRNAllocatedMessage] mustBe None
      }
      "should fail if xml mandatory fields are not present" in {
        val xml = <CC028B>
          <SynIdeMES1>SynIdeMES1</SynIdeMES1>
          <SynVerNumMES2>SynVerNumMES2</SynVerNumMES2>
          <MesSenMES3>MesSenMES3</MesSenMES3>
          <IntConRefMES11>IntConRefMES11</IntConRefMES11>
          <AppRefMES14>AppRefMES14</AppRefMES14>
          <MesIdeMES19>MesIdeMES19</MesIdeMES19>
          <MesTypMES20>MesTypMES20</MesTypMES20>
          <HEAHEA>
            <RefNumHEA4>lrn</RefNumHEA4>
            <DocNumHEA5>mrn</DocNumHEA5>
            <AccDatHEA158>12122020</AccDatHEA158>
          </HEAHEA>
          <TRAPRIPC1>
            <NamPC17>name</NamPC17>
            <StrAndNumPC122>street</StrAndNumPC122>
            <HITPC126>holder tir</HITPC126>
          </TRAPRIPC1>
          <CUSOFFDEPEPT>
          </CUSOFFDEPEPT>
        </CC028B>


        Json.obj(
          "dateTime" -> LocalDateTime.now().toString,
          "messageType" -> "IE028",
          "messageCorrelationId" -> 2,
          "meage" -> xml.toString()
        ).asOpt[MRNAllocatedMessage] mustBe None
      }
    }
  }

}
