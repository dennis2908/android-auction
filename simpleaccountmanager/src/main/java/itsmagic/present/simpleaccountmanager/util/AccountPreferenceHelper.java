package itsmagic.present.simpleaccountmanager.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import itsmagic.present.simpleaccountmanager.R;

/**
 * Created by Alvin Rusli on 04/04/2017.
 * <p/>
 * A {@link SharedPreferences} helper class.
 */
public class AccountPreferenceHelper {

    /** The context */
    private Context mContext = null;

    /** The {@link SharedPreferences} object */
    private SharedPreferences mPrefs;

    /** The {@link SharedPreferences.Editor} object */
    private SharedPreferences.Editor mPrefsEditor;

    /** Use a singleton instance to make sure only one helper exists */
    private static AccountPreferenceHelper mInstance = null;

    /**
     * Obtain the singleton instance of {@link AccountPreferenceHelper}.
     * @param context the context
     */
    public static AccountPreferenceHelper getInstance(Context context) {
        if (mInstance == null) mInstance = new AccountPreferenceHelper(context.getApplicationContext());
        return mInstance;
    }

    /**
     * Private constructor.
     * @param context the context
     */
    private AccountPreferenceHelper(Context context) {
        mContext = context;
        initSharedPreferences();
    }

    /**
     * Obtain the key for a pref item.
     * @param prefResId the preferences {@link String} resource
     * @return the pref key
     */
    private String getPrefKey(int prefResId) {
        return mContext.getResources().getString(prefResId);
    }

    /**
     * Initialize the app's default {@link SharedPreferences}
     * and its {@link SharedPreferences.Editor}.
     */
    @SuppressLint("CommitPrefEdits")
    private void initSharedPreferences() {
        mPrefs = mContext.getSharedPreferences("simaccmgr_prefs", Context.MODE_PRIVATE);
        mPrefsEditor = mPrefs.edit();
    }

    /** Clears all currently saved entries from prefs */
    public void removeEverything() {
        mPrefsEditor.clear();
        mPrefsEditor.apply();
    }

    /**
     * Saves the account name to avoid initial addition sync.
     * @param accountName the account name
     */
    public void saveAccountInitialAddition(@NonNull String accountName) {
        mPrefsEditor.putBoolean(accountName, true);
        mPrefsEditor.apply();
    }

    /**
     * Loads the account initial addition state.
     * @param accountName the account name
     * @return true if the specified account is initially added
     */
    public boolean loadAccountAdditionSyncing(@NonNull String accountName) {
        return mPrefs.contains(accountName);
    }

    /**
     * Clears the account initial addition state from prefs.
     * @param accountName the account name
     */
    public void removeAccountInitialAddition(@NonNull String accountName) {
        mPrefsEditor.remove(accountName);
        mPrefsEditor.apply();
    }

    /**
     * Saves the currently selected account name.
     * @param accountName the account name
     */
    public void saveSelectedAccount(@NonNull String accountName) {
        mPrefsEditor.putString(mContext.getString(R.string.itsmagic_present_simpleaccountmanager_selected_account_name), accountName);
        mPrefsEditor.apply();
    }

    /**
     * Loads the currently selected account name.
     * @return the account name if it exists
     */
    @Nullable
    public String loadSelectedAccount() {
        return mPrefs.getString(mContext.getString(R.string.itsmagic_present_simpleaccountmanager_selected_account_name), null);
    }

    /** Clears the currently selected account name from prefs */
    public void removeSelectedAccount() {
        mPrefsEditor.remove(mContext.getString(R.string.itsmagic_present_simpleaccountmanager_selected_account_name));
        mPrefsEditor.apply();
    }
}
