package filesender

import scalafx.event.ActionEvent
import scalafx.scene.control.Label
import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml

import akka.actor._


@sfxml
class MainController(private val pane: AnchorPane, private val label: Label) {
  var _submitCallbacks = List[PartialFunction[ActionEvent, Unit]]()

  def subscribeToSubmit(callback: PartialFunction[ActionEvent, Unit]) = {
    _submitCallbacks = callback :: _submitCallbacks
  }

  def setLabelText(text: String) = {
    label.text = text
  }

  def handleSubmit(event: ActionEvent) {
    for {callback <- _submitCallbacks} yield callback(event)
  }
}

case class ChangeMainLabel(newText: String)

class MainPresenterActor(sceneContext: SceneContext[MainController]) extends Actor with ActorLogging {
  // Inbound
  def receive = {
    case command: ChangeMainLabel => {
      log.debug("Executing command ChangeMainLabel(newText:{})", command.newText)
      sceneContext.controller.setLabelText(command.newText)
    }
    case unexpectedMessage: Any => {
      log.debug("Received unexpected message: {}", unexpectedMessage)
      throw new Exception("Can't handle %s".format(unexpectedMessage))
    }
  }
  // Outbound
  sceneContext.controller.subscribeToSubmit({
    case event: ActionEvent => context.parent ! new CloseAppCommand()
  })
}

object MainPresenterActor extends ProxyActorFactory[MainController] {
  override val resourcePath = "/Main.fxml"

  def actorOf(actorContext: ActorContext, uiContext: SceneContext[MainController]):ActorRef = {
    actorContext.actorOf(Props(classOf[MainPresenterActor], uiContext).withDispatcher("javafx-dispatcher"), "main-presenter")
  }
}