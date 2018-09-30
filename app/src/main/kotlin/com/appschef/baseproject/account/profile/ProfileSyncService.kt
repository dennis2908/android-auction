package com.appschef.baseproject.account.profile

import itsmagic.present.simpleaccountmanager.sync.AccountSyncAdapter
import itsmagic.present.simpleaccountmanager.sync.AccountSyncService

/**
 * Created by Alvin Rusli on 11/10/2016.
 *
 * A service to handle Account synchronization.
 */
class ProfileSyncService : AccountSyncService() {

    override fun initAccountSyncAdapter(): AccountSyncAdapter {
        // Return your sync adapter
        return ProfileSyncAdapter(this, true)
    }
}
