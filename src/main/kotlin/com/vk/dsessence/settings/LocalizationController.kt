package com.vk.dsessence.settings

import com.vk.dsessence.types.DeleteType
import com.vk.dsessence.types.localization.AlertType
import com.vk.dsessence.types.localization.ButtonsId
import com.vk.dsessence.types.localization.LangType
import com.vk.dsessence.types.localization.MainWindowId

typealias LocalizeEvent = () -> Unit

object LocalizationController {
    private val DefaultLang = LocalizationData (
            MainWindowData (
                    "Delete data:",
                    "Directory",
                    "File"
            ),
            ButtonsData (
                    "Ok",
                    "Cancel",
                    "Browse",
                    "Delete"
            ),
            mapOf(  AlertType.NotFoundPathForDelete to AlertData (
                    "Not fount path",
                    "Please enter the path to delete the file"),
                    AlertType.EnterPatternForDelete to AlertData (
                            "Enter delete string",
                            "Please enter the pattern for delete"),
                    AlertType.InvalidRegularExprPattern to AlertData(
                            "Invalid regular expression",
                            "The regular expression you entered is incorrect"
                    )
            ),
            mapOf(  DeleteType.Expansion to "Expansion",
                    DeleteType.Contain to "Contain",
                    DeleteType.StartWith to "Start with",
                    DeleteType.Regex to "Regular expr")
    )

    private var id: LangType? = null
    private var map : HashMap<LangType, LocalizationData?> = initialize()
    private var events = ArrayList<LocalizeEvent>()

    operator fun get(key: AlertType) : AlertData = map[id].let {
        return if(it == null) DefaultLang.alertData[key]!! else it.alertData[key]!!
    }

    operator fun get(key: MainWindowId) : String = map[id].let {
        return if(it == null) DefaultLang[key]!! else it[key]
    }

    operator fun get(key: DeleteType) : String = map[id].let {
        return if(it == null) DefaultLang[key]!! else it[key]!!
    }

    operator fun get(key: ButtonsId) : String = map[id].let {
        return if(it == null) DefaultLang[key]!! else it[key]
    }

    fun notification() {
        events.forEach { it() }
    }

    operator fun invoke(lang: LangType){
        if(id != null && lang == id) return
        id = lang

        if(map[id!!] == null){
            ConfigFileWorker.readLanguage(id!!).apply { if(this == null) return else map[id!!] = this }
        }

        events.forEach { it() }
    }

    operator fun plusAssign(handler: LocalizeEvent) {
        events.add(handler)
    }

    operator fun minusAssign(handler: LocalizeEvent) {
        events.remove(handler)
    }

    operator fun contains(handler: LocalizeEvent) = events.contains(handler)

    private fun initialize() = HashMap<LangType, LocalizationData?>()
            .apply {
                LangType.values().forEach { put(it, null) }
            }
}

inline fun MainWindowId.localizeEventCompose(crossinline handler: (String) -> Unit) =
        { handler(LocalizationController[this]) }

inline fun ButtonsId.localizeEventCompose(crossinline handler: (String) -> Unit) =
        { handler(LocalizationController[this]) }