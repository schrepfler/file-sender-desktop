package filesender

import scalafx.application.Platform
import scalafx.event.ActionEvent
import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml


@sfxml
class MainPresenter(private val pane: AnchorPane) {

  def handleSubmit(event: ActionEvent) {
    Platform.exit()
  }

}
