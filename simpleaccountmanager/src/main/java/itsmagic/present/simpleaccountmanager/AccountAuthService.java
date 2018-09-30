package itsmagic.present.simpleaccountmanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Alvin Rusli on 04/04/2017.
 * <p/>
 * A service to handle Account authentication.
 * It instantiates the account_auth and returns its IBinder.
 */
public class AccountAuthService extends Service {

    /** The {@link AccountAuthenticator} object */
    private AccountAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) Log.i(getClass().getSimpleName(), "Account authentication service started");
        mAuthenticator = new AccountAuthenticator(this);
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) Log.i(getClass().getSimpleName(), "Account authentication service stopped");
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (BuildConfig.DEBUG) Log.i(getClass().getSimpleName(), "getBinder()...  returning the AccountAuthenticator binder for intent " + intent);
        return mAuthenticator.getIBinder();
    }
}
