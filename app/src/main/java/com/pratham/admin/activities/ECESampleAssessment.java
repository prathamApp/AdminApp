package com.pratham.admin.activities;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.custom.RangeTimePickerDialog;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.ECEAsmt;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Modal_Log;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.DatePickerFragmentOne;
import com.pratham.admin.util.Utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ECESampleAssessment extends BaseActivity implements RangeTimePickerDialog.ISelectedTime {
    // Spinners
    @BindView(R.id.sp_State)
    Spinner sp_State;
    @BindView(R.id.sp_Block)
    Spinner sp_Block;
    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.sp_Groups)
    Spinner sp_Groups;
    @BindView(R.id.sp_existingStudent)
    Spinner sp_existingStudent;
    @BindView(R.id.sp_AsmtType)
    Spinner sp_AsmtType;
    //Basic Details
    @BindView(R.id.tv_FirstName)
    TextView tv_FirstName;
    @BindView(R.id.tv_MiddleName)
    TextView tv_MiddleName;
    @BindView(R.id.tv_LastName)
    TextView tv_LastName;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.btn_TimeRangePicker)
    Button btn_TimeRangePicker;
    // Activities
    @BindView(R.id.sp_MatchingCards)
    fr.ganfra.materialspinner.MaterialSpinner sp_MatchingCards;
    @BindView(R.id.sp_SequencingCards)
    fr.ganfra.materialspinner.MaterialSpinner sp_SequencingCards;
    @BindView(R.id.sp_NumberRecognition)
    fr.ganfra.materialspinner.MaterialSpinner sp_NumberRecognition;
    @BindView(R.id.sp_WordRecognition)
    fr.ganfra.materialspinner.MaterialSpinner sp_WordRecognition;
    // Work Sheet
    @BindView(R.id.sp_W11a)
    fr.ganfra.materialspinner.MaterialSpinner sp_W11a;
    @BindView(R.id.sp_W11b)
    fr.ganfra.materialspinner.MaterialSpinner sp_W11b;
    @BindView(R.id.sp_W12a)
    fr.ganfra.materialspinner.MaterialSpinner sp_W12a;
    @BindView(R.id.sp_W12b)
    fr.ganfra.materialspinner.MaterialSpinner sp_W12b;
    // Oral Questions
    @BindView(R.id.sp_OQ11)
    fr.ganfra.materialspinner.MaterialSpinner sp_OQ11;
    @BindView(R.id.sp_OQ12)
    fr.ganfra.materialspinner.MaterialSpinner sp_OQ12;
    @BindView(R.id.sp_OQ13)
    fr.ganfra.materialspinner.MaterialSpinner sp_OQ13;
    @BindView(R.id.sp_OQ14)
    fr.ganfra.materialspinner.MaterialSpinner sp_OQ14;
    // Submit & Clear Buttons
    @BindView(R.id.btn_Submit)
    Button btn_Submit;
    @BindView(R.id.btn_Clear)
    Button btn_Clear;

    private int vilID;
    private String startTime = "", endTime = "", GrpID = "", StudentUniqID = "", FirstName = "", MiddleName = "", LastName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecesample_assessment);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        populateAsmtSpinnerValues();
        populateAsmtSpinner();
        populateStatesSpinner();
    }

    private void populateAsmtSpinnerValues() {
        ArrayAdapter<String> asmtAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_asmt_status));
        sp_MatchingCards.setAdapter(asmtAdapter);
        sp_SequencingCards.setAdapter(asmtAdapter);
        sp_NumberRecognition.setAdapter(asmtAdapter);
        sp_WordRecognition.setAdapter(asmtAdapter);
        sp_W11a.setAdapter(asmtAdapter);
        sp_W11b.setAdapter(asmtAdapter);
        sp_W12a.setAdapter(asmtAdapter);
        sp_W12b.setAdapter(asmtAdapter);
        sp_OQ11.setAdapter(asmtAdapter);
        sp_OQ12.setAdapter(asmtAdapter);
        sp_OQ13.setAdapter(asmtAdapter);
        sp_OQ14.setAdapter(asmtAdapter);
    }

    private void populateStatesSpinner() {

        List<String> States = new ArrayList<>();
        States.clear();
        States = AppDatabase.getDatabaseInstance(ECESampleAssessment.this).getVillageDao().getState();
        States.add(0, getString(R.string.selectstate));
        ArrayAdapter<String> StateAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, States);
        sp_State.setAdapter(StateAdapter);

        sp_State.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = sp_State.getSelectedItem().toString();
                populateBlock(selectedState);
                resetFormPartially();
                tv_MiddleName.setVisibility(View.VISIBLE);
                tv_LastName.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void populateBlock(String selectedState) {
        List<String> Blocks = new ArrayList<>();
        Blocks.clear();
        Blocks = AppDatabase.getDatabaseInstance(ECESampleAssessment.this).getVillageDao().GetStatewiseBlock(selectedState);
        Blocks.add(0, getString(R.string.selectblock));
        ArrayAdapter<String> BlockAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, Blocks);
        sp_Block.setAdapter(BlockAdapter);

        sp_Block.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBlock = sp_Block.getSelectedItem().toString();
                populateVillage(selectedBlock);
                resetFormPartially();
                sp_Groups.setSelection(0);
                tv_MiddleName.setVisibility(View.VISIBLE);
                tv_LastName.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void populateVillage(String selectedBlock) {
        List<Village> BlocksVillages = new ArrayList<>();
        BlocksVillages.clear();
        Village v = new Village();
        v.VillageId = "0";
        v.VillageName = getString(R.string.selectvillage);
        BlocksVillages = AppDatabase.getDatabaseInstance(ECESampleAssessment.this).getVillageDao().GetVillages(selectedBlock);
        BlocksVillages.add(0, v);

        List<Village> SpinnerBlocksVillages = new ArrayList<>();
        for (int i = 0; i < BlocksVillages.size(); i++)
            SpinnerBlocksVillages.add(new Village(Integer.parseInt(BlocksVillages.get(i).VillageId), BlocksVillages.get(i).VillageName));

        ArrayAdapter<Village> VillagesAdapter = new ArrayAdapter<Village>(this, R.layout.custom_spinner, SpinnerBlocksVillages);
        sp_Village.setAdapter(VillagesAdapter);
        sp_Village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Village village = (Village) parent.getItemAtPosition(position);
                vilID = Integer.parseInt(village.getVillageId());
                populateGroups(vilID);
                sp_Groups.setSelection(0);
                resetFormPartially();
                tv_MiddleName.setVisibility(View.VISIBLE);
                tv_LastName.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void populateGroups(int villageID) {
        List<Groups> GroupsVillages = new ArrayList<Groups>();
        GroupsVillages.clear();
        Groups grp = new Groups();
        grp.GroupId = "0";
        grp.GroupName = getString(R.string.selectgroup);
        GroupsVillages = AppDatabase.getDatabaseInstance(ECESampleAssessment.this).getGroupDao().GetGroups(villageID);
        GroupsVillages.add(0, grp);

        List<Groups> SpinnerGroups = new ArrayList<>();
        for (int i = 0; i < GroupsVillages.size(); i++)
            SpinnerGroups.add(new Groups(GroupsVillages.get(i).GroupId, GroupsVillages.get(i).GroupName));

        final ArrayAdapter<Groups> GroupsAdapter = new ArrayAdapter<Groups>(this, R.layout.custom_spinner, SpinnerGroups);
        sp_Groups.setAdapter(GroupsAdapter);
        sp_Groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                sp_existingStudent.setSelection(0);
                tv_FirstName.setText("");
                tv_MiddleName.setText("");
                tv_MiddleName.setText("");
                btn_DatePicker.setText(R.string.selectdate);
                btn_TimeRangePicker.setText(R.string.selecttime);

                String GroupName = sp_Groups.getSelectedItem().toString();
                Groups SelectedGroupData = GroupsAdapter.getItem(sp_Groups.getSelectedItemPosition());
                GrpID = SelectedGroupData.getGroupId();
                String Id = GrpID;
                populateExistingStudents(Id);
                tv_MiddleName.setVisibility(View.VISIBLE);
                tv_LastName.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void populateExistingStudents(String GroupID) {
        List<Student> ExistingStudents = new ArrayList<>();
        ExistingStudents.clear();
        Student std = new Student();
        std.StudentId = "0";
        std.FirstName = getString(R.string.selectstudent);
        ExistingStudents = AppDatabase.getDatabaseInstance(ECESampleAssessment.this).getStudentDao().GetAllStudentsByGroupID(GroupID);
        ExistingStudents.add(0, std);

        List<Student> SpinnerStudents = new ArrayList<>();
        for (int i = 0; i < ExistingStudents.size(); i++) {
            if (ExistingStudents.get(i).FirstName != null && !ExistingStudents.get(i).FirstName.equals("")) {
                SpinnerStudents.add(new Student(ExistingStudents.get(i).StudentId, ExistingStudents.get(i).FirstName));
            } else {
                SpinnerStudents.add(new Student(ExistingStudents.get(i).StudentId, ExistingStudents.get(i).getFullName()));
            }
        }

        final ArrayAdapter<Student> ExistingStudentAdapter = new ArrayAdapter<Student>(this, R.layout.custom_spinner, SpinnerStudents);
        ExistingStudentAdapter.setDropDownViewResource(R.layout.custom_spinner);
        sp_existingStudent.setAdapter(ExistingStudentAdapter);

        sp_existingStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String StdID = sp_existingStudent.getSelectedItem().toString();
                Student SelectedStudentData = ExistingStudentAdapter.getItem(sp_existingStudent.getSelectedItemPosition());
                StudentUniqID = SelectedStudentData.getStudentId();
                FirstName = "";
                MiddleName = "";
                LastName = "";
                tv_FirstName.setText("");
                tv_MiddleName.setText("");
                tv_LastName.setText("");
                populateStudentData(StudentUniqID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void populateStudentData(String studentUniqID) {
        Student SelectedStudent = AppDatabase.getDatabaseInstance(ECESampleAssessment.this).getStudentDao().GetStudentDataByStdID(studentUniqID);

        if (SelectedStudent == null && sp_existingStudent.getSelectedItemPosition() == 0) {
        } else if (SelectedStudent == null && sp_existingStudent.getSelectedItemPosition() > 0) {
            Toast.makeText(ECESampleAssessment.this, R.string.sorrynodatafound, Toast.LENGTH_SHORT).show();
        } else {
            // Name Process
            if (SelectedStudent.FirstName == null) {
                String[] nameArray = SelectedStudent.FullName.split(" ");
                if (nameArray.length == 1) {
                    FirstName = nameArray[0];
                    MiddleName = "";
                    LastName = "";
                } else if (nameArray.length > 2) {
                    FirstName = nameArray[0];
                    MiddleName = nameArray[1];
                    LastName = nameArray[2];
                } else {
                    FirstName = nameArray[0];
                    LastName = nameArray[1];
                    MiddleName = "";
                }
            } else {
                FirstName = SelectedStudent.FullName;
                MiddleName = SelectedStudent.MiddleName;
                LastName = SelectedStudent.LastName;
            }

            tv_MiddleName.setVisibility(View.VISIBLE);
            tv_LastName.setVisibility(View.VISIBLE);
            tv_FirstName.setText("First Name : " + FirstName);
            tv_MiddleName.setText("Middle Name : " + MiddleName);
            tv_LastName.setText("Last Name : " + LastName);

            if (SelectedStudent.FullName.contains(" ")) {
                tv_MiddleName.setVisibility(View.GONE);
                tv_LastName.setVisibility(View.GONE);
                tv_FirstName.setText("Student Name : " + SelectedStudent.FullName);
            }
            populateAsmtSpinner();
        }
    }

    private void populateAsmtSpinner() {
        ArrayAdapter<String> asmtAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_asmtType));
        sp_AsmtType.setAdapter(asmtAdapter);

        sp_AsmtType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean recordExists = AppDatabase.getDatabaseInstance(ECESampleAssessment.this).getECEAsmtDao().CheckDataExists(StudentUniqID, position);
                if (recordExists) {
                    // populate data
                    List<ECEAsmt> eceAsmts = AppDatabase.getDatabaseInstance(ECESampleAssessment.this).getECEAsmtDao().GetAsmtDataByStudentID(StudentUniqID, position);
                    btn_DatePicker.setText(eceAsmts.get(0).Date);

                    // convert & Show time
                    String startT = getReadableTime(eceAsmts.get(0).StartTime);
                    String endT = getReadableTime(eceAsmts.get(0).EndTime);
                    btn_TimeRangePicker.setText("From : " + startT + " | To : " + endT);

                    sp_MatchingCards.setSelection(eceAsmts.get(0).ActMatchingCards);
                    sp_SequencingCards.setSelection(eceAsmts.get(0).ActSequencingCards);
                    sp_NumberRecognition.setSelection(eceAsmts.get(0).ActNumberReco);
                    sp_WordRecognition.setSelection(eceAsmts.get(0).ActWordReco);
                    sp_W11a.setSelection(eceAsmts.get(0).WS11a);
                    sp_W11b.setSelection(eceAsmts.get(0).WS11b);
                    sp_W12a.setSelection(eceAsmts.get(0).WS12a);
                    sp_W12b.setSelection(eceAsmts.get(0).WS12b);
                    sp_OQ11.setSelection(eceAsmts.get(0).OQ11);
                    sp_OQ12.setSelection(eceAsmts.get(0).OQ12);
                    sp_OQ13.setSelection(eceAsmts.get(0).OQ13);
                    sp_OQ14.setSelection(eceAsmts.get(0).OQ14);
                } else {
                    // reset spinners
                    populateAsmtSpinnerValues();
                    btn_DatePicker.setText(R.string.selectdate);
                    btn_TimeRangePicker.setText(R.string.selecttime);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private String getReadableTime(String startTime) {
        String sT = startTime;
        DateFormat df = new SimpleDateFormat("HH:mm");
        DateFormat outputformat = new SimpleDateFormat("hh:mm a");
        Date sdate = null;
        String startoutput = null;
        try {
            sdate = df.parse(sT);
            return startoutput = outputformat.format(sdate);
        } catch (Exception e) {
            e.printStackTrace();
            return "errpr";
        }
    }

    @OnClick(R.id.btn_Clear)
    public void reset(View view) {
//        populateEmptyAsmtTypeSpinner();
        sp_State.setSelection(0);
        sp_Block.setSelection(0);
        sp_Village.setSelection(0);
        sp_Groups.setSelection(0);
        sp_existingStudent.setSelection(0);
        sp_AsmtType.setSelection(0);
        tv_FirstName.setText("");
        tv_MiddleName.setText("");
        tv_LastName.setText("");
        btn_DatePicker.setText(R.string.selectdate);
        btn_TimeRangePicker.setText(R.string.selecttime);
        sp_MatchingCards.setSelection(0);
        sp_SequencingCards.setSelection(0);
        sp_NumberRecognition.setSelection(0);
        sp_WordRecognition.setSelection(0);
        sp_W11a.setSelection(0);
        sp_W11b.setSelection(0);
        sp_W12a.setSelection(0);
        sp_W12b.setSelection(0);
        sp_OQ11.setSelection(0);
        sp_OQ12.setSelection(0);
        sp_OQ13.setSelection(0);
        sp_OQ14.setSelection(0);
    }

    private void resetFormPartially() {
        sp_AsmtType.setSelection(0);
        btn_DatePicker.setText(R.string.selectdate);
        btn_TimeRangePicker.setText(R.string.selecttime);
        sp_MatchingCards.setSelection(0);
        sp_SequencingCards.setSelection(0);
        sp_NumberRecognition.setSelection(0);
        sp_WordRecognition.setSelection(0);
        sp_W11a.setSelection(0);
        sp_W11b.setSelection(0);
        sp_W12a.setSelection(0);
        sp_W12b.setSelection(0);
        sp_OQ11.setSelection(0);
        sp_OQ12.setSelection(0);
        sp_OQ13.setSelection(0);
        sp_OQ14.setSelection(0);
    }

    @OnClick(R.id.btn_DatePicker)
    public void visitDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentOne();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

    @OnClick(R.id.btn_TimeRangePicker)
    public void TimeRangePicker(View view) {
        // Create an instance of the dialog fragment and show it
        RangeTimePickerDialog dialog = new RangeTimePickerDialog();
        dialog.newInstance();
        dialog.setIs24HourView(false);
        dialog.setRadiusDialog(16);
        dialog.setTextTabStart(getString(R.string.start));
        dialog.setTextTabEnd(getString(R.string.end));
        dialog.setTextBtnPositive(getString(R.string.accept));
        dialog.setTextBtnNegative(getString(R.string.close));
        dialog.setValidateRange(true);
        dialog.setColorBackgroundHeader(R.color.colorPrimary); // top header background
        dialog.setColorBackgroundTimePickerHeader(R.color.colorPrimary); // Digital Time Backgrounds
        dialog.setColorTextButton(R.color.colorPrimaryDark);
        FragmentManager fragmentManager = getFragmentManager();
        dialog.show(fragmentManager, "");
    }

    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {
        startTime = hourStart + ":" + minuteStart;
        endTime = hourEnd + ":" + minuteEnd;
        String sT = startTime;
        String eT = endTime;
        DateFormat df = new SimpleDateFormat("HH:mm");
        DateFormat outputformat = new SimpleDateFormat("hh:mm a");
        Date sdate = null;
        Date edate = null;
        String startoutput = null;
        String endoutput = null;
        try {
            sdate = df.parse(sT);
            startoutput = outputformat.format(sdate);
            edate = df.parse(eT);
            endoutput = outputformat.format(edate);
            btn_TimeRangePicker.setText("From : " + startoutput + " | " + "\tTo : " + endoutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Data is filled correctly
    public boolean isValid() {
        if (sp_State.getSelectedItemPosition() > 0 && sp_Block.getSelectedItemPosition() > 0 && sp_Village.getSelectedItemPosition() > 0
                && sp_Groups.getSelectedItemPosition() > 0 && sp_existingStudent.getSelectedItemPosition() > 0 && sp_AsmtType.getSelectedItemPosition() > 0
                && !btn_DatePicker.getText().toString().contains("Date") && !btn_TimeRangePicker.getText().toString().contains("Time")
                && sp_MatchingCards.getSelectedItemPosition() > 0 && sp_SequencingCards.getSelectedItemPosition() > 0
                && sp_NumberRecognition.getSelectedItemPosition() > 0 && sp_WordRecognition.getSelectedItemPosition() > 0
                && sp_W11a.getSelectedItemPosition() > 0 && sp_W11b.getSelectedItemPosition() > 0 && sp_W12a.getSelectedItemPosition() > 0 && sp_W12b.getSelectedItemPosition() > 0
                && sp_OQ11.getSelectedItemPosition() > 0 && sp_OQ12.getSelectedItemPosition() > 0 && sp_OQ13.getSelectedItemPosition() > 0 && sp_OQ14.getSelectedItemPosition() > 0)
            return true;
        else
            return false;
    }

    @OnClick(R.id.btn_Submit)
    public void save() {
        if (isValid()) {
            boolean recordExists = AppDatabase.getDatabaseInstance(ECESampleAssessment.this).getECEAsmtDao().CheckDataExists(StudentUniqID, sp_AsmtType.getSelectedItemPosition());

            try {
                if (!recordExists) {
                    ECEAsmt eceAsmtObj = new ECEAsmt();
                    eceAsmtObj.StudentId = StudentUniqID;
                    eceAsmtObj.AsmtType = sp_AsmtType.getSelectedItemPosition();
                    eceAsmtObj.Date = btn_DatePicker.getText().toString();
                    eceAsmtObj.StartTime = startTime;
                    eceAsmtObj.EndTime = endTime;
                    eceAsmtObj.ActMatchingCards = sp_MatchingCards.getSelectedItemPosition();
                    eceAsmtObj.ActSequencingCards = sp_SequencingCards.getSelectedItemPosition();
                    eceAsmtObj.ActNumberReco = sp_NumberRecognition.getSelectedItemPosition();
                    eceAsmtObj.ActWordReco = sp_WordRecognition.getSelectedItemPosition();
                    eceAsmtObj.WS11a = sp_W11a.getSelectedItemPosition();
                    eceAsmtObj.WS11b = sp_W11b.getSelectedItemPosition();
                    eceAsmtObj.WS12a = sp_W12a.getSelectedItemPosition();
                    eceAsmtObj.WS12b = sp_W12b.getSelectedItemPosition();
                    eceAsmtObj.OQ11 = sp_OQ11.getSelectedItemPosition();
                    eceAsmtObj.OQ12 = sp_OQ12.getSelectedItemPosition();
                    eceAsmtObj.OQ13 = sp_OQ13.getSelectedItemPosition();
                    eceAsmtObj.OQ14 = sp_OQ14.getSelectedItemPosition();
                    eceAsmtObj.sentFlag = 0;

                    AppDatabase.getDatabaseInstance(this).getECEAsmtDao().insertECEAsmt(eceAsmtObj);
                    Toast.makeText(this, R.string.recordsavedtodb, Toast.LENGTH_SHORT).show();

                } else {
                    AppDatabase.getDatabaseInstance(this).getECEAsmtDao().UpdateECEAsmtData(btn_DatePicker.getText().toString(), startTime, endTime,
                            sp_MatchingCards.getSelectedItemPosition(), sp_SequencingCards.getSelectedItemPosition(), sp_NumberRecognition.getSelectedItemPosition(), sp_WordRecognition.getSelectedItemPosition(),
                            sp_W11a.getSelectedItemPosition(), sp_W11b.getSelectedItemPosition(), sp_W12a.getSelectedItemPosition(), sp_W12b.getSelectedItemPosition(),
                            sp_OQ11.getSelectedItemPosition(), sp_OQ12.getSelectedItemPosition(), sp_OQ13.getSelectedItemPosition(), sp_OQ14.getSelectedItemPosition(), 0,
                            StudentUniqID, sp_AsmtType.getSelectedItemPosition());
                    Toast.makeText(this, R.string.recordupdatetodb, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Modal_Log log = new Modal_Log();
                log.setCurrentDateTime(new Utility().GetCurrentDate());
                log.setErrorType("ERROR");
                log.setExceptionMessage(e.getMessage());
                log.setExceptionStackTrace(e.getStackTrace().toString());
                log.setMethodName("ECESampleAssessment" + "_" + "Save");
                log.setDeviceId("");
                AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                BackupDatabase.backup(ApplicationController.getInstance());

                e.printStackTrace();
            }
            resetFormPartially();
        } else {
            Toast.makeText(this, R.string.fillAllDetails, Toast.LENGTH_SHORT).show();
        }
    }
}
