package com.appschef.baseproject.model.core

import com.google.gson.annotations.SerializedName

/**
 * Created by Alvin Rusli on 11/15/2016.
 */
class Meta {

    @SerializedName("message")
    var message: String? = null

    @SerializedName("paging")
    var paging: Paging? = null
}
