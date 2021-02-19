package com.pratham.admin.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import com.pratham.admin.R;
import com.pratham.admin.modalclasses.EventMessageGreenRobot;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

public class BirthDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    int year;
    int month;
    int day;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final Calendar c = Calendar.getInstance();

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        return dialog;

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthofyear, int dayOfMonth) {
        this.year = year;
        this.month = monthofyear + 1;
        this.day = dayOfMonth;
        String date;
        Button btn = (Button) getActivity().findViewById(R.id.btn_BirthDatePicker);

        if (this.month < 10 && this.day < 10) {
            date = "0" + dayOfMonth + "-0" + month + "-" + year;
            if (btn != null)
                btn.setText(date);
            EventMessageGreenRobot eventMessage = new EventMessageGreenRobot();
            eventMessage.setMessage(date);
            eventMessage.setId("BIRTHDATE");
            EventBus.getDefault().post(eventMessage);
        } else if (this.month < 10) {
            date = "" + dayOfMonth + "-0" + month + "-" + year;
            if (btn != null)
                btn.setText(date);
            EventMessageGreenRobot eventMessage = new EventMessageGreenRobot();
            eventMessage.setMessage(date);
            eventMessage.setId("BIRTHDATE");
            EventBus.getDefault().post(eventMessage);
        } else if (this.day < 10) {
            date = "0" + dayOfMonth + "-" + month + "-" + year;
            if (btn != null)
                btn.setText(date);
            EventMessageGreenRobot eventMessage = new EventMessageGreenRobot();
            eventMessage.setMessage(date);
            eventMessage.setId("BIRTHDATE");
            EventBus.getDefault().post(eventMessage);
        } else {
            date="" + dayOfMonth + "-" + month + "-" + year;
            if (btn != null)
                btn.setText(date);

            EventMessageGreenRobot eventMessage = new EventMessageGreenRobot();
            eventMessage.setMessage(date);
            eventMessage.setId("BIRTHDATE");
            EventBus.getDefault().post(eventMessage);
        }
    }
}
