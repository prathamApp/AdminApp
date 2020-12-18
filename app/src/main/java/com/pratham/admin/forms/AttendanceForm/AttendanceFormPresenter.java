package com.pratham.admin.forms.AttendanceForm;

import android.content.Context;

import org.androidannotations.annotations.EBean;

@EBean
public class AttendanceFormPresenter implements AttendanceFormContract.AttendanceFormPresenter{

    Context context;
    AttendanceFormContract.AttendanceFormView attendanceFormView;

    public AttendanceFormPresenter(Context context){
        this.context=context;
    }
    @Override
    public void setView(AttendanceFormContract.AttendanceFormView attendanceFormView) {
        this.attendanceFormView=attendanceFormView;
    }
}
