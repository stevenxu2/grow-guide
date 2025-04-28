package com.xxu.growguide.data.models.plants.list


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlantsListData(
    @Json(name = "current_page")
    var currentPage: Int?,
    @Json(name = "data")
    var `data`: List<Data?>?,
    @Json(name = "from")
    var from: Int?,
    @Json(name = "last_page")
    var lastPage: Int?,
    @Json(name = "per_page")
    var perPage: Int?,
    @Json(name = "to")
    var to: Int?,
    @Json(name = "total")
    var total: Int?
)