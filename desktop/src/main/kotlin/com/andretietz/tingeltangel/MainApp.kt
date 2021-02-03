package com.andretietz.tingeltangel

import com.andretietz.tingeltangel.ui.ManagerView
import javafx.application.Platform
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import tornadofx.addStageIcon
import kotlin.system.exitProcess

class MainApp : App(ManagerView::class) {
  private fun exit() {
    Platform.exit()
    exitProcess(0)
  }

  override fun start(stage: Stage) {
    addStageIcon(Image("images/icon.png"))
    stage.isResizable = false
    super.start(stage)
  }

  override fun stop() {
    super.stop()
    exit()
  }
}
