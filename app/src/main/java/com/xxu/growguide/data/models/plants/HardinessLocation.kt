package com.xxu.growguide.data.models.plants


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HardinessLocation(
    @Json(name = "full_iframe")
    var fullIframe: String?,
    @Json(name = "full_url")
    var fullUrl: String?
)