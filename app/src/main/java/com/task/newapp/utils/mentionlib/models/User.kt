package com.percolate.mentions.sample.models

import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

/**
 * The local JSON file users.json contains sample user data that is loaded and used
 * to demonstrate '@' mentions.
 */
class User {
    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("picture")
    var imageUrl: String? = null

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
    }

    val fullName: String
        get() = "$firstName $lastName"
}