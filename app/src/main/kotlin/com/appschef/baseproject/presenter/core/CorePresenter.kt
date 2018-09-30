package com.appschef.baseproject.presenter.core

/**
 * Created by Alvin Rusli on 11/08/16.
 *
 * The base presenter interface.
 */
@Deprecated("Implement a LifecycleObserver instead")
interface CorePresenter<T> {

    /**
     * Attach 'something' to this presenter.
     * @param attache the attached 'something'
     */
    fun attach(attache: T)

    /** Detach this presenter  */
    fun detach()
}
