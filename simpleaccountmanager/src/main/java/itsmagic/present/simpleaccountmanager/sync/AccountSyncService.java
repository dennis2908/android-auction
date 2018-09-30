package itsmagic.present.simpleaccountmanager.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import itsmagic.present.simpleaccountmanager.BuildConfig;

/**
 * Created by Alvin Rusli on 11/10/2016.
 * <p/>
 * A service to handle Account synchronization.
 */
public abstract class AccountSyncService extends Service {

    /** The sync adapter lock object */
    private static final Object mSyncAdapterLock = new Object();

    /** The sync adapter object */
    private static AccountSyncAdapter mSyncAdapter = null;

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) Log.i(getClass().getSimpleName(), "Account synchronization service started");

        synchronized (mSyncAdapterLock) {
            if (mSyncAdapter == null) mSyncAdapter = initAccountSyncAdapter();
        }
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) Log.i(getClass().getSimpleName(), "Account synchronization service stopped");
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (BuildConfig.DEBUG) Log.i(getClass().getSimpleName(), "getBinder()...  returning the AccountSyncAdapter binder for intent " + intent);
        return mSyncAdapter.getSyncAdapterBinder();
    }

    /** Initialize the sync adapter for this service **/
    public abstract AccountSyncAdapter initAccountSyncAdapter();
}
