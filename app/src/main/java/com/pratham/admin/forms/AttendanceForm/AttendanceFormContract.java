package com.pratham.admin.forms.AttendanceForm;

public interface AttendanceFormContract {

    interface AttendanceFormView{

    }

    interface AttendanceFormPresenter{
        void setView(AttendanceFormContract.AttendanceFormView attendanceFormView);
    }
}
