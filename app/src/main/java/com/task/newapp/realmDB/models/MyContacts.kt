// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MyContacts : RealmObject() {

    @PrimaryKey
    var contactId: Long = 0
    var id: Int = 0
    var fullName: String = ""
    var email: String = ""
    var number: String = ""
    var isAppUser: Int = 0
    var appUserId: Int = 0
    var registerType: String = ""
    var profileColor: String = ""
    var profileImage: String = ""

}
