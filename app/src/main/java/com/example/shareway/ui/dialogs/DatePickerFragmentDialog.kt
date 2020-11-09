//package com.example.shareway.ui.dialogs
//
//import android.app.DatePickerDialog
//import android.app.Dialog
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.DialogFragment
//import com.example.shareway.ui.ArticlesFragment
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.InternalCoroutinesApi
//import java.text.SimpleDateFormat
//import java.util.*
//import kotlin.collections.ArrayList
//
//class DatePickerFragmentDialog : DialogFragment() {
//
//    private var selectedDate: String? = null
//
//
//    companion object {
//
//        const val TAG = "datePicker"
//        private const val SELECTED_DATE = "selectedDate"
//
//        fun newInstance(selectedDate: String?) {
//            DatePickerFragmentDialog().apply {
//                arguments = Bundle().apply {
//                    putString(SELECTED_DATE, selectedDate)
//                }
//            }
//        }
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            selectedDate = it.getString(SELECTED_DATE)
//        }
//    }
//
//    @ExperimentalCoroutinesApi
//    @InternalCoroutinesApi
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//
//        // Use the current date as the default date in the picker
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//        val datePickerDialog = DatePickerDialog(requireActivity(), parentFragment as ArticlesFragment, year, month, day)
//        val datePicker = datePickerDialog.datePicker
//        val oneMonthAgo = calendar.clone() as Calendar
//
//        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS")
//
//        Log.i("DatePickerFragmentDayOf", "max form ${formatter.format(calendar.timeInMillis)}") //2020-05-05
//        Log.i("DatePickerFragmentDayOf", "min form ${formatter.format(oneMonthAgo.timeInMillis)}") //2020-05-05
//
//        datePicker.maxDate = calendar.timeInMillis
//
//
//        datePicker.minDate = oneMonthAgo.timeInMillis
//
//        // Create a new instance of DatePickerDialog and return it
//        return datePickerDialog
//    }
//
//
//}