package com.example.repokoltin.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RepoRealm(
    @PrimaryKey
    var id: Int? = null,

    var fullname: String? = null,
    var des: String? = null,
    var star: String? = null,
    var fork: String? = null,
    var lang: String? = null
) : RealmObject() {

}