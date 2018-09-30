package com.appschef.baseproject.util

import com.appschef.baseproject.BuildConfig

/**
 * Created by Alvin Rusli on 09/12/2017.
 *
 * An object for accessing endpoint constants in this app.
 */
object JNIUtil {

    private val API_MOCK = "mock"
    private val API_STAGING = "staging"
    private val API_PRODUCTION = "production"

    init {
        System.loadLibrary("constant")
    }

    /** @return the web url */
    val webUrl: String
        get() {
            return when (BuildConfig.FLAVOR_api) {
                API_MOCK -> getWebUrlMock()
                API_STAGING -> getWebUrlStaging()
                API_PRODUCTION -> getWebUrlProduction()
                else -> throw IllegalStateException("Invalid build flavor")
            }
        }

    /** @return the API url */
    val apiUrl: String
        get() {
            return when (BuildConfig.FLAVOR_api) {
                API_MOCK -> getApiUrlMock()
                API_STAGING -> getApiUrlStaging()
                API_PRODUCTION -> getApiUrlProduction()
                else -> throw IllegalStateException("Invalid build flavor")
            }
        }

    /** @return the API key */
    val apiKey: String
        get() {
            return when (BuildConfig.FLAVOR_api) {
                API_MOCK -> getApiKeyMock()
                API_STAGING -> getApiKeyStaging()
                API_PRODUCTION -> getApiKeyProduction()
                else -> throw IllegalStateException("Invalid build flavor")
            }
        }

    private external fun getWebUrlMock(): String
    private external fun getApiUrlMock(): String
    private external fun getApiKeyMock(): String

    private external fun getWebUrlStaging(): String
    private external fun getApiUrlStaging(): String
    private external fun getApiKeyStaging(): String

    private external fun getWebUrlProduction(): String
    private external fun getApiUrlProduction(): String
    private external fun getApiKeyProduction(): String
}
