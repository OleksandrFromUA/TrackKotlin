package com.example.map

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class MyDialogCalendar: DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val SelectedDay = "date_Key"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        return DatePickerDialog(requireActivity(), this, year, month, day)

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDay = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }
        val selectedDayInMill = selectedDay.timeInMillis

        val result = Bundle().apply {
            putLong(SelectedDay, selectedDayInMill)
        }
        parentFragmentManager.setFragmentResult(SelectedDay, result)
    }
    }
