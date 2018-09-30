package com.appschef.baseproject.fragment

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.appschef.baseproject.R
import com.appschef.baseproject.adapter.recycler.SampleProductAdapter
import com.appschef.baseproject.adapter.recycler.core.DataListRecyclerViewAdapter
import com.appschef.baseproject.fragment.core.DataListFragment
import com.appschef.baseproject.model.core.AppError
import com.appschef.baseproject.model.core.Resource
import com.appschef.baseproject.model.remote.product.SampleProduct
import com.appschef.baseproject.presenter.SampleDataCallback
import com.appschef.baseproject.presenter.SampleDataPresenter
import kotlinx.android.synthetic.main.fragment_data_list_sample.*

/**
 * Created by Alvin Rusli on 1/25/2017.
 *
 * A sample data list fragment.
 */
class SampleDataFragment : DataListFragment(), SampleDataCallback {

    private var presenter: SampleDataPresenter? = null

    override val viewRes: Int? = R.layout.fragment_data_list_sample

    override fun initAppBarLayout(): AppBarLayout? {
        return appbar_data_list
    }

    override fun initSwipeRefreshLayout(): SwipeRefreshLayout? {
        return swipe_refresh_data_list
    }

    override fun initRecyclerView(): RecyclerView {
        return recycler_data_list
    }

    @Suppress("UNCHECKED_CAST")
    override fun initRecyclerAdapter(): DataListRecyclerViewAdapter<Any, RecyclerView.ViewHolder> {
        val adapter = SampleProductAdapter()
        adapter.emptyText = resources.getString(R.string.info_no_data)
        return adapter as DataListRecyclerViewAdapter<Any, RecyclerView.ViewHolder>
    }

    override fun initRecyclerLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SampleDataPresenter(this)
        lifecycle.addObserver(presenter!!)

        enableSwipeToRefresh()
        enableInfiniteScrolling()
    }

    override fun fetchData() {
        presenter?.fetchData()
    }

    override fun onSwipeToRefresh() {
        presenter?.onRefresh()
        enableInfiniteScrolling()
        fetchData()
    }

    override fun onLoadSuccess(isPaginatedLoad: Boolean) {
        if (presenter!!.isLoadFinished) disableInfiniteScrolling()
    }

    override fun onSampleDataLoading() {
        updateResource(Resource.loading())
    }

    override fun onSampleDataSuccess(dataList: List<SampleProduct>) {
        updateResource(Resource.success(dataList))
    }

    override fun onSampleDataFailure(error: AppError) {
        updateResource(Resource.error(error))
    }
}
