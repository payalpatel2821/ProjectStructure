package com.task.newapp.utils.mentionlib.adapters.utils

/**
 * Listener which informs you whether to show or hide a suggestions drop down.
 */
interface SuggestionsListener {
    /**
     * Informs to either display or hide a suggestions drop down.
     *
     * @param display   true if you should display a suggestions drop down or false if it should be
     * hidden.
     */
    fun displaySuggestions(display: Boolean)
}