package com.task.newapp.utils.mentionlib.models

import com.task.newapp.utils.mentionlib.utils.Mentionable
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

/**
 * A mention inserted into the [EditText]. All mentions inserted into the
 * [EditText] must implement the [Mentionable] interface.
 */
class Mention : Mentionable {

    private var offset = 0
    private var length = 0

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
    }

    override var mentionOffset: Int = 0

    override var mentionLength: Int = 0

    override var mentionName: String? = ""

    override var userId: String? = ""  //Add New

    override var mentionAccountId: String? = ""  //Add New

//    override fun getMentionOffset(): Int {
//        return offset
//    }
//
//    override fun getMentionLength(): Int {
//        return length
//    }
//
//    override fun getMentionName(): String {
//        return mentionName!!
//    }
//
//    override fun setMentionOffset(offset: Int) {
//        this.offset = offset
//    }
//
//    override fun setMentionLength(length: Int) {
//        this.length = length
//    }
//
//    override fun setMentionName(mentionName: String) {
//        this.mentionName = mentionName
//    }
}