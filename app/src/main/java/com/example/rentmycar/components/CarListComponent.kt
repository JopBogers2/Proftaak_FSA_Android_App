package com.example.rentmycar.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rentmycar.api.responses.CarResponse

@Composable
fun CarListComponent(cars: List<CarResponse>) {
    LazyColumn {
        items(cars) { car ->
            CarItem(car)
        }
    }
}

@Composable
fun CarItem(car: CarResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Model: ${car.model}")

            Text(text = "License Plate: ${car.licensePlate}")
            Text(text = "Year: ${car.year}")
            Text(text = "Color: ${car.color}")
            Text(text = "Fuel: ${car.fuel}")
            Text(text = "Transmission: ${car.transmission}")
            Text(text = "Price: $${car.price}")
            Text(text = "Category: ${car.category}")

        }
    }
}