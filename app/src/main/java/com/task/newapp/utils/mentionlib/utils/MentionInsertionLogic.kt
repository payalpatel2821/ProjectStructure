package com.task.newapp.utils.mentionlib.utils

import android.text.InputType
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.percolate.mentions.sample.R
import com.task.newapp.utils.mentionlib.utils.StringUtils.isBlank
import com.task.newapp.utils.mentionlib.utils.StringUtils.isNotBlank
import com.task.newapp.utils.mentionlib.utils.StringUtils.equals
import com.task.newapp.utils.mentionlib.utils.StringUtils.substring
import java.lang.Exception
import java.util.ArrayList

/**
 * Insert and highlights a [Mentionable] in the [EditText]. All [Mentionable]s
 * are appended to the `mentions` array. The [Mentionable]'s offset and length
 * values are updated as text is edited in the [EditText]. The default text highlight color
 * is orange and it is configurable.
 */
class MentionInsertionLogic(
    /**
     * The [EditText] we are inserting and highlighting mentions into.
     */
    private val editText: EditText?
) {
    /**
     * An internal array that keeps track of all the mentions added to [EditText].
     */
    private val mentions: MutableList<Mentionable?>

    /**
     * Text color of the mention in the [EditText]. The default color is orange.
     */
    var textHighlightColor: Int

    /**
     * Returns all the [Mentionable]s added to the [EditText] by invoking
     * `MentionInsertionLogic#insertMention`.
     *
     * @return List<Mentionable>    All the [Mentionable]s added to edit [EditText].
    </Mentionable> */
    fun getMentions(): List<Mentionable?> {
        return mentions
    }

    /**
     * Append predefined [Mentionable]s into the [EditText]. The [EditText] must
     * contain [Mentionable]s that you are adding.
     *
     * @param mentions List<Mentionable>   A list of mentions that you want to pre-populate.
    </Mentionable> */
    fun addMentions(mentions: List<Mentionable?>?) {
        require(!(mentions == null || mentions.isEmpty())) { "Appended Mentions cannot be null nor empty." }
        require(!(isBlank(editText!!.text) || !textHasMentions(mentions))) { "Appended Mentions must be in the edit text." }
        this.mentions.addAll(mentions)
        highlightMentionsText()
    }

    /**
     * Set text highlight of the [Mentionable]'s name.
     *
     * @param textHighlightColor The text color of the mention.
     */
    @JvmName("setTextHighlightColor1")
    fun setTextHighlightColor(textHighlightColor: Int) {
        this.textHighlightColor = textHighlightColor
    }

    /**
     * Inserts a [Mentionable] into an [EditText] by inserting the mentions'
     * name, highlighting it and keeping track of it in the array `mentions`.
     *
     * @param mention Mentionable     A mention to display in [EditText].
     */
    fun insertMention(mention: Mentionable) {
        checkMentionable(mention)
        mention.mentionLength = mention.mentionName!!.length
        val cursorPosition = editText!!.selectionEnd
        val text = editText.text.toString()
        val toReplace = text.substring(0, cursorPosition)
        val start = toReplace.lastIndexOf("@")
        if (start != -1) {
            val newCursorPosition: Int = start + mention.mentionName!!.length + 1
            editText.text.delete(start, cursorPosition)
            editText.text.insert(start, mention.mentionName.toString() + " ")

            // Fix bug on LG G3 phone, where EditText messes up when using insert() method.
            val originalInputType = editText.inputType
            editText.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            editText.inputType = originalInputType
            editText.setSelection(newCursorPosition)
            addMentionToInternalArray(mention, start)
            highlightMentionsText()
        }
    }

    /**
     * Determine if the user is not inserting a null [Mentionable] and the
     * [Mentionable]'s name was set.
     *
     * @param mentionable Mentionable     The [Mentionable] being inserted into the
     * [EditText].
     */
    private fun checkMentionable(mentionable: Mentionable?) {
        requireNotNull(mentionable) {
            "A null mentionable cannot be inserted into the " +
                    "EditText view"
        }
        require(!isBlank(mentionable.mentionName)) {
            "The mentions name must be set before inserting " +
                    "into the EditText view."
        }
    }

    /**
     * Appends a new [Mentionable] into the `mentions` array.
     *
     * @param mention Mentionable     A new mention that was inserted into [EditText].
     * @param start   int             The offset of the new mention.
     */
    private fun addMentionToInternalArray(mention: Mentionable?, start: Int) {
        if (mention != null) {
            mention.mentionOffset = start
            mentions.add(mention)
        }
    }

    /**
     * If you call `EditText#setText` and clear the text by insert an empty string, then
     * all the mentions added to the [EditText] should be removed. This method checks this
     * case and remove all the added [Mentionable]s from `mentions`.
     *
     * @param charSequence CharSequence    The text that will be changed.
     * @param start        int             The initial position in `charSequence` where
     * the text will be changed.
     * @param count        int             The number of characters that will be changed in
     * `charSequence`.
     * @param after        int             The length of the new text entered by the user.
     */
    fun checkIfProgrammaticallyClearedEditText(
        charSequence: CharSequence, start: Int,
        count: Int, after: Int
    ) {
        if (isNotBlank(charSequence) && start == 0 && count == charSequence.length && after == 0) {
            mentions.clear()
        }
    }

    /**
     * Need to keep mentions up-to-date
     * Consideration:
     * - If editing within an existing mention, remove it.
     * - If editing before an existing mentions, update the start of them.
     *
     * @param start  int     Initial position of the new text.
     * @param before int     Length of old text.
     * @param count  int     The number of characters in the new text.
     */
    fun updateInternalMentionsArray(start: Int, before: Int, count: Int) {
        if (!mentions.isEmpty()) {
            if (before != count) { // Text not changed if they ==.
                val iterator: MutableIterator<Mentionable?> = mentions.iterator()
                while (iterator.hasNext()) {
                    val mention: Mentionable? = iterator.next()
                    val mentionStart: Int = mention!!.mentionOffset
                    val mentionEnd: Int = mentionStart + mention!!.mentionLength
                    val editPos = start + count
                    if (start <= mentionStart) {
                        //Editing text before mention - change offset
                        val diff = count - before
                        mention.mentionOffset = mentionStart + diff
                    } else if (editPos > mentionStart + 1 && editPos < mentionEnd) {
                        //Editing text within mention - delete the mention
                        iterator.remove()
                    }
                }
                highlightMentionsText()
            }
        }
    }

    /**
     * Highlight all the [Mentionable]s in the [EditText]. A [ForegroundColorSpan]
     * is set at the starting and ending locations of the [Mentionable]s.
     */
    private fun highlightMentionsText() {
        // Clear current highlighting (note: just using clearSpans(); makes EditText fields act
        // strange).
        val spans = editText!!.editableText.getSpans(
            0,
            editText.text.length, ForegroundColorSpan::class.java
        )
        for (span in spans) {
            editText.editableText.removeSpan(span)
        }
        if (mentions.isNotEmpty()) {
            val iterator: MutableIterator<Mentionable?> = mentions.iterator()
            while (iterator.hasNext()) {
                val mention: Mentionable? = iterator.next()
                try {
                    val start: Int = mention!!.mentionOffset
                    val end: Int = start + mention!!.mentionLength
                    if (editText.length() >= end && equals(
                            editText.text
                                .subSequence(start, end), mention.mentionName
                        )
                    ) {
                        val highlightSpan = ForegroundColorSpan(
                            ContextCompat.getColor(editText.context, textHighlightColor)
                        )
                        editText.editableText.setSpan(
                            highlightSpan, start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    } else {
                        //Something went wrong.  The expected text that we're trying to highlight does
                        // not match the actual text at that position.
                        Log.w("Mentions", "Mention lost. [" + mention.mentionName.toString() + "]")
                        iterator.remove()
                    }
                } catch (ex: Exception) {
                    Log.e(
                        "Mentions", "Mention removed due to exception. + [" +
                                mention!!.mentionName.toString() + "]", ex
                    )
                    iterator.remove()
                }
            }
        }
    }

    /**
     * If you prepopulate an [EditText] with [Mentionable]s, then we check whether the
     * view has all the [Mentionable]s at their offsets and length.
     *
     * @param mentions List<Mentionable>   List of pre-defined mentions in the [EditText].
     * @return true or false
    </Mentionable> */
    private fun textHasMentions(mentions: List<Mentionable?>?): Boolean {
        if (editText != null && mentions != null && !mentions.isEmpty()) {
            for (mention in mentions) {
                val mentionStart: Int = mention!!.mentionOffset
                val mentionEnd: Int = mention!!.mentionLength
                if (mentionEnd <= editText.text.length) {
                    val displayText = substring(
                        editText.text.toString(),
                        mentionStart, mentionStart + mentionEnd
                    )
                    if (isBlank(displayText) || !equals(
                            displayText,
                            mention.mentionName
                        )
                    ) {
                        return false
                    }
                }
            }
        }
        return true
    }

    init {
        mentions = ArrayList<Mentionable?>()
        textHighlightColor = R.color.mentions_default_color
    }
}