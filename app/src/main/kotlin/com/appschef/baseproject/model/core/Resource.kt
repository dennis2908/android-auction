package com.appschef.baseproject.model.core

/**
 * Created by Alvin Rusli on 06/07/2017.
 *
 * The resource object for view models.
 */
class Resource<T> private constructor(var status: Int, var data: T? = null, var error: AppError = AppError(0, null)) {

    companion object {

        const val LOADING = 0
        const val SUCCESS = 1
        const val ERROR = 2

        /** Creates a new loading resource object  */
        fun <T> loading(): Resource<T> {
            return Resource(status = LOADING)
        }

        /**
         * Creates a new successful resource object.
         * @param data the data to be set
         */
        fun <T> success(data: T?): Resource<T> {
            return Resource(status = SUCCESS, data = data)
        }

        /**
         * Creates a new error resource object.
         * @param error the error
         */
        fun <T> error(error: AppError): Resource<T> {
            return Resource(status = ERROR, error = error)
        }
    }
}
