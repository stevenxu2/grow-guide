package com.xxu.growguide.data.models.weather


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherData(
    @Json(name = "current")
    var current: Current?,
    @Json(name = "location")
    var location: Location?
)