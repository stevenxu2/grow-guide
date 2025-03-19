package com.xxu.growguide.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxu.growguide.R
import com.xxu.growguide.data.models.OnboardingPage
import com.xxu.growguide.ui.theme.Sunny
import kotlinx.coroutines.launch

/**
 * Show the onboarding pages
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    // List of onboarding pages
    val pages = listOf(
        OnboardingPage(
            image = R.drawable.ic_plant_placeholder,
            title = "Track Your Plants",
            description = "Keep detailed records of all your plants and monitor their growth progress."
        ),
        OnboardingPage(
            image = R.drawable.ic_plant_placeholder,
            title = "Get Growing Tips",
            description = "Receive personalized recommendations and care reminders for your plants."
        ),
        OnboardingPage(
            image = R.drawable.ic_plant_placeholder,
            title = "Share Your Garden",
            description = "Connect with other plant enthusiasts and share your gardening journey."
        ),
        OnboardingPage(
            image = R.drawable.ic_plant_placeholder,
            title = "Grow Together",
            description = "Build your green thumb with our supportive community and expert resources."
        )
    )

    // Pager state
    val pagerState = rememberPagerState { pages.size }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top section with Skip button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(80.dp)
            ) {
                // Only show Skip button if not on the last page
                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(
                        onClick = onFinishOnboarding,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(
                            text = "Skip",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Pager for onboarding content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { pageIndex ->
                OnboardingPageContent(page = pages[pageIndex])
            }

            // Bottom section with page indicator dots and buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 50.dp),
            ) {
                // Indicator dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pages.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        }
                        Box(
                            modifier = Modifier
                                .padding(6.dp)
                                .padding(bottom = 16.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                // Navigation buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Show Get Started button on last page, otherwise show Next
                    if (pagerState.currentPage == pages.size - 1) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onFinishOnboarding,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Sunny)
                        ) {
                            Text(
                                text = "Get Started",
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    } else {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = "Next",
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Show the onboarding page content
 */
@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Title
        Text(
            text = page.title,
            fontSize = 40.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Description
        Text(
            text = page.description,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 80.dp),
        )

        // Image
        Image(
            painter = painterResource(id = page.image),
            contentDescription = page.title,
            modifier = Modifier
                .size(300.dp),
            colorFilter = ColorFilter.tint(
                color = MaterialTheme.colorScheme.tertiaryContainer,
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
fun OnboardingScreenPreview() {
    OnboardingScreen {}
}