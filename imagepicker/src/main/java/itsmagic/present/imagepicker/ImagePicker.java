package itsmagic.present.imagepicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

import itsmagic.present.imagepicker.fragment.ImagePickerFragment;
import itsmagic.present.imagepicker.util.ImagePickerUtil;

/**
 * Created by Alvin Rusli on 5/12/2016.
 * <p/>
 * An image picker utility core class that can be used to show the image selection dialog.
 * The result can be obtained via the {@link OnImagePickerListener#onImagePickerSuccess(Uri)} method.
 * <p/>
 * By default, this will show a generic {@link AlertDialog} for the camera / image selection,
 * but the dialog can be customized by providing a {@link Dialog} object.
 */
public class ImagePicker {

    /** The {@link Activity} */
    private Activity mActivity;

    /** The {@link OnImagePickerListener} object */
    private OnImagePickerListener mListener;

    /** The selected directory */
    private File mSelectedDirectory;

    /**
     * The specified filename for the created image.
     * By default, the filename is renamed into [current time (in millis)].jpg.
     */
    private String mFilename;

    /** The {@link Dialog} object for the image selection popup */
    private Dialog mCustomDialog;

    /** The {@link Dialog} object for the image selection popup (for Camera-less devices) */
    private Dialog mCustomNoCameraDialog;

    /** Determines if app should open the image cropping activity after obtaining the image */
    private boolean isWithCrop = false;

    /** The class of the {@link Activity} that will be called to handle cropping function */
    private Class mCroppingClass = null;

    /** The specified image ratio (width), only used in cropping */
    private int mWidthRatio = -1;

    /** The specified image ratio (height), only used in cropping */
    private int mHeightRatio = -1;

    /** The specified minimum image dimension (width), only used in cropping */
    private int mWidthMin = -1;

    /** The specified minimum image dimension (height), only used in cropping */
    private int mHeightMin = -1;

    /** The specified maximum image dimension (width), only used in cropping */
    private int mWidthMax = -1;

    /** The specified maximum image dimension (height), only used in cropping */
    private int mHeightMax = -1;

    /**
     * Determines if app should keep the temporary image, instead of auto-deleting it.
     * Useful for debugging.
     */
    private boolean isTempSaved = false;

    /** The {@link FragmentManager} to handle the GUI-less fragments */
    private FragmentManager mFragmentManager;

    /** The {@link ImagePickerFragment} objects for picker and cropper */
    private ImagePickerFragment mPickerFragment, mCropperFragment;

    /** The selected image path */
    private String mImagePath;

    /** The cropped image path */
    private String mCroppedImagePath;

    /**
     * The temporary image file.
     * Temporary image will be created if app needs to save the obtained image from camera,
     * before opening the image cropping intent, which will then saves the cropped image
     * with the specified filename.
     * <p/>
     * The temporary image will be deleted when it's not needed anymore.
     */
    private File mTempFile;

    /**
     * The constructor of this class.
     * @param activity The activity
     */
    public ImagePicker(Activity activity) {
        mActivity = activity;
        mListener = null;
        mSelectedDirectory = null;
        mFilename = null;
        mCustomDialog = null;
        mCustomNoCameraDialog = null;
        isWithCrop = false;
        mCroppingClass = null;
        mWidthRatio = -1;
        mHeightRatio = -1;
        mWidthMin = -1;
        mHeightMin = -1;
        mWidthMax = -1;
        mHeightMax = -1;
        isTempSaved = false;

        initDefaultValues();
        onInit();
    }

    /**
     * The constructor of this class.
     * @param builder The builder object
     */
    public ImagePicker(CustomBuilder builder) {
        mActivity = builder.getActivity();
        mListener = builder.getListener();
        mSelectedDirectory = builder.getDirectory();
        mFilename = builder.getFilename();
        mCustomDialog = builder.getCustomDialog();
        mCustomNoCameraDialog = builder.getCustomNoCameraDialog();
        isWithCrop = builder.isWithCrop();
        mCroppingClass = builder.getCroppingClass();
        mWidthRatio = builder.getCropWidthRatio();
        mHeightRatio = builder.getCropHeightRatio();
        mWidthMin = builder.getCropWidthMin();
        mHeightMin = builder.getCropHeightMin();
        mWidthMax = builder.getCropWidthMax();
        mHeightMax = builder.getCropHeightMax();
        isTempSaved = builder.isTempSaved();

        initDefaultValues();
        onInit();
    }

    /** Initialize the default values required by this class */
    private void initDefaultValues() {
        if (mSelectedDirectory == null) {
            mSelectedDirectory = new File(Environment.getExternalStorageDirectory() + "/imagepicker");
            Log.w(getClass().getSimpleName(), "Image directory isn't specified, trying to use default external storage folder: " + mSelectedDirectory.getPath());
        }

        if (mFilename == null || mFilename.isEmpty()) {
            mFilename = System.currentTimeMillis() + ".jpg";
        }

        mFragmentManager = mActivity.getFragmentManager();
        mImagePath = "";
        mCroppedImagePath = "";
    }

    /**
     * Add a listener for the obtained results.
     * @param listener the {@link OnImagePickerListener} object
     */
    public void setListener(OnImagePickerListener listener) {
        mListener = listener;
    }

    /**
     * Determines the storage directory to save the selected image.
     */
    public void setDirectory(File directory) {
        mSelectedDirectory = directory;
    }

    /**
     * Set the filename for the cropped image, ".jpg" suffix is added automatically when needed
     * @param filename the image filename
     */
    public void setFilename(String filename) {
        String newFilename = filename;
        if (!newFilename.endsWith(".jpg")) newFilename += ".jpg";
        mFilename = newFilename;
    }

    /**
     * Sets the custom {@link Dialog} for the image picker.
     */
    public void setCustomDialog(Dialog dialog) {
        mCustomDialog = dialog;
    }

    /**
     * Sets the custom {@link Dialog} for the image picker (for Camera-less devices).
     * If this isn't set (or is null), no {@link Dialog} will be shown,
     * and app will immediately show the file picker instead.
     */
    public void setCustomNoCameraDialog(Dialog dialog) {
        mCustomNoCameraDialog = dialog;
    }

    /**
     * Show a cropping intent after specifying the image.
     */
    public void setCrop(boolean isWithCrop) {
        this.isWithCrop = isWithCrop;
    }

    /**
     * Determines the class of an {@link Activity} to handle cropping function.
     * @param croppingClass the class
     */
    public void setCroppingClass(Class croppingClass) {
        mCroppingClass = croppingClass;
    }

    /**
     * Set the aspect ratio for the image cropping
     * @param widthRatio width ratio
     * @param heightRatio height ratio
     */
    public void setCropAspectRatio(int widthRatio, int heightRatio) {
        mWidthRatio = widthRatio;
        mHeightRatio = heightRatio;
    }

    /**
     * Set the dimension limit for the image cropping
     * @param widthMin width min
     * @param heightMin height min
     */
    public void setCropMinDimensions(int widthMin, int heightMin) {
        mWidthMin = widthMin;
        mHeightMin = heightMin;
    }

    /**
     * Set the dimension limit for the image cropping
     * @param widthMax width max
     * @param heightMax height max
     */
    public void setCropMaxDimensions(int widthMax, int heightMax) {
        mWidthMax = widthMax;
        mHeightMax = heightMax;
    }

    /**
     * Explicitly saves the temporary images as another file.
     * The temp image will be saved on the same directory as the result file.
     * <p/>
     * Mostly used for debugging.
     */
    public void setTempSaved() {
        isTempSaved = true;
    }

    /**
     * Show an {@link AlertDialog} allowing user to pick an image from camera or file.
     * <p/>
     * If a custom dialog is specified, show that instead.
     */
    public void showPicker() {
        if (Camera.getNumberOfCameras() > 0) {
            // If device has a camera
            if (mCustomDialog == null) {
                // If dialog is unspecified, display the default AlertDialog
                final String[] items = mActivity.getResources().getStringArray(R.array.impicker_selection);

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(mActivity.getResources().getString(R.string.impicker_title));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals(mActivity.getResources().getString(R.string.impicker_camera))) {
                            onCameraSelection();
                        } else if (items[item].equals(mActivity.getResources().getString(R.string.impicker_gallery))) {
                            onPickerSelection();
                        }
                    }
                });
                builder.show();
            } else {
                // Otherwise, display the custom AlertDialog
                mCustomDialog.show();
            }
        } else {
            // If device doesn't have any camera
            if (mCustomNoCameraDialog == null) {
                // If dialog is unspecified, display the file picker immediately
                onPickerSelection();
            } else {
                // Otherwise, display the custom AlertDialog
                mCustomNoCameraDialog.show();
            }
        }
    }

    /**
     * Opens the camera intent to obtain the image.
     */
    public void onCameraSelection() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            File photoFile;
            if (isWithCrop && mCroppingClass != null) {
                // Use the to-be-cropped image file instead of creating a temp file
                photoFile = createCroppedImageFile();
                assert photoFile != null;
                mImagePath = photoFile.getAbsolutePath();
            } else {
                photoFile = createImageFile();
            }
            
            Uri photoUri = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".imagepicker.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startPictureIntent(intent, ImagePickerUtil.REQUEST_CODE_CAMERA);
        }
    }

    /**
     * Opens the file selection intent to obtain the image.
     */
    public void onPickerSelection() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        startPictureIntent(intent, ImagePickerUtil.REQUEST_CODE_PICKER);
    }

//    /**
//     * Obtain a "content://" uri from a {@link File}
//     * @param imageFile the image {@link File}
//     */
//    private Uri getImageContentUri(File imageFile) {
//        Uri contentUri = null;
//
//        String filePath = imageFile.getAbsolutePath();
//        Cursor cursor = mActivity.getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                new String[] { MediaStore.Images.Media._ID },
//                MediaStore.Images.Media.DATA + "=? ",
//                new String[] { filePath }, null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
//            contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
//        } else {
//            if (imageFile.exists()) {
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.DATA, filePath);
//                contentUri = mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            }
//        }
//
//        if (cursor != null) cursor.close();
//        return contentUri;
//    }

    /**
     * Convert a "content://" uri to a "file://" uri.
     * @param contentUri the "content" uri
     * @return the "file" uri
     */
    private Uri convertToFileUri(Uri contentUri) {
        Uri fileUri = null;
        if (contentUri != null && ("content").equals(contentUri.getScheme())) {
            Cursor cursor = mActivity.getContentResolver().query(contentUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                String path = cursor.getString(0);
                if (path != null && !path.isEmpty()) {
                    if (!path.startsWith("file://")) path = "file://" + path;
                    fileUri = Uri.parse(path);
                }
                cursor.close();
            }
        }

        if (fileUri == null) fileUri = contentUri;
        return fileUri;
    }

    /**
     * Create an image file on the external pictures directory.
     * @return The target file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createImageFile() {
        File storageDir = mSelectedDirectory;
        File file;

        if (isWithCrop && mCroppingClass != null) {
            String tempFilename = "temp_" + System.currentTimeMillis() + ".jpg";
            mTempFile = new File(storageDir, tempFilename);
            file = mTempFile;
        } else {
            file = new File(storageDir, mFilename);
        }

        // Create the directories if needed
        if (!storageDir.exists()) storageDir.mkdirs();
        if (file.exists()) file.delete();

        mImagePath = file.getAbsolutePath();
        return file;
    }

    /**
     * Create a cropped image file on the external pictures directory.
     * @return The target file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createCroppedImageFile() {
        File storageDir = mSelectedDirectory;
        File file = new File(storageDir, mFilename);

        // Create the directories if needed
        if (!storageDir.exists()) storageDir.mkdirs();
        if (file.exists()) file.delete();

        mCroppedImagePath = file.getAbsolutePath();
        return file;
    }

    /**
     * Obtain the selected image by calling the activity result (with the camera / image picker intent)
     * in a GUI-less {@link Fragment} to obtain the result in the {@link Activity#onActivityResult(int, int, Intent)}
     * @param intent The specified intent to call
     */
    private void startPictureIntent(final Intent intent, final int requestCode) {
        // Send the temporary file as part of the bundle if applicable
        if (isTempSaved) mPickerFragment = ImagePickerFragment.newInstance(intent, requestCode);
        else mPickerFragment = ImagePickerFragment.newInstance(intent, requestCode, mTempFile);
        mPickerFragment.setOnResultListener(new ImagePickerFragment.OnResultListener() {
            @Override
            public void onResult(int requestCode, int resultCode, Intent data) {
                destroyPickerFragment();
                if (requestCode == ImagePickerUtil.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
                    if (mImagePath == null) {
                        if (mListener != null) mListener.onImagePickerFailure();
                    } else {
                        File sourceFile = new File(mImagePath);

                        if (isWithCrop && mCroppingClass != null) {
                            // The obtained photo is already set as the to-be-cropped image
                            File destinationFile = new File(mCroppedImagePath);
                            startCropIntent(Uri.fromFile(sourceFile), Uri.fromFile(destinationFile));
                        } else {
                            if (mListener != null) mListener.onImagePickerSuccess(Uri.fromFile(sourceFile));
                        }
                    }
                } else if (requestCode == ImagePickerUtil.REQUEST_CODE_PICKER && resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        if (mListener != null) mListener.onImagePickerFailure();
                    } else {
                        Uri obtainedUri = data.getData();
                        if (obtainedUri == null) {
                            if (mListener != null) mListener.onImagePickerFailure();
                        } else {
                            // Always try to use the "file://" Uri
                            Uri selectedFile = convertToFileUri(obtainedUri);

                            if (isWithCrop && mCroppingClass != null) {
                                createCroppedImageFile();
                                File destinationFile = new File(mCroppedImagePath);
                                startCropIntent(selectedFile, Uri.fromFile(destinationFile));
                            } else {
                                if (mListener != null) mListener.onImagePickerSuccess(selectedFile);
                            }
                        }
                    }
                }
            }
        });

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(mPickerFragment, "itsmagic.present.imagepicker");
        fragmentTransaction.commit();
    }

    /**
     * Call the image cropper in a GUI-less {@link Fragment}
     * to obtain the result in the {@link Activity#onActivityResult(int, int, Intent)}
     * @param source The source file {@link Uri}
     * @param destination The destination file {@link Uri}
     */
    private void startCropIntent(final Uri source, final Uri destination) {
        assert mCroppingClass != null;
        Intent intent = new Intent(mActivity, mCroppingClass);
        intent.putExtra(ImagePickerUtil.CROPPER_URI_SOURCE, source);
        intent.putExtra(ImagePickerUtil.CROPPER_URI_DEST, destination);
        intent.putExtra(ImagePickerUtil.CROPPER_RATIO_W, mWidthRatio);
        intent.putExtra(ImagePickerUtil.CROPPER_RATIO_H, mHeightRatio);
        intent.putExtra(ImagePickerUtil.CROPPER_MIN_W, mWidthMin);
        intent.putExtra(ImagePickerUtil.CROPPER_MIN_H, mHeightMin);
        intent.putExtra(ImagePickerUtil.CROPPER_MAX_W, mWidthMax);
        intent.putExtra(ImagePickerUtil.CROPPER_MAX_H, mHeightMax);

        // Send the temporary file as part of the bundle if applicable
        if (isTempSaved) mCropperFragment = ImagePickerFragment.newInstance(intent, ImagePickerUtil.REQUEST_CODE_CROP);
        else mCropperFragment = ImagePickerFragment.newInstance(intent, ImagePickerUtil.REQUEST_CODE_CROP, mTempFile);

        mCropperFragment.setOnResultListener(new ImagePickerFragment.OnResultListener() {
            @Override
            public void onResult(int requestCode, int resultCode, Intent data) {
                destroyCropperFragment();
                if (requestCode == ImagePickerUtil.REQUEST_CODE_CROP && resultCode == Activity.RESULT_OK) {
                    if (mListener != null) mListener.onImagePickerSuccess(destination);
                } else {
                    if (mListener != null) mListener.onImagePickerFailure();
                }
            }
        });

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(mCropperFragment, "itsmagic.present.imagecropper");
        fragmentTransaction.commit();
    }

    /**
     * Destroys the picker {@link Fragment}.
     */
    private void destroyPickerFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.remove(mPickerFragment);
        fragmentTransaction.commit();
    }

    /**
     * Destroys the cropper {@link Fragment}.
     */
    private void destroyCropperFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.remove(mCropperFragment);
        fragmentTransaction.commit();
    }

    /**
     * Gets the activity from the constructor.
     * @return The activity
     */
    public final Activity getActivity() {
        return mActivity;
    }

    /**
     * Stub method that's called when the image picker object has been created.
     * <p/>
     * Override this method to use customized features.
     */
    public void onInit() {
        // Stub!
    }

    /** The image picker builder */
    public static class CustomBuilder<T extends CustomBuilder> {

        /** The {@link Activity} */
        private Activity mActivity;

        /** The {@link OnImagePickerListener} object */
        private OnImagePickerListener mListener;

        /** The selected directory */
        private File mSelectedDirectory;

        /**
         * The specified filename for the created image.
         * By default, the filename is renamed into [current time (in millis)].jpg.
         */
        private String mFilename;

        /** The {@link Dialog} object for the image selection popup */
        private Dialog mCustomDialog;

        /** The {@link Dialog} object for the image selection popup (for Camera-less devices) */
        private Dialog mCustomNoCameraDialog;

        /** Determines if app should open the image cropping activity after obtaining the image */
        private boolean isWithCrop = false;

        /** The class of the {@link Activity} that will be called to handle cropping function */
        private Class mCroppingClass = null;

        /** The specified image ratio (width), only used in cropping */
        private int mCropWidthRatio = -1;

        /** The specified image ratio (height), only used in cropping */
        private int mCropHeightRatio = -1;

        /** The specified minimum image dimension (width), only used in cropping */
        private int mCropWidthMin = -1;

        /** The specified minimum image dimension (height), only used in cropping */
        private int mCropHeightMin = -1;

        /** The specified maximum image dimension (width), only used in cropping */
        private int mCropWidthMax = -1;

        /** The specified maximum image dimension (height), only used in cropping */
        private int mCropHeightMax = -1;

        /**
         * Determines if app should keep the temporary image, instead of auto-deleting it.
         * Useful for debugging.
         */
        private boolean isTempSaved = false;

        /** Default constructor */
        public CustomBuilder(Activity activity) {
            mActivity = activity;
        }

        /**
         * Add a listener for the obtained results.
         * @param listener the {@link OnImagePickerListener} object
         * @return The {@link CustomBuilder} object
         */
        @SuppressWarnings("unchecked")
        public T withListener(OnImagePickerListener listener) {
            mListener = listener;
            return (T) this;
        }

        /**
         * Determines the storage directory to save the selected image.
         * @return The {@link CustomBuilder} object
         */
        @SuppressWarnings("unchecked")
        public T withDirectory(File directory) {
            mSelectedDirectory = directory;
            return (T) this;
        }

        /**
         * Set the filename for the cropped image, ".jpg" suffix is added automatically when needed
         * @param filename the image filename
         * @return The {@link CustomBuilder} object
         */
        @SuppressWarnings("unchecked")
        public T withFilename(String filename) {
            String newFilename = filename;
            if (!newFilename.endsWith(".jpg")) newFilename += ".jpg";
            mFilename = newFilename;
            return (T) this;
        }

        /**
         * Sets the custom {@link Dialog} for the image picker.
         * @return The {@link CustomBuilder} object
         */
        @SuppressWarnings("unchecked")
        public T withCustomDialog(Dialog dialog) {
            mCustomDialog = dialog;
            return (T) this;
        }

        /**
         * Sets the custom {@link Dialog} for the image picker (for Camera-less devices).
         * If this isn't set (or is null), no {@link Dialog} will be shown,
         * and app will immediately show the file picker instead.
         * @return The {@link CustomBuilder} object
         */
        @SuppressWarnings("unchecked")
        public T withCustomNoCameraDialog(Dialog dialog) {
            mCustomNoCameraDialog = dialog;
            return (T) this;
        }

        /**
         * Show a cropping intent after specifying the image.
         */
        @SuppressWarnings("unchecked")
        public T withCrop(boolean isWithCrop) {
            this.isWithCrop = isWithCrop;
            return (T) this;
        }

        /**
         * Determines the class of an {@link Activity} to handle cropping function.
         * @param croppingClass the class
         */
        @SuppressWarnings("unchecked")
        public T withCroppingClass(Class croppingClass) {
            mCroppingClass = croppingClass;
            return (T) this;
        }

        /**
         * Set the aspect ratio for the image cropping
         * @param widthRatio width ratio
         * @param heightRatio height ratio
         * @return The {@link CustomBuilder} object
         */
        @SuppressWarnings("unchecked")
        public T withCropAspectRatio(int widthRatio, int heightRatio) {
            mCropWidthRatio = widthRatio;
            mCropHeightRatio = heightRatio;
            return (T) this;
        }

        /**
         * Set the dimension limit for the image cropping
         * @param widthMin width min
         * @param heightMin height min
         * @return The {@link CustomBuilder} object
         */
        @SuppressWarnings("unchecked")
        public T withCropMinDimensions(int widthMin, int heightMin) {
            mCropWidthMin = widthMin;
            mCropHeightMin = heightMin;
            return (T) this;
        }

        /**
         * Set the dimension limit for the image cropping
         * @param widthMax width max
         * @param heightMax height max
         * @return The {@link CustomBuilder} object
         */
        @SuppressWarnings("unchecked")
        public T withCropMaxDimensions(int widthMax, int heightMax) {
            mCropWidthMax = widthMax;
            mCropHeightMax = heightMax;
            return (T) this;
        }

        /**
         * Explicitly saves the temporary images as another file.
         * The temp image will be saved on the same directory as the result file.
         * <p/>
         * Mostly used for debugging.
         * @return The {@link CustomBuilder} object
         */
        @SuppressWarnings("unchecked")
        public T withTempSaved() {
            isTempSaved = true;
            return (T) this;
        }

        /** Obtain the activity */
        private Activity getActivity() {
            return mActivity;
        }

        /** Obtain the {@link OnImagePickerListener} */
        private OnImagePickerListener getListener() {
            return mListener;
        }

        /** Obtain the file directory */
        private File getDirectory() {
            return mSelectedDirectory;
        }

        /** Obtain the filename */
        private String getFilename() {
            return mFilename;
        }

        /** Obtain the custom dialog */
        private Dialog getCustomDialog() {
            return mCustomDialog;
        }

        /** Obtain the custom dialog when device has no camera */
        private Dialog getCustomNoCameraDialog() {
            return mCustomNoCameraDialog;
        }

        /** Obtain the state to determine if crop intent should be launched */
        private boolean isWithCrop() {
            return isWithCrop;
        }

        /** Obtain the cropping class for crop intent */
        private Class getCroppingClass() {
            return mCroppingClass;
        }

        /** Obtain the cropping width ratio */
        private int getCropWidthRatio() {
            return mCropWidthRatio;
        }

        /** Obtain the cropping height ratio */
        private int getCropHeightRatio() {
            return mCropHeightRatio;
        }

        /** Obtain the min cropping width */
        private int getCropWidthMin() {
            return mCropWidthMin;
        }

        /** Obtain the min cropping height */
        private int getCropHeightMin() {
            return mCropHeightMin;
        }

        /** Obtain the max cropping width */
        private int getCropWidthMax() {
            return mCropWidthMax;
        }

        /** Obtain the max cropping height */
        private int getCropHeightMax() {
            return mCropHeightMax;
        }

        /** Obtain the temp file save state */
        private boolean isTempSaved() {
            return isTempSaved;
        }

        /**
         * Build the image picker object.
         * @return the image picker
         */
        public ImagePicker build() {
            return new ImagePicker(this);
        }

        /**
         * Show an {@link AlertDialog} allowing user to pick an image from camera or file.
         */
        public void showPicker() {
            new ImagePicker(this).showPicker();
        }
    }

    public static class Builder extends CustomBuilder<Builder> {

        /**
         * Default constructor
         * @param activity
         */
        public Builder(Activity activity) {
            super(activity);
        }

    }

        /** The listener for the image picker */
    public interface OnImagePickerListener {

        /**
         * Called on {@link Activity#onActivityResult(int, int, Intent)} on successful result.
         * @param resultUri The result {@link Uri}
         */
        void onImagePickerSuccess(Uri resultUri);

        /**
         * Called on {@link Activity#onActivityResult(int, int, Intent)} on failed result.
         */
        void onImagePickerFailure();
    }
}
