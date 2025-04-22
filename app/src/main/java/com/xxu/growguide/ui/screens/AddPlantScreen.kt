package com.xxu.growguide.ui.screens

import android.graphics.drawable.Icon
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.xxu.growguide.R
import com.xxu.growguide.api.PlantsManager
import com.xxu.growguide.auth.AuthManager
import com.xxu.growguide.data.database.AppDatabase
import com.xxu.growguide.data.entity.UserPlantsEntity
import com.xxu.growguide.destinations.Destination
import com.xxu.growguide.ui.components.BackButton
import com.xxu.growguide.ui.viewmodels.PlantDetailViewModel
import com.xxu.growguide.viewmodels.AddPlantViewModel
import com.xxu.growguide.viewmodels.AddPlantViewModelFactory
import com.xxu.growguide.viewmodels.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects

/**
 * Purpose: Provides a form for users to add a new plant to their garden
 *
 * This screen allows users to input plant details such as name, type, planting date,
 * photos, and notes. It features a top app bar with a back button and a form with
 * multiple input fields.
 *
 * @param navController The NavController used to navigate between screens
 * @param innerPadding Padding values passed from the parent layout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    plantId: Int,
    navController: NavController,
    plantsManager: PlantsManager
) {
    // Get the view model
    val viewModel: AddPlantViewModel = viewModel(factory = AddPlantViewModelFactory(plantsManager))
    val authManager = AuthManager.getInstance(LocalContext.current.applicationContext)

    // Collect states from ViewModel
    val plantDetail by viewModel.plantDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val savingSuccess by viewModel.savingSuccess.collectAsState()

    var plantName = remember { mutableStateOf("") }
    var plantNotes = remember { mutableStateOf("") }
    var selectedDate = remember { mutableStateOf("") }
    val dateFormatter = remember { SimpleDateFormat("MM/dd/yyyy", Locale.CANADA) }
    var selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    // Load plant detail when screen is displayed
    LaunchedEffect(Unit) {
        viewModel.loadPlantDetail(plantId)
    }

    // Navigate to garden screen after saving plant to garden successfully
    LaunchedEffect(savingSuccess) {
        if (savingSuccess) {
            navController.navigate(Destination.Garden.route)
        }
    }

    Log.i("AddPlantScreen", "Rendering AddPlantScreen")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Loading state
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                // Error state
                else if (error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                // Content state
                else if (plantDetail != null) {
                    val plant = plantDetail!!
                    LaunchedEffect(Unit) {
                        plantName.value = plant.commonName
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            // Plant image
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                ) {
                                    if (plant.imageUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(plant.imageUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = plant.commonName,
                                            contentScale = ContentScale.FillWidth,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(MaterialTheme.colorScheme.tertiaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_plant_placeholder),
                                                contentDescription = "Plant Placeholder",
                                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiaryContainer),
                                                modifier = Modifier.size(80.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Plant information
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                            ) {
                                // Form fields
                                Text(
                                    text = "Add Plant",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Plant name
                                Text(
                                    text = "Plant Name",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                CustomTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = plantName,
                                    trailingIcon = {
                                        if (plantName.value.isNotEmpty()) {
                                            IconButton(onClick = { plantName.value = "" }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear"
                                                )
                                            }
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // Plant date
                                PlantingDate(
                                    selectedDate = selectedDate,
                                    dateFormatter = dateFormatter
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // Add photo
                                AddPhoto(selectedImageUri)

                                Spacer(modifier = Modifier.height(20.dp))

                                // Notes
                                Text(
                                    text = "Notes",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                CustomTextField(
                                    modifier = Modifier.fillMaxWidth().height(150.dp),
                                    text = plantNotes,
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }

                    // Floating back button - always visible at top left
                    BackButton(navController)
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Add Plant button
            Button(
                onClick = {
                    val userPlant = UserPlantsEntity(
                        userId = authManager.currentUser.value?.uid.toString(),
                        plantId = plantId,
                        nickname = plantName.value,
                        plantingDate = dateFormatter.parse(selectedDate.value)?.time ?: 0,
                        imageUri = selectedImageUri.value.toString(),
                        notes = plantNotes.value,
                        dateAdded = System.currentTimeMillis(),
                    )
                    viewModel.addUserPlant(userPlant)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Add to My Garden",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

    }
}

/**
 * Purpose: Provides UI for adding a photo to the application.
 *
 * It provides options to take a new photo with the camera or select one from the gallery.
 */
@Composable
private fun AddPhoto(selectedImageUri: MutableState<Uri?>) {
    var showPhotoOptions by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Function to get content URI using FileProvider
    fun getContentUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "com.xxu.growguide.provider",
            file
        )
    }

    // Function to create a unique file in app storage
    fun createImageFile(prefix: String = "PLANT"): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "${prefix}_${timeStamp}.jpg"
        return File(context.filesDir, fileName)
    }

    // Create temporary file for camera photo
    val tempImageFile = remember { createImageFile("TEMP") }
    val tempImageUri = remember { getContentUri(tempImageFile) }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val destFile = createImageFile()

                    context.contentResolver.openInputStream(uri)?.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        selectedImageUri.value = getContentUri(destFile)
                    }
                } catch (e: Exception) {
                    Log.e("AddPlantScreen", "Error copying gallery image: ${e.message}", e)
                }
            }
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            // The camera has already saved to tempImageUri location
            // For consistency, we'll copy to a permanent file with standard naming
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val destFile = createImageFile()

                    context.contentResolver.openInputStream(tempImageUri)?.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        selectedImageUri.value = getContentUri(destFile)
                    }
                } catch (e: Exception) {
                    Log.e("AddPlantScreen", "Error copying camera image: ${e.message}", e)
                    // Fallback to the temp URI if copying fails
                    withContext(Dispatchers.Main) {
                        selectedImageUri.value = tempImageUri
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Add Photo",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Photo display/selection area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            if (selectedImageUri.value != null) {
                // Display the selected image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(selectedImageUri.value)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Selected plant photo",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxSize()
                )

                // Add an edit button overlay in the corner
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clickable { showPhotoOptions = true },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    ),
                    shape = CircleShape,
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = "Change Photo",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                // Show add photo button when no image is selected
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .clickable { showPhotoOptions = true },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = "Add Photo",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Add Photo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Photo options dialog
    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text("Add Photo") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                cameraLauncher.launch(tempImageUri)
                                showPhotoOptions = false
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera"
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Take Photo")
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                galleryLauncher.launch("image/*")
                                showPhotoOptions = false
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery"
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Choose from Gallery")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPhotoOptions = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Purpose: Provides a UI element for selecting and displaying a planting date.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PlantingDate(
    selectedDate: MutableState<String>,
    dateFormatter: SimpleDateFormat
) {
    var showDatePicker by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (selectedDate.value.isEmpty()) {
            selectedDate.value = dateFormatter.format(Date())
        }
    }

    Log.i("AddPlantScreen", "Rendering AddPlantScreen - PlantingDate")
    Log.i("AddPlantScreen", "Rendering AddPlantScreen - PlantingDate - selectedDate: ${selectedDate.value}")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Planting Date",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.weight(1f))

//        Text(
//            text = selectedDate.value,
//            style = MaterialTheme.typography.bodyLarge
//        )
    }

    CustomTextField(
        modifier = Modifier.fillMaxWidth(),
        text = selectedDate,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select Date"
                )
            }
        }
    )

    // Date Picker Dialog
    if (showDatePicker) {
        // Parse selectedDate.value into milliseconds if it exists
        val initialDateMillis = if (selectedDate.value.isNotEmpty()) {
            try {
                val date = dateFormatter.parse(selectedDate.value)
                date?.time ?: System.currentTimeMillis()
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        } else {
            System.currentTimeMillis()
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateMillis,
            initialDisplayMode = DisplayMode.Picker
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Add one day to compensate for the timezone issue
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = millis
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                        selectedDate.value = dateFormatter.format(calendar.time)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

/**
 * Purpose: Customize the OutlinedTextField consistently
 */
@Composable
private fun CustomTextField(
    modifier: Modifier,
    text: MutableState<String>,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = text.value + "",
        onValueChange = { text.value = it },
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        shape = RoundedCornerShape(12.dp),
        trailingIcon = { trailingIcon?.invoke() }
    )
}
