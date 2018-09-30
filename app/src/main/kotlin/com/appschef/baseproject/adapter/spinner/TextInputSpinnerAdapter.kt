package com.appschef.baseproject.adapter.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appschef.baseproject.R
import itsmagic.present.easierspinner.adapter.EasierSpinnerAdapterCore
import itsmagic.present.easierspinner.adapter.EasierSpinnerStringAdapter
import itsmagic.present.easierspinner.adapter.EasierSpinnerViewHolder
import kotlinx.android.synthetic.main.adapter_spinner_dropdown.view.*
import kotlinx.android.synthetic.main.adapter_spinner_textinput.view.*

class TextInputSpinnerAdapter<DATA_TYPE>(context: Context) : EasierSpinnerAdapterCore<DATA_TYPE, TextInputSpinnerAdapter<DATA_TYPE>.ViewHolder, TextInputSpinnerAdapter<DATA_TYPE>.ViewHolder, TextInputSpinnerAdapter<DATA_TYPE>.DropDownViewHolder>(context) {

    /** The hint text  */
    var textInputHintText: String? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /** The placeholder text  */
    var textInputPlaceholderText: String? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /** The error text  */
    var textInputErrorText: String? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreatePlaceholderViewHolder(parent: ViewGroup): TextInputSpinnerAdapter<DATA_TYPE>.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_spinner_textinput, parent, false)
        return ViewHolder(view)
    }

    override fun onCreateSelectionViewHolder(parent: ViewGroup): TextInputSpinnerAdapter<DATA_TYPE>.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_spinner_textinput, parent, false)
        return ViewHolder(view)
    }

    override fun onCreateDropdownViewHolder(parent: ViewGroup): TextInputSpinnerAdapter<DATA_TYPE>.DropDownViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_spinner_dropdown, parent, false)
        return DropDownViewHolder(view)
    }

    override fun onBindPlaceholderViewHolder(holder: TextInputSpinnerAdapter<DATA_TYPE>.ViewHolder, placeholder: DATA_TYPE) {
        holder.bindView(placeholder, true)
    }

    override fun onBindSelectionViewHolder(holder: TextInputSpinnerAdapter<DATA_TYPE>.ViewHolder, position: Int, data: DATA_TYPE?) {
        holder.bindView(data, false)
    }

    override fun onBindDropdownViewHolder(holder: TextInputSpinnerAdapter<DATA_TYPE>.DropDownViewHolder, position: Int, data: DATA_TYPE?, isPlaceholder: Boolean) {
        holder.bindView(data)
    }

    inner class ViewHolder(itemView: View) : EasierSpinnerViewHolder(itemView) {

        init {
            itemView.txt_input.setOnClickListener { easierSpinner.showSelection() }
        }

        fun bindView(data: DATA_TYPE?, isPlaceholder: Boolean) {
            itemView.txt_input_layout.hint = textInputHintText

            if (textInputErrorText.isNullOrEmpty()) {
                itemView.txt_input_layout.isErrorEnabled = false
            } else {
                itemView.txt_input_layout.error = textInputErrorText
            }

            if (isPlaceholder) {
                if (textInputPlaceholderText.isNullOrEmpty()) {
                    itemView.txt_input.setText(null)
                } else {
                    itemView.txt_input.setText(textInputPlaceholderText)
                }
            } else {
                if (data != null) {
                    when (data) {
                        is EasierSpinnerStringAdapter.SpinnerText -> itemView.txt_input.setText(data.spinnerText)
                        is CharSequence -> itemView.txt_input.setText(data)
                        else -> itemView.txt_input.setText(data.toString())
                    }
                } else {
                    itemView.txt_input.text = null
                }
            }
        }
    }

    inner class DropDownViewHolder(itemView: View) : EasierSpinnerViewHolder(itemView) {

        fun bindView(data: DATA_TYPE?) {
            if (data != null) {
                when (data) {
                    is EasierSpinnerStringAdapter.SpinnerText -> itemView.txt_dropdown.text = data.spinnerText
                    is CharSequence -> itemView.txt_dropdown.text = data
                    else -> itemView.txt_dropdown.text = data.toString()
                }
            } else {
                itemView.txt_dropdown.text = null
            }
        }
    }
}