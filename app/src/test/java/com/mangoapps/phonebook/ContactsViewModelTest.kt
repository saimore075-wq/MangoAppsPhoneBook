package com.mangoapps.phonebook

import app.cash.turbine.test
import com.mangoapps.phonebook.feature.contacts.domain.model.LocalContact
import com.mangoapps.phonebook.feature.contacts.domain.repository.ContactsRepository
import com.mangoapps.phonebook.feature.contacts.presentation.ContactsUiState
import com.mangoapps.phonebook.feature.contacts.presentation.ContactsViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContactsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ContactsRepository
    private lateinit var viewModel: ContactsViewModel

    private val fakeContacts = listOf(
        LocalContact(1L, "Alice", listOf("555-0001"), null),
        LocalContact(2L, "Bob", listOf("555-0002"), null)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        every { repository.getRemoteContacts() } returns flowOf()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadLocalContacts emits Loading then Success`() = runTest {
        every { repository.getLocalContacts() } returns flowOf(fakeContacts)
        viewModel = ContactsViewModel(repository)

        viewModel.localContactsState.test {
            val loading = awaitItem()
            assertTrue(loading is ContactsUiState.Loading)
            val success = awaitItem()
            assertTrue(success is ContactsUiState.Success)
            assertEquals(fakeContacts, (success as ContactsUiState.Success).contacts)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadLocalContacts handles exception and emits Error`() = runTest {
        val errorMessage = "Permission denied"
        every { repository.getLocalContacts() } returns flow { throw RuntimeException(errorMessage) }
        viewModel = ContactsViewModel(repository)

        viewModel.localContactsState.test {
            val loading = awaitItem()
            assertTrue(loading is ContactsUiState.Loading)
            val error = awaitItem()
            assertTrue(error is ContactsUiState.Error)
            assertEquals(errorMessage, (error as ContactsUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refresh re-fetches data`() = runTest {
        every { repository.getLocalContacts() } returns flowOf(fakeContacts)
        viewModel = ContactsViewModel(repository)

        viewModel.localContactsState.test {
            awaitItem() // Loading
            awaitItem() // Success

            viewModel.refresh()
            val loading2 = awaitItem()
            assertTrue(loading2 is ContactsUiState.Loading)
            val success2 = awaitItem()
            assertTrue(success2 is ContactsUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
