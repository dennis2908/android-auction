package com.appschef.baseproject.api.callback

import com.appschef.baseproject.api.util.OnAPIListener
import com.appschef.baseproject.api.callback.core.CoreCallback
import com.appschef.baseproject.model.core.AppResponse

import retrofit2.Response

/**
 * Created by Alvin Rusli on 2/7/2016.
 *
 * The default callback for the application.
 */
class AppCallback<RESPONSE: AppResponse>(listener: OnAPIListener<RESPONSE>?) : CoreCallback<RESPONSE>(listener) {

    override fun onSuccess(response: Response<RESPONSE>) {
        handleSuccess(response)
    }

    override fun onFailure(responseCode: Int, errorMessage: String) {
        handleFailure(responseCode, errorMessage)
    }
}
