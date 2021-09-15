package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ContactHistory : RealmObject() {

    @PrimaryKey
    var id: Int = 0
    var firstName: String = ""
    var lastName: String = ""
    var accountId: String = ""
    var profileImage: String = ""
    var profileColor: String = ""
    var isVisible: Int = 0
    var email: String = ""
    var mobile: String = ""
    var about: String = ""

}
