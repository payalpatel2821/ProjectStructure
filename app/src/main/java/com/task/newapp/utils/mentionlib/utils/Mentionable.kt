package com.task.newapp.utils.mentionlib.utils

/**
 * An interface that all models need to inherit. It contains methods for setting the offset,
 * length and mention name. It is used to keep track all the mentions internally in the library.
 */
interface Mentionable {
    /**
     * Get mentions' start location.
     */
    /**
     * Set mentions' start location.
     *
     * @param offset int     The starting locating of the mention in the [EditText].
     */
    var mentionOffset: Int
    /**
     * Get length of mention.
     */
    /**
     * Set mentions' length.
     *
     * @param length int     The length of the mention in the [EditText].
     */
    var mentionLength: Int
    /**
     * Get mentions' display name.
     */
    /**
     * Set mentions' display name.
     */
    var mentionName: String?

    var userId: String?  //Add New

    var mentionAccountId: String?  //Add New
}