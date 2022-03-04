package controllers

import controllers.actions._
import forms.$className$FormProvider

import javax.inject.Inject
import models.{DepartureId, UserAnswers, Mode}
import navigation.Navigator
import pages.$className$Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class $className$Controller @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       formProvider: $className$FormProvider,
                                       checkCancellationStatus: CheckCancellationStatusProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()
  private val template = "$className;format="decap"$.njk"

  def onPageLoad(departureId: DepartureId, mode: Mode): Action[AnyContent] =
    (identify andThen checkCancellationStatus(departureId) andThen getData(departureId) andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get($className$Page(departureId)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "lrn"         -> request.lrn,
        "departureId"  -> departureId,
        "mode" -> mode
      )

      renderer.render(template, json).map(Ok(_))
  }

  def onSubmit(departureId: DepartureId, mode: Mode): Action[AnyContent] =
    (identify andThen checkCancellationStatus(departureId) andThen getData(departureId)).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "lrn"         -> request.lrn,
            "departureId"  -> departureId,
            "mode" -> mode
          )
    
          renderer.render(template, json).map(BadRequest(_))
        },
        value => {
          val userAnswers = request.userAnswers match {
            case Some(value) => value
            case None => UserAnswers(departureId, request.eoriNumber)
          }
          for {
            updatedAnswers <- Future.fromTry(userAnswers.set($className$Page(departureId), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage($className$Page(departureId), mode, updatedAnswers, departureId))
        }
      )
  }
}
