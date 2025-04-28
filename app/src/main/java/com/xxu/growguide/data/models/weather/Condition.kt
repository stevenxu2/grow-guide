package com.xxu.growguide.data.models.weather


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Condition(
    @Json(name = "code")
    var code: Int?,
    @Json(name = "icon")
    var icon: String?,
    @Json(name = "text")
    var text: String?
)