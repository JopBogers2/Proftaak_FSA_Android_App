package com.example.rentmycar.utils.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri

class RedirectHelper {
    @SuppressLint("QueryPermissionsNeeded")
    fun redirectToEmail(
        userEmail: String,
        context: Context,
        onError: () -> Unit,
    ) {
        // Create SENDTO intent with all the data needed to open mail app
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$userEmail")
        }

        // Start activity with the intent if possible
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Else, the phone does not have an email app installed.
            // Call the onError event
            onError()
        }
    }
}