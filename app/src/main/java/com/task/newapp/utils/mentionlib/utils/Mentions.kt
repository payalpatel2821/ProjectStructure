package com.task.newapp.utils.mentionlib.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.task.newapp.utils.mentionlib.adapters.utils.SuggestionsListener
import com.task.newapp.utils.mentionlib.utils.StringUtils.isNotBlank
import java.util.ArrayList

/**
 * Mentions class contains a Builder through which you could set the text highlight color,
 * query listener or add mentions. Pass in an [EditText] view to the builder and the library
 * will setup the ability to '@' mention.
 */
class Mentions private constructor(
    /**
     * [Context].
     */
    protected val context: Context,
    /**
     * [EditText] to setup @ mentions.
     */
    protected val editText: EditText
) {
    /**
     * Notifies client of queries determined to be valid by [MentionCheckerLogic].
     */
    protected var queryListener: QueryListener? = null

    /**
     * Notifies client when to display and hide a suggestions drop down.
     */
    protected var suggestionsListener: SuggestionsListener? = null

    /**
     * Helper class that determines whether a query after @ is valid or not.
     */
    protected val mentionCheckerLogic: MentionCheckerLogic

    /**
     * Helper class for inserting and highlighting mentions.
     */
    protected val mentionInsertionLogic: MentionInsertionLogic

    /**
     * This Builder allows you to configure mentions by setting up a highlight color, max
     * number of words to search or by pre-populating mentions.
     */
    class Builder(context: Context?, editText: EditText?) {
        /**
         * Mention instance.
         */
        private val mentionsLib: Mentions

        /**
         * The [EditText] may have some text and mentions already in it. This method is used
         * to pre-populate the existing [Mentionable]s and highlight them.
         *
         * @param mentions  List<Mentionable>   An array of [Mentionable]s that are
         * currently in the [EditText].
        </Mentionable> */
        fun addMentions(mentions: List<Mentionable?>?): Builder {
            mentionsLib.mentionInsertionLogic.addMentions(mentions)
            return this
        }

        /**
         * Set a color to highlight the mentions' text. The default color is orange.
         *
         * @param color     int     The color to use to highlight a [Mentionable]'s text.
         */
        fun highlightColor(color: Int): Builder {
            mentionsLib.mentionInsertionLogic.textHighlightColor = color
            return this
        }

        /**
         * Set the maximum number of characters the user may have typed until the search text
         * is invalid.
         *
         * @param maxCharacters     int     The number of characters within which anything typed
         * after the '@' symbol will be evaluated.
         */
        fun maxCharacters(maxCharacters: Int): Builder {
            mentionsLib.mentionCheckerLogic.setMaxCharacters(maxCharacters)
            return this
        }

        /**
         * Set a listener that will provide you with a valid token.
         *
         * @param queryListener     QueryListener   The listener to set to be notified of a valid
         * query.
         */
        fun queryListener(queryListener: QueryListener?): Builder {
            mentionsLib.queryListener = queryListener
            return this
        }

        /**
         * Set a listener to notify you whether you should hide or display a drop down with
         * [Mentionable]s.
         *
         * @param suggestionsListener   SuggestionsListener     The listener for display
         * suggestions.
         */
        fun suggestionsListener(suggestionsListener: SuggestionsListener?): Builder {
            mentionsLib.suggestionsListener = suggestionsListener
            return this
        }

        /**
         * Builds and returns a [Mentions] object.
         */
        fun build(): Mentions {
            mentionsLib.hookupInternalTextWatcher()
            mentionsLib.hookupOnClickListener()
            return mentionsLib
        }

        /**
         * Builder which allows you configure mentions. A [Context] and [EditText] is
         * required by the Builder.
         *
         * @param context   Context     Context
         * @param editText  EditText    The [EditText] view to which you want to add the
         * ability to mention.
         */
        init {
            requireNotNull(context) { "Context must not be null." }
            requireNotNull(editText) { "EditText must not be null." }
            mentionsLib = Mentions(context, editText)
        }
    }

    /**
     * You may be pre-loading text with [Mentionable]s. In order to highlight and make those
     * [Mentionable]s recognizable by the library, you may add them by using this method.
     *
     * @param mentionables  List  Any pre-existing mentions that you
     * want to add to the library.
     */
    fun addMentions(mentionables: List<Mentionable?>?) {
        mentionInsertionLogic.addMentions(mentionables)
    }

    /**
     * Returns an array with all the inserted [Mentionable]s in the [EditText].
     *
     * @return List<Mentionable>    An array containing all the inserted [Mentionable]s.
    </Mentionable> */
    val insertedMentions: List<Mentionable>
        get() = ArrayList<Mentionable>(mentionInsertionLogic.getMentions())

    /**
     * If the user sets the cursor after an '@', then perform a mention.
     */
    private fun hookupOnClickListener() {
        editText.setOnClickListener {
            if (mentionCheckerLogic.currentWordStartsWithAtSign()) {
                val query = mentionCheckerLogic.doMentionCheck()
                queryReceived(query)
            } else {
                suggestionsListener!!.displaySuggestions(false)
            }
        }
    }

    /**
     * Set a [TextWatcher] for mentions.
     */
    private fun hookupInternalTextWatcher() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {
                mentionInsertionLogic.checkIfProgrammaticallyClearedEditText(
                    charSequence, start,
                    count, after
                )
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                mentionInsertionLogic.updateInternalMentionsArray(start, before, count)
            }

            override fun afterTextChanged(s: Editable) {
                val query = mentionCheckerLogic.doMentionCheck()
                queryReceived(query)
            }
        })
    }

    /**
     * Insert a mention the user has chosen in the [EditText] and notify the user
     * to hide the suggestions list.
     *
     * @param mentionable   Mentionable     A [Mentionable] chosen by the user to display
     * and highlight in the [EditText].
     */
    fun insertMention(mentionable: Mentionable?) {
        mentionInsertionLogic.insertMention(mentionable!!)
        suggestionsListener!!.displaySuggestions(false)
    }

    /**
     * If the user typed a query that was valid then return it. Otherwise, notify you to close
     * a suggestions list.
     *
     * @param query     String      A valid query.
     */
    fun queryReceived(query: String?) {
        if (queryListener != null && isNotBlank(query)) {
            queryListener!!.onQueryReceived(query)
        } else {
            suggestionsListener!!.displaySuggestions(false)
        }
    }

    /**
     * Pass in your [EditText] to give it the ability to @ mention.
     *
     * @param context   Context     Although not used in the library, it passed for future use.
     * @param editText  EditText    The EditText that will have @ mention capability.
     */
    init {

        // instantiate helper classes
        mentionCheckerLogic = MentionCheckerLogic(editText)
        mentionInsertionLogic = MentionInsertionLogic(editText)
    }
}