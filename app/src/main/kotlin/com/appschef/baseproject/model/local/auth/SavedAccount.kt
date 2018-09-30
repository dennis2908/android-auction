package com.appschef.baseproject.model.local.auth

import com.appschef.baseproject.model.remote.auth.Profile
import com.google.gson.annotations.SerializedName

/**
 * Created by Alvin Rusli on 06/07/2016.
 */
class SavedAccount {

    @SerializedName("id")
    var id: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("expired_date")
    var expiryDate: String? = null

    @SerializedName("profile_data")
    var profile: Profile? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as SavedAccount
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}