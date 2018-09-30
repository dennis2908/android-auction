package com.appschef.baseproject.api.callback

import com.appschef.baseproject.api.util.OnAPIListener
import com.appschef.baseproject.api.callback.core.CoreCallback
import com.appschef.baseproject.model.core.AppError
import com.appschef.baseproject.model.remote.product.SampleProductListResponse
import com.appschef.baseproject.util.Common

import retrofit2.Response

/**
 * Created by Alvin Rusli on 2/7/2016.
 *
 * A sample custom callback for product list.
 */
class ProductListCallback(listener: OnAPIListener<SampleProductListResponse>) : CoreCallback<SampleProductListResponse>(listener) {

    override fun onSuccess(response: Response<SampleProductListResponse>) {
        // Do heavy job in a different thread,
        // e.g. Store the obtained data list into local table, etc.
        val thread = Thread {
            try {
                // Simulate a long process
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                Common.printStackTrace(e)
            }

            callListenerSuccessOnUiThread(response.body())
        }

        // Execute the heavy job
        thread.start()
    }

    override fun onFailure(responseCode: Int, errorMessage: String) {
        handleFailure(responseCode, errorMessage)
    }
}
