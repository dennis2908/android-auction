package com.appschef.baseproject.api

import com.appschef.baseproject.util.JNIUtil
import com.appschef.baseproject.api.util.OkHttpClientHelper
import com.appschef.baseproject.model.remote.auth.ProfileResponse
import com.appschef.baseproject.model.remote.auth.SignInRequest
import com.appschef.baseproject.model.remote.auth.SignInResponse
import com.appschef.baseproject.model.remote.product.SampleProductListResponse

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Alvin Rusli on 7/28/2016.
 *
 * The API interface service.
 */
object APIService {

    /** The [APIInterface] object */
    private var apiInterface: APIInterface? = null

    /** @return the [APIInterface] object */
    fun getAPIInterface(): APIInterface {
        if (apiInterface == null) {
            val retrofit = Retrofit.Builder()
                    .baseUrl(JNIUtil.apiUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkHttpClientHelper().initOkHttpClient())
                    .build()

            apiInterface = retrofit.create(APIInterface::class.java)
        }

        return apiInterface!!
    }

    /** The interface for retrofit's API calls */
    interface APIInterface {

        /** Logs the user into the system */
        @POST("login")
        fun signIn(
                // For easy visibility (test purposes), this sample will use a query
                @Query("Api-Key") apiKey: String?,
                //                @Header("Api-Key") String apiKey,
                @Body body: SignInRequest?)
                : Call<SignInResponse>

        /** Obtain the user profile */
        @GET("profile")
        fun getProfile(
                // For easy visibility (test purposes), this sample will use a query
                @Query("Api-Key") apiKey: String?,
                @Query("Token") accessToken: String?)
//                @Header("Api-Key") String apiKey,
//                @Header("Token") String accessToken);
                : Call<ProfileResponse>

        /** Obtain the product list */
        @GET("products")
        fun getProducts(
                @Header("Api-Key") apiKey: String?,
                @Header("Token") accessToken: String?,
                @Query("page") page: Int?)
                : Call<SampleProductListResponse>
    }
}
