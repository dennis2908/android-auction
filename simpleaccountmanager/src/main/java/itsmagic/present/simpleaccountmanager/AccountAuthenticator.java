package itsmagic.present.simpleaccountmanager;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import itsmagic.present.simpleaccountmanager.json.JSONException;
import itsmagic.present.simpleaccountmanager.json.JSONObject;
import itsmagic.present.simpleaccountmanager.util.AccountBundleParser;
import itsmagic.present.simpleaccountmanager.util.AccountConstants;

/**
 * Created by Alvin Rusli on 04/04/2017.
 * <p/>
 * The account authenticator class.
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private final Context mContext;

    public AccountAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Class accountManagerActivity = AccountHelper.getAccountManagerActivity();
        if (accountManagerActivity == null) return new Bundle();

        Intent intent = new Intent(mContext, accountManagerActivity);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AccountHelper.getAccountType())) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager accountManager = AccountManager.get(mContext);
        String accountName = account.name;
        String accountType = AccountHelper.getAccountType();
        String authToken = accountManager.peekAuthToken(account, authTokenType);
        Bundle accountData = null;
        try {
            accountData = AccountBundleParser.jsonToBundle(
                    new JSONObject(accountManager.getUserData(account, AccountConstants.KEY_ACCOUNT_DATA)));
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }

        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            result.putString(AccountConstants.KEY_ACCOUNT_NAME, accountName);
            result.putString(AccountConstants.KEY_ACCOUNT_TOKEN, authToken);
            result.putBundle(AccountConstants.KEY_ACCOUNT_DATA, accountData);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity panel.
        Class accountManagerActivity = AccountHelper.getAccountManagerActivity();
        if (accountManagerActivity == null) return new Bundle();

        final Intent intent = new Intent(mContext, accountManagerActivity);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        // null means we don't support multiple authToken types
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        // This call is used to query whether the Authenticator supports
        // specific features. We don't expect to get called, so we always
        // return false (no) for any queries.
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

}
