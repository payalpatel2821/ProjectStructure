package com.task.newapp.utils.mentionlib.utils

/**
 * Char Sequence utility methods.
 *
 * Methods were taken from Apache Commons Lang (http://commons.apache.org/proper/commons-lang/)
 * Copied over to avoid requiring additional dependencies.
 */
object CharSequenceUtils {
    /**
     * Used by the indexOf(CharSequence methods) as a green implementation of indexOf.
     *
     * @param cs the `CharSequence` to be processed
     * @param searchChar the `CharSequence` to be searched for
     * @param start the start index
     * @return the index where the search sequence was found
     */
    fun indexOf(cs: CharSequence, searchChar: CharSequence, start: Int): Int {
        return cs.toString().indexOf(searchChar.toString(), start)
    }

    /**
     * Used by the lastIndexOf(CharSequence methods) as a green implementation of lastIndexOf
     *
     * @param cs the `CharSequence` to be processed
     * @param searchChar the `CharSequence` to be searched for
     * @param start the start index
     * @return the index where the search sequence was found
     */
    fun lastIndexOf(cs: CharSequence, searchChar: CharSequence, start: Int): Int {
        return cs.toString().lastIndexOf(searchChar.toString(), start)
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
    fun regionMatches(
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
}