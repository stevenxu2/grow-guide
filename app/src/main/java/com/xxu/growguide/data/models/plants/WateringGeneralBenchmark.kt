package com.xxu.growguide.data.models.plants


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WateringGeneralBenchmark(
    @Json(name = "unit")
    var unit: String?,
    @Json(name = "value")
    var value: Any?
)