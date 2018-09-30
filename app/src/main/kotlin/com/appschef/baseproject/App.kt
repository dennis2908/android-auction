package com.appschef.baseproject

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Pair
import com.appschef.baseproject.account.AccountConstant
import com.appschef.baseproject.activity.SampleAuthActivity
import itsmagic.present.simpleaccountmanager.AccountHelper
import java.util.concurrent.TimeUnit

/**
 * Created by Alvin Rusli on 06/07/2017.
 *
 * The [Application] class,
 * important initializations should take place here.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this

        // Initialize the account helper
        AccountHelper.init(AccountConstant.ACCOUNT_TYPE, SampleAuthActivity::class.java,
                Pair(AccountConstant.ACCOUNT_PROVIDER_PROFILE, TimeUnit.DAYS.toMillis(7)))
    }

    companion object {

        /**
         * The application [Context] made static.
         * Do **NOT** use this as the context for a view,
         * this is mostly used to simplify calling of resources
         * (esp. String resources) outside activities.
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set
    }
}
