package com.appschef.baseproject.api

import com.appschef.baseproject.api.callback.AppCallback
import com.appschef.baseproject.api.callback.ProductListCallback
import com.appschef.baseproject.api.callback.SignInCallback
import com.appschef.baseproject.api.callback.core.CoreCallback
import com.appschef.baseproject.util.JNIUtil
import com.appschef.baseproject.api.util.OnAPIListener
import com.appschef.baseproject.model.core.AppResponse
import com.appschef.baseproject.model.remote.auth.ProfileResponse
import com.appschef.baseproject.model.remote.auth.SignInRequest
import com.appschef.baseproject.model.remote.auth.SignInResponse
import com.appschef.baseproject.model.remote.product.SampleProductListResponse
import retrofit2.Call
import retrofit2.Callback

/**
 * Created by Alvin Rusli on 6/23/2016.
 *
 * A class that handles all API calls.
 */
class APICaller<RESPONSE : AppResponse> {

    /** The access token for API calls */
    private var accessToken: String? = null

    /** The api key for API calls */
    private var apiKey: String? = JNIUtil.apiKey

    /**
     * Determines if this API call uses a specific access token,
     * instead of using the access token from saved profile.
     */
    private var isUsingCustomToken = false

    /** The listener object for API calls */
    private var listener: OnAPIListener<RESPONSE>? = null

    /** The [Call] object */
    private var call: Call<*>? = null

    /** The [CoreCallback] */
    private var callback: Callback<*>? = null

    /**
     * Sets the access token.
     * When called, this will override the default token loaded from saved data.
     * @param accessToken the access token
     * @return this
     */
    fun withAccessToken(accessToken: String): APICaller<RESPONSE> {
        this.accessToken = accessToken
        isUsingCustomToken = true
        return this
    }

    /**
     * Sets the API key.
     * When called, this will override the default API key.
     * @param apiKey the API key
     * @return this
     */
    fun withApiKey(apiKey: String): APICaller<RESPONSE> {
        this.apiKey = apiKey
        return this
    }

    /**
     * Adds a listener for the helper class.
     * @param listener the [OnAPIListener] object
     * @return this
     */
    fun withListener(listener: OnAPIListener<RESPONSE>): APICaller<RESPONSE> {
        this.listener = listener
        return this
    }

    /**
     * Checks if the call has been executed / enqueued.
     * Be careful, a call that has been executed will return true even on failure.
     */
    val isExecuted: Boolean?
        get() = call?.isExecuted

    /** Cancels any ongoing call */
    fun cancel() {
        call?.cancel()
    }

    /** Clears the current callback and call */
    fun clear() {
        callback = null
        call = null
    }

    /** Refresh the current access token with the token from the profile */
    private fun refreshAccessToken() {
        if (!isUsingCustomToken) accessToken = "new-access-token"
//        if (!isUsingCustomToken) accessToken = AccountHelper.getToken(App.context)
    }

    /**
     * Calls the sign in API.
     * Logs the user in to the system.
     * @param data the data to be posted
     */
    @Suppress("UNCHECKED_CAST")
    fun signIn(data: SignInRequest) {
        refreshAccessToken()

        callback = SignInCallback(listener as OnAPIListener<SignInResponse>)
        call = APIService.getAPIInterface().signIn(apiKey, data)
        (call as Call<SignInResponse>).enqueue(callback as SignInCallback)
    }

    /**
     * Calls the sign in API.
     * Obtain the user profile.
     */
    @Suppress("UNCHECKED_CAST")
    fun getProfile() {
        refreshAccessToken()

        callback = AppCallback(listener)
        call = APIService.getAPIInterface().getProfile(apiKey, accessToken)
        (call as Call<ProfileResponse>).enqueue(callback as Callback<ProfileResponse>?)
    }

    /**
     * Calls the product list API.
     * Obtain the product list.
     */
    @Suppress("UNCHECKED_CAST")
    fun getProducts(page: Int) {
        refreshAccessToken()

//        callback = AppCallback(listener as OnAPIListener<AppResponse>)
        callback = ProductListCallback(listener as OnAPIListener<SampleProductListResponse>)
        call = APIService.getAPIInterface().getProducts(apiKey, accessToken, page)
        (call as Call<SampleProductListResponse>).enqueue(callback as ProductListCallback)
    }
}
