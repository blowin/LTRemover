package com.vk.dsessence.settings

import com.vk.dsessence.types.localization.AlertType
import com.vk.dsessence.types.localization.LangType
import com.vk.dsessence.types.DeleteType
import com.vk.dsessence.types.localization.ButtonsId
import com.vk.dsessence.types.localization.MainWindowId

data class LocalizationData(val mainWindowData: MainWindowData,
                            val buttonsData: ButtonsData,
                            val alertData: Map<AlertType, AlertData>,
                            val deleteDeleteType : Map<DeleteType, String>){

    operator fun get(key: MainWindowId) = mainWindowData[key]

    operator fun get(key: ButtonsId) = buttonsData[key]

    operator fun get(key: DeleteType) = deleteDeleteType[key]
}

data class MainWindowData(val deleteData: String,
                          val dir: String,
                          val file: String){
    operator fun get(key: MainWindowId) = when(key){
        MainWindowId.DeleteData -> deleteData
        MainWindowId.Dir -> dir
        MainWindowId.File -> file
    }
}

data class ButtonsData(val ok: String,
                       val cancel: String,
                       val browse: String,
                       val delete: String){
    operator fun get(key: ButtonsId) = when(key){
        ButtonsId.Ok -> ok
        ButtonsId.Cancel -> cancel
        ButtonsId.Browse -> browse
        ButtonsId.Delete -> delete
    }
}

data class AlertData(val header: String, val content: String)

data class SettingData(val selectLang: LangType = LangType.En,
                       val browsePath: String = "",
                       val deleteFile: Boolean = true,
                       val deleteDir: Boolean = false,
                       val lastDeleteType: DeleteType = DeleteType.Expansion)