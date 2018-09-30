package com.appschef.baseproject.api.util

import com.appschef.baseproject.model.core.AppError

/**
 * The listener interface for API calls.
 */
interface OnAPIListener<in RESPONSE> {

    /** Called when an API is successfully called  */
    fun onApiSuccess(response: RESPONSE)

    /** Called when the API doesn't respond or something went wrong  */
    fun onApiFailure(error: AppError)
}