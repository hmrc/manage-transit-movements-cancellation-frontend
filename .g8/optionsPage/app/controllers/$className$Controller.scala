package controllers

import controllers.actions._
import forms.$className$FormProvider

import javax.inject.Inject
import models.{$className$, DepartureId, Mode, UserAnswers}
import navigation.Navigator
import pages.$className$Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.{ExecutionContext, Future}

class $className$Controller @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       checkCancellationStatus: CheckCancellationStatusProvider,
                                       formProvider: $className$FormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()
  private val template = "$className;format="decap"$.njk"

  def onPageLoad(departureId: DepartureId, mode: Mode): Action[AnyContent] = (identify andThen checkCancellationStatus(departureId) andThen getData(departureId) andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get($className$Page(departureId)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "lrn"    -> request.lrn,
        "departureId" -> departureId,
        "radios"  -> $className$.radios(preparedForm)
      )

      renderer.render(template, json).map(Ok(_))
  }

  def onSubmit(departureId: DepartureId, mode: Mode): Action[AnyContent] = (identify andThen checkCancellationStatus(departureId) andThen getData(departureId) andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "lrn"    -> request.lrn,
            "departureId" -> departureId,
            "radios" -> $className$.radios(formWithErrors)
          )

          renderer.render(template, json).map(BadRequest(_))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(KenPage(departureId), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(KenPage(departureId), mode, updatedAnswers, departureId))
        }
      )
  }
}
