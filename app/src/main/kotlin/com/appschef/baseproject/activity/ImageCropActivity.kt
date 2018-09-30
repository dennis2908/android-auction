package com.appschef.baseproject.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.appschef.baseproject.R
import com.appschef.baseproject.activity.core.CoreActivity
import com.appschef.baseproject.util.Common
import itsmagic.present.imagepicker.util.ImagePickerUtil
import kotlinx.android.synthetic.main.activity_imagecrop.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Alvin Rusli on 12/23/2016.
 *
 * Image cropping activity.
 */
class ImageCropActivity : CoreActivity() {

    // TODO: Change layout if needed
    override val viewRes = R.layout.activity_imagecrop

    private var btnRotate: MenuItem? = null
    private var btnSave: MenuItem? = null

    private lateinit var sourceImage: Uri
    private lateinit var destinationImage: Uri
    private var aspectWidth = -1
    private var aspectHeight = -1
    private var minWidth = -1
    private var minHeight = -1
    private var maxWidth = -1
    private var maxHeight = -1

    private var isMinValid = false
    private var isMaxValid = false

    private var isRotateButtonVisible = false
    private var isSaveButtonVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        initExtras()
        beginEditing()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_image_crop, menu)
        btnRotate = menu.findItem(R.id.action_crop_rotate)
        btnSave = menu.findItem(R.id.action_crop_done)
        if (isRotateButtonVisible) btnRotate!!.isVisible = true
        if (isSaveButtonVisible) btnSave!!.isVisible = true

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_crop_rotate -> {
                img_cropper.rotateImage(90)
                return true
            }
            R.id.action_crop_done -> {
                finishEditing()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initExtras() {
        try {
            val extras = intent.extras
            sourceImage = extras!!.getParcelable(ImagePickerUtil.CROPPER_URI_SOURCE)
            destinationImage = extras.getParcelable(ImagePickerUtil.CROPPER_URI_DEST)
            aspectWidth = extras.getInt(ImagePickerUtil.CROPPER_RATIO_W)
            aspectHeight = extras.getInt(ImagePickerUtil.CROPPER_RATIO_H)
            minWidth = extras.getInt(ImagePickerUtil.CROPPER_MIN_W)
            minHeight = extras.getInt(ImagePickerUtil.CROPPER_MIN_H)
            maxWidth = extras.getInt(ImagePickerUtil.CROPPER_MAX_W)
            maxHeight = extras.getInt(ImagePickerUtil.CROPPER_MAX_H)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        isMinValid = minWidth != -1 && minHeight != -1
        isMaxValid = maxWidth != -1 && maxHeight != -1

        if (isMinValid && isMaxValid) {
            if (minWidth > maxWidth) throw IllegalArgumentException("Minimum width is larger than maximum width!")
            else if (minHeight > maxHeight) throw IllegalArgumentException("Minimum height is larger than maximum height!")
        }
    }

    /** Begin the editing process of the image  */
    private fun beginEditing() {
        @Suppress("SENSELESS_COMPARISON")
        if (sourceImage == null || destinationImage == null) throw NullPointerException("Source / destination image is null!")
        initCropper()
    }

    /**
     * Initialize the image cropper.
     * Also sets the aspect ratio if it's specified
     */
    private fun initCropper() {
        img_cropper.setOnSetImageUriCompleteListener { _, _, _ ->
            isRotateButtonVisible = true
            isSaveButtonVisible = true
            btnRotate?.isVisible = true
            btnSave?.isVisible = true
        }

        // Set the source image to be cropped
        img_cropper.setImageUriAsync(sourceImage)

        // Set other stuff
        // TODO: Change background color if needed
        img_cropper.setBackgroundResource(android.R.color.darker_gray)

        // Set the aspect ratio if applicable
        if (aspectWidth != -1 && aspectHeight != -1) {
            img_cropper.setAspectRatio(aspectWidth, aspectHeight)
            img_cropper.setFixedAspectRatio(true)
        }
    }

    /** Finish the editing process of the image  */
    private fun finishEditing() {
        try {
            saveImageToStorage(img_cropper.croppedImage)
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        } catch (e: IOException) {
            Common.printStackTrace(e)

            // TODO: Change error warning
            Toast.makeText(this, "Failed to finish editing: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Save an image to storage
     * @param image The image to save
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun saveImageToStorage(image: Bitmap) {
        var newImage = image
        var isEdited = false

        // Resize the image if applicable
        if (isMinValid || isMaxValid) {
            var width = image.width
            var height = image.height

            if (!isEdited && isMinValid) {
                if (width < minWidth || height < minHeight) {
                    when {
                        width > height -> {
                            // landscape
                            val ratio = width.toFloat() / minWidth
                            width = minWidth
                            height = (height / ratio).toInt()
                        }
                        height > width -> {
                            // portrait
                            val ratio = height.toFloat() / minHeight
                            width = (width / ratio).toInt()
                            height = minHeight
                        }
                        else -> {
                            // square
                            width = minWidth
                            height = minHeight
                        }
                    }

                    newImage = Bitmap.createScaledBitmap(image, width, height, false)
                    isEdited = true
                }
            }

            if (!isEdited && isMaxValid) {
                if (width > maxWidth || height > maxHeight) {
                    when {
                        width > height -> {
                            // landscape
                            val ratio = width.toFloat() / maxWidth
                            width = maxWidth
                            height = (height / ratio).toInt()
                        }
                        height > width -> {
                            // portrait
                            val ratio = height.toFloat() / maxHeight
                            width = (width / ratio).toInt()
                            height = maxHeight
                        }
                        else -> {
                            // square
                            width = maxWidth
                            height = maxHeight
                        }
                    }

                    newImage = Bitmap.createScaledBitmap(image, width, height, false)
                    isEdited = true
                }
            }
        }

        val file = File(destinationImage.path)
        val out = FileOutputStream(file)

        // TODO: Change the image compression
        newImage.compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.flush()
        out.close()
    }
}
