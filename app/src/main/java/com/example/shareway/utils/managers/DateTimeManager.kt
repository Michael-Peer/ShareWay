package com.example.shareway.utils.managers

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.shareway.listeners.ReminderListeners
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

object DateTimeManager {

    private lateinit var context: Context
    private lateinit var listener: ReminderListeners

    private const val TAG = "DateTimeManager"

    fun launchDateTimeDialogs(activityContext: Context, reminderListener: ReminderListeners) {
        context = activityContext
        listener = reminderListener
        val datePicker = getDatePickerDialog()
        Log.d(TAG, "onSetReminderClicked: $datePicker")

        datePicker.show(
            (activityContext as AppCompatActivity).supportFragmentManager,
            datePicker.toString()
        )

        handleDatePickerOnPositiveButtonClicked(datePicker)
        handleDatePickerOnCancelOrNegativeButtonClicked(datePicker)
    }

    /**
     *
     *
     * DatePicker Handlers
     *
     * **/


    private fun handleDatePickerOnCancelOrNegativeButtonClicked(datePicker: MaterialDatePicker<Long>) {
        datePicker.addOnCancelListener {
//            uiCommunicationListener.onResponseReceived("cancel", UIComponentType.Toast)

        }
        datePicker.addOnNegativeButtonClickListener {
//            uiCommunicationListener.onResponseReceived("negative", UIComponentType.Toast)

        }
    }

    private fun handleDatePickerOnPositiveButtonClicked(datePicker: MaterialDatePicker<Long>) {
        /**
         *
         * The picker interprets all long values as milliseconds from the UTC Epoch.
         * We can use Instant.ofEpochMilli(dateSelected) and LocalDateTime.ofInstant(...) otherwise Calendar.setTimeInMillis(dateSelected).
         * Attention to LocalDateTime.ofEpochSecond; it works with seconds and not milliseconds
         *
         * **/


        datePicker.addOnPositiveButtonClickListener {
            Log.d(TAG, "handleDatePickerOnPositiveButtonClicked: ")
            val timePicker = getTimePickerDialog()
            timePicker.show(
                (context as AppCompatActivity).supportFragmentManager,
                "tag"
            )

            Log.d(
                TAG,
                "handleDatePickerOnPositiveButtonClicked: SELECTION: ${datePicker.selection}"
            )
            Log.d(TAG, "handleDatePickerOnPositiveButtonClicked: SELECTION: $it")
            Log.d(
                TAG,
                "handleDatePickerOnPositiveButtonClicked: SELECTION: ${Instant.ofEpochMilli(it)}"
            )
            Log.d(TAG, "handleDatePickerOnPositiveButtonClicked: SELECTION: ")



            handleTimePickerOnPositiveButtonClicked(timePicker, datePicker, datePicker.headerText)
            handleTimePickerOnCancelOrNegativeButtonClicked(timePicker)


            //TODO: Handle time dismiss. set the reminder time to the current time.
            //TODO: show toast with the time + date

        }
    }

    private fun handleTimePickerOnCancelOrNegativeButtonClicked(timePicker: MaterialTimePicker) {

    }

    private fun handleTimePickerOnPositiveButtonClicked(
        timePicker: MaterialTimePicker,
        datePicker: MaterialDatePicker<Long>,
        headerText: String

    ) {
        timePicker.addOnPositiveButtonClickListener {
            Log.d(TAG, "handleTimePickerOnPositiveButtonClicked: $it")
            Log.d(TAG, "handleTimePickerOnPositiveButtonClicked: ${timePicker.inputMode}")
            Log.d(
                TAG,
                "handleTimePickerOnPositiveButtonClicked: ${timePicker.minute.javaClass.name}"
            )
            Log.d(TAG, "handleTimePickerOnPositiveButtonClicked: ${timePicker.hour}")

            Log.d(TAG, "handleTimePickerOnPositiveButtonClicked: ${datePicker.selection}")

            val reminder = Instant.ofEpochMilli(datePicker.selection!!)
                .plus(timePicker.hour.toLong(), ChronoUnit.HOURS)
                .plus(timePicker.minute.toLong(), ChronoUnit.MINUTES)


            listener.onSuccess(
                reminder,
                timePicker.hour,
                timePicker.minute,
                getDayFromInstant (reminder)
            )


            /**
             *
             * TODO: Cancel when reminder end. implement funcion what happen when reminder done(mayble check on reycler launch if there is active reminder will be a better solution)
             *
             * **/

        }
    }

    private fun getDayFromInstant(instant: Instant): Int =
        instant.atZone(ZoneId.systemDefault()).dayOfMonth


    /**
     *
     * Get DatePicker & TimePicker Dialogs
     *
     * **/

    private fun getTimePickerDialog(): MaterialTimePicker {
        val calendar = Calendar.getInstance(Locale.getDefault())
        return MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .build()
    }

    private fun getDatePickerDialog(): MaterialDatePicker<Long> {
        val calendar = Calendar.getInstance(Locale.getDefault())

        /**
         *
         *
         *  Depending on the type of MaterialDatePicker you have built, we can handle this in one of two ways.
         *  For a DatePicker, the selected value would be passed a unix epoch time value in a Long variable that is accessible as an argument in the PositiveButtonClickedListener lambda.
         *  it can also be read as a string in the header text of the Picker( Jan 10, Jul 28 etc)
         *
         *
         * **/
        val buildDatePicker = MaterialDatePicker.Builder.datePicker()
        val constraintBuilder = CalendarConstraints.Builder()
        val dateValidator: CalendarConstraints.DateValidator = DateValidatorPointForward.now()
        buildDatePicker.setTitleText("Select Reminder Date")
        constraintBuilder.setValidator(dateValidator)
        buildDatePicker.setCalendarConstraints(constraintBuilder.build())
        buildDatePicker.setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        return buildDatePicker.build()
    }

}