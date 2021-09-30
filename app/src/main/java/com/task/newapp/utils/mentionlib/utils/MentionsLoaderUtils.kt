package com.task.newapp.utils.mentionlib.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.percolate.mentions.sample.R
import com.percolate.mentions.sample.models.User
import com.task.newapp.models.post.ResponseFriendsList
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

/**
 * Converts all the user data in the file users.json to an array of [User] objects.
 */
class MentionsLoaderUtils(private val context: Context) {
    private val userList: List<ResponseFriendsList.Data>?

    /**
     * Loads users from JSON file.
     */
    private fun loadUsers(): List<ResponseFriendsList.Data> {
        val gson = Gson()
        var users: List<ResponseFriendsList.Data> = ArrayList()
        try {
            val fileReader = context.resources.openRawResource(R.raw.users)
            val bufferedReader = BufferedReader(InputStreamReader(fileReader, "UTF-8"))
            users = gson.fromJson(bufferedReader, object : TypeToken<List<ResponseFriendsList.Data?>?>() {}.type)
        } catch (ex: IOException) {
            Log.e("Mentions Sample", "Error: Failed to parse json file.")
        }
        return users
    }

    /**
     * Search for user with name matching `query`.
     *
     * @return a list of users that matched `query`.
     */
    fun searchUsers(query: String): ArrayList<ResponseFriendsList.Data> {
        var query = query
//        val searchResults: MutableList<ResponseFriendsList.Data> = ArrayList()
        val searchResults: ArrayList<ResponseFriendsList.Data> = ArrayList()
        if (StringUtils.isNotBlank(query)) {
            query = query.toLowerCase(Locale.US)
            if (userList != null && userList.isNotEmpty()) {
                for (user in userList) {
                    val firstName = user.firstName!!.toLowerCase()
                    val lastName = user.lastName!!.toLowerCase()
                    if (firstName.startsWith(query) || lastName.startsWith(query)) {
                        searchResults.add(user)
                    }
                }
            }
        }
        return searchResults
    }

    init {
        userList = loadUsers()
    }
}