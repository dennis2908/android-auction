package com.appschef.baseproject.api.util

import android.os.Handler
import android.os.Looper

import java.io.File
import java.io.FileInputStream
import java.io.IOException

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink

/**
 * Created by Alvin Rusli on 6/24/2016.
 *
 * A customized [RequestBody] that can display progress updates.
 * Obtained from: http://stackoverflow.com/a/33384551/5315490
 */
class ProgressRequestBody (private val mediaType: MediaType, private val file: File, private val onUploadProgressListener: ProgressRequestBody.OnUploadProgressListener) : RequestBody() {

    override fun contentType(): MediaType {
        return mediaType
    }

    /**
     * This method will be called twice if a [okhttp3.logging.HttpLoggingInterceptor] is used.
     * Other than that, the progress updates work fine.
     */
    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength = file.length()
        val buffer = ByteArray(2048) // Determine your default buffer size here
        val inputStream = FileInputStream(file)
        var uploaded: Long = 0

        inputStream.use {
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (inputStream.read(buffer) != -1) {
                read = inputStream.read(buffer)
                uploaded += read.toLong()
                sink.write(buffer, 0, read)

                // update progress on UI thread
                handler.post(ProgressUpdater(uploaded, fileLength))
            }
        }
    }

    /** Private class to handle updated in the main thread */
    private inner class ProgressUpdater (private val uploaded: Long, private val total: Long) : Runnable {

        override fun run() {
            onUploadProgressListener.onProgressUpdate((100 * uploaded / total).toInt())
        }
    }

    /** The interface for upload callbacks */
    interface OnUploadProgressListener {

        /** Called when progress is updated */
        fun onProgressUpdate(percentage: Int)
    }
}