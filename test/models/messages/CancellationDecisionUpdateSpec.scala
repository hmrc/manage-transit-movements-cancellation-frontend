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

import base.SpecBase
import com.lucidchart.open.xtract.XmlReader
import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import java.time.LocalDate
import scala.xml.Elem

class CancellationDecisionUpdateSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "CancellationDecisionUpdate" - {

    "outcome" - {
      "when rejected and not initiated by customs" - {
        "must return cancellationDecision.cancellationRejected" in {
          forAll(arbitrary[CancellationDecisionUpdate]) {
            message =>
              val updatedMessage = message.copy(
                cancellationDecision = Some(0),
                cancellationInitiatedBy = 0
              )

              updatedMessage.outcome mustBe "cancellationDecision.cancellationRejected"
          }
        }
      }

      "when otherwise" - {
        "must return cancellationDecision.declarationCancelled" in {
          forAll(arbitrary[CancellationDecisionUpdate].suchThat {
            message =>
              !(message.cancellationDecision.contains(0) && message.cancellationInitiatedBy == 0)
          }) {
            _.outcome mustBe "cancellationDecision.declarationCancelled"
          }
        }
      }
    }

    "must create valid object when passed valid xml" in {

      val validXml: Elem = <CC009A>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesSenMES3>NTA.GB</MesSenMES3>
        <MesRecMES6>SYST17B-NCTS_EU_EXIT</MesRecMES6>
        <DatOfPreMES9>20190912</DatOfPreMES9>
        <TimOfPreMES10>1552</TimOfPreMES10>
        <IntConRefMES11>82390912155232</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <TesIndMES18>0</TesIndMES18>
        <MesIdeMES19>82390912155232</MesIdeMES19>
        <MesTypMES20>GB009A</MesTypMES20>
        <HEAHEA>
          <DocNumHEA5>19GB00006010021477</DocNumHEA5>
          <CanDecHEA93>1</CanDecHEA93>
          <DatOfCanReqHEA147>20190912</DatOfCanReqHEA147>
          <CanIniByCusHEA94>0</CanIniByCusHEA94>
          <DatOfCanDecHEA146>20190912</DatOfCanDecHEA146>
          <CanJusHEA248>ok thats fine</CanJusHEA248>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>CITY WATCH SHIPPING</NamPC17>
          <StrAndNumPC122>125 Psuedopolis Yard</StrAndNumPC122>
          <PosCodPC123>SS99 1AA</PosCodPC123>
          <CitPC124>Ank-Morpork</CitPC124>
          <CouPC125>GB</CouPC125>
          <TINPC159>GB652420267000</TINPC159>
        </TRAPRIPC1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>GB000060</RefNumEPT1>
        </CUSOFFDEPEPT>
      </CC009A>

      val expected =
        CancellationDecisionUpdate(
          "19GB00006010021477",
          Some(LocalDate.parse("2019-09-12")),
          0,
          Some(1),
          LocalDate.parse("2019-09-12"),
          Some("ok thats fine")
        )

      XmlReader.of[CancellationDecisionUpdate].read(validXml).toOption.value mustBe expected
    }
  }
}
