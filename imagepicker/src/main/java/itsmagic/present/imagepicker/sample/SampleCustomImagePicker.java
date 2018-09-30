package itsmagic.present.imagepicker.sample;

import android.app.Activity;
import android.app.Dialog;
import android.os.Environment;

import java.io.File;

import itsmagic.present.imagepicker.ImagePicker;

/**
 * Created by Alvin Rusli on 6/30/2016.
 * <p/>
 * Sample of customized image picker class.
 */
public class SampleCustomImagePicker extends ImagePicker {

    /**
     * The constructor of this class.
     * @param activity The activity
     */
    public SampleCustomImagePicker(Activity activity) {
        super(activity);
    }

    /**
     * The constructor of this class.
     * @param builder The builder object
     */
    public SampleCustomImagePicker(Builder builder) {
        super(builder);
    }

    @Override
    public void onInit() {
        setDirectory(initCustomDirectory());
        setCroppingClass(initCroppingClass());
        setCustomDialog(initCustomDialog());
        setCustomNoCameraDialog(initCustomNoCameraDialog());
    }

    /** Initialize a custom directory for this image picker */
    private File initCustomDirectory() {
        return new File(Environment.getDataDirectory() + "imagepicker");
    }

    /** Initialize a custom cropping class for this image picker */
    private Class initCroppingClass() {
        // This library does not handle cropping.
        // Return your custom made class for cropping here.
        return null;
    }

    /** Initialize a custom dialog for this image picker */
    private Dialog initCustomDialog() {
        // If you need a custom dialog for the image picker,
        // Simply return a Dialog object, and specify the actions to be called.
        //   To call the camera intent: use #onCameraSelection()
        //   To call the image picker intent: use #onPickerSelection()
        return null;
    }

    /** Initialize a custom dialog for this image picker for devices without camera */
    private Dialog initCustomNoCameraDialog() {
        // If you need a custom dialog for the image picker (for Camera-less devices),
        // Simply return a Dialog object, and specify the actions to be called.
        //   To call the image picker intent: use #onPickerSelection()
        return null;
    }

    /** The custom image picker builder */
    public static class Builder extends ImagePicker.CustomBuilder<Builder> {

        /**
         * Default constructor
         * @param activity
         */
        public Builder(Activity activity) {
            super(activity);
        }

        @Override
        public SampleCustomImagePicker build() {
            return new SampleCustomImagePicker(this);
        }

        @Override
        public void showPicker() {
            new SampleCustomImagePicker(this).showPicker();
        }
    }
}
