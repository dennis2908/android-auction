package itsmagic.present.permissionhelper.util;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import itsmagic.present.permissionhelper.fragment.PermissionHelperFragment;

/**
 * Created by Alvin Rusli on 2/9/2016.
 * <p/>
 * A helper class for runtime permissions.
 */
public class PermissionHelper {

    /** The {@link Activity} */
    private Activity mActivity;

    /** The fragment manager */
    private FragmentManager mFragmentManager;

    /** The GUI-less fragment to manage facebook login */
    private PermissionHelperFragment mFragment;

    /** The {@link List} of permissions */
    private List<String> mPermissions;

    /** The {@link List} of permission information */
    private List<String> mPermissionInfos;

    /**
     * The request code. When unspecified,
     * the permission helper will generate a random number (1-100).
     */
    private int mRequestCode = -1;

    /** The listener when app should show permission information */
    private OnShouldShowPermissionInfoListener mShouldShowPermissionInfoListener;

    /** The listener when all requested permission is granted */
    private OnPermissionGrantedListener mPermissionGrantedListener;

    /** The listener when some permission is denied */
    private OnPermissionDeniedListener mPermissionDeniedListener;

    /** The constructor for this class */
    public PermissionHelper(Activity activity) {
        mActivity = activity;
        initDefaultValues();
    }

    /** The constructor for this class */
    public PermissionHelper(Builder builder) {
        mActivity = builder.getActivity();
        mPermissions = builder.getPermissions();
        mPermissionInfos = builder.getPermissionInfos();
        mRequestCode = builder.getRequestCode();
        mShouldShowPermissionInfoListener = builder.getOnShouldShowPermissionInfoListener();
        mPermissionGrantedListener = builder.getOnPermissionGrantedListener();
        mPermissionDeniedListener = builder.getOnPermissionDeniedListener();

        initDefaultValues();
    }

    /** Initialize the default values for the helper */
    private void initDefaultValues() {
        mFragmentManager = mActivity.getFragmentManager();

        if (mRequestCode == -1) {
            // Generate a random request code (1-100) if request code is unspecified
            mRequestCode = new Random().nextInt(100) + 1;
        }
    }

    /** Get the specified permissions */
    public final List<String> getPermissions() {
        return mPermissions;
    }

    /** Set the permissions for this class */
    public final void setPermissions(String ... permissions) {
        mPermissions = Arrays.asList(permissions);
    }

    /** Set the permissions for this class */
    public final void setPermissions(List<String> permissions) {
        mPermissions = permissions;
    }

    /** Get the specified permission information */
    public final List<String> getPermissionInfos() {
        return mPermissionInfos;
    }

    /** Set the permission information for this class */
    public final void setPermissionInfos(String ... permissionInfos) {
        mPermissionInfos = Arrays.asList(permissionInfos);
    }

    /** Set the permission information for this class */
    public final void setPermissionInfos(List<String> permissionInfos) {
        mPermissionInfos = permissionInfos;
    }

    /** Set the permissions information for this class */
    public final void setPermissionInfoStringIds(Integer ... permissionInfoStringIds) {
        setPermissionInfoStringIds(Arrays.asList(permissionInfoStringIds));
    }

    /** Set the permission information for this class */
    public final void setPermissionInfoStringIds(List<Integer> permissionInfoStringIds) {
        mPermissionInfos = new ArrayList<>();
        for (int id: permissionInfoStringIds) {
            mPermissionInfos.add(mActivity.getString(id));
        }
    }

    /** Set the request code */
    public final void setRequestCode(int requestCode) {
        mRequestCode = requestCode;
    }

    /** Set the listener when app should show permission information */
    public final void setOnShouldShowPermissionInfoListener(OnShouldShowPermissionInfoListener listener) {
        mShouldShowPermissionInfoListener = listener;
    }

    /** Set the listener when all requested permission is granted */
    public final void setOnPermissionGrantedListener(OnPermissionGrantedListener listener) {
        mPermissionGrantedListener = listener;
    }

    /** Set the listener when some permission is denied */
    public final void setOnPermissionDeniedListener(OnPermissionDeniedListener listener) {
        mPermissionDeniedListener = listener;
    }

    /** Request all permission access */
    public final void requestPermission() {
        launchFragment(true);
    }

    /** Request all permission access, this ignores the requirement to display permission info */
    public final void requestPermissionWithoutInformation() {
        launchFragment(false);
    }

    /**
     * Launches a GUI-less fragment to request the permissions.
     * @param isWithInformation determines if permission request should ignore
     *                          the requirement to display permission info
     */
    private void launchFragment(final boolean isWithInformation) {
        // Manage the fragment
        final boolean isNewFragment;
        mFragment = (PermissionHelperFragment) mFragmentManager.findFragmentByTag(PermissionHelperFragment.TAG);
        if (mFragment == null) {
            mFragment = PermissionHelperFragment.newInstance();
            isNewFragment = true;
        } else {
            isNewFragment = false;
        }

        // Set additional arguments
        mFragment.setPermissionHelper(this);
        mFragment.setPermissions(mPermissions);
        mFragment.setPermissionInfos(mPermissionInfos);
        mFragment.setRequestCode(mRequestCode);
        mFragment.setWithInformation(isWithInformation);

        // Set the listeners
        mFragment.setOnShouldShowPermissionInfoListener(mShouldShowPermissionInfoListener);
        mFragment.setOnPermissionGrantedListener(mPermissionGrantedListener);
        mFragment.setOnPermissionDeniedListener(mPermissionDeniedListener);

        if (isNewFragment) {
            // Begin the fragment transaction
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.add(mFragment, PermissionHelperFragment.TAG);
            fragmentTransaction.commit();
        } else {
            mFragment.startProcess();
        }
    }

    /** Launch an application's settings page */
    public static void startSettingsActivity(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", packageName, null));
        context.startActivity(intent);
    }

    /** The permission helper builder */
    public static class Builder {

        /** The {@link Activity} */
        private Activity mActivity;

        /** The {@link List} of permissions */
        private List<String> mPermissions;

        /** The {@link List} of permission information */
        private List<String> mPermissionInfos;

        /**
         * The request code. When unspecified,
         * the permission helper will generate a random number (1-100).
         */
        private int mRequestCode = -1;

        /** The listener when app should show permission information */
        private OnShouldShowPermissionInfoListener mShouldShowPermissionInfoListener;

        /** The listener when all requested permission is granted */
        private OnPermissionGrantedListener mPermissionGrantedListener;

        /** The listener when some permission is denied */
        private OnPermissionDeniedListener mPermissionDeniedListener;

        /** Default constructor */
        public Builder(Activity activity) {
            mActivity = activity;
        }

        /** Set the permissions */
        public final Builder permissions(String ... permissions) {
            mPermissions = Arrays.asList(permissions);
            return this;
        }

        /** Set the permissions */
        public final Builder permissions(List<String> permissions) {
            mPermissions = permissions;
            return this;
        }

        /** Set the permissions information */
        public final Builder permissionInfos(String ... permissionInfos) {
            return permissionInfos(Arrays.asList(permissionInfos));
        }

        /** Set the permission information */
        public final Builder permissionInfos(List<String> permissionInfos) {
            mPermissionInfos = permissionInfos;
            return this;
        }

        /** Set the permissions information */
        public final Builder permissionInfoStringIds(Integer ... permissionInfoStringIds) {
            return permissionInfoStringIds(Arrays.asList(permissionInfoStringIds));
        }

        /** Set the permission information */
        public final Builder permissionInfoStringIds(List<Integer> permissionInfoStringIds) {
            mPermissionInfos = new ArrayList<>();
            for (int id: permissionInfoStringIds) {
                mPermissionInfos.add(mActivity.getString(id));
            }
            return this;
        }

        /** Set the request code */
        public final Builder requestCode(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }

        /** Set the listener when app should show permission information */
        public final Builder onShouldShowPermissionInfo(OnShouldShowPermissionInfoListener listener) {
            mShouldShowPermissionInfoListener = listener;
            return this;
        }

        /** Set the listener when all requested permission is granted */
        public final Builder onPermissionGranted(OnPermissionGrantedListener listener) {
            mPermissionGrantedListener = listener;
            return this;
        }

        /** Set the listener when some permission is denied */
        public final Builder onPermissionDenied(OnPermissionDeniedListener listener) {
            mPermissionDeniedListener = listener;
            return this;
        }

        /** Obtain the activity */
        private Activity getActivity() {
            return mActivity;
        }

        /** Obtain the permission list */
        private List<String> getPermissions() {
            return mPermissions;
        }

        /** Obtain the permission information list */
        private List<String> getPermissionInfos() {
            return mPermissionInfos;
        }

        /** Obtain the request code */
        private int getRequestCode() {
            return mRequestCode;
        }

        /** Obtain the listener when app should show permission information */
        public final OnShouldShowPermissionInfoListener getOnShouldShowPermissionInfoListener() {
            return mShouldShowPermissionInfoListener;
        }

        /** Obtain the listener when all requested permission is granted */
        public final OnPermissionGrantedListener getOnPermissionGrantedListener() {
            return mPermissionGrantedListener;
        }

        /** Obtain the listener when some permission is denied */
        public final OnPermissionDeniedListener getOnPermissionDeniedListener() {
            return mPermissionDeniedListener;
        }

        /** Build the permission helper */
        public final PermissionHelper build() {
            return new PermissionHelper(this);
        }

        /** Build the permission helper */
        public final void requestPermission() {
            new PermissionHelper(this).requestPermission();
        }
    }

    /** Listener to handle permission requests */
    public interface OnShouldShowPermissionInfoListener {

        /**
         * Called when app should show permission information.
         * @param permissionHelper the permission helper object
         * @param permissions the requested permissions
         * @param infoMessages the permission information messages
         */
        void onShouldShowPermissionInfo(PermissionHelper permissionHelper, List<String> permissions, List<String> infoMessages);
    }

    /** Listener to handle permission requests */
    public interface OnPermissionGrantedListener {

        /**
         * Called when all requested permission is granted.
         * @param permissionHelper the permission helper object
         * @param isPermissionAlreadyGranted determines if the permission(s)
         *                                   were already granted before.
         */
        void onPermissionGranted(PermissionHelper permissionHelper, boolean isPermissionAlreadyGranted);
    }

    /** Listener to handle permission requests */
    public interface OnPermissionDeniedListener {

        /**
         * Called when some permission is denied.
         * @param permissionHelper the permission helper object
         * @param deniedPermissions the list of denied permissions
         * @param isCompletelyDenied determines if the permission is completely denied
         */
        void onPermissionDenied(PermissionHelper permissionHelper, List<String> deniedPermissions, boolean isCompletelyDenied);
    }
}
