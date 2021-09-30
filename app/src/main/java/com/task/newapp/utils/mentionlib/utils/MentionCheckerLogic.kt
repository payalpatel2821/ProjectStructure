package com.task.newapp.utils.mentionlib.utils

import android.widget.EditText
import com.task.newapp.utils.mentionlib.utils.StringUtils.contains
import com.task.newapp.utils.mentionlib.utils.StringUtils.substringAfterLast
import com.task.newapp.utils.mentionlib.utils.StringUtils.lastIndexOf
import com.task.newapp.utils.mentionlib.utils.StringUtils.startsWith

/**
 * Contains helper methods to determine if a search is valid.
 */
class MentionCheckerLogic internal constructor(private val editText: EditText) {
    /* Default limit of 13 characters to evaluate after the '@' symbol. */
    private var maxCharacters = 13

    /**
     * A user may type an '@' and keep typing words without choosing a mention. This method is used
     * to prevent evaluating all the characters after @ for valid mentions. A default limit of
     * 13 character is set. However, you could configure it to any number of characters.
     *
     * @param maxCharacters int     The maximum number of characters to considered after the
     * '@' symbol as a query. The default is 13 characters.
     */
    fun setMaxCharacters(maxCharacters: Int) {
        require(maxCharacters > 0) {
            "Maximum number of characters must be greater " +
                    "than 0."
        }
        this.maxCharacters = maxCharacters
    }

    /**
     * Takes the string typed by the user after the '@' symbol and checks the following rules:
     * 1. The length of the searched string is within the `maxCharacters` limit.
     * 2. An alphanumeric character is after the '@' symbol.
     * 3. If there are no characters before the '@' symbol.
     *
     *
     * If these rules are satisfied, then the search text is valid.
     *
     * @return String   A valid query that satisfies the three rules above.
     */
    fun doMentionCheck(): String {
        var queryToken = ""

        // perform a search if the {@link EditText} has an '@' symbol.
        if (contains(editText.text, "@")) {
            val cursorPosition = editText.selectionStart
            val allTextBeforeCursor = editText.text.toString().substring(0, cursorPosition)
            val providedSearchText = substringAfterLast(allTextBeforeCursor, "@")

            // check search text is within <code>maxCharacters</code> and begins with a
            // alpha numeric char.
            if (searchIsWithinMaxChars(providedSearchText, maxCharacters)
                && searchBeginsWithAlphaNumericChar(providedSearchText)
            ) {
                val atSymbolPosition = lastIndexOf(allTextBeforeCursor, "@")

                // check if search text is first in the view or has a space beforehand if there are
                // more characters in the view.
                if (atSymbolPosition == 0
                    || spaceBeforeAtSymbol(allTextBeforeCursor, atSymbolPosition)
                ) {
                    queryToken = providedSearchText
                }
            }
        }
        return queryToken
    }

    /**
     * If there is text before the '@' symbol, then check if it a white space. This is to prevent
     * performing a mention when the user is typing an email.
     *
     * @param currentTextBeforeCursor String   This is all the text that has been typed into the
     * [EditText], before the current cursor location.
     * @param atSymbolPosition        String   The location of the '@' symbol in the
     * [EditText].
     * @return true or false
     */
    private fun spaceBeforeAtSymbol(currentTextBeforeCursor: String, atSymbolPosition: Int): Boolean {
        if (atSymbolPosition > 0) {
            val charBeforeAtSymbol = currentTextBeforeCursor[atSymbolPosition - 1]
            if (Character.isWhitespace(charBeforeAtSymbol)) {
                return true
            }
        }
        return false
    }

    /**
     * Check if the number of characters after the '@' symbol is within `maxCharacters`.
     *
     * @param providedSearchText String  The text after the '@' symbol entered by the user.
     * @param maxCharacters      int     The maximum number of characters that should be used
     * as a search query. The default is 13 characters, but this
     * value is configurable.
     * @return true or false
     */
    private fun searchIsWithinMaxChars(providedSearchText: String, maxCharacters: Int): Boolean {
        return providedSearchText.length in 1..maxCharacters
    }

    /**
     * Checks if the starting character in the search text is a letter or digit.
     *
     * @param providedSearchText String  The text after the '@' symbol entered by the user.
     * @return true or false
     */
    private fun searchBeginsWithAlphaNumericChar(providedSearchText: String): Boolean {
        return Character.isLetterOrDigit(providedSearchText[0])
    }

    /**
     * Return true if the position of the cursor in [EditText] is on a word that
     * starts with an '@' sign.
     *
     * @return true or false
     */
    fun currentWordStartsWithAtSign(): Boolean {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        if (start == end) {
            //Multiple text is not highlighted
            if (editText.length() >= start) {
                var text = editText.text.toString().substring(0, start)
                text = substringAfterLast(text, " ")
                if (startsWith(text, "@")) {
                    return true
                }
            }
        }
        return false
    }
}