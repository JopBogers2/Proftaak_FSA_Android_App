package com.example.rentmycar.screens.app

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import com.example.rentmycar.utils.helpers.LocationHelper
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LocationHelperTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var locationHelper: LocationHelper
    private lateinit var mockContext: Context
    private lateinit var mockActivity: Activity

    @Before
    fun setUp() {
        mockContext = mockk()
        mockActivity = mockk()
        locationHelper = LocationHelper(mockContext, mockActivity)
    }

    @Test
    fun hasLocationPermissionsIsTrueWhenPermissionsAreGranted() {
        every { ContextCompat.checkSelfPermission(mockContext, any()) } returns PERMISSION_GRANTED
        val result = locationHelper.hasLocationPermissions()
        assert(result)
    }

    @Test
    fun hasLocationPermissionsIsFalseWhenPermissionsAreNotGranted() {
        every { ContextCompat.checkSelfPermission(mockContext, any()) } returns PERMISSION_DENIED
        val result = locationHelper.hasLocationPermissions()
        assert(!result)
    }
}