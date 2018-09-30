package com.appschef.baseproject.model.remote.auth

import com.google.gson.annotations.SerializedName

/**
 * Created by Alvin Rusli on 10/10/2016.
 */
class SignInRequest() {

    @SerializedName("email")
    var email: String? = null

    @SerializedName("password")
    var password: String? = null

    constructor(email: String, password: String) : this() {
        this.email = email
        this.password = password
    }
}
