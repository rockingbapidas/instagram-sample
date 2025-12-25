package com.example.instagramclone.presentation.screens.feed

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.instagramclone.domain.model.FeedItem
import com.example.instagramclone.domain.model.Ad
import com.example.instagramclone.domain.model.Post
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    navController: NavController,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.feedPagingFlow.collectAsLazyPagingItems()
    val hasNewPosts by viewModel.hasNewPosts.collectAsState()
    
    val pullToRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Instagram Clone") },
                actions = {
                    IconButton(onClick = { navController.navigate("create_post") }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Post")
                    }
                }
            )
        }
    ) { paddingValues ->
        val isRefreshing = pagingItems.loadState.refresh is LoadState.Loading
        
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { pagingItems.refresh() },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (isRefreshing && pagingItems.itemCount == 0) {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(pagingItems.itemCount) { index ->
                            val item = pagingItems[index]
                            if (item != null) {
                                when (item) {
                                    is FeedItem.PostItem -> PostItem(post = item.post)
                                    is FeedItem.AdItem -> AdItem(ad = item.ad)
                                }
                            }
                        }

                        // Append Loader or Error
                        item {
                            val appendState = pagingItems.loadState.append
                            if (appendState is LoadState.Loading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else if (appendState is LoadState.Error) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Failed to load more posts",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(onClick = { pagingItems.retry() }) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                    }
                }
                
                // New Posts Banner
                if (hasNewPosts) {
                     ExtendedFloatingActionButton(
                        text = { Text("New Posts") },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) }, 
                        onClick = {
                            viewModel.onNewPostsShown()
                            pagingItems.refresh()
                            coroutineScope.launch {
                                listState.animateScrollToItem(0)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        
        // Initial Refresh Error Snackbar
        val refreshState = pagingItems.loadState.refresh
        if (refreshState is LoadState.Error && pagingItems.itemCount == 0) {
             Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { pagingItems.retry() }) {
                            Text("Retry")
                        }
                    }
                ) {
                    Text(refreshState.error.message ?: "Unknown Error")
                }
            }
        }
    }
}

@Composable
fun PostItem(post: Post) {
    var isLiked by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }
    var showFullImage by remember { mutableStateOf(false) }
    var doubleTapScale by remember { mutableFloatStateOf(1f) }
    val doubleTapAnimation = rememberInfiniteTransition(label = "doubleTap")
    val heartScale = doubleTapAnimation.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(onClick = { showOptions = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
            }

            // Post image with double tap to like
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    isLiked = true
                                    doubleTapScale = 1f
                                }
                            )
                        },
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Failed to load image",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                // Double tap heart animation
                if (doubleTapScale != 1f) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .scale(heartScale.value),
                        tint = Color.White
                    )
                }
            }

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { isLiked = !isLiked }) {
                    Icon(
                        if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isLiked) "Unlike" else "Like",
                        tint = if (isLiked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            // Likes and caption
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "${post.likes} likes",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = post.caption,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "View all ${post.comments.size} comments",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    // Full screen image dialog
    if (showFullImage) {
        Dialog(onDismissRequest = { showFullImage = false }) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Full size post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }
    }

    // Options menu
    if (showOptions) {
        AlertDialog(
            onDismissRequest = { showOptions = false },
            title = { Text("Post Options") },
            text = { Text("What would you like to do with this post?") },
            confirmButton = {
                TextButton(onClick = { showOptions = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun AdItem(ad: Ad) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("SPONSORED", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(ad.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Ad Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(ad.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(ad.content, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = { /* Open Ad */ }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Learn More")
            }
        }
    }
}


