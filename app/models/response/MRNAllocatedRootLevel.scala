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

import com.lucidchart.open.xtract.{__, XmlReader}
import cats.syntax.all._

import scala.xml.NodeSeq

case class MRNAllocatedRootLevel(
  SynIdeMES1: String,
  SynVerNumMES2: String,
  MesSenMES3: String,
  SenIdeCodQuaMES4: Option[String],
  MesRecMES6: String,
  RecIdeCodQuaMES7: Option[String],
  DatOfPreMES9: String,
  TimOfPreMES10: String,
  IntConRefMES11: String,
  RecRefMES12: Option[String],
  RecRefQuaMES13: Option[String],
  AppRefMES14: Option[String],
  PriMES15: Option[String],
  AckReqMES16: Option[String],
  ComAgrIdMES17: Option[String],
  TesIndMES18: Option[String],
  MesIdeMES19: String,
  ComAccRefMES21: Option[String],
  MesSeqNumMES22: Option[String],
  FirAndLasTraMES23: Option[String]
) {

  def toXml: NodeSeq =
    <SynIdeMES1>{SynIdeMES1}</SynIdeMES1>
      <SynVerNumMES2>{SynVerNumMES2}</SynVerNumMES2>
      <MesSenMES3>{MesSenMES3}</MesSenMES3> ++ {
      SenIdeCodQuaMES4
        .map(
          value => <SenIdeCodQuaMES4>{value}</SenIdeCodQuaMES4>
        )
        .getOrElse(NodeSeq.Empty)
    } ++
      <MesRecMES6>{MesRecMES6}</MesRecMES6> ++ {
        RecIdeCodQuaMES7
          .map(
            value => <RecIdeCodQuaMES7>{value}</RecIdeCodQuaMES7>
          )
          .getOrElse(NodeSeq.Empty)
      } ++
      <DatOfPreMES9>{DatOfPreMES9}</DatOfPreMES9>
      <TimOfPreMES10>{TimOfPreMES10}</TimOfPreMES10>
      <IntConRefMES11>{IntConRefMES11}</IntConRefMES11> ++ {
        RecRefMES12
          .map(
            value => <RecRefMES12>{value}</RecRefMES12>
          )
          .getOrElse(NodeSeq.Empty)
      } ++ {
        RecRefQuaMES13
          .map(
            value => <RecRefQuaMES13>{value}</RecRefQuaMES13>
          )
          .getOrElse(NodeSeq.Empty)
      } ++ {
        AppRefMES14
          .map(
            value => <AppRefMES14>{value}</AppRefMES14>
          )
          .getOrElse(NodeSeq.Empty)
      } ++ {
        PriMES15
          .map(
            value => <PriMES15>{value}</PriMES15>
          )
          .getOrElse(NodeSeq.Empty)
      } ++ {
        AckReqMES16
          .map(
            value => <AckReqMES16>{value}</AckReqMES16>
          )
          .getOrElse(NodeSeq.Empty)
      } ++ {
        ComAgrIdMES17
          .map(
            value => <ComAgrIdMES17>{value}</ComAgrIdMES17>
          )
          .getOrElse(NodeSeq.Empty)
      } ++ {
        TesIndMES18
          .map(
            value => <TesIndMES18>{value}</TesIndMES18>
          )
          .getOrElse(NodeSeq.Empty)
      } ++
      <MesIdeMES19>{MesIdeMES19}</MesIdeMES19>
      <MesTypMES20>GB014A</MesTypMES20> ++ {
        ComAccRefMES21
          .map(
            value => <ComAccRefMES21>{value}</ComAccRefMES21>
          )
          .getOrElse(NodeSeq.Empty)
      } ++ {
        MesSeqNumMES22
          .map(
            value => <MesSeqNumMES22>{value}</MesSeqNumMES22>
          )
          .getOrElse(NodeSeq.Empty)
      } ++ {
        FirAndLasTraMES23
          .map(
            value => <FirAndLasTraMES23>{value}</FirAndLasTraMES23>
          )
          .getOrElse(NodeSeq.Empty)
      }
}

object MRNAllocatedRootLevel {

  implicit val xmlReader: XmlReader[MRNAllocatedRootLevel] = (
    (__ \ "SynIdeMES1").read[String],
    (__ \ "SynVerNumMES2").read[String],
    (__ \ "MesSenMES3").read[String],
    (__ \ "SenIdeCodQuaMES4").read[String].optional,
    (__ \ "MesRecMES6").read[String],
    (__ \ "RecIdeCodQuaMES7").read[String].optional,
    (__ \ "DatOfPreMES9").read[String],
    (__ \ "TimOfPreMES10").read[String],
    (__ \ "IntConRefMES11").read[String],
    (__ \ "RecRefMES12").read[String].optional,
    (__ \ "RecRefQuaMES13").read[String].optional,
    (__ \ "AppRefMES14").read[String].optional,
    (__ \ "PriMES15").read[String].optional,
    (__ \ "AckReqMES16").read[String].optional,
    (__ \ "ComAgrIdMES17").read[String].optional,
    (__ \ "TesIndMES18").read[String].optional,
    (__ \ "MesIdeMES19").read[String],
    (__ \ "ComAccRefMES21").read[String].optional,
    (__ \ "MesSeqNumMES22").read[String].optional,
    (__ \ "FirAndLasTraMES23").read[String].optional
  ).mapN(apply)
}
