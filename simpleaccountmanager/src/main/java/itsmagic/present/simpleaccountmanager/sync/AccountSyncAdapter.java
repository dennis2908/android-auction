package itsmagic.present.simpleaccountmanager.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.annotation.NonNull;

import itsmagic.present.simpleaccountmanager.util.AccountPreferenceHelper;

/**
 * Created by Alvin Rusli on 04/04/2017.
 * <p/>
 * The base class for sync adapters.
 */
public abstract class AccountSyncAdapter extends AbstractThreadedSyncAdapter {

    /** The constructor for this class **/
    public AccountSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(
            final Account account,
            final Bundle extras,
            final String authority,
            final ContentProviderClient provider,
            final SyncResult syncResult) {

        boolean isAccountAdditionSyncing = AccountPreferenceHelper.getInstance(getContext()).loadAccountAdditionSyncing(account.name);
        if (isAccountAdditionSyncing) return;

        // Don't start any background sync on initial account addition
        onBackgroundSync(account, extras, authority, provider, syncResult);
    }

    /** Updates a sync adapter duration.
     * @param account the account that should be synced
     * @param authority the authority of this sync request
     * @param duration the specified duration before doing a sync
     */
    public final void setSyncAdapterDuration(
            final @NonNull Account account,
            final @NonNull String authority,
            final @NonNull Long duration) {

        ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, duration);
    }

    /**
     * Perform a background sync for the specified account.
     * @param account the account that should be synced
     * @param extras SyncAdapter-specific parameters
     * @param authority the authority of this sync request
     * @param provider a ContentProviderClient that points to the ContentProvider for this authority
     * @param syncResult SyncAdapter-specific parameters
     */
    public abstract void onBackgroundSync(
            final Account account,
            final Bundle extras,
            final String authority,
            final ContentProviderClient provider,
            final SyncResult syncResult);
}
