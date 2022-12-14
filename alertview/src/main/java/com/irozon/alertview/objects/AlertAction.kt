package com.irozon.alertview.objects

import com.irozon.alertview.enums.AlertActionStyle
import com.irozon.alertview.interfaces.AlertActionListener

/**
 * Created by hammad.akram on 3/14/18.
 */
class AlertAction {
    var title: String
    var icon: Int = 0
    var style: AlertActionStyle
    var action: ((AlertAction) -> Unit)?
    var actionListener: AlertActionListener?
    var isChecked: Boolean = false

    constructor(title: String, icon: Int, style: AlertActionStyle, action: (AlertAction) -> Unit) {
        this.title = title
        this.style = style
        this.action = action
        this.icon = icon
        this.actionListener = null
    }

    constructor(title: String, style: AlertActionStyle, action: (AlertAction) -> Unit) {
        this.title = title
        this.style = style
        this.action = action

        this.actionListener = null
    }

    constructor(title: String, isChecked: Boolean, style: AlertActionStyle, action: (AlertAction) -> Unit) {
        this.title = title
        this.style = style
        this.action = action
        this.isChecked = isChecked
        this.actionListener = null
    }

    constructor(title: String, style: AlertActionStyle, actionListener: AlertActionListener) {
        this.title = title
        this.style = style
        this.actionListener = actionListener
        this.action = null
    }


}