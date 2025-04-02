package com.xxu.growguide.data.models.plants.guides


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "common_name")
    var commonName: String?,
    @Json(name = "id")
    var id: Int?,
    @Json(name = "scientific_name")
    var scientificName: List<String?>?,
    @Json(name = "section")
    var section: List<Section?>?,
    @Json(name = "species_id")
    var speciesId: Int?
)