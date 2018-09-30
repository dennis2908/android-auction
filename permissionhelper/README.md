# PermissionHelper
A library made to simplify Permission check and requests for Android M and above.

# Features:
* Request for permission(s) and obtain the result easily.
* Returns an information message of why a permission is required.

# Usage:
* Request the required permissions by creating a `PermissionHelper` object:
  * Normal:

            PermissionHelper permissionHelper = new PermissionHelper(activity);
            
            // Set the required permissions
            permissionHelper.setPermissions(Manifest.permission.GET_ACCOUNTS);
            
            // Set the information message to be shown if user has previously denied the permission
            permissionHelper.setPermissionInfos("Account permission required");
            
            // Add the listener when permission is granted
            permissionHelper.setOnPermissionGrantedListener(new PermissionHelper.OnPermissionGrantedListener() {
                @Override
                public void onPermissionGranted(PermissionHelper permissionHelper, boolean isPermissionAlreadyGranted) {
		            // Permission is granted
                }
            });
            
            // Add the listener when permission is denied
            permissionHelper.setOnPermissionDeniedListener(new PermissionHelper.OnPermissionDeniedListener() {
                @Override
                public void onPermissionDenied(PermissionHelper permissionHelper, List<String> deniedPermissions, boolean isCompletelyDenied) {
                    // Permission is denied
                }
            });
            
            // Request the permission
            permissionHelper.requestPermission();

  * Builder:

            new PermissionHelper.Builder(activity)
                    .permissions(Manifest.permission.GET_ACCOUNTS)
                    .permissionInfos("Account permission required")
                    .onPermissionGranted(new PermissionHelper.OnPermissionGrantedListener() {
                        @Override
                        public void onPermissionGranted(PermissionHelper permissionHelper, boolean isPermissionAlreadyGranted) {
                            // Permission is granted
                        }
                    })
                    .onPermissionDenied(new PermissionHelper.OnPermissionDeniedListener() {
                        @Override
                        public void onPermissionDenied(PermissionHelper permissionHelper, List<String> deniedPermissions, boolean isCompletelyDenied) {
                            // Permission is denied
                        }
                    })
                    .requestPermission();

* For multiple permissions, simply add as many as you need when specifying the required permissions:
  * Normal:

            permissionHelper.setPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            
            // Information message must have the same size as the permissions,
            // otherwise the information dialog will never be shown
            permissionHelper.setPermissionInfos(
                    "Camera permission required.",
                    "External storage read permission required.",
                    "External storage write permission required.");

  * Builder:

            new PermissionHelper.Builder(activity)
                    .permissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .permissionInfos(
                            "Camera permission required.",
                            "External storage read permission required.",
                            "External storage write permission required.")
                    ...

* By default, this library generates a random integer (between 1-100) as the request code. This can be overridden by specifying your own request code by calling `#setRequestCode()` before requesting for permission.

# Customizing the information dialog
* To implement your own dialog when user has denied a permission and re-request it, implement a `OnShouldShowPermissionInfoListener` on the PermissionHelper object:
  * Normal:

            permissionHelper.setOnShouldShowPermissionInfoListener(new PermissionHelper.OnShouldShowPermissionInfoListener() {
                @Override
                public void onShouldShowPermissionInfo(PermissionHelper permissionHelper, List<String> permissions, List<String> infoMessages) {
                    // Do your own implementation here
                    // To finally re-request the permission, use:
                    //     permissionHelper.requestPermissionWithoutInformation();
                    // e.g. After user clicks "OK" on your custom dialog
                }
            });

  * Builder:

            new PermissionHelper.Builder(activity)
                    .onShouldShowPermissionInfo(new PermissionHelper.OnShouldShowPermissionInfoListener() {
                        @Override
                        public void onShouldShowPermissionInfo(PermissionHelper permissionHelper, List<String> permissions, List<String> infoMessages) {
                            // Do your own implementation here
                        }
                    })
                    ...

