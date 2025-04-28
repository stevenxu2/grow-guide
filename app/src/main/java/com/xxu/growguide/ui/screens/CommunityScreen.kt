package com.xxu.growguide.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

/**
 * Purpose: Displays the community screen with posts and social features
 *
 * @param navController Navigation controller for screen navigation
 * @param innerPadding Padding values from the parent layout
 * @param scrollState State object for handling scrolling behavior
 */
@Composable
fun CommunityScreen(
    navController: NavHostController,
    innerPadding: PaddingValues,
    scrollState: ScrollState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        CommunityHeader()
        CommunityTabs()
    }
}

/**
 * Purpose: Display the header of the community screen with title and add button
 */
@Composable
fun CommunityHeader(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Header
        Text(
            text = "Community",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Add icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

/**
 * Purpose: Creates a tabbed interface for navigating between different community views
 *
 * Displays tabs for "All Posts" and "Following" with corresponding content
 */
@Composable
fun CommunityTabs() {
    val tabs = listOf("All Posts", "Following")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
        ) { page ->
            when (page) {
                0 -> AllPosts()
                1 -> Following()
            }
        }
    }
}

/**
 * Purpose: Displays all community posts regardless of follow status
 */
@Composable
fun AllPosts() {
    Column(modifier = Modifier.padding(16.dp)) {
        PostList()
    }
}

/**
 * Purpose: Displays posts only from users that the current user is following
 */
@Composable
fun Following() {
    Column(modifier = Modifier.padding(16.dp)) {
        PostList()
    }
}

/**
 * Purpose: Data class representing a community post
 *
 * @property author The name of the post author
 * @property title The title or main content of the post
 * @property date When the post was created, in relative time format
 */
data class PostData(
    val author: String,
    val title: String,
    val date: String
)

/**
 * Purpose: Displays a scrollable list of community posts
 *
 * Currently uses test data, but would connect to real post data in production
 */
@Composable
fun PostList() {
    val testPosts = listOf(
        PostData("Sarah", "Water Only When Needed", "21 days ago"),
        PostData("Mike", "Let the Sunshine In", "5 days ago"),
        PostData("Garden", "Mind Your Soil Quality", "10 day sago"),
        PostData("Ken", "Prune for Better Growth", "7 days ago"),
        PostData("John", "Feed Plants Seasonally", "2 days ago"),
        PostData("Steven", "Check Roots When Repotting", "yesterday"),
        PostData("Sarah", "Water Only When Needed", "21 days ago"),
        PostData("Mike", "Let the Sunshine In", "5 days ago"),
        PostData("Garden", "Mind Your Soil Quality", "10 day sago"),
        PostData("Ken", "Prune for Better Growth", "7 days ago"),
        PostData("John", "Feed Plants Seasonally", "2 days ago"),
        PostData("Steven", "Check Roots When Repotting", "yesterday"),
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(testPosts) { post ->
            PostCard(post)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow
            )
        }
    }
}

/**
 * Purpose: Displays a single community post with author avatar, title, and date
 *
 * @param post The post data to display
 */
@Composable
fun PostCard(post: PostData) {
    Row {
        Column(
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = post.date,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}