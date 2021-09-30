package com.task.newapp.utils.mentionlib.utils

/**
 * String utility methods.
 *
 * Methods were taken from Apache Commons Lang (http://commons.apache.org/proper/commons-lang/)
 * Copied over to avoid requiring additional dependencies.
 */
object StringUtils {
    /**
     * The empty String `""`.
     */
    private const val EMPTY = ""

    /**
     * Represents a failed index search.
     * @since 2.1
     */
    private const val INDEX_NOT_FOUND = -1

    /**
     *
     * Checks if a CharSequence is not empty (""), not null and not whitespace only.
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
    </pre> *
     *
     * @param cs  the CharSequence to check, may be null
     * @return `true` if the CharSequence is
     * not empty and not null and not whitespace
     * @since 2.0
     * @since 3.0 Changed signature from isNotBlank(String) to isNotBlank(CharSequence)
     */
    fun isNotBlank(cs: CharSequence?): Boolean {
        return !isBlank(cs)
    }

    /**
     *
     * Checks if a CharSequence is whitespace, empty ("") or null.
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
    </pre> *
     *
     * @param cs  the CharSequence to check, may be null
     * @return `true` if the CharSequence is null, empty or whitespace
     * @since 2.0
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    fun isBlank(cs: CharSequence?): Boolean {
        var strLen: Int = 0
        if (cs == null || cs.length.also { strLen = it } == 0) {
            return true
        }
        for (i in 0 until strLen) {
            if (Character.isWhitespace(cs[i]) == false) {
                return false
            }
        }
        return true
    }

    /**
     *
     * Compares two CharSequences, returning `true` if they represent
     * equal sequences of characters.
     *
     *
     * `null`s are handled without exceptions. Two `null`
     * references are considered to be equal. The comparison is case sensitive.
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
    </pre> *
     *
     * @see Object.equals
     * @param cs1  the first CharSequence, may be `null`
     * @param cs2  the second CharSequence, may be `null`
     * @return `true` if the CharSequences are equal (case-sensitive), or both `null`
     * @since 3.0 Changed signature from equals(String, String) to equals(CharSequence, CharSequence)
     */
    fun equals(cs1: CharSequence?, cs2: CharSequence?): Boolean {
        if (cs1 === cs2) {
            return true
        }
        if (cs1 == null || cs2 == null) {
            return false
        }
        return if (cs1 is String && cs2 is String) {
            cs1 == cs2
        } else regionMatches(cs1, false, 0, cs2, 0, Math.max(cs1.length, cs2.length))
    }

    /**
     * Green implementation of regionMatches.
     *
     * @param cs the `CharSequence` to be processed
     * @param ignoreCase whether or not to be case insensitive
     * @param thisStart the index to start on the `cs` CharSequence
     * @param substring the `CharSequence` to be looked for
     * @param start the index to start on the `substring` CharSequence
     * @param length character length of the region
     * @return whether the region matched
     */
    private fun regionMatches(
        cs: CharSequence, ignoreCase: Boolean, thisStart: Int,
        substring: CharSequence, start: Int, length: Int
    ): Boolean {
        if (cs is String && substring is String) {
            return cs.regionMatches(thisStart, substring, start, length, ignoreCase = ignoreCase)
        }
        var index1 = thisStart
        var index2 = start
        var tmpLen = length
        while (tmpLen-- > 0) {
            val c1 = cs[index1++]
            val c2 = substring[index2++]
            if (c1 == c2) {
                continue
            }
            if (!ignoreCase) {
                return false
            }

            // The same check as in String.regionMatches():
            if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
                && Character.toLowerCase(c1) != Character.toLowerCase(c2)
            ) {
                return false
            }
        }
        return true
    }

    /**
     *
     * Gets a substring from the specified String avoiding exceptions.
     *
     *
     * A negative start position can be used to start/end `n`
     * characters from the end of the String.
     *
     *
     * The returned substring starts with the character in the `start`
     * position and ends before the `end` position. All position counting is
     * zero-based -- i.e., to start at the beginning of the string use
     * `start = 0`. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.
     *
     *
     * If `start` is not strictly to the left of `end`, ""
     * is returned.
     *
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", * ,  *)    = "";
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, 4)   = "c"
     * StringUtils.substring("abc", 4, 6)   = ""
     * StringUtils.substring("abc", 2, 2)   = ""
     * StringUtils.substring("abc", -2, -1) = "b"
     * StringUtils.substring("abc", -4, 2)  = "ab"
    </pre> *
     *
     * @param str  the String to get the substring from, may be null
     * @param start  the position to start from, negative means
     * count back from the end of the String by this many characters
     * @param end  the position to end at (exclusive), negative means
     * count back from the end of the String by this many characters
     * @return substring from start position to end position,
     * `null` if null String input
     */
    fun substring(str: String?, start: Int, end: Int): String? {
        var start = start
        var end = end
        if (str == null) {
            return null
        }

        // handle negatives
        if (end < 0) {
            end = str.length + end // remember end is negative
        }
        if (start < 0) {
            start = str.length + start // remember start is negative
        }

        // check length next
        if (end > str.length) {
            end = str.length
        }

        // if start is greater than end, return ""
        if (start > end) {
            return EMPTY
        }
        if (start < 0) {
            start = 0
        }
        if (end < 0) {
            end = 0
        }
        return str.substring(start, end)
    }

    /**
     *
     * Checks if CharSequence contains a search CharSequence, handling `null`.
     * This method uses [String.indexOf] if possible.
     *
     *
     * A `null` CharSequence will return `false`.
     *
     * <pre>
     * StringUtils.contains(null, *)     = false
     * StringUtils.contains(*, null)     = false
     * StringUtils.contains("", "")      = true
     * StringUtils.contains("abc", "")   = true
     * StringUtils.contains("abc", "a")  = true
     * StringUtils.contains("abc", "z")  = false
    </pre> *
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchSeq  the CharSequence to find, may be null
     * @return true if the CharSequence contains the search CharSequence,
     * false if not or `null` string input
     * @since 2.0
     * @since 3.0 Changed signature from contains(String, String) to contains(CharSequence, CharSequence)
     */
    fun contains(seq: CharSequence?, searchSeq: CharSequence?): Boolean {
        return if (seq == null || searchSeq == null) {
            false
        } else CharSequenceUtils.indexOf(seq, searchSeq, 0) >= 0
    }

    /**
     *
     * Gets the substring after the last occurrence of a separator.
     * The separator is not returned.
     *
     *
     * A `null` string input will return `null`.
     * An empty ("") string input will return the empty string.
     * An empty or `null` separator will return the empty string if
     * the input string is not `null`.
     *
     *
     * If nothing is found, the empty string is returned.
     *
     * <pre>
     * StringUtils.substringAfterLast(null, *)      = null
     * StringUtils.substringAfterLast("", *)        = ""
     * StringUtils.substringAfterLast(*, "")        = ""
     * StringUtils.substringAfterLast(*, null)      = ""
     * StringUtils.substringAfterLast("abc", "a")   = "bc"
     * StringUtils.substringAfterLast("abcba", "b") = "a"
     * StringUtils.substringAfterLast("abc", "c")   = ""
     * StringUtils.substringAfterLast("a", "a")     = ""
     * StringUtils.substringAfterLast("a", "z")     = ""
    </pre> *
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring after the last occurrence of the separator,
     * `null` if null String input
     * @since 2.0
     */
    fun substringAfterLast(str: String, separator: String): String {
        if (isEmpty(str)) {
            return str
        }
        if (isEmpty(separator)) {
            return EMPTY
        }
        val pos = str.lastIndexOf(separator)
        return if (pos == INDEX_NOT_FOUND || pos == str.length - separator.length) {
            EMPTY
        } else str.substring(pos + separator.length)
    }

    /**
     *
     * Checks if a CharSequence is empty ("") or null.
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
    </pre> *
     *
     *
     * NOTE: This method changed in Lang version 2.0.
     * It no longer trims the CharSequence.
     * That functionality is available in isBlank().
     *
     * @param cs  the CharSequence to check, may be null
     * @return `true` if the CharSequence is empty or null
     * @since 3.0 Changed signature from isEmpty(String) to isEmpty(CharSequence)
     */
    private fun isEmpty(cs: CharSequence?): Boolean {
        return cs == null || cs.length == 0
    }

    /**
     *
     * Finds the last index within a CharSequence, handling `null`.
     * This method uses [String.lastIndexOf] if possible.
     *
     *
     * A `null` CharSequence will return `-1`.
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *)          = -1
     * StringUtils.lastIndexOf(*, null)          = -1
     * StringUtils.lastIndexOf("", "")           = 0
     * StringUtils.lastIndexOf("aabaabaa", "a")  = 7
     * StringUtils.lastIndexOf("aabaabaa", "b")  = 5
     * StringUtils.lastIndexOf("aabaabaa", "ab") = 4
     * StringUtils.lastIndexOf("aabaabaa", "")   = 8
    </pre> *
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchSeq  the CharSequence to find, may be null
     * @return the last index of the search String,
     * -1 if no match or `null` string input
     * @since 2.0
     * @since 3.0 Changed signature from lastIndexOf(String, String) to lastIndexOf(CharSequence, CharSequence)
     */
    fun lastIndexOf(seq: CharSequence?, searchSeq: CharSequence?): Int {
        return if (seq == null || searchSeq == null) {
            INDEX_NOT_FOUND
        } else CharSequenceUtils.lastIndexOf(seq, searchSeq, seq.length)
    }

    /**
     *
     * Check if a CharSequence starts with a specified prefix.
     *
     *
     * `null`s are handled without exceptions. Two `null`
     * references are considered to be equal. The comparison is case sensitive.
     *
     * <pre>
     * StringUtils.startsWith(null, null)      = true
     * StringUtils.startsWith(null, "abc")     = false
     * StringUtils.startsWith("abcdef", null)  = false
     * StringUtils.startsWith("abcdef", "abc") = true
     * StringUtils.startsWith("ABCDEF", "abc") = false
    </pre> *
     *
     * @see String.startsWith
     * @param str  the CharSequence to check, may be null
     * @param prefix the prefix to find, may be null
     * @return `true` if the CharSequence starts with the prefix, case sensitive, or
     * both `null`
     * @since 2.4
     * @since 3.0 Changed signature from startsWith(String, String) to startsWith(CharSequence, CharSequence)
     */
    fun startsWith(str: CharSequence?, prefix: CharSequence?): Boolean {
        return startsWith(str, prefix, false)
    }

    /**
     *
     * Check if a CharSequence starts with a specified prefix (optionally case insensitive).
     *
     * @see String.startsWith
     * @param str  the CharSequence to check, may be null
     * @param prefix the prefix to find, may be null
     * @param ignoreCase indicates whether the compare should ignore case
     * (case insensitive) or not.
     * @return `true` if the CharSequence starts with the prefix or
     * both `null`
     */
    private fun startsWith(str: CharSequence?, prefix: CharSequence?, ignoreCase: Boolean): Boolean {
        if (str == null || prefix == null) {
            return str == null && prefix == null
        }
        return if (prefix.length > str.length) {
            false
        } else CharSequenceUtils.regionMatches(str, ignoreCase, 0, prefix, 0, prefix.length)
    }
}