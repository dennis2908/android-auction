package com.appschef.baseproject.model.remote.product

import com.google.gson.annotations.SerializedName

/**
 * Created by Alvin Rusli on 2/7/2017.
 */
class SampleProduct {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("image")
    var image: String? = null

    @SerializedName("image_thumb")
    var imageThumbnail: String? = null
}
