package com.vk.dsessence.view.custom

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import com.vk.dsessence.settings.LocalizationController
import com.vk.dsessence.types.localization.AlertType
import com.vk.dsessence.types.localization.ButtonsId
import javafx.scene.layout.StackPane
import javafx.scene.text.Text
import tornadofx.*

fun View.matAlert(parent: StackPane, alertType: AlertType, type: ButtonsId = ButtonsId.Ok) : JFXDialog {
    return JFXDialogLayout().let{ dial ->
        val (header, content) = LocalizationController[alertType]
        dial.setHeading(Text(header))
        dial.setBody(Text(content))
        val dialog = JFXDialog(parent,
                dial,
                JFXDialog.DialogTransition.CENTER)

        val h = primaryStage.height
        dialog.setOnDialogClosed {primaryStage.height = h }

        primaryStage.height += 200
        JFXButton(LocalizationController[type]).let { but->
            but.styleClass.add("JFXButton")
            dial.setActions(but)
            but.action {
                dialog.close()
            }
        }
        dialog
    }
}