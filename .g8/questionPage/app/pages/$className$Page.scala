package pages

import models.{$className$, DepartureId}
import play.api.libs.json.JsPath

case class $className$Page(departureId: DepartureId) extends QuestionPage[$className$] {
  
  override def path: JsPath = JsPath \ toString
  
  override def toString: String = "$className;format="decap"$"
}
