package com.appschef.baseproject.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.appschef.baseproject.BuildConfig
import com.appschef.baseproject.R
import com.appschef.baseproject.activity.core.CoreActivity
import com.appschef.baseproject.util.Common
import com.appschef.baseproject.util.MyImagePicker
import itsmagic.present.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_sample_selection.*

/**
 * Created by Alvin Rusli on 06/07/2017.
 *
 * Sample activity to open another sample activities.
 */
class SampleSelectionActivity : CoreActivity() {

    override val viewRes = R.layout.activity_sample_selection

    // TODO: Remove these useless vars
    var selectedUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        txt_sample.text = BuildConfig.M_SAMPLE_STRING
        txt_sample_api.text = getString(R.string.flavor_api_sample)
        txt_sample_mode.text = getString(R.string.flavor_mode_sample)

        btn_datalist.setOnClickListener { SampleDataActivity.launchIntent(this) }
        btn_authentication.setOnClickListener { SampleAuthActivity.launchIntent(this) }
        btn_image_picker.setOnClickListener({
            MyImagePicker.Builder(this)
                    .withCrop(true)
                    .withImageRemoveListener(
                            if (selectedUri == null) null
                            else object : MyImagePicker.OnImageRemoveListener {
                                override fun onImageRemoved() {
                                    selectedUri = null
                                    Common.showToast("Removed previously selected image")
                                }
                            })
                    .withListener(object: ImagePicker.OnImagePickerListener {
                        override fun onImagePickerSuccess(resultUri: Uri?) {
                            selectedUri = resultUri
                            Common.showToast("Uri = $selectedUri")
                        }

                        override fun onImagePickerFailure() {
                            Common.showToast("Failed to pick image")
                        }
                    })
                    .showPicker()
        })
        btn_settings_account.setOnClickListener({ startActivity(Intent(Settings.ACTION_SYNC_SETTINGS)) })

        spinner_sample.setTextInputHintText("HINT")
        spinner_sample.setTextInputPlaceholderText("PLACEHOLDER")
        spinner_sample.setTextInputErrorText("ERROR")
    }

    companion object {

        /**
         * Launch this activity.
         * @param context the context
         */
        fun launchIntent(context: Context) {
            val intent = Intent(context, SampleSelectionActivity::class.java)
            context.startActivity(intent)
        }
    }
}
