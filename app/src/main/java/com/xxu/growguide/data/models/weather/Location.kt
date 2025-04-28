package com.xxu.growguide.data.models.weather


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Location(
    @Json(name = "country")
    var country: String?,
    @Json(name = "lat")
    var lat: Double?,
    @Json(name = "localtime")
    var localtime: String?,
    @Json(name = "localtime_epoch")
    var localtimeEpoch: Int?,
    @Json(name = "lon")
    var lon: Double?,
    @Json(name = "name")
    var name: String?,
    @Json(name = "region")
    var region: String?,
    @Json(name = "tz_id")
    var tzId: String?
)