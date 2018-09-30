package com.appschef.baseproject.model.core

import com.appschef.baseproject.App
import com.appschef.baseproject.R
import com.appschef.baseproject.util.Common
import com.appschef.baseproject.util.ValidationHelper
import com.google.gson.annotations.SerializedName

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Alvin on 10/10/16.
 *
 * The base response object that the app will receive.
 */
open class AppResponse() {

    @SerializedName("meta")
    var meta: Meta? = null

    /** Constructor of this class  */
    constructor(response: String) : this() {
        // Check if the response is a JSON before confirming it as a success
        var isValid = true
        if (ValidationHelper.isValidJson(response)) {
            try {
                val json = JSONObject(response)

                // Parse the metadata
                val metaJson = json.optJSONObject("meta")
                if (metaJson != null) {
                    meta = Meta()

                    // The response message
                    meta!!.message = metaJson.optString("message")

                    // The pagination metadata
                    val pagingJson = metaJson.optJSONObject("paging")
                    if (pagingJson != null) {
                        val paging = Paging()
                        paging.total = pagingJson.optInt("total")
                        paging.offset = pagingJson.optInt("offset")
                        paging.limit = pagingJson.optInt("limit")
                        meta!!.paging = paging
                    }
                } else {
                    // If the response doesn't have any metadata, treat it as invalid
                    isValid = false
                }
            } catch (e: JSONException) {
                Common.printStackTrace(e)
                isValid = false
            }
        } else {
            isValid = false
        }

        if (!isValid) {
            meta = Meta()
            meta!!.message = App.context.resources.getString(R.string.error_json)
        }
    }
}
