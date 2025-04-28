package com.xxu.growguide.data.models.plants.list


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DefaultImage(
    @Json(name = "license")
    var license: Int?,
    @Json(name = "license_name")
    var licenseName: String?,
    @Json(name = "license_url")
    var licenseUrl: String?,
    @Json(name = "medium_url")
    var mediumUrl: String?,
    @Json(name = "original_url")
    var originalUrl: String?,
    @Json(name = "regular_url")
    var regularUrl: String?,
    @Json(name = "small_url")
    var smallUrl: String?,
    @Json(name = "thumbnail")
    var thumbnail: String?
)