package com.swagcoders.khataguard

import com.swagcoders.khataguard.data.repository.CaseRepository
import com.swagcoders.khataguard.viewmodel.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @Test
    fun `checkApi updates status`() = runTest {
        val repository = mockk<CaseRepository>()
        coEvery { repository.health() } returns "ok"

        val vm = HomeViewModel(repository)
        vm.checkApi()

        assertEquals("ok", vm.status.value)
    }
}
