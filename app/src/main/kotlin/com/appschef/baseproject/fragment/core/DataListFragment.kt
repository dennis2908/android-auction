package com.appschef.baseproject.fragment.core

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.appschef.baseproject.R
import com.appschef.baseproject.adapter.recycler.core.DataListRecyclerViewAdapter
import com.appschef.baseproject.model.core.AppError
import com.appschef.baseproject.model.core.Resource
import kotlinx.android.synthetic.main.base_fragment_data_list.*

/**
 * Created by Alvin Rusli on 06/09/2017.
 *
 * A fragment that can easily display data in a [RecyclerView].
 * Also capable of handling infinite scrolling behaviour,
 * and calculates app bar offset to determine swipe-to-refresh behaviour.
 */
abstract class DataListFragment : CoreFragment(), AppBarLayout.OnOffsetChangedListener {

    override val viewRes: Int? = R.layout.base_fragment_data_list

    /** The adapter for the [RecyclerView] */
    private lateinit var adapterImpl: DataListRecyclerViewAdapter<Any, RecyclerView.ViewHolder>

    /** The [RecyclerView] */
    private lateinit var recyclerViewImpl: RecyclerView

    /** The [RecyclerView.LayoutManager] for the [RecyclerView] */
    private lateinit var layoutManagerImpl: RecyclerView.LayoutManager

    /** The [AppBarLayout] */
    private var appBarLayoutImpl: AppBarLayout? = null

    /** The [SwipeRefreshLayout] */
    private var swipeRefreshLayoutImpl: SwipeRefreshLayout? = null

    /** Swipe-to-refresh behaviour */
    private var swipeToRefresh = false

    /** Infinite scrolling behaviour */
    private var infiniteScrolling = false

    /** Determines if this [DataListFragment] is currently doing a loading process */
    private var isLoading = false

    /** The value of this [DataListFragment] app bar vertical offset */
    private var appBarVerticalOffset = -1

    /** Pagination variables */
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var pastVisibleItems: Int = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAbstracts()
        initView()
        initData()
    }

    /** Sets the data from some abstract methods  */
    private fun initAbstracts() {
        adapterImpl = initRecyclerAdapter()
        recyclerViewImpl = initRecyclerView()
        layoutManagerImpl = initRecyclerLayoutManager()
        appBarLayoutImpl = initAppBarLayout()
        swipeRefreshLayoutImpl = initSwipeRefreshLayout()

        initGridLayoutManagerSpan()
    }

    /** Initializes the view for this fragment  */
    private fun initView() {
        initAppBarLayoutImpl()
        initSwipeRefreshLayoutImpl()
        initRecyclerViewImpl()
    }

    /** Initializes some app bar layout behaviour  */
    private fun initAppBarLayoutImpl() {
        appBarLayoutImpl?.addOnOffsetChangedListener(this)
    }

    /** Initialize some customization for the swipe refresh layout  */
    private fun initSwipeRefreshLayoutImpl() {
        if (swipeRefreshLayoutImpl != null) {
            swipeRefreshLayoutImpl!!.setColorSchemeResources(R.color.colorAccent)
            swipeRefreshLayoutImpl!!.isEnabled = false
            swipeRefreshLayoutImpl!!.setOnRefreshListener {
                adapterImpl.setDataList(null)
                adapterImpl.notifyDataSetChanged()
                onSwipeToRefresh()
            }
        }
    }

    /**
     * Initializes the recycler view, its adapter, and its layout manager.
     * Also adds a scroll listener to handle infinite scrolling behaviour.
     */
    private fun initRecyclerViewImpl() {
        adapterImpl.retryListener = object : DataListRecyclerViewAdapter.OnRetryListener {
            override fun onErrorRetryClicked() {
                this@DataListFragment.onErrorRetryClicked()
            }

            override fun onPaginationErrorRetryClicked() {
                this@DataListFragment.onPaginationErrorRetryClicked()
            }
        }

        recyclerViewImpl.layoutManager = LinearLayoutManager(context)
        recyclerViewImpl.adapter = adapterImpl

        // Add a scroll listener
        recyclerViewImpl.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> onOffsetChanged(appBarLayoutImpl, appBarVerticalOffset)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading) {
                    if (infiniteScrolling && adapterImpl.getContentViewType() == DataListRecyclerViewAdapter.NULL) {
                        if (dy > 0) {
                            visibleItemCount = layoutManagerImpl.childCount
                            totalItemCount = layoutManagerImpl.itemCount

                            pastVisibleItems = when (layoutManagerImpl) {
                                is LinearLayoutManager -> (layoutManagerImpl as LinearLayoutManager).findFirstVisibleItemPosition()
                                is GridLayoutManager -> (layoutManagerImpl as GridLayoutManager).findFirstVisibleItemPosition()
                                else -> 0 // TODO: 1/25/2017 Add additional support for other layout managers
                            }

                            // Start fetching new data when 2nd to last item is already visible
                            if (visibleItemCount + pastVisibleItems >= totalItemCount - 2) {
                                fetchData()
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (swipeRefreshLayoutImpl == null || !swipeToRefresh) return
        appBarVerticalOffset = verticalOffset

        if (!isLoading) {
            if (appBarLayout != null) {
                swipeRefreshLayoutImpl!!.isEnabled = (verticalOffset == 0 && recyclerViewImpl.computeVerticalScrollOffset() == 0)
            } else {
                swipeRefreshLayoutImpl!!.isEnabled = true
            }
        }
    }

    /** Initializes data load */
    private fun initData() {
        if (adapterImpl.getDataList().isEmpty()) fetchData()
        else onLoadSuccessImpl()
    }

    /**
     * Obtain the [RecyclerView] adapter.
     * @return the adapter
     */
    fun getAdapter(): DataListRecyclerViewAdapter<Any, RecyclerView.ViewHolder> {
        return adapterImpl
    }

    /**
     * Obtain the [RecyclerView] view.
     * @return the [RecyclerView]
     */
    fun getRecyclerView(): RecyclerView {
        return recyclerViewImpl
    }

    /**
     * Obtain the [RecyclerView] layout manager.
     * @return the layout manager
     */
    fun getLayoutManager(): RecyclerView.LayoutManager {
        return layoutManagerImpl
    }

    /**
     * Obtain the [AppBarLayout] view.
     * @return the [AppBarLayout]
     */
    fun getAppBarLayout(): AppBarLayout? {
        return appBarLayoutImpl
    }

    /**
     * Obtain the [SwipeRefreshLayout] view.
     * @return the [SwipeRefreshLayout]
     */
    fun getSwipeRefreshLayout(): SwipeRefreshLayout? {
        return swipeRefreshLayoutImpl
    }

    /** Enables swipe-to-refresh layout behaviour */
    fun enableSwipeToRefresh() {
        swipeToRefresh = true
        if (!isLoading) {
            if (appBarLayoutImpl != null) {
                swipeRefreshLayoutImpl?.isEnabled = (appBarVerticalOffset == 0 && recyclerViewImpl.computeVerticalScrollOffset() == 0)
            } else {
                swipeRefreshLayoutImpl?.isEnabled = true
            }
        }
    }

    /** Disables swipe-to-refresh layout behaviour */
    fun disableSwipeToRefresh() {
        swipeToRefresh = false
        swipeRefreshLayoutImpl?.isEnabled = true
    }

    /** Enables infinite scrolling layout behaviour */
    fun enableInfiniteScrolling() {
        infiniteScrolling = true
    }

    /** Disables infinite scrolling layout behaviour */
    fun disableInfiniteScrolling() {
        infiniteScrolling = false
    }

    /**
     * Updates the resource.
     * @param resource the [Resource] object
     */
    fun updateResource(resource: Resource<Collection<Any>?>?) {
        if (resource == null) return
        when (resource.status) {
            Resource.LOADING -> onLoading()
            Resource.SUCCESS -> onSuccess(resource.data)
            Resource.ERROR -> onError(resource.error)
        }
    }

    /**
     * Updates the layout manager.
     * @param newLayoutManager the new layout manager
     */
    fun updateLayoutManager(newLayoutManager: RecyclerView.LayoutManager) {
        layoutManagerImpl = newLayoutManager
        initGridLayoutManagerSpan()
        if (adapterImpl.getDataList().size > 0) {
            if (recyclerViewImpl.layoutManager !== layoutManagerImpl) {
                recyclerViewImpl.layoutManager = layoutManagerImpl
            }
        }
    }

    /** Initializes the grid layout manager span size */
    private fun initGridLayoutManagerSpan() {
        if (layoutManagerImpl is GridLayoutManager) {
            updateGridLayoutManagerSpan(layoutManagerImpl as GridLayoutManager)
        }
    }

    /**
     * Updates the [GridLayoutManager] span size.
     * The default implementation will automatically resize
     * the pagination view holder to span all columns.
     * Override this method to use your own implementation.
     * @param layoutManager the [GridLayoutManager]
     */
    open fun updateGridLayoutManagerSpan(layoutManager: GridLayoutManager) {
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position >= adapterImpl.getDataList().size) {
                    return layoutManager.spanCount
                }

                return 1
            }
        }
    }

    /** Updates the adapter to display loading progress */
    private fun onLoading() {
        isLoading = true
        val isChanged = adapterImpl.setContentViewType(DataListRecyclerViewAdapter.PROGRESS)
        if (isChanged) recyclerViewImpl.post( { adapterImpl.notifyItemChanged(adapterImpl.getDataList().size)})
        if (swipeRefreshLayoutImpl != null) {
            swipeRefreshLayoutImpl!!.isEnabled = false
            swipeRefreshLayoutImpl!!.isRefreshing = false
        }

        onLoadStart(adapterImpl.getDataList().size > 0)
    }

    /**
     * Called when a loading process is starting.
     * Override this method if further customization is required.
     */
    open fun onLoadStart(isPaginatedLoad : Boolean) {
        // Stub!
    }

    /**
     * Hides the loading progress, and updates the new collection to the adapter list.
     * @param collection the collection
     */
    private fun onSuccess(collection: Collection<Any>?) {
        if (collection != null) addCollection(collection)
        val isChanged: Boolean = if (adapterImpl.getDataList().size == 0) {
            adapterImpl.setContentViewType(DataListRecyclerViewAdapter.EMPTY)
        } else {
            updateLayoutManager(layoutManagerImpl)
            adapterImpl.setContentViewType(DataListRecyclerViewAdapter.NULL)
        }

        if (isChanged) recyclerViewImpl.post( { adapterImpl.notifyItemChanged(adapterImpl.getDataList().size)})
        if (swipeRefreshLayoutImpl != null) {
            swipeRefreshLayoutImpl!!.isEnabled = false
            swipeRefreshLayoutImpl!!.isRefreshing = false
        }

        onLoadSuccessImpl()

        // Check if RecyclerView is already at the bottom.
        // If it's currently at the bottom, call API to obtain the next page's data.
        // Delay it by 500ms to wait until adapter has been notified,
        // too bad there aren't any reliable way to wait until notify has been finished.
        if (infiniteScrolling) {
            recyclerViewImpl.postDelayed({
                if (activity != null && activity!!.isFinishing) return@postDelayed
                if (recyclerViewImpl.canScrollVertically(1)) return@postDelayed
                if (!isLoading && adapterImpl.getContentViewType() == DataListRecyclerViewAdapter.NULL) fetchData()
            }, 500)
        }
    }

    /**
     * Adds the collection to the adapter list.
     * Override this method if the addition logic to the adapter needs to be customized.
     * @param collection the collection
     */
    open fun addCollection(collection: Collection<Any>) {
        val index = adapterImpl.getDataList().size
        adapterImpl.getDataList().addAll(collection)
        adapterImpl.notifyItemRangeChanged(index, collection.size)
    }

    /** Sets this fragment state to not loading anymore */
    fun onLoadSuccessImpl() {
        onLoadSuccess(adapterImpl.getDataList().size > 0)
        isLoading = false
    }

    /**
     * Called when fetching data process is completed successfully.
     * Override this method if further customization is required.
     */
    open fun onLoadSuccess(isPaginatedLoad : Boolean) {
        // Stub!
    }

    /**
     * Updates the adapter to display error.
     * @param error the error
     */
    private fun onError(error: AppError) {
        adapterImpl.errorText = error.message
        val isChanged = adapterImpl.setContentViewType(DataListRecyclerViewAdapter.ERROR)
        if (isChanged) recyclerViewImpl.post( { adapterImpl.notifyItemChanged(adapterImpl.getDataList().size)})
        if (swipeRefreshLayoutImpl != null) {
            swipeRefreshLayoutImpl!!.isEnabled = false
            swipeRefreshLayoutImpl!!.isRefreshing = false
        }

        onLoadFailure(error, adapterImpl.getDataList().size > 0)
        isLoading = false
    }

    /**
     * Called when fetching data process has failed.
     * Override this method if further customization is required.
     */
    open fun onLoadFailure(error: AppError, isPaginatedLoad : Boolean) {
        // Stub!
    }

    /**
     * Called when a swipe-to-refresh is performed.
     * Override this method to customize its implementation.
     */
    open fun onSwipeToRefresh() {
        // Stub!
    }

    /**
     * Called when the retry button on error layout is performed.
     * Override this method to customize its implementation.
     */
    open fun onErrorRetryClicked() {
        fetchData()
    }

    /**
     * Called when the retry button on pagination error layout is performed.
     * Override this method to customize its implementation.
     */
    open fun onPaginationErrorRetryClicked() {
        fetchData()
    }

    /**
     * Sets the [RecyclerView].
     *
     * Override this method to return your customized fragment's [RecyclerView],
     * this fragment will automatically use your specified view to display the items.
     * @return the recycler view for the fragment
     */
    open fun initRecyclerView(): RecyclerView {
        return recycler_data_list
    }

    /**
     * Sets the [AppBarLayout]
     *
     * Override this method to return your customized fragment's [AppBarLayout],
     * this fragment will automatically calculate the height and determine
     * if swipe-to-refresh should be enabled.
     * @return the app bar layout for the fragment
     */
    open fun initAppBarLayout(): AppBarLayout? {
        return null
    }

    /**
     * Sets the [SwipeRefreshLayout]
     *
     * Override this method to return your customized fragment's [SwipeRefreshLayout],
     * this fragment will automatically determine if swipe-to-refresh should be enabled.
     * @return the swipe refresh layout for the fragment
     */
    open fun initSwipeRefreshLayout(): SwipeRefreshLayout? {
        return swipe_refresh_data_list
    }

    /** Sets adapter for the [RecyclerView]  */
    abstract fun initRecyclerAdapter(): DataListRecyclerViewAdapter<Any, RecyclerView.ViewHolder>

    /** Sets the layout manager for the [RecyclerView]  */
    abstract fun initRecyclerLayoutManager(): RecyclerView.LayoutManager

    /** Fetch the data from any source  */
    abstract fun fetchData()
}
