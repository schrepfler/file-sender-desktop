package filesender

import java.io.IOException
import javafx.{scene => jfxs}

import akka.actor.{ActorContext, ActorRef}

import scalafx.Includes._
import scalafx.scene.Scene
import scalafxml.core.{DependenciesByType, FXMLLoader}


case class SceneContext[T](scene: Scene, controller:T)

trait ProxyActorFactory[T] {
  val resourcePath = ""
  def sceneContext = {
    Option(getClass.getResource(resourcePath)) match {
      case None => throw new IOException("Cannot load default resource path for class:" + getClass.getSimpleName)
      case Some(path) =>
        val loader = new FXMLLoader(path, new DependenciesByType(Map()))
        loader.load()
        val root = loader.getRoot[jfxs.Parent]()
        val scene = new Scene(root)
        val controller = loader.getController[T]()
        new SceneContext[T](scene, controller)
    }
  }

  def actorOf(actorContext: ActorContext, uiContext: SceneContext[T]):ActorRef
}