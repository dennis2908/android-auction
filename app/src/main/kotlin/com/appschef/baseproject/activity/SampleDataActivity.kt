package com.appschef.baseproject.activity

import android.content.Context
import android.content.Intent
import android.widget.FrameLayout
import com.appschef.baseproject.R
import com.appschef.baseproject.activity.core.DataListActivity
import com.appschef.baseproject.fragment.SampleDataFragment
import kotlinx.android.synthetic.main.activity_sample_data.*

/**
 * Created by Alvin Rusli on 1/25/2017.
 *
 * A sample data list activity.
 */
class SampleDataActivity : DataListActivity() {

    override val viewRes = R.layout.activity_sample_data
    override val dataFragment = SampleDataFragment()
    override var fragmentLayout: FrameLayout? = null
        get() = layout_fragment

    companion object {

        /** Launch this activity  */
        fun launchIntent(context: Context) {
            val intent = Intent(context, SampleDataActivity::class.java)
            context.startActivity(intent)
        }
    }
}
