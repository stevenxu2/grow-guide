package com.xxu.growguide.data.models.weather


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Current(
    @Json(name = "cloud")
    var cloud: Int?,
    @Json(name = "condition")
    var condition: Condition?,
    @Json(name = "dewpoint_c")
    var dewpointC: Double?,
    @Json(name = "dewpoint_f")
    var dewpointF: Double?,
    @Json(name = "feelslike_c")
    var feelslikeC: Double?,
    @Json(name = "feelslike_f")
    var feelslikeF: Double?,
    @Json(name = "gust_kph")
    var gustKph: Double?,
    @Json(name = "gust_mph")
    var gustMph: Double?,
    @Json(name = "heatindex_c")
    var heatindexC: Double?,
    @Json(name = "heatindex_f")
    var heatindexF: Double?,
    @Json(name = "humidity")
    var humidity: Int?,
    @Json(name = "is_day")
    var isDay: Int?,
    @Json(name = "last_updated")
    var lastUpdated: String?,
    @Json(name = "last_updated_epoch")
    var lastUpdatedEpoch: Int?,
    @Json(name = "precip_in")
    var precipIn: Double?,
    @Json(name = "precip_mm")
    var precipMm: Double?,
    @Json(name = "pressure_in")
    var pressureIn: Double?,
    @Json(name = "pressure_mb")
    var pressureMb: Double?,
    @Json(name = "temp_c")
    var tempC: Double?,
    @Json(name = "temp_f")
    var tempF: Double?,
    @Json(name = "uv")
    var uv: Double?,
    @Json(name = "vis_km")
    var visKm: Double?,
    @Json(name = "vis_miles")
    var visMiles: Double?,
    @Json(name = "wind_degree")
    var windDegree: Int?,
    @Json(name = "wind_dir")
    var windDir: String?,
    @Json(name = "wind_kph")
    var windKph: Double?,
    @Json(name = "wind_mph")
    var windMph: Double?,
    @Json(name = "windchill_c")
    var windchillC: Double?,
    @Json(name = "windchill_f")
    var windchillF: Double?
)