package com.andretietz.tingeltangel

import com.andretietz.tingeltangel.ui.ManagerView
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import tornadofx.addStageIcon

class MainApp : App(ManagerView::class) {
    override fun start(stage: Stage) {
        addStageIcon(Image("images/icon.png"))
        stage.isResizable = false
        super.start(stage)
    }
}
