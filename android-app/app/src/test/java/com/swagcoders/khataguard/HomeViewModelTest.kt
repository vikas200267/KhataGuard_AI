package com.swagcoders.khataguard

import com.swagcoders.khataguard.data.model.CaseResponse
import com.swagcoders.khataguard.data.repository.CaseRepository
import com.swagcoders.khataguard.viewmodel.CreateCaseViewModel
import com.swagcoders.khataguard.viewmodel.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDashboard updates status and states`() = runTest {
        val repository = mockk<CaseRepository>()
        coEvery { repository.healthStatus() } returns Result.success("ok")
        coEvery { repository.getStates() } returns Result.success(listOf("Karnataka", "Maharashtra"))

        val vm = HomeViewModel(repository)
        vm.loadDashboard()

        assertEquals("ok", vm.uiState.value.apiStatus)
        assertTrue(vm.uiState.value.states.contains("Karnataka"))
    }

    @Test
    fun `createCase sets createdCaseId on success`() = runTest {
        val repository = mockk<CaseRepository>()
        coEvery { repository.createCase("Demo Plot", "Karnataka") } returns Result.success(
            CaseResponse(caseId = "case-123", propertyName = "Demo Plot", state = "Karnataka")
        )

        val vm = CreateCaseViewModel(repository)
        vm.onPropertyNameChange("Demo Plot")
        vm.onStateSelect("Karnataka")
        vm.createCase()

        assertEquals("case-123", vm.uiState.value.createdCaseId)
    }
}
