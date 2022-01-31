package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    private fun getReminder(): ReminderDTO {
        return ReminderDTO(
            title = "title",
            description = "desc",
            location = "loc",
            latitude = 47.5456551,
            longitude = 122.0101731)
    }
    @Test
    fun insertReminder_Reminder_checkwithDB() = runBlockingTest {
        // GIVEN -
        val reminder = getReminder()
        database.reminderDao().saveReminder(reminder)

        // WHEN -
        val addedReminder = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values

        assertThat<ReminderDTO>(addedReminder as ReminderDTO, notNullValue())
        assertThat(addedReminder.id, `is`(reminder.id))
        assertThat(addedReminder.title, `is`(reminder.title))
        assertThat(addedReminder.description, `is`(reminder.description))
        assertThat(addedReminder.latitude, `is`(reminder.latitude))
        assertThat(addedReminder.longitude, `is`(reminder.longitude))
        assertThat(addedReminder.location, `is`(reminder.location))

    }

}