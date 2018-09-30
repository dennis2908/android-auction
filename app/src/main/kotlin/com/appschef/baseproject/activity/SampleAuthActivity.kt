package com.appschef.baseproject.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.appschef.baseproject.App
import com.appschef.baseproject.R
import com.appschef.baseproject.account.AccountConstant
import com.appschef.baseproject.activity.core.CoreActivity
import com.appschef.baseproject.api.APICaller
import com.appschef.baseproject.api.util.OnAPIListener
import com.appschef.baseproject.model.core.AppError
import com.appschef.baseproject.model.local.auth.SavedAccount
import com.appschef.baseproject.model.remote.auth.SignInRequest
import com.appschef.baseproject.model.remote.auth.SignInResponse
import com.appschef.baseproject.util.Common
import com.appschef.baseproject.util.ValidationHelper
import com.appschef.baseproject.view.ProgressDialog
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import itsmagic.present.simpleaccountmanager.AccountHelper
import kotlinx.android.synthetic.main.activity_sample_auth.*

/**
 * Created by Alvin Rusli on 2/7/2017.
 *
 * A sample authentication activity.
 */
class SampleAuthActivity : CoreActivity(), View.OnClickListener {

    override val viewRes = R.layout.activity_sample_auth

    private var accountHelper: AccountHelper? = null
    private val apiCaller: APICaller<SignInResponse>

    init {
        apiCaller = APICaller<SignInResponse>().withListener(object : OnAPIListener<SignInResponse> {
            override fun onApiSuccess(response: SignInResponse) {
                val gson = GsonBuilder().setPrettyPrinting().create()
                Common.dismissProgressDialog()
                Common.showMessageDialog(this@SampleAuthActivity, "Success", "Successful sign in:\n" + gson.toJson(response.profile), DialogInterface.OnDismissListener { finish() })
            }

            override fun onApiFailure(error: AppError) {
                Common.dismissProgressDialog()
                Common.showMessageDialog(this@SampleAuthActivity, "Error", error.message)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountHelper = AccountHelper(this)

        btn_submit.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        checkAccount()
    }

    override fun onClick(view: View) {
        if (view === btn_submit) {
            if (isDataValid) {
                signIn(SignInRequest(
                        txt_username.text.toString(),
                        txt_password.text.toString()))
            }
        }
    }

    /** Checks if user is already logged in via Account Manager  */
    private fun checkAccount() {
        if (accountHelper!!.isLoggedIn) {
            Common.log(Log.DEBUG, javaClass.simpleName, "User is already logged with Android's account manager")

            val data = accountHelper!!.accountBundle
            val savedAccount: SavedAccount? = Gson().fromJson(data?.getString(AccountConstant.ACCOUNT_PROFILE), SavedAccount::class.java)
            if (savedAccount != null) {
                Common.log(Log.DEBUG, javaClass.simpleName, "Saved account data exists: " + savedAccount.toString())
            } else {
                Common.log(Log.DEBUG, javaClass.simpleName, "Saved account data doesn't exist")
            }

            // When user is already signed in, finish this activity
            Common.showToast(String.format(resources.getString(R.string.error_auth_already_logged_in), accountHelper!!.selectedAccountName))
            finish()
        } else {
            Common.log(Log.DEBUG, javaClass.simpleName, "User is not logged in")
        }
    }

    /**
     * Checks if user has input the correct values before calling the sign up API.
     * @return true if all data is valid
     */
    private val isDataValid: Boolean
        get() {
            var isValid = true
            clearEditTextError()

            if (ValidationHelper.isEmpty(txt_username)) {
                txt_username.requestFocus()
                txt_username_layout.error = resources.getString(R.string.error_input_username)
                isValid = false
            }

            if (ValidationHelper.isEmpty(txt_password)) {
                if (isValid) txt_password.requestFocus()
                txt_password_layout.error = resources.getString(R.string.error_input_password)
                isValid = false
            }

            return isValid
        }

    /** Clears all [android.support.design.widget.TextInputLayout] on the activity from the error  */
    private fun clearEditTextError() {
        txt_username_layout.isErrorEnabled = false
        txt_password_layout.isErrorEnabled = false
    }

    /** Load user's profile from a remote server (async)  */
    private fun signIn(request: SignInRequest) {
        Common.showProgressDialog(this, onBackPressListener = object : ProgressDialog.OnBackPressListener {
            override fun onProgressBackPressed() {
                apiCaller.cancel()
                Common.dismissProgressDialog()
            }
        })

        apiCaller.signIn(request)
    }

    companion object {

        /**
         * Launch this activity.
         * @param context the context
         */
        fun launchIntent(context: Context) {
            val intent = Intent(context, SampleAuthActivity::class.java)
            context.startActivity(intent)
        }
    }
}
