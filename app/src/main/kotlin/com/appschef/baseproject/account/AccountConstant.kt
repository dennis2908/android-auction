package com.appschef.baseproject.account

import com.appschef.baseproject.App
import com.appschef.baseproject.R

/**
 * Created by Alvin Rusli on 04/04/2017.
 *
 * A class that holds constant variables for app's account manager.
 */
object AccountConstant {

    /** The account type  */
    val ACCOUNT_TYPE: String = App.context.getString(R.string.account_manager_account_type)

    /** The account sync provider  */
    val ACCOUNT_PROVIDER_PROFILE: String = App.context.getString(R.string.account_manager_account_provider_profile)

    /** The bundle key for user's profile  */
    val ACCOUNT_PROFILE = "account_profile"

}
