package com.example.instagramclone.presentation.screens.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.instagramclone.domain.model.Post

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

            // Post image with layered loading and double tap to like
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                // Layer 1: Low-res / Thumbnail (often already prefetched)
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.imageUrl)
                        .size(300, 300) // Small size for faster load/prefetch hit
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        // Level 0: Blur / Placeholder color
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.scale(0.5f))
                            }
                        }
                    }
                )

                // Layer 2: Full-res image
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
                                },
                                onTap = { showFullImage = true }
                            )
                        },
                    contentScale = ContentScale.Crop,
                    loading = {
                        // Do nothing, let the low-res layer show through
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
fun PostGridItem(post: Post) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
    ) {
        // Layer 1: Low-res / Thumbnail (often already prefetched)
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(post.imageUrl)
                .size(300, 300) // Small size for faster load/prefetch hit
                .crossfade(true)
                .build(),
            contentDescription = "Post thumbnail",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                // Level 0: Blur / Placeholder color
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.scale(0.5f))
                    }
                }
            }
        )

        // Layer 2: Full-res image
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(post.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Post full image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        )
    }
}
