package com.appschef.baseproject.model.core

import com.google.gson.annotations.SerializedName

/**
 * Created by Alvin Rusli on 06/16/2017.
 */
class Paging {

    @SerializedName("total")
    var total: Int = 0

    @SerializedName("offset")
    var offset: Int = 0

    @SerializedName("limit")
    var limit: Int = 0
}