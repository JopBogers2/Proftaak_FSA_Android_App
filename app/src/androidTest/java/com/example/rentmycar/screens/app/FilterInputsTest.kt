package com.example.rentmycar.screens.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.rentmycar.components.car.FilterInputs
import com.example.rentmycar.viewmodel.car.CarFiltersViewModel
import io.mockk.mockk
import io.mockk.verify
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilterInputsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun viewModelsMethodsAreTriggered() {
        val mockCarFiltersViewModel = mockk<CarFiltersViewModel>(relaxed = true)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            FilterInputs(
                carFiltersViewModel = mockCarFiltersViewModel,
                filters = mapOf(
                    "category" to "ICE",
                    "minPrice" to "20000",
                    "maxPrice" to "50000",
                    "radius" to "600000"
                ),
                context = context
            )
        }

        composeTestRule.onNodeWithText("Apply filters").performClick()

        verify { mockCarFiltersViewModel.addOrRemoveFilter("category", "ICE") }
        verify { mockCarFiltersViewModel.addOrRemoveFilter("minPrice", "20000") }
        verify { mockCarFiltersViewModel.addOrRemoveFilter("maxPrice", "50000") }
        verify { mockCarFiltersViewModel.addOrRemoveFilter("radius", "600000") }
    }
}