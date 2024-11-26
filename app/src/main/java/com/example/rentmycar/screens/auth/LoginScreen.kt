package com.example.rentmycar.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentmycar.components.CustomButton
import com.example.rentmycar.components.TextInputField
import com.example.rentmycar.navigation.BottomNavItem

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextInputField(value = email, label = "Email", onValueChange = { email = it })
        Spacer(modifier = Modifier.height(8.dp))
        TextInputField(
            value = password,
            label = "Password",
            onValueChange = { password = it },
            isPassword = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomButton(text = "Login") {
            navController.navigate(BottomNavItem.Home.route)
        }
        TextButton(onClick = { navController.navigate("register") }) {
            Text("No account? Register here")
        }
    }
}
