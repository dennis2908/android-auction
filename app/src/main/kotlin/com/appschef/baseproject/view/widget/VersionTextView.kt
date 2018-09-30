package com.appschef.baseproject.view.widget

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.TextView

import com.appschef.baseproject.BuildConfig
import com.appschef.baseproject.util.Config

/**
 * Created by Alvin Rusli on 02/05/16.
 *
 * A [TextView] that shows current app version.
 *
 * Debug builds will show more detailed version, but release build will only show the version name.
 */
class VersionTextView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : AppCompatTextView(context, attrs, defStyleAttr) {

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    /** The app's version as a simple readable String */
    private var appVersion = ""

    init {
        initAppVersion()
        if (BuildConfig.DEBUG) appVersion += " - DEBUG"
        super.setText(appVersion)
    }

    /** Obtain the application version as a String */
    private fun initAppVersion() {
        if (isInEditMode) {
            appVersion = "App Version"
            return
        }

        appVersion = if (!Config.versionName.toLowerCase().startsWith("v")) "" else "v"
        if (BuildConfig.DEBUG) {
            appVersion += Config.versionName + ", build " +
                    Config.versionCode + ", flavor " +
                    Config.variant + "\n" +
                    Config.getSimpleBuildTime()
        } else {
            appVersion += Config.versionName
        }
    }
}
