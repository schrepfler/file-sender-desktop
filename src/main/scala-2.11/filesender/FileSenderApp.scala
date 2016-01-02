package filesender

import akka.actor._

import scala.concurrent.duration._
import scala.language.postfixOps
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}

object FileSenderApp extends JFXApp {

  stage = new PrimaryStage()

  // Start akka
  val system = ActorSystem("FileSenderApp")
  val main = system.actorOf(Props(classOf[FileSenderAppActor], stage).withDispatcher("javafx-dispatcher"), "main-actor")
  val terminator = system.actorOf(Props(classOf[Terminator], main).withDispatcher("javafx-dispatcher"), "app-terminator")

  // Send a message from outside the JavaFX Application Thread!!
  import system.dispatcher //Import implicit default execution context
  system.scheduler.scheduleOnce(5 seconds){
    main ! AddTaskRow("Time's up!", "Sent")
  }
}

case class CloseAppCommand()

class FileSenderAppActor(stage: PrimaryStage) extends Actor with ActorLogging {
  val mainSceneContext = MainPresenterActor.sceneContext
  val mainPresenter = MainPresenterActor.actorOf(context, mainSceneContext)
  stage.scene = mainSceneContext.scene
  stage.show()

  def receive = {
    case command: CloseAppCommand =>
      log.debug("Executing command CloseAppCommand")
      context.stop(self)
    case command: AddTaskRow =>
      log.debug("Executing command ChangeMainLabel")
      mainPresenter ! command
    case unexpectedMessage: Any =>
      log.debug("Received unexpected message: {}", unexpectedMessage)
      throw new Exception("Can't handle %s".format(unexpectedMessage))
    case _ =>
      log.debug("Received unexpected object as message.")
      throw new Exception("Can't handle a non message object")
  }

}

class Terminator(app: ActorRef) extends Actor with ActorLogging {
  context watch app

  def receive = {
    case Terminated(_) =>
      log.info("application supervisor has terminated, shutting down")
      if (!Platform.isFxApplicationThread) log.warning("Terminator actor is not running in the GUI thread!")
      context.system.terminate()
      Platform.exit()
  }
}