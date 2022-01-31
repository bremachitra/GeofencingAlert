package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.collection.IsEmptyCollection
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // Executes each task synchronously using Architecture Components.Must for livedata testing
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    //subject under test
    private lateinit var viewModel: RemindersListViewModel
    private lateinit var  fakeDataSource : FakeDataSource



    @Before
    fun initializeViewModel(){
        stopKoin()
        fakeDataSource = FakeDataSource()
        //initializing viewModel
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),fakeDataSource)

    }


    @Test
    fun loadReminder_emptyList_liveDataReminderListEmpty() = mainCoroutineRule.runBlockingTest{
        //given no data in the list @Before

        //when loading reminder for ui
        viewModel.loadReminders()

        val list = viewModel.remindersList.getOrAwaitValue()
        //then list should be null
        MatcherAssert.assertThat(list, IsEmptyCollection.empty())

    }

    //testing for load reminder function and and live data reminderList
    @Test
    fun loadReminders_reminderListWithThreeReminders_LiveDataListIsNotEmpty () = mainCoroutineRule.runBlockingTest{
//initializing list
        val reminder1 = ReminderDTO("reminder1","description1","location1",10.0,20.2)
        val reminder2 = ReminderDTO("reminder2","description2","location2",9.0,30.2)
        val reminder3 = ReminderDTO("reminder3","description3","location3",15.0,29.233)
        val reminderList = mutableListOf(reminder1,reminder2,reminder3)
        fakeDataSource = FakeDataSource(reminderList)
        //given data source with data
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),fakeDataSource)
//when loading reminder for ui
        viewModel.loadReminders()
        val list= viewModel.remindersList.getOrAwaitValue()
        //then list should not be null
        MatcherAssert.assertThat(list, Matchers.not(IsEmptyCollection.empty()))
    }


    @Test
    fun loadReminders_emptyList_showNoDataIsTrue()= mainCoroutineRule.runBlockingTest{
        //given viewModel @Before

        //when loading reminder for ui
        viewModel.loadReminders()

        val value = viewModel.showNoData.getOrAwaitValue()
        //then show No data is true
        Assert.assertTrue(value)
    }


    @Test
    fun loadReminders_hasErrors_showExceptionMessage() = mainCoroutineRule.runBlockingTest{

        //given viewModel
        fakeDataSource.shouldReturnError()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),fakeDataSource)

        //when returned result has error
        viewModel.loadReminders()
        val message = viewModel.showSnackBar.getOrAwaitValue()

        //then display result with error message which is displayed via showSnackBar
        MatcherAssert.assertThat(message, Matchers.`is`("Reminders not found"))


    }

    @Test
    fun check_Loading() = mainCoroutineRule.runBlockingTest{

        val reminder1 = ReminderDTO("reminder1","description1","location1",10.0,20.2)
        val reminder2 = ReminderDTO("reminder2","description2","location2",9.0,30.2)
        val reminder3 = ReminderDTO("reminder3","description3","location3",15.0,29.233)
        val reminderList = mutableListOf(reminder1,reminder2,reminder3)

        fakeDataSource = FakeDataSource(reminderList)
        //given viewModel  with data
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),fakeDataSource)

        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        //when loaded with data

        //then verify that showLoading is visible which let user know that data is loading
        MatcherAssert.assertThat(viewModel.showLoading.getOrAwaitValue(), Matchers.`is`(true))
        //resuming dispatcher
        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(viewModel.showLoading.getOrAwaitValue(), Matchers.`is`(false))
    }

}