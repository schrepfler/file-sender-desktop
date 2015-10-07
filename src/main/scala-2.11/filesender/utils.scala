package filesender

import java.io.IOException
import javafx.{scene => jfxs}

import akka.actor.ActorContext

import scalafx.Includes._
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, NoDependencyResolver}


case class SceneContext[T](scene: Scene, controller:T)

trait ProxyActorFactory[T] {
  val resourcePath = ""
  def sceneContext = {
    val resource = getClass.getResource(resourcePath)
    if (resource == null) {
      throw new IOException("Cannot load resource:" + resource)
    }
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.Parent]()
    val scene = new Scene(root)
    val controller = loader.getController[T]()
    new SceneContext[T](scene, controller)
  }

  def actorOf(actorContext: ActorContext, uiContext: SceneContext[T])
}