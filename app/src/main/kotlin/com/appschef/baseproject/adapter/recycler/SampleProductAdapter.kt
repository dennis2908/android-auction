package com.appschef.baseproject.adapter.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appschef.baseproject.R
import com.appschef.baseproject.adapter.recycler.core.DataListRecyclerViewAdapter
import com.appschef.baseproject.model.remote.product.SampleProduct
import kotlinx.android.synthetic.main.adapter_recycler_sample.view.*

/**
 * Created by Alvin Rusli on 06/09/2017.
 *
 * Sample adapter for sample product.
 */
class SampleProductAdapter : DataListRecyclerViewAdapter<SampleProduct, SampleProductAdapter.ViewHolder>() {

    override fun onCreateDefaultViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.adapter_recycler_sample, parent, false))
    }

    override fun onBindDefaultViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView() {
            val data = getDataList()[adapterPosition]
            itemView.txt_sample.text = "[${adapterPosition + 1}] - ${data.name}"
        }
    }
}
