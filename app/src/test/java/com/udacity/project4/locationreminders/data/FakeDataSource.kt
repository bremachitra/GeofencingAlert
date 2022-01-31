package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source

    private var returnError = false

    fun setReturnError(shouldReturn: Boolean) {
        this.returnError = shouldReturn
    }
    fun shouldReturnError(){
        returnError = true
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (returnError){
            return Result.Error("Reminders not found", 404)
        }else{
            return return Result.Success(ArrayList(reminders))
        }

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {

        if(returnError){

            return com.udacity.project4.locationreminders.data.dto.Result.Error("Error")

        }else{

            val reminder = reminders.find { it.id == id }

            if (reminder != null) {
                return com.udacity.project4.locationreminders.data.dto.Result.Success(reminder)
            } else {
                return com.udacity.project4.locationreminders.data.dto.Result.Error("Reminder not found", 404)
            }

        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }


}