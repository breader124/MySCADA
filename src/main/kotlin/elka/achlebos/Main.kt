package elka.achlebos

import elka.achlebos.view.MainView
import javafx.stage.Stage
import tornadofx.*

class Main : App(MainView::class) {
    override fun start(stage: Stage) {
        stage.isMaximized = true
        super.start(stage)
    }
}