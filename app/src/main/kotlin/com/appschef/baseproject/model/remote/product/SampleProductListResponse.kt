package com.appschef.baseproject.model.remote.product

import com.appschef.baseproject.model.core.AppResponse
import com.google.gson.annotations.SerializedName

/**
 * Created by Alvin Rusli on 2/7/2017.
 */
class SampleProductListResponse : AppResponse() {

    @SerializedName("data")
    var productList: List<SampleProduct>? = null
}
