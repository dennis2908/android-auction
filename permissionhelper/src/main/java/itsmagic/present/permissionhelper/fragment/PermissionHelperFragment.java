package itsmagic.present.permissionhelper.fragment;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import itsmagic.present.permissionhelper.util.PermissionHelper;
import itsmagic.present.permissionhelper.view.PermissionInfoDialog;

/**
 * Created by Alvin Rusli on 5/12/2016.
 * <p/>
 * The custom GUI-less fragment for the class.
 */
public class PermissionHelperFragment extends Fragment {

    /** This fragment's tag */
    public static final String TAG = "itsmagic.present.permissionhelper";

    /** The {@link PermissionHelper} object */
    private PermissionHelper mPermissionHelper;

    /** The {@link List} of permissions */
    private List<String> mPermissions;

    /** The {@link List} of permission information */
    private List<String> mPermissionInfos;

    /** The request code */
    private int mRequestCode;

    /** Determines if the permission request should show the information beforehand */
    private boolean isWithInformation;

    /** The listener when app should show permission information */
    private PermissionHelper.OnShouldShowPermissionInfoListener mShouldShowPermissionInfoListener;

    /** The listener when all requested permission is granted */
    private PermissionHelper.OnPermissionGrantedListener mPermissionGrantedListener;

    /** The listener when some permission is denied */
    private PermissionHelper.OnPermissionDeniedListener mPermissionDeniedListener;

    /** The {@link List} of denied permissions */
    private final List<String> mDeniedPermissions = new ArrayList<>();

    /** The {@link List} of denied permission information */
    private final List<String> mDeniedPermissionInfos = new ArrayList<>();

    /** Sets the permission helper object */
    public void setPermissionHelper(final PermissionHelper helper) {
        mPermissionHelper = helper;
    }

    /** Sets the read permission fields */
    public void setPermissions(final List<String> permissions) {
        mPermissions = permissions;
    }

    /** Sets the graph request fields */
    public void setPermissionInfos(final List<String> permissionInfos) {
        mPermissionInfos = permissionInfos;
    }

    /** Sets the graph request fields */
    public void setRequestCode(final int requestCode) {
        mRequestCode = requestCode;
    }

    /** Sets if the permission request should show the information beforehand */
    public void setWithInformation(boolean withInformation) {
        isWithInformation = withInformation;
    }

    /** Set the listener when app should show permission information */
    public final void setOnShouldShowPermissionInfoListener(PermissionHelper.OnShouldShowPermissionInfoListener listener) {
        mShouldShowPermissionInfoListener = listener;
    }

    /** Set the listener when all requested permission is granted */
    public final void setOnPermissionGrantedListener(PermissionHelper.OnPermissionGrantedListener listener) {
        mPermissionGrantedListener = listener;
    }

    /** Set the listener when some permission is denied */
    public final void setOnPermissionDeniedListener(PermissionHelper.OnPermissionDeniedListener listener) {
        mPermissionDeniedListener = listener;
    }

    /**
     * Creates a new instance of this class.
     * @return A new {@link PermissionHelperFragment} instance
     */
    public static PermissionHelperFragment newInstance() {
        return new PermissionHelperFragment();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mRequestCode) {
            if (isPermissionAllowed()) {
                // All permission is allowed
                if (mPermissionGrantedListener != null) mPermissionGrantedListener.onPermissionGranted(mPermissionHelper, false);
            } else {
                // Some permission is still denied
                if (mPermissionDeniedListener != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Check if rationale should be shown,
                        // if it isn't then the user has completely denied the permission
                        boolean shouldShowPermissionRationale = false;
                        for (String permission : mDeniedPermissions) {
                            if (shouldShowRequestPermissionRationale(permission)) {
                                shouldShowPermissionRationale = true;
                                break;
                            }
                        }
                        mPermissionDeniedListener.onPermissionDenied(mPermissionHelper, mDeniedPermissions, !shouldShowPermissionRationale);
                    } else {
                        mPermissionDeniedListener.onPermissionDenied(mPermissionHelper, mDeniedPermissions, false);
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startProcess();
    }

    /** Begin the social media sign in / sign out process */
    public final void startProcess() {
        if (isWithInformation) requestPermission();
        else requestPermissionWithoutInformation();
    }

    /** Request all permission access */
    public final void requestPermission() {
        requestPermission(true);
    }

    /** Request all permission access, this ignores the requirement to display permission info */
    public final void requestPermissionWithoutInformation() {
        requestPermission(false);
    }

    /**
     * Request all permission access
     * @param isWithInformation determines if permission request should ignore
     *                          the requirement to display permission info
     */
    private void requestPermission(final boolean isWithInformation) {
        if (isPermissionAllowed()) {
            // All permission is already allowed
            if (mPermissionGrantedListener != null) mPermissionGrantedListener.onPermissionGranted(mPermissionHelper, true);
        } else {
            // Some permission is denied, request the permissions
            requestPermissions(mDeniedPermissions, mDeniedPermissionInfos, isWithInformation);
        }
    }

    /**
     * Determines if app has full specified permission access.
     * @return true if <strong>ALL</strong> permissions are allowed, otherwise false
     */
    private boolean isPermissionAllowed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPermissions == null) {
                Log.e(getClass().getSimpleName(), "Permission list is null, make sure the permissions has been set!");
                return false;
            } else {
                boolean isInfoValid = false;
                if (mPermissionInfos != null) {
                    if (mPermissions.size() == mPermissionInfos.size()) {
                        isInfoValid = true;
                    } else {
                        Log.w(getClass().getSimpleName(), "Permission info list doesn't have the same size as permission list. Not showing permission information dialog.");
                    }
                }

                mDeniedPermissions.clear();
                mDeniedPermissionInfos.clear();

                boolean isAllPermissionAllowed = true;
                int size = mPermissions.size();
                for (int i = 0; i < size; i++) {
                    if (!hasPermission(mPermissions.get(i))) {
                        isAllPermissionAllowed = false;

                        // Add the permission
                        if (!mDeniedPermissions.contains(mPermissions.get(i))) {
                            mDeniedPermissions.add(mPermissions.get(i));

                            // Add the permission information if specified correctly
                            if (isInfoValid) mDeniedPermissionInfos.add(mPermissionInfos.get(i));
                        }
                    }
                }

                return isAllPermissionAllowed;
            }
        } else {
            // Treat all permission request as a granted for API<23
            Log.i(getClass().getSimpleName(), "Requesting permission for API<23, treating it as a success");
            return true;
        }
    }

    /**
     * Checks if application has a specified permission.
     * @param permission the specified permission
     * @return true if permission is granted
     */
    private boolean hasPermission(final String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                (getActivity().checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Request permissions access.
     * @param permissions the permission list
     * @param permissionInfos the information for the specified permissions
     * @param isWithInformation determines if permission request should ignore
     *                          the requirement to display permission info
     */
    private void requestPermissions(final List<String> permissions, final List<String> permissionInfos, final boolean isWithInformation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final List<String> requiredPermissions = new ArrayList<>();
            final List<String> requiredPermissionInfos = new ArrayList<>();

            boolean shouldShowPermissionRationale = false;
            if (isWithInformation && permissionInfos != null && permissionInfos.size() > 0) {
                if (permissions.size() == permissionInfos.size()) {
                    // Find out if any of the requested permission should show the rationale
                    for (String permission : permissions) {
                        if (shouldShowRequestPermissionRationale(permission)) {
                            shouldShowPermissionRationale = true;
                            break;
                        }
                    }
                } else {
                    Log.w(getClass().getSimpleName(), "Permission info list doesn't have the same size as permission list. Not showing permission information dialog.");
                }
            }

            int size = permissions.size();
            String permission;
            for (int i = 0; i < size; i++) {
                permission = permissions.get(i);
                if (!hasPermission(permission)) {
                    requiredPermissions.add(permission);
                    if (shouldShowPermissionRationale) requiredPermissionInfos.add(permissionInfos.get(i));
                }
            }

            if (requiredPermissions.size() > 0) {
                if (shouldShowPermissionRationale) {
                    if (mShouldShowPermissionInfoListener != null) {
                        // Call the listener
                        mShouldShowPermissionInfoListener.onShouldShowPermissionInfo(
                                mPermissionHelper,
                                requiredPermissions,
                                requiredPermissionInfos);
                    } else {
                        // Show the default implementation of permission info dialog
                        new PermissionInfoDialog(
                                getActivity(),
                                mPermissionHelper,
                                requiredPermissions,
                                requiredPermissionInfos)
                                .show();
                    }
                } else {
                    // Don't display any explanation, request immediately
                    requestPermissions(
                            requiredPermissions.toArray(new String[requiredPermissions.size()]),
                            mRequestCode);
                }
            }
        } else {
            // Treat the permission request as a success for API<23
            Log.i(getClass().getSimpleName(), "Requesting permission for API<23, treating it as a success");
            if (mPermissionGrantedListener != null) mPermissionGrantedListener.onPermissionGranted(mPermissionHelper, true);
        }
    }
}
