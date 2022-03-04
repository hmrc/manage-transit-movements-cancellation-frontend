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
import com.lucidchart.open.xtract.__
import cats.syntax.all._

import scala.xml.NodeSeq

case class PrincipalTraderDetails(
                                 name: Option[String],
                                 streetAndNumber: Option[String],
                                 postCode: Option[String],
                                 city: Option[String],
                                 countryCode: Option[String],
                                 nadLang: Option[String],
                                 principalEori: Option[String],
                                 holderEori: Option[String]
                                 ) {
  def toXml: NodeSeq = <TRAPRIPC1>
    {name.map(value => <NamPC17>{value}</NamPC17>).getOrElse(NodeSeq.Empty)}
    {streetAndNumber.map(value => <StrAndNumPC122>{value}</StrAndNumPC122>).getOrElse(NodeSeq.Empty)}
    {postCode.map(value => <PosCodPC123>{value}</PosCodPC123>).getOrElse(NodeSeq.Empty)}
    {city.map(value => <CitPC124>{value}</CitPC124>).getOrElse(NodeSeq.Empty)}
    {countryCode.map(value => <CouPC125>{value}</CouPC125>).getOrElse(NodeSeq.Empty)}
    {nadLang.map(value => <NADLNGPC>{value}</NADLNGPC>).getOrElse(NodeSeq.Empty)}
    {principalEori.map(value => <TINPC159>{value}</TINPC159>).getOrElse(NodeSeq.Empty)}
    {holderEori.map(value => <HITPC126>{value}</HITPC126>).getOrElse(NodeSeq.Empty)}
  </TRAPRIPC1>
}

object PrincipalTraderDetails {

  implicit val xmlReader: XmlReader[PrincipalTraderDetails] = (
    (__ \ "NamPC17").read[String].optional,
    (__ \ "StrAndNumPC122").read[String].optional,
    (__ \ "PosCodPC123").read[String].optional,
    (__ \ "CitPC124").read[String].optional,
    (__ \ "CouPC125").read[String].optional,
    (__ \ "NADLNGPC").read[String].optional,
    (__ \ "TINPC159").read[String].optional,
    (__ \ "HITPC126").read[String].optional
    ).mapN(apply)

}