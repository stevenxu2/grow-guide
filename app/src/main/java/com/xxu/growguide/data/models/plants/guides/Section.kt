package com.xxu.growguide.data.models.plants.guides


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Section(
    @Json(name = "description")
    var description: String?,
    @Json(name = "id")
    var id: Int?,
    @Json(name = "type")
    var type: String?
)