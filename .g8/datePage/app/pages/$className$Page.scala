package pages

import models.DepartureId

import java.time.LocalDate
import play.api.libs.json.JsPath

case class $className$Page(departureId: DepartureId) extends QuestionPage[LocalDate] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "$className;format="decap"$"
}
