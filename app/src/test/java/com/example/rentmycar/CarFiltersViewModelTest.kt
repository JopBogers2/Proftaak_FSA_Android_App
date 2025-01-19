package com.example.rentmycar

import com.example.rentmycar.viewmodel.car.CarFiltersViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test
import org.junit.Assert.*

class CarFiltersViewModelTest {
    private lateinit var viewModel: CarFiltersViewModel

    @Before
    fun setup() {
        viewModel = CarFiltersViewModel()
    }

    @Test
    fun addFilters() = runTest {
        viewModel.filters.test {
            assertEquals(emptyMap<String, String>(), awaitItem())

            viewModel.addOrRemoveFilter("category", "ICE")
            assertEquals(mapOf("category" to "ICE"), awaitItem())
        }
    }

    @Test
    fun removeFilters() = runTest {
        viewModel.addOrRemoveFilter("minPrice", "444")

        viewModel.filters.test {
            assertEquals(mapOf("minPrice" to "444"), awaitItem())

            viewModel.addOrRemoveFilter("minPrice", null)
            assertEquals(emptyMap<String, String>(), awaitItem())
        }
    }

    @Test
    fun removeFilterIfBlankValue() = runTest {
        viewModel.addOrRemoveFilter("minPrice", "444")

        viewModel.filters.test {
            assertEquals(mapOf("minPrice" to "444"), awaitItem())

            viewModel.addOrRemoveFilter("minPrice", "")
            assertEquals(emptyMap<String, String>(), awaitItem())
        }
    }
}