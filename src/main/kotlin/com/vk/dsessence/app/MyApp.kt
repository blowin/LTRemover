package com.vk.dsessence.app

import com.vk.dsessence.settings.*
import com.vk.dsessence.types.localization.AlertType
import com.vk.dsessence.types.DeleteType
import com.vk.dsessence.view.MainFXMLView
import com.vk.dsessence.settings.ResourceManager
import com.vk.dsessence.types.localization.LangType
import javafx.stage.Stage
import tornadofx.*

class MyApp: App(MainFXMLView::class){
    init {
        ResourceManager.load(resources)
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.setOnCloseRequest {
            ConfigFileWorker.writeSettings(find<MainFXMLView>().config())
        }
    }
}