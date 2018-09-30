package com.appschef.baseproject.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast

import com.appschef.baseproject.App
import com.appschef.baseproject.BuildConfig
import com.appschef.baseproject.R
import com.appschef.baseproject.view.ProgressDialog

/**
 * Created by Alvin Rusli on 1/24/2016.
 *
 * A class that handles basic universal methods.
 */
object Common {

    /** The loading progress dialog object */
    var progressDialog: ProgressDialog? = null

    /**
     * Shows a loading progress dialog.
     * @param context the context
     * @param stringRes the dialog message string resource id
     * @param onBackPressListener the back button press listener when loading is shown
     */
    fun showProgressDialog(context: Context, stringRes: Int = -1, onBackPressListener: ProgressDialog.OnBackPressListener? = null) {
        dismissProgressDialog()
        progressDialog = ProgressDialog(context)
        progressDialog!!.setText(stringRes)
        progressDialog!!.backPressListener = onBackPressListener
        if (context is Activity && !context.isFinishing) progressDialog!!.show()
    }

    /** Hides the currently shown loading progress dialog */
    fun dismissProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
    }

    /**
     * Sets the progress dialog progress in percent.
     * @param progress The loading progress in percent
     */
    fun setProgressDialogProgress(progress: Int) {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.setProgress(progress)
            progressDialog!!.setText(progress.toString() + "%")
        }
    }

    /**
     * Sets the progress dialog progress indeterminate state.
     * @param isIndeterminate Determines if progress dialog is indeterminate
     */
    fun setProgressDialogIndeterminate(isIndeterminate: Boolean) {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.setIndeterminate(isIndeterminate)
        }
    }

    /**
     * Sets the progress dialog message.
     * @param message The dialog message string
     */
    fun setProgressDialogText(message: String) {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.setText(message)
        }
    }

    /**
     * Display a simple [Toast].
     * @param stringRes The message string resource id
     */
    fun showToast(stringRes: Int) {
        showToast(App.context.getString(stringRes))
    }

    /**
     * Display a simple [Toast].
     * @param message The message string
     */
    fun showToast(message: String) {
        Toast.makeText(App.context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Display a simple [AlertDialog] with a simple OK button.
     * If the dismiss listener is specified, the dialog becomes uncancellable
     * @param context The context
     * @param title The title string
     * @param message The message string
     * @param dismissListener The dismiss listener
     */
    fun showMessageDialog(
            context: Context,
            title: String?,
            message: String?,
            dismissListener: DialogInterface.OnDismissListener? = null) {
        val builder = AlertDialog.Builder(context, R.style.AppTheme_Dialog_Alert)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(android.R.string.ok, { dialog, _ -> dialog.dismiss() })

        val dialog = builder.create()
        if (dismissListener != null) {
            dialog.setOnDismissListener(dismissListener)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
        }
        dialog.show()
    }

    /**
     * Prints an exception's stack trace.
     * Stack traces printed via this method will only show up on debug builds.
     * @param throwable the throwable
     */
    fun printStackTrace(throwable: Throwable) {
        if (BuildConfig.DEBUG) throwable.printStackTrace()
    }

    /**
     * Prints a [Log] message.
     * Log messages printed via this method will only show up on debug builds.
     * @param type The specified log type, may be [Log.DEBUG], [Log.INFO], and other log types
     * @param tag The log tag to print
     * @param message The log message to print
     */
    fun log(type: Int = Log.DEBUG, tag: String? = "BaseProject", message: String?) {
        if (BuildConfig.DEBUG) {
            var logMessage = message
            if (logMessage.isNullOrEmpty()) logMessage = "Message is null, what exactly do you want to print?"
            Log.println(type, tag, logMessage)
        }
    }
}
