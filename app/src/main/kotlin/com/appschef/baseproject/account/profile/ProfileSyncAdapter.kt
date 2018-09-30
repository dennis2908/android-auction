package com.appschef.baseproject.account.profile

import android.accounts.Account
import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import android.util.Log

import com.appschef.baseproject.account.AccountConstant
import com.appschef.baseproject.model.remote.auth.Profile
import com.appschef.baseproject.util.Common
import com.google.gson.Gson

import java.util.concurrent.TimeUnit

import itsmagic.present.simpleaccountmanager.AccountHelper
import itsmagic.present.simpleaccountmanager.sync.AccountSyncAdapter

/**
 * Created by Alvin Rusli on 11/10/2016.
 *
 * The sync adapter for user's profile.
 */
class ProfileSyncAdapter(context: Context, autoInitialize: Boolean) : AccountSyncAdapter(context, autoInitialize) {

    override fun onBackgroundSync(account: Account, extras: Bundle, authority: String, provider: ContentProviderClient, syncResult: SyncResult) {
        Common.log(tag = javaClass.simpleName, message = "Performing sync for account[${account.name}]")
        try {
            // Get the current account's profile
            val accountHelper = AccountHelper(context)
            val data = accountHelper.accountBundle
            val profile = Gson().fromJson(data?.getString(AccountConstant.ACCOUNT_PROFILE), Profile::class.java)
            if (profile == null) {
                Common.log(Log.ERROR, javaClass.simpleName, "Profile is null, removing account")
                accountHelper.removeAccount()
                return
            }

            try {
                // TODO: 06/15/2017 This is where your actual sync process should be done
                // Simulate a long sync process
                Thread.sleep(3000)

                // Set periodic sync every week
                val weekDuration = TimeUnit.DAYS.toMillis(7)
                ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, weekDuration)

                Common.log(Log.DEBUG, javaClass.simpleName, "Account sync finished successfully")
            } catch (e: InterruptedException) {
                Common.printStackTrace(e)

                // Set periodic sync to a day on failure
                val dayDuration = TimeUnit.DAYS.toMillis(1)
                ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, dayDuration)

                Common.log(Log.ERROR, javaClass.simpleName, "Account sync finished with some error(s)")
            }

        } catch (e: Exception) {
            Common.printStackTrace(e)
        }

    }
}
