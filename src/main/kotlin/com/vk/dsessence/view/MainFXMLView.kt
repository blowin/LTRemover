package com.vk.dsessence.view

import com.jfoenix.controls.*
import com.vk.dsessence.settings.*
import com.vk.dsessence.types.localization.LangType
import com.vk.dsessence.types.DeleteType
import com.vk.dsessence.types.localization.AlertType
import com.vk.dsessence.types.localization.ButtonsId
import com.vk.dsessence.types.localization.MainWindowId
import com.vk.dsessence.view.custom.DeleteTypeItem
import com.vk.dsessence.view.custom.item
import com.vk.dsessence.view.custom.matAlert
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import javafx.util.StringConverter
import kotlinx.coroutines.experimental.async
import tornadofx.*
import java.io.File
import java.util.regex.Pattern

class MainFXMLView : View("My View") {
    private val deleteLabel : Label by fxid()

    private val buttBrowse : JFXButton by fxid()
    private val buttDelete : JFXButton by fxid()

    private val fieldBrowse : JFXTextField by fxid()
    private val fieldDelete : JFXTextField by fxid()

    private val checkFile : JFXCheckBox by fxid()
    private val checkDir : JFXCheckBox by fxid()

    private val langBox : JFXComboBox<LangType> by fxid()
    private val deleteDeleteTypeBox : JFXComboBox<DeleteTypeItem> by fxid()
    private val stackPane: StackPane by fxid()

    override val root: VBox by fxml("/MainFXMLView.fxml")

    fun config() : SettingData =
            SettingData(langBox.selectedItem!!, fieldBrowse.text ?: "",
                    checkFile.isSelected, checkDir.isSelected, deleteDeleteTypeBox.selectedItem!!.deleteType)

    private fun JFXComboBox<DeleteTypeItem>.configComboBox() {
        items.addAll(DeleteType.values().map { it item it.toString() })
        converter = object : StringConverter<DeleteTypeItem>() {
            override fun toString(`object`: DeleteTypeItem?): String = `object`?.printVal ?: "Null"

            override fun fromString(string: String?) = DeleteTypeItem(DeleteType.Expansion, "")

        }
    }

    private fun loadSettings() = ConfigFileWorker.readSettings()?.apply {
        fieldBrowse.text = if(File(browsePath).exists()) browsePath else ""
        langBox.selectionModel.select(LangType.values().indexOf(selectLang))
        deleteDeleteTypeBox.selectionModel.select(DeleteType.values().indexOf(lastDeleteType))
        checkFile.isSelected = deleteFile
        checkDir.isSelected = deleteDir
    }

    fun initialize(){
        primaryStage.apply {
            minWidth = 500.0
            minHeight = 230.0
            title = "LTMover"
        }

        langBox.apply {
            items.addAll(LangType.values())
            valueProperty().addListener { _, _, newValue ->  LocalizationController(newValue)}
        }

        deleteDeleteTypeBox.configComboBox()

        checkDir.selectedIfAnotherEmpty(checkFile)
        checkFile.selectedIfAnotherEmpty(checkDir)

        buttDelete.action { runDeleter() }
        buttBrowse.action{ dirChooser { fieldBrowse.text = it } }

        primaryStage.icons.add(ResourceManager.appIcon)
        loadSettings()
        subscribeOnEventLocalization()
        LocalizationController.notification()
    }

    private fun subscribeOnEventLocalization() {
        LocalizationController += ButtonsId.Browse.localizeEventCompose { buttBrowse.text = it }
        LocalizationController += ButtonsId.Delete.localizeEventCompose { buttDelete.text = it }

        LocalizationController += MainWindowId.DeleteData.localizeEventCompose { deleteLabel.text = it }
        LocalizationController += MainWindowId.Dir.localizeEventCompose { checkDir.text = it }
        LocalizationController += MainWindowId.File.localizeEventCompose { checkFile.text = it }

        LocalizationController += {
            val selType = deleteDeleteTypeBox.selectedItem!!.deleteType
            deleteDeleteTypeBox.items.clear()
            DeleteType.values().forEach {
                val item = it item LocalizationController[it]
                deleteDeleteTypeBox.items.add(item)
                if(it == selType) deleteDeleteTypeBox.selectionModel.select(item)
            }
        }
    }

    private fun CheckBox.selectedIfAnotherEmpty(another : CheckBox){
        this.selectedProperty().addListener { _, _, newValue ->
            if(newValue == false && !another.isSelected){
                this.isSelected = true
            }
        }
    }

    private inline fun dirChooser(action: (String) -> Unit){
        val selectedDir = DirectoryChooser().showDialog(primaryStage)
        if(selectedDir != null)
            action(selectedDir.absolutePath)
    }

    private fun checkDeletedTypeFun() : (File) -> Boolean {
        return when{
            checkDir.isSelected && checkFile.isSelected -> { f : File -> f.isFile || f.isDirectory }
            checkFile.isSelected                        -> { f : File -> f.isFile }
            else                                        -> { f : File -> f.isDirectory }
        }
    }

    private fun checkNeedDelete(str: String) : Pair<Boolean, (String) -> Boolean> {
        return when(deleteDeleteTypeBox.selectedItem!!.deleteType) {
            DeleteType.Expansion -> Pair(false, { f: String -> f.endsWith(str) })
            DeleteType.StartWith -> Pair(false, { f: String -> f.startsWith(str) })
            DeleteType.Contain -> Pair(false, { f: String -> f.contains(str) })
            DeleteType.Regex -> try{
                Pattern.compile(str)!!.let { Pair(false, { f: String -> it.matcher(f).matches() }) }
            }catch (_ : Exception){
                Pair(true, { _: String -> false })
            }
        }
    }

    private fun runDeleter(){
        val deletePattern = fieldDelete.text
        if(deletePattern.isNullOrEmpty()){
            matAlert(stackPane, AlertType.EnterPatternForDelete).show()
            return
        }

        val path = fieldBrowse.text
        val checkFunc = checkDeletedTypeFun()
        val (hasError, checkDeleteFunc) = checkNeedDelete(deletePattern)
        if(hasError) {
            matAlert(stackPane, AlertType.InvalidRegularExprPattern).show()
            return
        }

        when(path) {
            null, "" -> matAlert(stackPane, AlertType.NotFoundPathForDelete).show()
            else -> {
                File(path).walk().forEach {
                    async {
                        if (checkFunc(it) && checkDeleteFunc(it.path)) {
                            it.deleteRecursively()
                        }
                    }
                }
            }
        }
    }
}
