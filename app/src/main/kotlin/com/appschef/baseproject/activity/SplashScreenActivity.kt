package com.appschef.baseproject.activity

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.appschef.baseproject.R
import com.appschef.baseproject.activity.core.CoreActivity
import com.appschef.baseproject.model.core.AppError
import com.appschef.baseproject.presenter.SplashScreenSampleCallback
import com.appschef.baseproject.presenter.SplashScreenSamplePresenter
import com.appschef.baseproject.util.Config
import kotlinx.android.synthetic.main.activity_splash_screen.*

/**
 * Created by Alvin Rusli on 06/15/2016.
 *
 * Sample splash screen activity.
 * This splash screen already handles its lifecycle, meaning that the
 * pending intent doesn't launch even after user has closed the app.
 */
class SplashScreenActivity : CoreActivity(), SplashScreenSampleCallback {

    override val viewRes = R.layout.activity_splash_screen

    private var presenter: SplashScreenSamplePresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SplashScreenSamplePresenter(this)
        lifecycle.addObserver(presenter!!)

        btn_retry.setOnClickListener { initSplashScreen() }
        btn_skip.setOnClickListener { onSampleSuccess("whatever", "whatever") }
    }

    /** Called when app is ready to continue with its splash screen  */
    override fun onSplashScreenReady() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            runOnUiThread {
                // Check for storage space before continuing
                // Low-end devices should have at least 64MB of free space, otherwise 128MB
                val storageThreshold = if (Config.isLowRamDevice()) 64 else 128
                if (Config.freeInternalSpaceInMb < storageThreshold)
                    showStorageSpaceWarningDialog()
                else
                    initSplashScreen()
            }
        }
    }

    private fun initSplashScreen() {
        presenter!!.fetchData("Param A", "Param B")
    }

    override fun onSampleLoading() {
        layout_error.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE
    }

    override fun onSampleSuccess(data1: String, data2: String) {
        SampleSelectionActivity.launchIntent(this)
        finish()
    }

    override fun onSampleFailure(error: AppError) {
        val message = String.format(getString(R.string.error_message_generic_detail_code), error.code, error.message)
        txt_error.text = message
        progress_bar.visibility = View.GONE
        layout_error.visibility = View.VISIBLE
    }

    /**
     * Display an [AlertDialog].
     * The dialog contains a warning message about storage space.
     * When the dialog is dismissed, the app will continue.
     */
    private fun showStorageSpaceWarningDialog() {
        val builder = AlertDialog.Builder(this, R.style.AppTheme_Dialog_Alert)
        builder.setTitle(resources.getString(R.string.info_warning))
        builder.setMessage(resources.getString(R.string.warning_storage_space))
        builder.setPositiveButton(resources.getString(android.R.string.ok)) { dialog, _ -> dialog.dismiss() }
        builder.setOnDismissListener { initSplashScreen() }
        builder.show()
    }
}
