package controllers

import controllers.actions._
import javax.inject.Inject
import models.LocalReferenceNumber
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

class $className$Controller @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>

      val json = Json.obj("lrn" -> lrn)

      renderer.render("$className;format="decap"$.njk", json).map(Ok(_))
  }
}
