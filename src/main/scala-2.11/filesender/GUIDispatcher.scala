package filesender

import java.util.Collections
import java.util.concurrent.{AbstractExecutorService, ExecutorService, ThreadFactory, TimeUnit}
import javafx.application.Platform

import akka.dispatch.{DispatcherPrerequisites, ExecutorServiceConfigurator, ExecutorServiceFactory}
import com.typesafe.config.Config

// First we wrap invokeLater/runLater as an ExecutorService
abstract class GUIExecutorService extends AbstractExecutorService {
  def execute(command: Runnable): Unit

  def shutdown(): Unit = ()

  def shutdownNow() = Collections.emptyList[Runnable]

  def isShutdown = false

  def isTerminated = false

  def awaitTermination(l: Long, timeUnit: TimeUnit) = true
}

object JavaFXExecutorService extends GUIExecutorService {
  override def execute(command: Runnable) = Platform.runLater(command)
}

// Then we create an ExecutorServiceConfigurator so that Akka can use our JavaFXExecutorService for the dispatchers
class JavaFXEventThreadExecutorServiceConfigurator(config: Config, prerequisites: DispatcherPrerequisites) extends ExecutorServiceConfigurator(config, prerequisites) {
  private val f = new ExecutorServiceFactory {
    def createExecutorService: ExecutorService = JavaFXExecutorService
  }

  def createExecutorServiceFactory(id: String, threadFactory: ThreadFactory): ExecutorServiceFactory = f
}
