package com.appschef.baseproject.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.appschef.baseproject.api.APICaller
import com.appschef.baseproject.api.util.OnAPIListener
import com.appschef.baseproject.model.core.AppError
import com.appschef.baseproject.model.remote.product.SampleProduct
import com.appschef.baseproject.model.remote.product.SampleProductListResponse
import java.util.*

/**
 * Created by Alvin Rusli on 08/16/2017.
 */
class SampleDataPresenter(val callback: SampleDataCallback) : LifecycleObserver {

    /** The API Caller */
    private var apiCaller: APICaller<SampleProductListResponse>? = null

    /** The pagination variable  */
    private var page = 1

    /** Determines if this view model has successfully fetch a data from local storage  */
    private var hasFetchedFromLocal = false

    /** Determines if this view model has completed **ALL** of its fetching process  */
    var isLoadFinished = false
        private set

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun onDestroy() {
        apiCaller?.cancel()
    }

    fun fetchData() {
        when (hasFetchedFromLocal) {
            true -> fetchFromRemote()
            else -> fetchFromLocal()
        }
    }

    fun onRefresh() {
        page = 1
        hasFetchedFromLocal = false
        isLoadFinished = false
    }

    /**
     * Fetches a sample list from local storage.
     * For this example, app will generate fixed samples.
     */
    private fun fetchFromLocal() {
        callback.onSampleDataLoading()

        val list = ArrayList<SampleProduct>()
        var product: SampleProduct
        for (i in 0..9) {
            product = SampleProduct()
            product.id = i
            product.name = "local product " + i
            list.add(product)
        }

        callback.onSampleDataSuccess(list)
        hasFetchedFromLocal = true
    }

    /** Fetches a sample list from a remote server  */
    private fun fetchFromRemote() {
        callback.onSampleDataLoading()

        if (apiCaller == null) {
            apiCaller = APICaller<SampleProductListResponse>()
                    .withListener(object : OnAPIListener<SampleProductListResponse> {
                        override fun onApiSuccess(response: SampleProductListResponse) {
                            // Set your condition to handle the end of infinite scroll behaviour
                            if (page >= 5) isLoadFinished = true
                            else page++
                            val dataList = if (response.productList != null) response.productList else null

                            if (dataList != null) callback.onSampleDataSuccess(dataList)
                            else callback.onSampleDataFailure(AppError(AppError.DEVELOPMENT_UNKNOWN, "Data list is null!"))
                        }

                        override fun onApiFailure(error: AppError) {
                            callback.onSampleDataFailure(error)
                        }
                    })
        }

        apiCaller?.getProducts(page)
    }
}

