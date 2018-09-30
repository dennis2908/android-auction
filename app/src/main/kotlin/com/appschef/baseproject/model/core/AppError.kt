package com.appschef.baseproject.model.core

/**
 * Created by Alvin on 10/10/16.
 *
 * The custom error codes are:
 * * 1000~1999 - Client error
 * * 2000~2999 - Development error
 */
class AppError(val code: Int, val error: String?) : Exception(error) {

    companion object {

        const val CLIENT_UNKNOWN = 1000
        const val CLIENT_CANCELLED = 1001
        const val CLIENT_CONNECTION = 1002
        const val CLIENT_TIMEOUT = 1003
        const val DEVELOPMENT_UNKNOWN = 2000
        const val DEVELOPMENT_NULL_POINTER = 2001
        const val DEVELOPMENT_CLASS_CAST = 2002
    }
}
