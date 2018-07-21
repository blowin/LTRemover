package com.vk.dsessence.settings

import javafx.scene.image.Image
import tornadofx.*

object ResourceManager {
    private var _appIcon : Image? = null
    val appIcon : Image
        get() = _appIcon!!

    fun load(resource: ResourceLookup){
        if(_appIcon != null)
            return

        _appIcon = Image(resource["/icons/baseline_devices_black_18dp.png"])
    }
}