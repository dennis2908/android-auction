package com.appschef.baseproject.api.callback.core

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.appschef.baseproject.App
import com.appschef.baseproject.BuildConfig
import com.appschef.baseproject.R
import com.appschef.baseproject.api.util.OnAPIListener
import com.appschef.baseproject.model.core.AppError
import com.appschef.baseproject.model.core.AppResponse
import com.appschef.baseproject.util.Common
import itsmagic.present.simpleaccountmanager.AccountHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.charset.Charset
import javax.net.ssl.SSLException

/**
 * Created by Alvin Rusli on 2/7/2016.
 *
 * A custom [Callback] class that handles the obtained response to
 * actually determine if it's a success or failure.
 */
abstract class CoreCallback<RESPONSE : AppResponse>(private val listener: OnAPIListener<RESPONSE>?) : Callback<RESPONSE> {

    override fun onResponse(call: Call<RESPONSE>, response: Response<RESPONSE>) {
        handleResponse(response)
    }

    override fun onFailure(call: Call<RESPONSE>, throwable: Throwable) {
        // If call is cancelled, don't do anything
        if (call.isCanceled) return

        if (throwable is UnknownHostException || throwable is SSLException || throwable is ConnectException) {
            onFailure(AppError.CLIENT_CONNECTION, if (BuildConfig.DEBUG) throwable.toString() else App.context.getString(R.string.error_conn_generic))
        } else if (throwable is SocketTimeoutException) {
            onFailure(AppError.CLIENT_TIMEOUT, if (BuildConfig.DEBUG) throwable.toString() else App.context.getString(R.string.error_conn_time_out))
        } else {
            onFailure(AppError.CLIENT_UNKNOWN, if (BuildConfig.DEBUG) throwable.toString() else App.context.getString(R.string.error_unknown))
        }
    }

    /**
     * Handles the obtained [Response] to determine if response is a success or failure.
     * This method calls the abstract [.onSuccess] and [.onFailure] method when needed.
     */
    private fun handleResponse(response: Response<RESPONSE>?) {
        var responseCode = AppError.CLIENT_UNKNOWN
        var errorMessage: String = ""
        if (response != null) {
            // Get the response code first
            responseCode = response.code()

            if (!response.isSuccessful) {
                // On error

                // Set the error message from the response first
                if (response.errorBody() != null) {
                    try {
                        val bytes = response.errorBody()!!.bytes()
                        val appResponse = AppResponse(String(bytes, Charset.forName("UTF-8")))
                        errorMessage = appResponse.meta!!.message!!
                    } catch (e: IOException) {
                        Common.printStackTrace(e)
                    }
                } else if (response.raw()?.message() != null) {
                    errorMessage = response.raw().message()
                }

                // If error message contains a "</html>" (basically broken response) on release builds,
                // make it an empty String here instead
                if (!BuildConfig.DEBUG) {
                    if (errorMessage.contains("</html>")) errorMessage = ""
                }

                // If error message is still empty or is a JSON error,
                // use the specified error messages from the error code
                if (errorMessage.isEmpty() || errorMessage == App.context.getString(R.string.error_json)) {
                    errorMessage = when (responseCode) {
                        400 -> App.context.getString(R.string.error_conn_400)
                        401 -> App.context.getString(R.string.error_conn_401)
                        403 -> App.context.getString(R.string.error_conn_403)
                        404 -> App.context.getString(R.string.error_conn_404)
                        408 -> App.context.getString(R.string.error_conn_408)
                        500 -> App.context.getString(R.string.error_conn_500)
                        501 -> App.context.getString(R.string.error_conn_501)
                        502 -> App.context.getString(R.string.error_conn_502)
                        503 -> App.context.getString(R.string.error_conn_503)
                        504 -> App.context.getString(R.string.error_conn_504)
                        else -> errorMessage
                    }

                    if (response.message() != null) {
                        if (BuildConfig.DEBUG) errorMessage = response.message()
                        else errorMessage = App.context.getString(R.string.error_conn_generic)
                    }

                    // Additional check for release builds
                    if (!BuildConfig.DEBUG) {
                        if (responseCode in 500..599) {
                            errorMessage = App.context.getString(R.string.error_conn_maintenance)
                        }
                    }
                }

                // TODO: Add project specific condition here
                if (responseCode == 401) onUnauthorizedFailure(errorMessage)

                // Finally call on failure
                onFailure(responseCode, errorMessage)
            } else {
                // On success
                // Null check before confirming it as a success
                if (response.body() == null) {
                    errorMessage = App.context.getString(R.string.error_conn_generic)
                    onFailure(responseCode, errorMessage)
                } else {
                    // Finally, a successful response
                    onSuccess(response)
                }
            }
        } else {
            errorMessage = App.context.getString(R.string.error_unknown)
            onFailure(responseCode, errorMessage)
        }
    }

    /**
     * Handles the generic success response by calling the listener with the obtained data.
     * @param response the obtained response
     */
    fun handleSuccess(response: Response<RESPONSE>?) {
        if (response == null) {
            callListenerFailure(AppError(AppError.DEVELOPMENT_NULL_POINTER, App.context.getString(R.string.error_unknown)))
            return
        }

        callListenerSuccess(response.body())
    }

    /**
     * Handles the generic failure response by calling the listener with the generic error message.
     * @param responseCode the response code
     * @param errorMessage the error message
     */
    fun handleFailure(responseCode: Int, errorMessage: String) {
        val message: String
        if (BuildConfig.DEBUG) {
            message = String.format(
                    App.context.getString(R.string.error_message_generic_detail_code),
                    responseCode,
                    errorMessage)
        } else {
            message = String.format(
                    App.context.getString(R.string.error_message_generic_detail),
                    errorMessage)
        }

        val appError = AppError(responseCode, message)
        callListenerFailure(appError)
    }

    /**
     * Call a success on the listener.
     * @param data the obtained data
     */
    fun callListenerSuccess(data: RESPONSE?) {
        if (listener != null) {
            if (data == null) {
                callListenerFailure(AppError(AppError.DEVELOPMENT_NULL_POINTER, App.context.getString(R.string.error_unknown)))
                return
            }

            var exception: Exception? = null

            try {
                listener.onApiSuccess(data)
            } catch (e: Exception) {
                exception = e
                Common.printStackTrace(e)
            }

            if (exception != null) {
                if (exception is ClassCastException) callListenerFailure(AppError(AppError.DEVELOPMENT_CLASS_CAST, exception.message))
                else callListenerFailure(AppError(AppError.DEVELOPMENT_UNKNOWN, exception.message))
            }
        }
    }

    /**
     * Call a success on the listener.
     * The listener will be explicitly called on UI thread.
     * @param data the obtained data
     */
    fun callListenerSuccessOnUiThread(data: RESPONSE?) {
        Handler(Looper.getMainLooper()).post { callListenerSuccess(data) }
    }

    /**
     * Call a failure on the listener.
     * @param error the [Exception] object
     */
    fun callListenerFailure(error: AppError) {
        if (listener != null) {
            try {
                listener.onApiFailure(error)
            } catch (e: Exception) {
                listener.onApiFailure(AppError(AppError.DEVELOPMENT_UNKNOWN, e.message))
                Common.printStackTrace(e)
            }
        }
    }

    /**
     * Call a failure on the listener.
     * The listener will be explicitly called on UI thread.
     * @param error the [Exception] object
     */
    fun callListenerFailureOnUiThread(error: AppError) {
        Handler(Looper.getMainLooper()).post { callListenerFailure(error) }
    }

    /**
     * Sample function to handle 401 error code.
     * Each project may differ in its implementation, this is just a sample.
     *
     * Unauthorized means invalid API key or invalid token,
     * in this app, log the user out forcefully.
     */
    protected fun onUnauthorizedFailure(errorMessage: String) {
        Common.log(Log.ERROR, javaClass.simpleName, "Unauthorized access: $errorMessage - Removing account")
        Common.log(Log.ERROR, javaClass.simpleName, "Removing saved account from this device")

        AccountHelper(App.context).removeAccount()
        Common.showToast(R.string.error_auth_log_in_expired)
    }

    /** Called when the obtained response is successful  */
    abstract fun onSuccess(response: Response<RESPONSE>)

    /** Called when the obtained response is a failure  */
    abstract fun onFailure(responseCode: Int, errorMessage: String)
}