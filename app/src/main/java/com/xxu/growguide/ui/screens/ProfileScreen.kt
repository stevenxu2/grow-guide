package com.xxu.growguide.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.xxu.growguide.R
import com.xxu.growguide.viewmodels.ThemeViewModel

/**
 * Purpose: Displays the user profile screen with personal information and app settings
 *
 * @param navController Navigation controller for screen navigation
 * @param innerPadding Padding values from the parent layout
 * @param scrollState State object for handling scrolling behavior
 * @param themeViewModel ViewModel that manages theme preferences
 */
@Composable
fun ProfileScreen(navController: NavHostController, innerPadding: PaddingValues, scrollState: ScrollState, themeViewModel: ThemeViewModel){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        ProfileHeader()
        ProfileBanner()
        CommonSettings(themeViewModel)
    }
}

/**
 * Purpose: Provides a preview of the ProfileScreen for Android Studio design view
 */
@Composable
@Preview(showBackground = true)
fun ProfileScreenPreview() {
    val mockNavController = rememberNavController()
    val mockPaddingValues = PaddingValues(0.dp)
    val mockScrollState = rememberScrollState()
    val mockThemeViewModel = remember { ThemeViewModel() }

    ProfileScreen(
        navController = mockNavController,
        innerPadding = mockPaddingValues,
        scrollState = mockScrollState,
        themeViewModel = mockThemeViewModel
    )
}

/**
 * Purpose: Displays the header section of the profile screen with title
 */
@Composable
fun ProfileHeader(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Header
        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}


/**
 * Purpose: Displays the user's profile information including avatar, name, and location
 */
@Composable
fun ProfileBanner(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(16.dp)
                .height(150.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.size(60.dp),
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "Plant Placeholder",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiaryContainer)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Column {
                    Text(
                        text = "Jane Smith",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Winnipeg, MB",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gardening since 2023",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

        }

    }
    Spacer(modifier = Modifier.height(16.dp))
}

/**
 * Purpose: Displays a list of common settings and options for the user
 *
 * @param themeViewModel ViewModel that manages theme preferences
 */
@Composable
fun CommonSettings(themeViewModel: ThemeViewModel) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        ThemeToggle(themeViewModel)

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceContainer
        )

        LanguageSetting()

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceContainer
        )

        AccountManagement()

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceContainer
        )

        MoreSettings()
    }

    Spacer(modifier = Modifier.height(16.dp))

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        LogOut()
    }
}

/**
 * Purpose: Provides a toggle switch for changing between light and dark themes
 *
 * @param themeViewModel ViewModel that manages theme preferences
 */
@Composable
fun ThemeToggle(themeViewModel: ThemeViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Dark Mode")
        Switch(
            checked = themeViewModel.isDarkTheme.value,
            onCheckedChange = { themeViewModel.toggleTheme() }
        )
    }
}

/**
 * Purpose: Displays language selection option
 */
@Composable
fun LanguageSetting() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Language", Modifier.weight(1f))
        Text(
            text = "English",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Select language",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Purpose: Provides navigation to account management options
 */
@Composable
fun AccountManagement() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Account Management", Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Select language",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Purpose: Provides navigation to additional app settings
 */
@Composable
fun MoreSettings() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Settings", Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Select language",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Purpose: Provides option for user to log out of their account
 */
@Composable
fun LogOut() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Log Out", Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "Select language",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}