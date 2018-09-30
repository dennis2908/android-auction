# Simple AccountManager
A library to simplify the overly complex Android's AccountManager.

# Features
* Simplify creating accounts on the account manager
* Put any additional data on the account
* Add your own sync adapter(s) for background synchronization
* Multiple account management

# Usage
* Create an AccountHelper object:

        AccountHelper accountHelper = new AccountHelper(context);

* Adding an account (with additional data - this example uses Gson):

		Profile profile = new Profile(); // A POJO for user's profile
		profile.setId(1);
		profile.setEmail("john.doe@email.com");
		profile.setAccessToken("myaccesstoken");
		profile.setBirthdate("2000/12/31");
		profile.setAddress("myhomeaddress");

		// Create a bundle
		Bundle data = new Bundle();
		data.putString("user_string", "mycustomstring");
		data.putString("user_profile", new Gson().toJson(profile));

		// Add the account
		accountHelper.addAccount(profile.getEmail(), profile.getAccessToken(), data);
		// Different methods for adding accounts are also available

* Obtain the saved account:

        Account account = accountHelper.getAccount(); // Account name must be set beforehand
        or
        Account account = accountHelper.getAccount(accountName);

* Example usage for obtaining an account data (with additional data - this example uses Gson):

        Bundle data = accountHelper.getAccountBundle(); // Account name must be set beforehand
        or
        Bundle data = accountHelper.getAccountBundle(accountName);
        
        // Obtain the data from a bundle normally
        String userString = data.getString("user_string");
        String userProfileJson = data.getString("user_profile");
        Profile profile = new Gson().fromJson(data.getString("user_profile", Profile.class);

* By default, the first ever added account will automatically be set as the selected one.
  * To manually set which account to use, use:

            // This will allow methods that doesn't have account name as its parameter
            // to use the specified account name in this method until it's changed.
            accountHelper.setSelectedAccountName(accountName);

  * To open an account selection dialog, use:

            accountHelper.showAccountSelection(activity, requestCode);

  * Afterwards, on your Activity class:

            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                
                // This method will return true if the result is a success
                if (accountHelper.onActivityResult(requestCode, resultCode, data)) {
                    // Do your stuff here
                }
            }

# Setup
* Create an account-authenticator file on your project (e.g. `\res\xml\account_auth.xml`):

		<?xml version="1.0" encoding="utf-8"?>
		<account-authenticator xmlns:android="http://schemas.android.com/apk/res/android"
			android:accountType="com.example.myproject"
			android:label="@string/app_name"
			android:icon="@mipmap/ic_launcher"
			android:smallIcon="@mipmap/ic_launcher" />

* Add a service on your project's Manifest:

        <!-- Account auth service -->
        <service
            android:name="itsmagic.present.simpleaccountmanager.AccountAuthService"
            android:exported="true">
            <intent-filter>
                <action
                    android:name="android.accounts.AccountAuthenticator" />
            </intent-filter>
            <meta-data
                android:name="android.accounts.AccountAuthenticator"
                android:resource="@xml/account_auth" /> <!-- Your account-authenticator file -->
        </service>

* Initialize `AccountHelper` on your Application class:

		public class App extends Application {

			@Override
			public void onCreate() {
				super.onCreate();

				// Initialize the account helper
				AccountHelper.init(AccountConstant.ACCOUNT_TYPE, LoginActivity.class);
			}
		}

# Sync Adapter
* To add your sync adapter on your account, you will need to make a sync-adapter file on your project (e.g. `\res\xml\account_sync_profile.xml`):

		<?xml version="1.0" encoding="utf-8"?>
		<sync-adapter xmlns:android="http://schemas.android.com/apk/res/android"
			android:accountType="com.example.myproject"
			android:contentAuthority="com.example.myproject.provider.profile"
			android:userVisible="true"
			android:isAlwaysSyncable="true" />

* To add **a single sync adapter** on the account, you will need to **create 3 classes** that extends from 3 other classes from the library:
  * Sync adapter - This class will handle your background sync process:

			public class ProfileSyncAdapter extends AccountSyncAdapter {

				public ProfileSyncAdapter(Context context, boolean autoInitialize) {
					super(context, autoInitialize);
				}

				@Override
				public void onBackgroundSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
					// Do your background sync process here - e.g. sync your profile data
				}
			}

  * Sync provider - An empty class required to provide the sync adapter:

			public class ProfileSyncProvider extends AccountSyncProvider {}

  * Sync service - A service that creates the sync adapter:

			public class ProfileSyncService extends AccountSyncService {

				@Override
				public AccountSyncAdapter initAccountSyncAdapter() {
					return new ProfileSyncAdapter(this, true);
				}
			}

* After you created all 3 classes, add the following to your project's Manifest:

        <!-- Profile sync provider and service -->
        <provider android:name="com.example.myproject.ProfileSyncProvider"
            android:authorities="com.example.myproject.provider.profile"
            android:syncable="true"
            android:label="Profile Information" />
        <service
            android:name="com.example.myproject.ProfileSyncService"
            android:exported="true">
            <intent-filter>
                <action android:name="android.content.SyncAdapter" />
            </intent-filter>
            <meta-data
                android:name="android.content.SyncAdapter"
                android:resource="@xml/account_sync_profile" />
        </service>

* On your application class, modify the `AccountHelper`'s initialization by adding a `Pair` of the provider (String) and sync period (long):

        // Initialize the account helper
		AccountHelper.init(AccountConstant.ACCOUNT_TYPE, LoginActivity.class,
                new Pair<>(AccountConstant.ACCOUNT_PROVIDER_PROFILE, TimeUnit.DAYS.toMillis(7))); // You can add as many sync adapters as you want

* To update the duration of a sync adapter, call `#setSyncAdapterDuration()` and pass the required paremeters with the duration in your `SyncAdapter` class.

**For each sync adapter, you will need to create another 3 classes, add them in your project's Manifest, and specify the authorities and duration during `AccountHelper`'s initialization**.

# Permission

* This library already adds several required permissions in the manifest:

        android.permission.GET_ACCOUNTS
        android.permission.MANAGE_ACCOUNTS
        android.permission.AUTHENTICATE_ACCOUNTS
        android.permission.READ_SYNC_SETTINGS
        android.permission.WRITE_SYNC_SETTINGS

* If your application targets API>23, only one permission is required to be requested at runtime:

        android.permission.GET_ACCOUNTS

# Additional Information
* This library is using several dependencies:

        'com.android.support:support-core-utils:27.0.2'

* To avoid build conflicts, exclude the support library if needed:

        compile (project(':simpleaccountmanager')) {
            exclude group: 'com.android.support'
        }
