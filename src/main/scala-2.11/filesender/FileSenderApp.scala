package filesender

import akka.actor._

import scalafx.application.{Platform, JFXApp}
import scalafx.application.JFXApp.PrimaryStage

object FileSenderApp extends JFXApp {

  stage = new PrimaryStage()

  // Start akka
  val system = ActorSystem("FileSenderApp")
  val main = system.actorOf(Props(classOf[FileSenderAppActor], stage).withDispatcher("javafx-dispatcher"), "main-actor")
  val terminator = system.actorOf(Props(classOf[Terminator], main).withDispatcher("javafx-dispatcher"), "app-terminator")

}

case class CloseAppCommand()

class FileSenderAppActor(stage: PrimaryStage) extends Actor with ActorLogging {
  val mainSceneContext = MainPresenterActor.sceneContext
  val mainPresenter = MainPresenterActor.actorOf(context, mainSceneContext)

  def receive = {
    case command: CloseAppCommand => {
      log.debug("Executing command CloseAppCommand")
      context.stop(self)
    }
    case unexpectedMessage: Any => {
      log.debug("Received unexpected message: {}", unexpectedMessage)
      throw new Exception("Can't handle %s".format(unexpectedMessage))
    }
  }

}

class Terminator(app: ActorRef) extends Actor with ActorLogging {
  context watch app

  def receive = {
    case Terminated(_) ⇒
      log.info("application supervisor has terminated, shutting down")
      if (!Platform.isFxApplicationThread) log.warning("Terminator actor is not running in the GUI thread!")
      context.system.terminate()
      Platform.exit()
  }
}