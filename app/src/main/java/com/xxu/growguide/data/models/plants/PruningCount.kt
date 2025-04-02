package com.xxu.growguide.data.models.plants

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PruningCount(
    @Json(name = "amount")
    var amount: Int?,
    @Json(name = "interval")
    var interval: String?
)