package filesender

import scalafx.event.ActionEvent
import scalafx.scene.control.Label
import scalafxml.core.macros.sfxml

import akka.actor._


trait MainController {
  def handleSubmit(event: ActionEvent): Unit
  def setLabelText(text: String): Unit
  def subscribeToSubmit(callback: Function[ActionEvent, Unit]): Unit
}

@sfxml
class MainControllerImpl(private val helloLabel: Label) extends MainController {
  private var _submitCallbacks = List[Function[ActionEvent, Unit]]()

  def subscribeToSubmit(callback: Function[ActionEvent, Unit]) = {
    _submitCallbacks = callback :: _submitCallbacks
  }

  def setLabelText(text: String) = {
    helloLabel.text = text
  }

  def handleSubmit(event: ActionEvent) = _submitCallbacks.foreach(_(event))
}

case class ChangeMainLabel(newText: String)

class MainPresenterActor(sceneContext: SceneContext[MainController]) extends Actor with ActorLogging {
  // Inbound
  def receive = {
    case command: ChangeMainLabel =>
      log.debug("Executing command ChangeMainLabel(newText:{})", command.newText)
      sceneContext.controller.setLabelText(command.newText)
    case unexpectedMessage: Any =>
      log.debug("Received unexpected message: {}", unexpectedMessage)
      throw new Exception("Can't handle %s".format(unexpectedMessage))
  }
  // Outbound
  sceneContext.controller.subscribeToSubmit(
    event => context.parent ! new CloseAppCommand()
  )
}

object MainPresenterActor extends ProxyActorFactory[MainController] {
  override val resourcePath = "/Main.fxml"

  def actorOf(actorContext: ActorContext, uiContext: SceneContext[MainController]):ActorRef = {
    actorContext.actorOf(Props(classOf[MainPresenterActor], uiContext).withDispatcher("javafx-dispatcher"), "main-presenter")
  }
}