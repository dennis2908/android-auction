package itsmagic.present.simpleaccountmanager;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;

import java.util.Arrays;
import java.util.List;

import itsmagic.present.simpleaccountmanager.json.JSONException;
import itsmagic.present.simpleaccountmanager.json.JSONObject;
import itsmagic.present.simpleaccountmanager.util.AccountBundleParser;
import itsmagic.present.simpleaccountmanager.util.AccountConstants;
import itsmagic.present.simpleaccountmanager.util.AccountPreferenceHelper;

/**
 * Created by Alvin Rusli on 04/04/2017.
 * <p/>
 * The account manager helper class.
 */
public class AccountHelper {

    /** Determines the account type */
    private static String mAccountType = null;

    /** Determines the account manager activity class */
    private static Class mAccountManagerActivity = null;

    /** Determines sync adapters */
    private static List<Pair<String, Long>> mSyncAdapters = null;

    /**
     * Initializes this helper class
     * @param accountType the account type
     * @param accountManagerActivity the activity class to add an account
     */
    public static void init(
            @NonNull final String accountType,
            @NonNull final Class accountManagerActivity) {

        init(accountType, accountManagerActivity, (Pair<String, Long>) null);
    }

    /**
     * Initializes this helper class
     * @param accountType the account type
     * @param accountManagerActivity the activity class to add an account
     * @param syncAdapters the sync adapter as a map (provider, sync duration)
     */
    @SafeVarargs
    public static void init(
            @NonNull final String accountType,
            @NonNull final Class accountManagerActivity,
            @Nullable final Pair<String, Long> ... syncAdapters) {

        mAccountType = accountType;
        mAccountManagerActivity = accountManagerActivity;
        if (syncAdapters == null) mSyncAdapters = null;
        else mSyncAdapters = Arrays.asList(syncAdapters);
    }

    /**
     * Checks if the account helper has been initialized.
     * @return true if it's initialized
     */
    private static boolean isInitialized() {
        if (mAccountType != null && !mAccountType.isEmpty()) return true;
        if (BuildConfig.DEBUG) Log.e("AccountHelper", "AccountHelper has not been initialized! Call init before doing anything!");
        return false;
    }

    /**
     * Obtain the specified account type.
     * @return the account type
     */
    @Nullable
    protected static String getAccountType() {
        if (isInitialized()) return mAccountType;
        else return null;
    }

    /**
     * Obtain the specified account manager activity class.
     * @return the account manager activity class
     */
    @Nullable
    protected static Class getAccountManagerActivity() {
        if (isInitialized()) return mAccountManagerActivity;
        else return null;
    }

    /** The context */
    private Context mContext = null;

    /** The account selection request code */
    private int mRequestCode = 3587;

    /** Default constructor */
    public AccountHelper(Context context) {
        mContext = context;
    }

    /**
     * Obtain the {@link Account} from the account manager.
     * Uses the currently saved account name.
     */
    public Account getAccount() {
        return getAccount(AccountManager.get(mContext));
    }

    /**
     * Obtain the {@link Account} from the account manager.
     * Uses the currently saved account name.
     * @param accountManager the account manager
     */
    public Account getAccount(
            @NonNull final AccountManager accountManager) {

        final String accountName = AccountPreferenceHelper.getInstance(mContext).loadSelectedAccount();
        if (accountName == null || accountName.isEmpty()) return null;
        else return getAccount(accountName, accountManager);
    }

    /**
     * Obtain the {@link Account} from the account manager.
     * @param accountName the account name
     */
    public Account getAccount(
            @NonNull final String accountName) {

        return getAccount(accountName, AccountManager.get(mContext));
    }

    /**
     * Obtain the {@link Account} from the account manager.
     * @param accountName the account name
     * @param accountManager the account manager
     */
    public Account getAccount(
            @NonNull final String accountName,
            @NonNull final AccountManager accountManager) {

        if (!isInitialized()) return null;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        Account[] list = accountManager.getAccounts();
        for (Account account : list) {
            if (account.type.equals(mAccountType) && account.name.equals(accountName)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Obtain the current login state.
     * @return true if device has any account saved for the specified account type
     */
    public boolean isLoggedIn() {
        return isLoggedIn(AccountManager.get(mContext));
    }

    /**
     * Obtain the current login state.
     * @param accountManager the account manager
     * @return true if device has any account saved for the specified account type
     */
    public boolean isLoggedIn(
            @NonNull AccountManager accountManager) {

        if (!isInitialized()) return false;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        Account[] list = accountManager.getAccounts();
        for (Account account : list) {
            if (account.type.equals(mAccountType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtain the current login state.
     * @param accountName the account name
     * @return true if device has an account with the specified account type and name
     */
    public boolean isLoggedIn(
            @Nullable final String accountName) {

        return isLoggedIn(accountName, AccountManager.get(mContext));
    }

    /**
     * Obtain the current login state.
     * @param accountName the account name
     * @param accountManager the account manager
     * @return true if device has an account with the specified account type and name
     */
    public boolean isLoggedIn(
            @Nullable final String accountName,
            @NonNull AccountManager accountManager) {

        if (!isInitialized()) return false;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        Account[] list = accountManager.getAccounts();
        for (Account account : list) {
            if (account.type.equals(mAccountType) && account.name.equals(accountName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds an {@link Account} to the {@link AccountManager}.
     * Runs on UI thread, which may cause app to freeze because of the process duration.
     * If an account with the same account name already exists,
     * this will update its token and data.
     * @param accountName the account name
     * @param authToken the account auth token
     */
    public void addAccount(
            @NonNull final String accountName,
            @NonNull final String authToken) {

        addAccount(accountName, authToken, null);
    }

    /**
     * Adds an {@link Account} to the {@link AccountManager}.
     * Runs on UI thread, which may cause app to freeze because of the process duration.
     * If an account with the same account name already exists,
     * this will update its token and data.
     * @param accountName the account name
     * @param authToken the account auth token
     * @param accountData the account data
     */
    public void addAccount(
            @NonNull final String accountName,
            @NonNull final String authToken,
            @Nullable final Bundle accountData) {

        addAccount(accountName, authToken, accountData, null);
    }

    /**
     * Adds an {@link Account} to the {@link AccountManager}.
     * If callback is specified, this will run in background and calls when process is done.
     * Otherwise will run on UI thread, which may cause app to freeze because of the process duration.
     * If an account with the same account name already exists,
     * this will update its token and data.
     * @param accountName the account name
     * @param authToken the account auth token
     * @param accountData the account data
     * @param callback the callback
     */
    public void addAccount(
            @NonNull final String accountName,
            @NonNull final String authToken,
            @Nullable final Bundle accountData,
            @Nullable final AccountAdditionCallback callback) {

        addAccountOnAccountManager(accountName, authToken, accountData, callback);
    }

    /**
     * Adds an {@link Account} to the {@link AccountManager}.
     * @param accountName the account name
     * @param authToken the account auth token
     * @param accountData the account data
     * @param callback the callback
     */
    @SuppressWarnings("StaticFieldLeak")
    private void addAccountOnAccountManager(
            @Nullable final String accountName,
            @NonNull final String authToken,
            @Nullable final Bundle accountData,
            @Nullable final AccountAdditionCallback callback) {

        if (!isInitialized()) return;

        if (callback != null) {
            new AsyncTask<Boolean, Boolean, Boolean>() {
                @Override
                protected Boolean doInBackground(Boolean... params) {
                    addAccountExplicitly(accountName, authToken, accountData);
                    return null;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    callback.onAccountAdded();
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            addAccountExplicitly(accountName, authToken, accountData);
        }
    }

    /**
     * Adds an account to the Android's account manager.
     * @param accountName the account name
     * @param authToken the account auth token
     * @param extras the bundle extras
     */
    private void addAccountExplicitly(
            @Nullable final String accountName,
            @NonNull final String authToken,
            @Nullable final Bundle extras) {

        if (accountName == null || accountName.isEmpty()) {
            throw new NullPointerException("Account name must not be null!");
        }

        // Temporarily set the account addition sync as true to avoid immediate sync
        AccountPreferenceHelper.getInstance(mContext).saveAccountInitialAddition(accountName);

        // Prepare the account manager
        AccountManager accountManager = AccountManager.get(mContext);

        // Create a new account with the specified account name
        final Account account = new Account(accountName, mAccountType);

        // Set the access token for the account
        accountManager.setAuthToken(account, AccountConstants.KEY_ACCOUNT_TOKEN, authToken);

        // If user has no account, set the newly added one as the selected one
        if (!isLoggedIn()) {
            setSelectedAccountName(accountName);
        }

        // Add the new account to the AccountManager
        if (!isLoggedIn(accountName)) {
            accountManager.addAccountExplicitly(account, null, null);

            // Add all sync adapters
            if (mSyncAdapters != null) {
                for (Pair<String, Long> syncAdapter : mSyncAdapters) {
                    // Set sync to be enabled by default (for newly added account)
                    ContentResolver.setIsSyncable(account, syncAdapter.first, 1);
                    ContentResolver.setSyncAutomatically(account, syncAdapter.first, true);

                    // Set periodic sync duration
                    ContentResolver.addPeriodicSync(account, syncAdapter.first, Bundle.EMPTY, syncAdapter.second);
                }
            }
        }

        // Adds additional information to the account extras
        accountManager.setUserData(account, AccountConstants.KEY_ACCOUNT_NAME, accountName);
        accountManager.setUserData(account, AccountConstants.KEY_ACCOUNT_TOKEN, authToken);
        if (extras != null) accountManager.setUserData(account, AccountConstants.KEY_ACCOUNT_DATA, AccountBundleParser.bundleToJson(extras).toString());

        // Remove the account addition sync state
        AccountPreferenceHelper.getInstance(mContext).removeAccountInitialAddition(accountName);
    }

    /**
     * Remove the saved {@link Account} from the {@link AccountManager}.
     * Uses the currently saved account name.
     * The removal process runs on a background thread.
     */
    public void removeAccount() {
        final String accountName = AccountPreferenceHelper.getInstance(mContext).loadSelectedAccount();
        if (accountName != null && !accountName.isEmpty()) removeAccount(accountName);
    }

    /**
     * Remove the saved {@link Account} from the {@link AccountManager}.
     * The removal process runs on a background thread.
     * @param accountName the account name
     */
    public void removeAccount(
            @NonNull final String accountName) {

        removeAccount(accountName, null);
    }

    /**
     * Remove the saved {@link Account} from the {@link AccountManager}.
     * Uses the currently saved account name.
     * The removal process runs on a background thread,
     * and calls the callback listener when the process is done.
     * @param callback the callback
     */
    @SuppressWarnings("unchecked")
    public void removeAccount(
            @Nullable final AccountRemovalCallback callback) {

        final String accountName = AccountPreferenceHelper.getInstance(mContext).loadSelectedAccount();
        if (accountName != null && !accountName.isEmpty()) removeAccount(accountName, callback);
    }

    /**
     * Remove the saved {@link Account} from the {@link AccountManager}.
     * The removal process runs on a background thread,
     * and calls the callback listener when the process is done.
     * @param accountName the account name
     * @param callback the callback
     */
    @SuppressWarnings("unchecked")
    public void removeAccount(
            @NonNull final String accountName,
            @Nullable final AccountRemovalCallback callback) {

        if (!isInitialized()) return;

        AccountManager accountManager = AccountManager.get(mContext);
        Account account = getAccount(accountName, accountManager);
        if (account != null) {
            final AccountManagerCallback accountManagerCallback;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && mContext instanceof Activity) {
                if (callback == null) accountManagerCallback = null;
                else accountManagerCallback = new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        // Re-check the selected account still exists,
                        // otherwise remove it from prefs
                        if (!isLoggedIn(getSelectedAccountName())) {
                            clearSelectedAccountName();
                        }

                        callback.onAccountRemoved();
                    }
                };
                accountManager.removeAccount(account, (Activity) mContext, accountManagerCallback, null);
            } else {
                if (callback == null) accountManagerCallback = null;
                else accountManagerCallback = new AccountManagerCallback<Boolean>() {
                    @Override
                    public void run(AccountManagerFuture<Boolean> future) {
                        // Re-check the selected account still exists,
                        // otherwise remove it from prefs
                        if (!isLoggedIn(getSelectedAccountName())) {
                            clearSelectedAccountName();
                        }

                        callback.onAccountRemoved();
                    }
                };
                //noinspection deprecation
                accountManager.removeAccount(account, accountManagerCallback, null);
            }
        }
    }

    /**
     * Obtain the auth token from the saved {@link Account}.
     * Uses the currently saved account name.
     * @return the token if it exists
     */
    @Nullable
    public String getToken() {
        final String accountName = AccountPreferenceHelper.getInstance(mContext).loadSelectedAccount();
        if (accountName == null || accountName.isEmpty()) return null;
        else return getToken(accountName);
    }

    /**
     * Obtain the auth token from the saved {@link Account}.
     * @param accountName the account name
     * @return the token if it exists
     */
    @Nullable
    public String getToken(
            @NonNull final String accountName) {

        if (!isInitialized()) return null;

        AccountManager accountManager = AccountManager.get(mContext);
        Account account = getAccount(accountName);
        String authToken = null;
        if (account != null) {
            authToken = accountManager.getUserData(account, AccountConstants.KEY_ACCOUNT_TOKEN);
        }

        return authToken;
    }

    /**
     * Obtain the saved {@link Bundle} from the saved {@link Account}.
     * Uses the currently saved account name.
     * @return the bundle if it exists
     */
    @Nullable
    public Bundle getAccountBundle() {
        final String accountName = AccountPreferenceHelper.getInstance(mContext).loadSelectedAccount();
        if (accountName == null || accountName.isEmpty()) return null;
        else return getAccountBundle(accountName);
    }

    /**
     * Obtain the saved {@link Bundle} from the saved {@link Account}.
     * @param accountName the account name
     * @return the bundle if it exists
     */
    @Nullable
    public Bundle getAccountBundle(
            @NonNull final String accountName) {

        if (!isInitialized()) return null;

        AccountManager accountManager = AccountManager.get(mContext);
        Account account = getAccount(accountName);
        Bundle accountData = null;
        if (account != null) {
            String accountDataJson = accountManager.getUserData(account, AccountConstants.KEY_ACCOUNT_DATA);
            if (accountDataJson != null) {
                try {
                    accountData = AccountBundleParser.jsonToBundle(new JSONObject(accountDataJson));
                } catch (JSONException e) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                }
            }
        }

        return accountData;
    }

    /**
     * Sets the currently selected account name as the
     * first account found that matches the account type.
     * This will allow methods that doesn't have account name as its parameter
     * to use the specified account name in this method until it's changed.
     */
    public void setSelectedAccountNameAsFirstAccount() {
        setSelectedAccountNameAsFirstAccount(AccountManager.get(mContext));
    }

    /**
     * Sets the currently selected account name as the
     * first account found that matches the account type.
     * This will allow methods that doesn't have account name as its parameter
     * to use the specified account name in this method until it's changed.
     * @param accountManager the account manager
     */
    public void setSelectedAccountNameAsFirstAccount(
            @NonNull AccountManager accountManager) {

        if (!isInitialized()) return;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String accountName = null;
        Account[] list = accountManager.getAccounts();
        for (Account account : list) {
            if (account.type.equals(mAccountType)) {
                accountName = account.name;
                break;
            }
        }

        setSelectedAccountName(accountName);
    }

    /**
     * Sets the currently selected account name.
     * This will allow methods that doesn't have account name as its parameter
     * to use the specified account name in this method until it's changed.
     * @param accountName the account name
     */
    public void setSelectedAccountName(
            @Nullable final String accountName) {

        if (accountName != null) AccountPreferenceHelper.getInstance(mContext).saveSelectedAccount(accountName);
        else AccountPreferenceHelper.getInstance(mContext).removeSelectedAccount();
    }

    /**
     * Obtain the currently selected account name.
     * @return the currently saved name
     */
    @Nullable
    public String getSelectedAccountName() {
        return AccountPreferenceHelper.getInstance(mContext).loadSelectedAccount();
    }

    /** Clears the currently selected account name */
    public void clearSelectedAccountName() {
        AccountPreferenceHelper.getInstance(mContext).removeSelectedAccount();
    }

    /**
     * Shows the account selection.
     * When no account with the specified account type exists,
     * Android will open the add account activity.
     * @param requestCode the request code
     */
    public void showAccountSelection(
            @NonNull final Activity activity,
            final int requestCode) {

        if (!isInitialized()) return;

        mRequestCode = requestCode;
        Intent intent = AccountManager.newChooseAccountIntent(
                getAccount(),
                null,
                new String[] { mAccountType },
                true,
                null,
                null,
                null,
                null);
        activity.startActivityForResult(intent, mRequestCode);
    }

    /**
     * Sets the selected account automatically depending on which account is selected.
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the extra data
     * @return true if account manager changed the selected account
     */
    public boolean onActivityResult(
            final int requestCode,
            final int resultCode,
            final @Nullable Intent data) {

        if (!isInitialized()) return false;

        if (requestCode == mRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    setSelectedAccountName(accountName);
                    return true;
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (BuildConfig.DEBUG) Log.d("AccountHelper", "Account selection is cancelled");
            }
        }
        return false;
    }

    /** The callback when account has been successfully added */
    public interface AccountAdditionCallback {

        /** Called when account has been added to Android's account manager */
        void onAccountAdded();
    }

    /** The callback when account has been successfully removed */
    public interface AccountRemovalCallback {

        /** Called when account has been removed from Android's account manager */
        void onAccountRemoved();
    }
}
