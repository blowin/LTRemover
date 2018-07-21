package com.vk.dsessence.settings

import com.google.gson.Gson
import com.vk.dsessence.types.localization.LangType
import java.io.File

object ConfigFileWorker {
    private const val settingsDir = "data/"
    private const val localizationPath = "${settingsDir}locale/"
    private const val appPath = "${settingsDir}settings.json"

    fun writeSettings(data: SettingData) = writeToFile(data, appPath)

    fun writeLanguage(data: LocalizationData, lang: LangType) = writeToFile(data, langPath(lang))

    fun readLanguage(lang: LangType) : LocalizationData? = readFromFile(langPath(lang))

    fun readSettings() : SettingData? = readFromFile(appPath)

    private inline fun Boolean.ifTrue(block: () -> Unit ) { if(this) block() }

    private fun langPath(lang: LangType) = localizationPath.plus(lang).plus(".json")

    private fun createIfNotExist(path: String) =
            File(path).let { (!it.exists() && it.parentFile.mkdirs()).ifTrue{ it.createNewFile() } }

    private inline fun <reified T> readFromFile(path: String) : T? {
        val strData = readFromFileText(path)
        return if(strData.isNullOrEmpty()) null
        else Gson().fromJson<T>(strData, T::class.java)
    }

    private fun writeToFile(data: Any, path: String){
        createIfNotExist(path)
        File(path).bufferedWriter().use { out ->
            out.write(Gson().toJson(data))
        }
    }

    private fun readFromFileText(path: String) : String{
        val file = File(path)
        if(!file.exists()) return ""

        return file.bufferedReader().use { it.readText() }
    }
}