package com.xxu.growguide.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxu.growguide.R
import com.xxu.growguide.data.models.weather.WeatherData
import com.xxu.growguide.ui.theme.Cloudy
import com.xxu.growguide.ui.theme.Misty
import com.xxu.growguide.ui.theme.PartlyCloudy
import com.xxu.growguide.ui.theme.Rainy
import com.xxu.growguide.ui.theme.Snowy
import com.xxu.growguide.ui.theme.Stormy
import com.xxu.growguide.ui.theme.Sunny
import com.xxu.growguide.viewmodels.WeatherUiState
import com.xxu.growguide.viewmodels.WeatherViewModel

/**
 * Purpose: Displays weather information based on the current UI state (loading, success, or error)
 *
 * @param weatherViewModel ViewModel that provides weather data and refresh functionality
 * @param weatherState Current UI state containing weather data or error information
 */
@Composable
fun WeatherCard(weatherViewModel: WeatherViewModel ,weatherState: WeatherUiState){
    Log.i("GrowGuide", "${weatherState}")
    // Display content based on UI state
    when (weatherState) {
        is WeatherUiState.Loading -> {
            LoadingContent()
        }
        is WeatherUiState.Success -> {
            WeatherContent(
                weatherData = weatherState.weatherData,
                onRefresh = { weatherViewModel.refreshWeather() }
            )
        }
        is WeatherUiState.Error -> {
            ErrorContent(message = weatherState.message)
        }
    }

    Spacer(modifier = Modifier.height(18.dp))
}

/**
 * Purpose: Displays detailed weather information
 *
 * @param modifier Optional Modifier for customizing the layout
 * @param weatherData The weather data to display
 * @param onRefresh Optional callback function for refreshing weather data
 */
@Composable
private fun WeatherContent(
    modifier: Modifier = Modifier,
    weatherData: WeatherData,
    onRefresh: () -> Unit = {}
) {
    val location = weatherData.location?.name ?: ""
    val tempC = weatherData.current?.tempC ?: 0.0
    val conditionCode = weatherData.current?.condition?.code ?: 1000 // Default to sunny if null
    val isDay = weatherData.current?.isDay == 1 // API returns 1 for day, 0 for night

    // Find the weather condition text based on the code
    val weatherCondition = getWeatherCondition(conditionCode, isDay)

    // Get the corresponding icon resource ID
    val iconResId = getWeatherIcon(conditionCode)

    // Get the color tint based on weather condition
    val iconTint = getWeatherIconTint(conditionCode)

    // Determine watering advice based on weather condition
    val wateringAdvice = getWateringAdvice(conditionCode)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceDim)
            .padding(16.dp)
            .clickable { onRefresh() },
    ) {
        Column(
            modifier = Modifier.weight(3f)
        ) {
            Text(
                text = "${weatherData.location?.name}",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${tempC.toInt()}°C",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "$weatherCondition, $wateringAdvice",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(80.dp),
                painter = painterResource(id = iconResId),
                contentDescription = weatherCondition,
                // colorFilter = ColorFilter.tint(iconTint)
            )
        }
    }
}

/**
 * Purpose: Displays a loading indicator while weather data is being fetched
 */
@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Loading weather data...")
    }
}

/**
 * Purpose: Displays an error message when weather data cannot be loaded
 *
 * @param message The error message to display
 */
@Composable
private fun ErrorContent(message: String) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error loading weather data",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Purpose: Returns the weather condition text based on code and time of day
 *
 * @param code The weather condition code from the API
 * @param isDay Boolean indicating whether it's daytime (true) or nighttime (false)
 * @return String description of the weather condition
 */
private fun getWeatherCondition(code: Int, isDay: Boolean): String {
    // This would be more efficient with a map, but using when for readability
    return when (code) {
        1000 -> if (isDay) "Sunny" else "Clear"
        1003 -> "Partly cloudy"
        1006 -> "Cloudy"
        1009 -> "Overcast"
        1030 -> "Mist"
        1063 -> "Patchy rain possible"
        1066 -> "Patchy snow possible"
        1069 -> "Patchy sleet possible"
        1072 -> "Patchy freezing drizzle possible"
        1087 -> "Thundery outbreaks possible"
        1114 -> "Blowing snow"
        1117 -> "Blizzard"
        1135 -> "Fog"
        1147 -> "Freezing fog"
        1150 -> "Patchy light drizzle"
        1153 -> "Light drizzle"
        1168 -> "Freezing drizzle"
        1171 -> "Heavy freezing drizzle"
        1180 -> "Patchy light rain"
        1183 -> "Light rain"
        1186 -> "Moderate rain at times"
        1189 -> "Moderate rain"
        1192 -> "Heavy rain at times"
        1195 -> "Heavy rain"
        1198 -> "Light freezing rain"
        1201 -> "Moderate or heavy freezing rain"
        1204 -> "Light sleet"
        1207 -> "Moderate or heavy sleet"
        1210 -> "Patchy light snow"
        1213 -> "Light snow"
        1216 -> "Patchy moderate snow"
        1219 -> "Moderate snow"
        1222 -> "Patchy heavy snow"
        1225 -> "Heavy snow"
        1237 -> "Ice pellets"
        1240 -> "Light rain shower"
        1243 -> "Moderate or heavy rain shower"
        1246 -> "Torrential rain shower"
        1249 -> "Light sleet showers"
        1252 -> "Moderate or heavy sleet showers"
        1255 -> "Light snow showers"
        1258 -> "Moderate or heavy snow showers"
        1261 -> "Light showers of ice pellets"
        1264 -> "Moderate or heavy showers of ice pellets"
        1273 -> "Patchy light rain with thunder"
        1276 -> "Moderate or heavy rain with thunder"
        1279 -> "Patchy light snow with thunder"
        1282 -> "Moderate or heavy snow with thunder"
        else -> if (isDay) "Sunny" else "Clear" // Default case
    }
}

/**
 * Purpose: Returns appropriate icon resource based on weather condition code
 *
 * @param code The weather condition code from the API
 * @return Resource ID for the corresponding weather icon
 */
private fun getWeatherIcon(code: Int): Int {
    return when (code) {
        1000 -> R.drawable.ic_sun
        1003 -> R.drawable.ic_partly_cloudy
        1006, 1009 -> R.drawable.ic_cloudy
        1030, 1135, 1147 -> R.drawable.ic_foggy
        1063, 1150, 1153, 1180, 1183, 1240 -> R.drawable.ic_light_rain
        1066, 1210, 1213, 1255 -> R.drawable.ic_light_snow
        1069, 1204, 1249 -> R.drawable.ic_sleet
        1072, 1168, 1171, 1198, 1201 -> R.drawable.ic_freezing_rain
        1087 -> R.drawable.ic_thunder
        1114, 1117 -> R.drawable.ic_blizzard
        1186, 1189, 1243 -> R.drawable.ic_moderate_rain
        1192, 1195, 1246 -> R.drawable.ic_heavy_rain
        1207, 1252 -> R.drawable.ic_sleet_heavy
        1216, 1219 -> R.drawable.ic_moderate_snow
        1222, 1225, 1258 -> R.drawable.ic_heavy_snow
        1237, 1261, 1264 -> R.drawable.ic_hail
        1273, 1276 -> R.drawable.ic_thunderstorm
        1279, 1282 -> R.drawable.ic_thundersnow
        else -> R.drawable.ic_sun // Default
    }
}

/**
 * Purpose: Returns icon tint color based on weather type
 *
 * @param code The weather condition code from the API
 * @return Color to use for tinting the weather icon
 */
private fun getWeatherIconTint(code: Int): Color {
    return when (code) {
        1000 -> Sunny
        1003 -> PartlyCloudy
        1006, 1009 -> Cloudy
        1030, 1135, 1147 -> Misty
        in 1063..1083, in 1150..1201, in 1240..1252, 1273, 1276 -> Rainy
        in 1066..1072, in 1204..1237, in 1255..1264, 1279, 1282 -> Snowy
        1087, 1273, 1276, 1279, 1282 -> Stormy
        else -> Sunny // Default
    }
}

/**
 * Purpose: Returns plant watering advice based on weather condition
 *
 * @param code The weather condition code from the API
 * @return String containing watering recommendation
 */
private fun getWateringAdvice(code: Int): String {
    return when (code) {
        1000 -> "perfect for watering" // Sunny
        1003 -> "good for watering" // Partly cloudy
        1006, 1009 -> "consider watering" // Cloudy, Overcast
        1030, 1135, 1147 -> "moderate watering needed" // Mist, Fog
        1063, 1150, 1153, 1180, 1183, 1240 -> "light watering may be needed" // Light rain
        1186, 1189, 1243 -> "no watering needed" // Moderate rain
        1192, 1195, 1246 -> "skip watering today" // Heavy rain
        1066, 1069, 1072, 1114, 1117, 1168, 1171, 1198, 1201, 1204, 1207,
        1210, 1213, 1216, 1219, 1222, 1225, 1237, 1249, 1252, 1255, 1258,
        1261, 1264, 1279, 1282 -> "protect plants from frost" // Snow and ice conditions
        1087, 1273, 1276 -> "delay watering" // Thunder conditions
        else -> "check soil moisture" // Default advice
    }
}