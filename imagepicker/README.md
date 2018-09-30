# ImagePicker
A library made to simplify the image picker dialog.

This library also handles the problem with Android N's [FileUriExposedException](https://developer.android.com/reference/android/os/FileUriExposedException.html). 

# Important information
* This library does **NOT** handle any kind of image cropping function, this library only handles starting and obtaining the result image URI.
* This library also adds a provider tag in manifest with a `${applicationId}.imagepicker.provider` authority.

# Usage
* Simply create a new `ImagePicker` object, and obtain the result.

        new ImagePicker.Builder(this)
                .withListener(new ImagePicker.OnImagePickerListener() {
                    @Override
                    public void onImagePickerSuccess(Uri resultUri) {
                        // Do something with the obtained Uri
                        mImageView.setImageURI(mImageUri);
                    }

                    @Override
                    public void onImagePickerFailure() {
                        // Do something else
                    }
                })
                .showPicker();

* Some options may be set either via the builder or on the `ImagePicker` object itself (e.g. file directory, filename, etc.) 
  * By default, the library will save the image saved from the Camera intent to the default external storage path for the file directory, and uses the current time (in millis) as its filename. 

# Customization
* By default, the `ImagePicker` uses an `AlertDialog` for the image selection dialog. This can be customized by supplying your own `Dialog` object.
  * To call the camera selection, call `#onCameraSelection()`
  * To call the gallery selection, call `#onPickerSelection()`
* For an even complex customization, sample can be seen on the `itsmagic.present.imagepicker.sample.SampleCustomImagePicker` class.
  * When creating your own custom ImagePicker, even the builder can be customized (see sample).
  * The demo app have an even more complex customization that checks for permission on Android M and above, and an additional button on the dialog when an image is already set.
* To use the picker and immediately call a cropping class, call `#withCrop(true)` before `#showPicker()`.
  * To pass specified aspect ratio and max dimensions, call `#withCropAspectRatio(w, h)` and/or `#withCropMaxDimensions(w, h)`  before `#showPicker()`. This adds the specified values as Bundle to your image cropping Activity.
  * When no cropping class is found to handle the cropping intent, the original image will be returned on the success callback instead.

# Bundle keys
* `CROPPER_URI_SOURCE` - The image source as URI (Parcelable)
* `CROPPER_URI_DEST` - The image destination as URI (Parcelable), when the cropping intent returns a `RESULT_OK`, this will be the Uri specified on the `#onImagePickerSuccess()` callback.
* `CROPPER_RATIO_W` - Cropping width aspect ratio (int)
* `CROPPER_RATIO_H` - Cropping height aspect ratio (int)
* `CROPPER_MIN_W` - Cropping width min dimension (int)
* `CROPPER_MIN_H` - Cropping height min dimension (int)
* `CROPPER_MAX_W` - Cropping width max dimension (int)
* `CROPPER_MAX_H` - Cropping height max dimension (int)

# Method information
* `onImagePickerSuccess(Uri)` - Called when image has been obtained successfully (with or without cropping).
* `onImagePickerFailure()` - Called when image can't be obtained.

# Additional Information
* This library is using several dependencies:

        'com.android.support:support-core-utils:27.0.1'

* To avoid build conflicts, exclude the support library if needed:

        implementation (project(':imagepicker')) {
            exclude group: 'com.android.support'
        }
