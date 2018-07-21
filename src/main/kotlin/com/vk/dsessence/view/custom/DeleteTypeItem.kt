package com.vk.dsessence.view.custom

import com.vk.dsessence.types.DeleteType

data class DeleteTypeItem(val deleteType: DeleteType, var printVal: String){
    override fun toString(): String {
        return printVal
    }
}

infix fun DeleteType.item(toString: String): DeleteTypeItem = DeleteTypeItem(this, toString)