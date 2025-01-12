package com.example.rentmycar.components.car

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.rentmycar.R
import com.example.rentmycar.viewmodel.CarViewModel

/**
 * Card which contains general info about the car without details or location.
 */
@Composable
fun CarCard(carViewModel: CarViewModel, navController: NavController) {
    val context = LocalContext.current
    val car = carViewModel.car.observeAsState().value ?: return
    val imagePaths by carViewModel.images.observeAsState(emptyList())

    OutlinedCard (
        onClick = {
            // Navigate to Car Item screen where additional info about the car is displayed
            navController.navigate("carItem/${car.id}")
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {

            // Car images
            CarFirstImage(imagePaths, context)

            // Car info section
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Brand & model
                    Text(
                        "${car.brand}, ${car.model}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    // Price
                    Text("$${car.price}", style = MaterialTheme.typography.bodySmall)
                }

                // Owner
                Text("Owner: @${car.ownerName}")
            }
        }
    }
}

/**
 * Composable to display either placeholder image, or first image from those which are attached to the car.
 */
@Composable
fun CarFirstImage(imagePaths: List<String>, context: Context) {
    if (imagePaths.isNotEmpty()) {
    // Display first image of car if exists
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("http://10.0.2.2:8080/images/${imagePaths.first()}")
            .crossfade(true)
            .build(),
        contentDescription = "Car image",
        modifier = Modifier
            .width(60.dp)
            .height(60.dp),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.car_img_placeholder),
        error = painterResource(id = R.drawable.error)
    )
} else {
    // Placeholder image if the car does not have images
    Image(
        painter = painterResource(R.drawable.car_img_placeholder),
        contentDescription = "Car image placeholder",
        modifier = Modifier
            .width(60.dp)
            .height(60.dp),
    )
}

}