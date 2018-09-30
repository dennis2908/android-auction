package com.appschef.baseproject.view.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet

import com.appschef.baseproject.adapter.spinner.TextInputSpinnerAdapter

import java.util.ArrayList
import java.util.Random

import itsmagic.present.easierspinner.EasierSpinnerCore

/**
 * Created by Alvin Rusli on 9/22/2016.
 */
class SampleTextInputSpinner : EasierSpinnerCore<String> {

    private lateinit var adapter: TextInputSpinnerAdapter<String>

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setTextInputHintText(hint: String) {
        adapter.textInputHintText = hint
    }

    fun setTextInputPlaceholderText(placeholder: String) {
        adapter.textInputPlaceholderText = placeholder
    }

    fun setTextInputErrorText(error: String) {
        adapter.textInputErrorText = error
    }

    override fun onPrepare() {
        removeSpinnerBackground()
        initAdapter()
        initSpinner()
    }

    private fun initAdapter() {
        adapter = TextInputSpinnerAdapter(context)
        setAdapter(adapter)
    }

    private fun initSpinner() {
        // Generate the adapter data
        val simpleData = ArrayList<String>()
        var data: String
        val rnd = Random()
        var color: Int
        for (i in 0..9) {
            color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            data = String.format("#%06X", 0xFFFFFF and color)
            simpleData.add(data)
        }
        setData(simpleData)

        // Generate the placeholder data
        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        setPlaceholder(String.format("#%06X", 0xFFFFFF and color))
        showPlaceholder()
    }
}
