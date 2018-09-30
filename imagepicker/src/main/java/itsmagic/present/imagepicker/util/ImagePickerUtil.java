package itsmagic.present.imagepicker.util;

/**
 * Created by Alvin Rusli on 5/12/2016.
 * <p/>
 * The utility class for the image picker library.
 */
public class ImagePickerUtil {

    /** The request code for cropping activity */
    public static final int REQUEST_CODE_CROP = 3587;

    /** The request code for camera activity */
    public static final int REQUEST_CODE_CAMERA = 3588;

    /** The request code for image picker activity */
    public static final int REQUEST_CODE_PICKER = 3589;

    /** The cropping activity image source (as an {@link android.net.Uri} */
    public static final String CROPPER_URI_SOURCE = "crop_image_source";

    /** The cropping activity to-be-cropped image result (as an {@link android.net.Uri} */
    public static final String CROPPER_URI_DEST = "crop_image_dest";

    /** The cropping activity image ratio (width) */
    public static final String CROPPER_RATIO_W = "crop_ratio_w";

    /** The cropping activity image ratio (height) */
    public static final String CROPPER_RATIO_H = "crop_ratio_h";

    /** The cropping activity minimum image dimension (width) */
    public static final String CROPPER_MIN_W = "crop_min_w";

    /** The cropping activity minimum image dimension (height) */
    public static final String CROPPER_MIN_H = "crop_min_h";

    /** The cropping activity maximum image dimension (width) */
    public static final String CROPPER_MAX_W = "crop_max_w";

    /** The cropping activity maximum image dimension (height) */
    public static final String CROPPER_MAX_H = "crop_max_h";

}
