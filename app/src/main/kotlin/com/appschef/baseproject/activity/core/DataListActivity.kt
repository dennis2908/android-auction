package com.appschef.baseproject.activity.core

import android.os.Bundle
import android.support.annotation.CallSuper
import android.widget.FrameLayout
import com.appschef.baseproject.R
import com.appschef.baseproject.fragment.core.DataListFragment
import kotlinx.android.synthetic.main.base_activity_data_list.*

/**
 * Created by Alvin Rusli on 1/25/2016.
 *
 * The data list activity.
 * This activity contains a [DataListFragment] to easily display a list.
 */
abstract class DataListActivity : CoreActivity() {

    /** @return the view res for simple layout for data list */
    override val viewRes: Int = R.layout.base_activity_data_list

    /** @return the [DataListFragment] to be shown in the activity */
    abstract val dataFragment: DataListFragment

    /**
     * Obtain the [FrameLayout] that's going to be used to be replaced with the fragment.
     *
     * Override this method to return your customized activity's [FrameLayout],
     * this activity will automatically use your specified view to display the fragment.
     */
    open var fragmentLayout: FrameLayout? = null
        get() {
            if (viewRes == R.layout.base_activity_data_list) {
                return layout_fragment
            } else {
                throw NullPointerException("No FrameLayout found, " +
                        "make sure a FrameLayout exists, override val fragmentLayout, " +
                        "and return a valid FrameLayout!")
            }
        }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment()
    }

    /** Initialize the fragment  */
    private fun initFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(fragmentLayout?.id!!, dataFragment)
        fragmentTransaction.commit()
    }
}
