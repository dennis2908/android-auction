package com.appschef.baseproject.presenter

import com.appschef.baseproject.model.core.AppError
import com.appschef.baseproject.model.remote.product.SampleProduct

/**
 * Created by Alvin Rusli on 09/27/2017.
 */
interface SampleDataCallback {

    /** Called when the loading process is started by this presenter */
    fun onSampleDataLoading()

    /** Called when the API is successfully called */
    fun onSampleDataSuccess(dataList: List<SampleProduct>)

    /** Called when the API doesn't respond or something went wrong */
    fun onSampleDataFailure(error: AppError)
}