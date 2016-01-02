package filesender

import akka.actor._

import scala.collection.mutable
import scalafx.beans.property.StringProperty
import scalafx.event.ActionEvent
import scalafx.scene.control.{TableColumn, TableView}
import scalafxml.core.macros.sfxml


class TaskRow(initialName: String, initialStatus: String) {
  val name = StringProperty(initialName)
  val status = StringProperty(initialStatus)
}

trait MainController {
  def handleLoad(event: ActionEvent): Unit
  def handleSend(event: ActionEvent): Unit
  def subscribeToLoad(callback: Function[ActionEvent, Unit]): Unit
  def subscribeToSend(callback: Function[ActionEvent, Unit]): Unit
  def addTaskRow(taskName: String, taskStatus: String): Unit
}

@sfxml
class MainControllerImpl(private val taskTable:TableView[TaskRow],
                         private val taskNameColumn:TableColumn[TaskRow, String],
                         private val taskStatusColumn:TableColumn[TaskRow, String]) extends MainController {
  private val loadCallbacks = mutable.MutableList[Function[ActionEvent, Unit]]()
  private val sendCallbacks = mutable.MutableList[Function[ActionEvent, Unit]]()

  taskNameColumn.cellValueFactory = {_.value.name}
  taskStatusColumn.cellValueFactory = {_.value.status}

  def handleLoad(event: ActionEvent) = loadCallbacks.foreach(_(event))

  def handleSend(event: ActionEvent) = sendCallbacks.foreach(_(event))

  def subscribeToLoad(callback: Function[ActionEvent, Unit]) = {
    loadCallbacks += callback
  }

  def subscribeToSend(callback: Function[ActionEvent, Unit]) = {
    sendCallbacks += callback
  }

  def addTaskRow(taskName: String, taskStatus: String) = {
    taskTable.items.getValue.add(new TaskRow(taskName, taskStatus))
  }
}

case class AddTaskRow(taskName: String, taskStatus: String)

class MainPresenterActor(sceneContext: SceneContext[MainController]) extends Actor with ActorLogging {
  // Inbound
  def receive = {
    case command: AddTaskRow =>
      log.debug("Executing command AddTaskRow(taskName:{}, taskStatus:{})", command.taskName, command.taskStatus)
      sceneContext.controller.addTaskRow(command.taskName, command.taskStatus)
    case unexpectedMessage: Any =>
      log.debug("Received unexpected message: {}", unexpectedMessage)
      throw new Exception("Can't handle %s".format(unexpectedMessage))
    case _ =>
      log.debug("Received unexpected object as message.")
      throw new Exception("Can't handle a non message object")
  }
  // Outbound
  sceneContext.controller.subscribeToSend(
    event => context.parent ! new CloseAppCommand()
  )
}

object MainPresenterActor extends ProxyActorFactory[MainController] {
  override val resourcePath = "/Main.fxml"

  def actorOf(actorContext: ActorContext, uiContext: SceneContext[MainController]):ActorRef = {
    actorContext.actorOf(Props(classOf[MainPresenterActor], uiContext).withDispatcher("javafx-dispatcher"), "main-presenter")
  }
}