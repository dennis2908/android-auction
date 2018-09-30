package com.appschef.baseproject.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.AsyncTask
import android.util.Log
import com.appschef.baseproject.App
import com.appschef.baseproject.R
import com.appschef.baseproject.model.core.AppError
import com.appschef.baseproject.util.Common

/**
 * Created by Alvin Rusli on 06/07/2017.
 *
 * The sample presenter for splash screen.
 * This sample shows how multiple fake API calls can be handled by one presenter.
 */
class SplashScreenSamplePresenter(val callback: SplashScreenSampleCallback): LifecycleObserver {

    /** The preparation async task */
    private var prepareTask: AsyncTask<String?, String?, String?>? = null

    /** The async task */
    private var task1: AsyncTask<String?, String?, String?>? = null

    /** The another async task */
    private var task2: AsyncTask<String?, String?, String?>? = null

    /** The sample data */
    private var sample1Data: String? = null
    private var sample2Data: String? = null

    /** The error that has been obtained when any of the fetching process fails */
    private var error: AppError? = null

    /** The state that shows if sample data is currently being fetched (API call is starting) */
    private var isSampleLoading = false

    /** The state that shows if sample data has been fetched (API call has been done) */
    private var isSample1Fetched = false
    private var isSample2Fetched = false

    /** The state that shows if sample data has been fetched (API call has been done SUCCESSFULLY) */
    private var isSample1Ready = false
    private var isSample2Ready = false

    /** Delay time before splash screen begins to load the data  */
    private val SPLASH_TIME_MILLISECOND = 2000

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun onResume() {
        // Wait for the specified timer before determining if splash screen is ready
        prepareTask = object : AsyncTask<String?, String?, String?>() {
            override fun doInBackground(vararg strings: String?): String? {
                try {
                    Thread.sleep(SPLASH_TIME_MILLISECOND.toLong())
                } catch (e: InterruptedException) {
                    Common.printStackTrace(e)
                }

                return null
            }

            override fun onPostExecute(s: String?) {
                super.onPostExecute(s)
                callback.onSplashScreenReady()
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun onPause() {
        cancel()
    }

    /**
     * Obtain the sample data.
     * @return the live data for sample data
     */
    fun fetchData(param1: String, param2: String) {
        // Simulate an API call
        task1 = object : AsyncTask<String?, String?, String?>() {

            var error: AppError? = null

            override fun onPreExecute() {
                isSample1Fetched = false
                onLoadStart()
            }

            override fun doInBackground(vararg strings: String?): String? {
                Common.log(Log.DEBUG, "SplashScreenPresenter (2)", "Sleeping for 2 seconds...")
                try {
                    Thread.sleep(2000)
                } catch (e: InterruptedException) {
                    Common.printStackTrace(e)
                    error = AppError(AppError.CLIENT_UNKNOWN, e.message)
                }

                return null
            }

            override fun onPostExecute(s: String?) {
                isSample1Fetched = true
                val temp = if (Math.random() <= 0.5) 1 else 2
                if (temp == 1) error = AppError(AppError.CLIENT_UNKNOWN, "Task1 is deliberately failing")

                when (error) {
                    null -> {
                        sample1Data = "Sample successful, param: [$param1]"
                        isSample1Ready = true
                        Common.log(Log.DEBUG, "SplashScreenPresenter (2)", "Task1 is successful")
                        onLoadSuccess()
                    }
                    else -> {
                        Common.log(Log.DEBUG, "SplashScreenPresenter (2)", "Task1 is failed")
                        onLoadFailure(error!!)
                    }
                }
            }
        }

        // Simulate another API call
        task2 = object : AsyncTask<String?, String?, String?>() {

            var error: AppError? = null

            override fun onPreExecute() {
                isSample2Fetched = false
                onLoadStart()
            }

            override fun doInBackground(vararg strings: String?): String? {
                Common.log(Log.DEBUG, "SplashScreenPresenter (5)", "Sleeping for 5 seconds...")
                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                    Common.printStackTrace(e)
                    error = AppError(AppError.CLIENT_UNKNOWN, e.message)
                }

                return null
            }

            override fun onPostExecute(s: String?) {
                isSample2Fetched = true
                val temp = if (Math.random() <= 0.5) 1 else 2
                if (temp == 1) error = AppError(AppError.CLIENT_UNKNOWN, "Task2 is deliberately failing")

                when (error) {
                    null -> {
                        sample2Data = "Another sample successful, param: [$param2]"
                        isSample2Ready = true
                        Common.log(Log.DEBUG, "SplashScreenPresenter (5)", "Task2 is successful")
                        onLoadSuccess()
                    }
                    else -> {
                        Common.log(Log.DEBUG, "SplashScreenPresenter (5)", "Task2 is failed")
                        onLoadFailure(error!!)
                    }
                }
            }
        }

        // Execute the tasks
        if (!isSample1Ready) task1!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        if (!isSample2Ready) task2!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    /** Cancels the ongoing task */
    private fun cancel() {
        prepareTask?.cancel(true)
        task1?.cancel(true)
        task2?.cancel(true)
    }

    /** Display the loading process */
    private fun onLoadStart() {
        if (isSampleLoading) return
        else callback.onSampleLoading()
        isSampleLoading = true
    }

    /** Data load for one of the sample has been successful */
    private fun onLoadSuccess() {
        if (isSample1Fetched && isSample2Fetched) {
            isSampleLoading = false
            if (isSample1Ready && isSample2Ready) {
                callback.onSampleSuccess(sample1Data!!, sample2Data!!)
            } else {
                if (error == null) error = AppError(AppError.DEVELOPMENT_UNKNOWN, App.context.getString(R.string.error_unknown))
                callback.onSampleFailure(this.error!!)
            }
        }
    }

    /** Data load for one of the sample has failed */
    private fun onLoadFailure(error: AppError) {
        this.error = error
        if (isSample1Fetched && isSample2Fetched) {
            isSampleLoading = false
            callback.onSampleFailure(error)
        }
    }
}
