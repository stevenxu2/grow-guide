package com.xxu.growguide.data.models.plants.list


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "authority")
    var authority: Any?,
    @Json(name = "common_name")
    var commonName: String?,
    @Json(name = "cultivar")
    var cultivar: String?,
    @Json(name = "default_image")
    var defaultImage: DefaultImage?,
    @Json(name = "family")
    var family: String?,
    @Json(name = "genus")
    var genus: String?,
    @Json(name = "hybrid")
    var hybrid: Any?,
    @Json(name = "id")
    var id: Int?,
    @Json(name = "other_name")
    var otherName: List<String?>?,
    @Json(name = "scientific_name")
    var scientificName: List<String?>?,
    @Json(name = "species_epithet")
    var speciesEpithet: String?,
    @Json(name = "subspecies")
    var subspecies: Any?,
    @Json(name = "variety")
    var variety: Any?
)