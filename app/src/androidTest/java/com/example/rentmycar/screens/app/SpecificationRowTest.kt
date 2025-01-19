package com.example.rentmycar.screens.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.rentmycar.components.car.SpecificationRow
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpecificationRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun specificationRowDisplaysCorrectText() {
        composeTestRule.setContent {
            SpecificationRow(key = "Brand", value = "Tesla")
        }

        composeTestRule.onNodeWithText("Brand").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tesla").assertIsDisplayed()
    }
}
