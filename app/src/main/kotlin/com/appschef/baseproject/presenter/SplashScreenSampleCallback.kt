package com.appschef.baseproject.presenter

import com.appschef.baseproject.model.core.AppError

/**
 * Created by Alvin Rusli on 06/07/2017.
 *
 * The sample presenter for splash screen.
 * This sample shows how multiple fake API calls can be handled by one presenter.
 */
interface SplashScreenSampleCallback {

    /** Called when the splash screen is ready */
    fun onSplashScreenReady()

    /** Called when the loading process is started by this presenter */
    fun onSampleLoading()

    /** Called when the API is successfully called */
    fun onSampleSuccess(data1: String, data2: String)

    /** Called when the API doesn't respond or something went wrong */
    fun onSampleFailure(error: AppError)
}
