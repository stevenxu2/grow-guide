package com.xxu.growguide.data.models.plants


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Dimension(
    @Json(name = "max_value")
    var maxValue: Int?,
    @Json(name = "min_value")
    var minValue: Int?,
    @Json(name = "type")
    var type: String?,
    @Json(name = "unit")
    var unit: String?
)