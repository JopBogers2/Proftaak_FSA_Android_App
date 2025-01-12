package com.example.rentmycar.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.rentmycar.BuildConfig

@Composable
fun ImageCarousel(imagePaths: List<String>, context: Context) {
    val pagerState = rememberPagerState(pageCount = { imagePaths.size })

    HorizontalPager(
        state = pagerState,
        pageSpacing = 4.dp,
        modifier = Modifier.fillMaxWidth().height(400.dp).padding(vertical = 16.dp),
    ) { page ->
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("${BuildConfig.BASE_URL}images/${imagePaths[page]}")
                .crossfade(true)
                .build(),
            contentDescription = "Car image",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Fit,
        )
    }
}