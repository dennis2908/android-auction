package com.appschef.baseproject.model.remote.auth

import com.appschef.baseproject.model.core.AppResponse
import com.google.gson.annotations.SerializedName

/**
 * Created by Alvin Rusli on 10/10/2016.
 */
class SignInResponse : AppResponse() {

    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("expiry_date")
    var expiryDate: String? = null

    @SerializedName("profile_data")
    var profile: Profile? = null
}
