package com.appschef.baseproject.api.util

import com.appschef.baseproject.App
import com.appschef.baseproject.BuildConfig
import com.readystatesoftware.chuck.ChuckInterceptor

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by Alvin Rusli on 6/14/2016.
 */
class OkHttpClientHelper {

    /**
     * Initialize the [OkHttpClient] for retrofit.
     *
     * Server uses SSL, we need to install the certificate on the [OkHttpClient].
     * Debug and mock builds doesn't have valid certificates, so we'll allow all connections there.
     */
    fun initOkHttpClient(): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
//        try {
//            // TODO: 10/10/2016 Will this app use SSL for API urls?
//            // If app doesn't use SSL for API urls, leave this commented
//            if (BuildConfig.FLAVOR_api.equals("production", ignoreCase = true)) {
//                okHttpClientBuilder = buildSecureOkHttpClient(App.context.resources.openRawResource(R.raw.mycertfile))
//            } else {
//                okHttpClientBuilder = buildInsecureOkHttpClient()
//            }
//        } catch (e: Exception) {
//            Common.log(Log.ERROR, javaClass.simpleName + "#initOkHttpClient()", e.message)
//            okHttpClientBuilder = OkHttpClient.Builder()
//        }

        // Add logging for debug builds
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(logging)
        }

        // Add Chuck interceptor
        okHttpClientBuilder.addInterceptor(ChuckInterceptor(App.context))

        // Set timeout duration
        okHttpClientBuilder.connectTimeout(30, TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(30, TimeUnit.SECONDS)

        return okHttpClientBuilder.build()
    }

//    /**
//     * Generates an [OkHttpClient.Builder] object that trusts only the specified connections.
//     *
//     * This is used on release builds, using the specified certificate file.
//     * @return The secure [OkHttpClient.Builder] object
//     * @throws Exception
//     */
//    @Throws(Exception::class)
//    fun buildSecureOkHttpClient(cert: InputStream): OkHttpClient.Builder {
//        val okHttpClientBuilder = OkHttpClient.Builder()
//
//        // Load CA(s) from an InputStream
//        val cf = CertificateFactory.getInstance("X.509")
//        val ca = cf.generateCertificate(cert)
//
//        // Create a KeyStore containing our trusted CA(s)
//        val keyStoreType = KeyStore.getDefaultType()
//        val keyStore = KeyStore.getInstance(keyStoreType)
//        keyStore.load(null, null)
//        keyStore.setCertificateEntry("ca", ca)
//
//        // Create a TrustManager that trusts the CA(s) in our KeyStore
//        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
//        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
//        tmf.init(keyStore)
//
//        // Create an SSLContext that uses our TrustManager
//        val sslContext = SSLContext.getInstance("TLS")
//        sslContext.init(null, tmf.trustManagers, null)
//
//        // Add the SSL Socket Factory
//        // FIXME Use non deprecated method
//        okHttpClientBuilder.sslSocketFactory(sslContext.socketFactory)
//
//        return okHttpClientBuilder
//    }
//
//    /**
//     * Generates an [OkHttpClient.Builder] object that trusts all connections.
//     *
//     * This is mainly used on debug and mock builds, because staging's
//     * "https://www1.danamas.co.id/" web url  is most likely not registered on the certificate.
//     *
//     * **NEVER allow all SSL connections on release builds**
//     *
//     * Update 7/25/2016 PlayStore might check for insecure SSL implementation in app,
//     * this method may need to be commented before the APK is uploaded to PlayStore.
//     * @return The insecure [OkHttpClient.Builder] object
//     * @throws Exception
//     */
//    @Throws(Exception::class)
//    fun buildInsecureOkHttpClient(): OkHttpClient.Builder {
//        val okHttpClientBuilder = OkHttpClient.Builder()
//
//        // Create a HostnameVerifier to make sure the host matches the specified URL
//        val hostnameVerifier = object : HostnameVerifier {
//            override fun verify(hostname: String, session: SSLSession): Boolean {
//                try {
//                    val apiUrl = URL(Common.apiURL)
//                    return hostname.equals(apiUrl.host, ignoreCase = true)
//                } catch (e: MalformedURLException) {
//                    Common.log(Log.ERROR, javaClass.simpleName + "#buildInsecureOkHttpClient()", e.message)
//                    return false
//                }
//
//            }
//        }
//
//        // Trust everything
//        val trustManager = object : X509TrustManager {
//            @SuppressLint("TrustAllX509TrustManager")
//            @Throws(CertificateException::class)
//            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
//                // Do nothing
//            }
//
//            @SuppressLint("TrustAllX509TrustManager")
//            @Throws(CertificateException::class)
//            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
//                // Do nothing
//            }
//
//            override fun getAcceptedIssuers(): Array<X509Certificate> {
//                return arrayOf()
//            }
//        }
//
//        // Create an SSLContext that uses the "trust everything" TrustManager
//        val sslContext = SSLContext.getInstance("TLS")
//        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
//
//        // Add the Hostname Verifier
//        okHttpClientBuilder.hostnameVerifier(hostnameVerifier)
//
//        // Add the SSL Socket Factory
//        okHttpClientBuilder.sslSocketFactory(sslContext.socketFactory)
//
//        return okHttpClientBuilder
//    }
}
