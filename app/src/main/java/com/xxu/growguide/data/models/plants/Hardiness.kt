package com.xxu.growguide.data.models.plants


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Hardiness(
    @Json(name = "max")
    var max: String?,
    @Json(name = "min")
    var min: String?
)