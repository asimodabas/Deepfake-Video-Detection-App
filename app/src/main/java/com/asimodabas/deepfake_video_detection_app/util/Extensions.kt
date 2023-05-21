package com.asimodabas.deepfake_video_detection_app.util

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.asimodabas.deepfake_video_detection_app.R
import com.google.android.material.button.MaterialButton
import io.github.muddz.styleabletoast.StyleableToast
import java.util.*

fun toastMessage(mContext: Context, message: String, style: Int = R.style.myToast) {
    StyleableToast.makeText(mContext, message, style).show()
}

fun datePickerCreate(mContext: Context, clckDate: MaterialButton) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        mContext,
        { _, mYear, mMonth, mDay ->
            clckDate.text = "$mDay-${mMonth + 1}-$mYear"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.datePicker.maxDate = calendar.timeInMillis
    datePickerDialog.show()
}

fun closeKeyboard(activity: Activity, mContext: Context) {
    val view = activity.currentFocus
    if (view != null) {
        val inputMethodManager =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}