package com.pratham.admin.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Aser;
import com.pratham.admin.modalclasses.EventMessageGreenRobot;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Modal_Log;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.BirthDatePickerFragment;
import com.pratham.admin.util.DatePickerFragment;
import com.pratham.admin.util.Utility;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditStudent extends BaseActivity/* implements ConnectionReceiverListener */ {

    private static final int TAKE_Thumbnail = 1;
    private static String TAG = "PermissionDemo";
    public boolean EndlineButtonClicked = false;
    Spinner blocks_spinner, villages_spinner, groups_spinner, existingStudent_Spinner, phone_type, relation_with_phone_owner;
    TextView edt_Fname, edt_Mname, edt_Lname, edt_Age, tv_Gender;
    EditText edt_moNumber;
    Button btn_Baseline_Submit, btn_Clear, btn_Capture;
    String GrpID;
    List<String> Blocks = new ArrayList<>();
    int vilID;
    Utility Util;
    List<Student> ExistingStudents = new ArrayList<>();
    String FirstName;
    String MiddleName;
    String LastName;
    int Age;
    int Class;
    String Gender;
    ImageView imgView;
    String StudentUniqID;
    String createdBy = "";
    Spinner sp_BaselineLang, sp_NumberReco, sp_English;
    Button btn_EndlineDatePicker, btn_DatePicker, btn_Endline1, btn_Endline2, btn_Endline3, btn_Endline4, btn_BirthDatePicker;
    LinearLayout AserForm;
    int testT = 0, langSpin, numSpin, engSpin;
    int OA = 0;
    int OS = 0;
    int OM = 0;
    int OD = 0;
    int WA = 0;
    int WS = 0;
    int IC = 0;
    String AserTestDate;

    List<Aser> AserData;
    String aserDate;
    List<Groups> GroupsVillages = new ArrayList<Groups>();
    private boolean captureButtonPressed = false;

    int engMeaning = 0;
    AlertDialog meaningDialog;
    CharSequence[] values = {" Yes", "No"};

    Spinner sp_Class;
    EditText edt_GuardianName;
    RadioGroup rg_SchoolType, rg_Gender;
    RadioButton selectedSchoolType, rb_Govt, rb_Private, rb_Male, rb_Female;
    private String guardian = "";

    @Subscribe
    public void onEvent(String msg) {
        if (!msg.isEmpty()) {
            btn_EndlineDatePicker.setText(msg);
        }
    }

    @Subscribe
    public void onEvent(EventMessageGreenRobot eventMessageGreenRobot) {
        if (eventMessageGreenRobot.getId().equalsIgnoreCase("BIRTHDATE")) {
            btn_BirthDatePicker.setText(eventMessageGreenRobot.getMessage());
            String DOB = btn_BirthDatePicker.getText().toString();
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            String stdAge = "-1";
            try {
                cal.setTime(sdf.parse(btn_BirthDatePicker.getText().toString()));
                stdAge = "" + Integer.parseInt(Integer.toString(calculateAge(cal.getTimeInMillis())));
            } catch (ParseException e) {
                Toast.makeText(EditStudent.this, "something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Student student = AppDatabase.getDatabaseInstance(EditStudent.this).getStudentDao().GetStudentDataByStdID(StudentUniqID);
                stdAge = student.Age;
            }
            edt_Age.setText("Age : " + stdAge);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);
        getSupportActionBar().hide();

        //EventBus.getDefault().register(EditStudent.this);

        initializeVariables();
        initializeClassSpinner();
        initializeState();
        initializeBaselineSpinner();
        initializeNumberRecoSpinner();
        initializeAserDate();
        initializeEnglishSpinner();
        initializeStudBirthDate();
        initializePhoneNumberSpinner();
        sp_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                // if Word or Sentence selected & dont popup show if fetched from db
                if ((pos == 4 || pos == 5) && !(engMeaning == 1 || engMeaning == 2)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditStudent.this);
                    builder.setTitle("Meaning : ");
                    // check meaning & set checked item accordingly
                    int checkedItemValue = 1;
                    if (engMeaning == 0 || engMeaning == 2)
                        checkedItemValue = 1;
                    else
                        checkedItemValue = 0;
                    builder.setSingleChoiceItems(values, checkedItemValue, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0:
                                    engMeaning = 1;
                                    break;
                                case 1:
                                    engMeaning = 2;
                                    break;
                            }
//                            meaningDialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sp_English.setSelection(0);
                            engMeaning = 0;
                            dialog.dismiss();
                        }
                    });

                    builder.setCancelable(false);
                    meaningDialog = builder.create();
                    meaningDialog.setCanceledOnTouchOutside(false);
                    meaningDialog.setCancelable(false);
                    meaningDialog.show();
                } else {
                    engMeaning = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        btn_Endline1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                setDefaults();

                // initialize dialog
                final Dialog endlineDialog = new Dialog(EditStudent.this);
                endlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                endlineDialog.setContentView(R.layout.fragment_endline_dialog);
                endlineDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                // initialize dialog's widgets
                TextView title = endlineDialog.findViewById(R.id.tv_EndlineTitle);
                final Spinner spinner_BaselineLang = endlineDialog.findViewById(R.id.spinner_BaselineLang);
                final Spinner spinner_NumberReco = endlineDialog.findViewById(R.id.spinner_NumberReco);
                final Spinner spinner_English = endlineDialog.findViewById(R.id.spinner_Engish);
                final RadioGroup rg_Meaning = endlineDialog.findViewById(R.id.rg_EngSelected);
                final RadioButton rb_Yes = endlineDialog.findViewById(R.id.rb_Yes);
                final RadioButton rb_No = endlineDialog.findViewById(R.id.rb_No);
                final CheckBox OprAdd = endlineDialog.findViewById(R.id.OprAdd);
                final CheckBox OprSub = endlineDialog.findViewById(R.id.OprSub);
                final CheckBox OprMul = endlineDialog.findViewById(R.id.OprMul);
                final CheckBox OprDiv = endlineDialog.findViewById(R.id.OprDiv);
                final TextView tv_WordProblem = endlineDialog.findViewById(R.id.tv_WordProblem);
                final CheckBox WordAdd = endlineDialog.findViewById(R.id.WordAdd);
                final CheckBox WordSub = endlineDialog.findViewById(R.id.WordSub);
                Button btn_Submit_endline1 = endlineDialog.findViewById(R.id.btn_Submit);
                btn_EndlineDatePicker = endlineDialog.findViewById(R.id.btn_EndlineDatePicker);
                btn_EndlineDatePicker.setText(Util.GetCurrentDate().toString());
                btn_EndlineDatePicker.setPadding(8, 8, 8, 8);
                btn_EndlineDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getFragmentManager(), "EndlineDatePicker");
                    }
                });

                // set values of endline
                title.setText("Endline 1");

                String[] baselineLangAdapter = {"Language", "Beg", "Letter", "Word", "Para", "Story"};
                ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(EditStudent.this, R.layout.custom_spinner, baselineLangAdapter);
                spinner_BaselineLang.setAdapter(baselineAdapter);

                String[] NumberRecoAdapter = {"Number Recognition", "Beg", "0-9", "10-99", "Sub", "Div"};
                ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(EditStudent.this, R.layout.custom_spinner, NumberRecoAdapter);
                spinner_NumberReco.setAdapter(recoAdapter);

                String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
                ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(EditStudent.this, R.layout.custom_spinner, engAdapter);
                spinner_English.setAdapter(EnglishAdapter);

                // show dialog
                endlineDialog.setCanceledOnTouchOutside(false);
                endlineDialog.show();

                // get Aser Data for Endline 1
                AserData = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().GetAllByStudentID(StudentUniqID, 1);
                if (AserData == null || AserData.size() == 0) {
                    setDefaults();
                    testT = 1;
                    btn_EndlineDatePicker.setText(Util.GetCurrentDate().toString());
                    spinner_BaselineLang.setSelection(0);
                    spinner_NumberReco.setSelection(0);
                    spinner_English.setSelection(0);

                    spinner_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            // if Word or Sentence selected
                            if (pos == 4 || pos == 5)
                                rg_Meaning.setVisibility(View.VISIBLE);
                            else
                                rg_Meaning.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                } else {
                    // fetch endline 1 aser
                    engSpin = AserData.get(0).English;
                    engMeaning = AserData.get(0).EnglishSelected;
                    testT = AserData.get(0).TestType;
                    langSpin = AserData.get(0).Lang;
                    numSpin = AserData.get(0).Num;
                    engSpin = AserData.get(0).English;
                    aserDate = AserData.get(0).TestDate;
                    OA = AserData.get(0).OAdd;
                    OS = AserData.get(0).OSub;
                    OM = AserData.get(0).OMul;
                    OD = AserData.get(0).ODiv;
                    WA = AserData.get(0).WAdd;
                    WS = AserData.get(0).WSub;

                    spinner_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            // if Word or Sentence selected
                            if (pos == 4 || pos == 5) {
                                rg_Meaning.setVisibility(View.VISIBLE);
                                if (engMeaning == 1)
                                    rb_Yes.setChecked(true);
                                else
                                    rb_No.setChecked(true);
                            } else {
                                rg_Meaning.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                    // set endline 1 aser
                    spinner_English.setSelection(engSpin);
                    btn_EndlineDatePicker.setText(aserDate);
                    spinner_BaselineLang.setSelection(langSpin);
                    spinner_NumberReco.setSelection(numSpin);

                    if (OA == 1) {
                        OprAdd.setChecked(true);
                    } else {
                        OprAdd.setChecked(false);
                    }
                    if (OS == 1) {
                        OprSub.setChecked(true);
                    } else {
                        OprSub.setChecked(false);
                    }
                    if (OM == 1) {
                        OprMul.setChecked(true);
                    } else {
                        OprMul.setChecked(false);
                    }
                    if (OD == 1) {
                        OprDiv.setChecked(true);
                    } else {
                        OprDiv.setChecked(false);
                    }
                    if (WA == 1) {
                        tv_WordProblem.setVisibility(View.VISIBLE);
                        WordAdd.setChecked(true);
                    } else {
                        WordAdd.setChecked(false);
                    }
                    if (WS == 1) {
                        tv_WordProblem.setVisibility(View.VISIBLE);
                        WordSub.setChecked(true);
                    } else {
                        WordSub.setChecked(false);
                    }
                }

                if (!OprAdd.isChecked() && !OprSub.isChecked()) {
                    tv_WordProblem.setVisibility(View.GONE);
                    WordAdd.setVisibility(View.GONE);
                    WordSub.setVisibility(View.GONE);
                }

                OprAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (OprAdd.isChecked()) {
                            tv_WordProblem.setVisibility(View.VISIBLE);
                            WordAdd.setVisibility(View.VISIBLE);
                        } else {
                            if (!OprAdd.isChecked() && !OprSub.isChecked()) {
                                tv_WordProblem.setVisibility(View.GONE);
                                WordAdd.setVisibility(View.GONE);
                                WordSub.setVisibility(View.GONE);
                            }
                            WordAdd.setChecked(false);
                            WordAdd.setVisibility(View.GONE);
                        }
                    }
                });

                OprSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (OprSub.isChecked()) {
                            tv_WordProblem.setVisibility(View.VISIBLE);
                            WordSub.setVisibility(View.VISIBLE);
                        } else {
                            if (!OprAdd.isChecked() && !OprSub.isChecked()) {
                                tv_WordProblem.setVisibility(View.GONE);
                                WordAdd.setVisibility(View.GONE);
                                WordSub.setVisibility(View.GONE);
                            }
                            WordSub.setChecked(false);
                            WordSub.setVisibility(View.GONE);
                        }
                    }
                });

                btn_Submit_endline1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int BaselineSpinnerValue = spinner_BaselineLang.getSelectedItemPosition();
                        int NumberSpinnerValue = spinner_NumberReco.getSelectedItemPosition();
                        int EngSpinnerValue = spinner_English.getSelectedItemPosition();

                        if (BaselineSpinnerValue > 0 || NumberSpinnerValue > 0 || EngSpinnerValue > 0) {
                            sp_English.setSelection(0);
                            engSpin = EngSpinnerValue;
                            if (engSpin == 4 || engSpin == 5) {
                                int selectedId = rg_Meaning.getCheckedRadioButtonId();
                                RadioButton selectedMeaning = (RadioButton) endlineDialog.findViewById(selectedId);
                                String selected = selectedMeaning.getText().toString();
                                if (selected.equalsIgnoreCase("yes"))
                                    engMeaning = 1;
                                else if (selected.equalsIgnoreCase("no"))
                                    engMeaning = 2;
                                else
                                    engMeaning = 0;
                            } else {
                                engMeaning = 0;
                            }

                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            EndlineButtonClicked = true;
                            aserDate = btn_EndlineDatePicker.getText().toString();
                            testT = 1;
                            langSpin = BaselineSpinnerValue;
                            numSpin = NumberSpinnerValue;
                            //engMeaning;
                            OA = OprAdd.isChecked() ? 1 : 0;
                            OS = OprSub.isChecked() ? 1 : 0;
                            OM = OprMul.isChecked() ? 1 : 0;
                            OD = OprDiv.isChecked() ? 1 : 0;
                            WA = WordAdd.isChecked() ? 1 : 0;
                            WS = WordSub.isChecked() ? 1 : 0;

                            // insert or update baseline in db
                            boolean result;
                            result = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().CheckDataExists(StudentUniqID, testT);
                            if (result) {
                                //update
                                AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().UpdateAserData(engSpin, engMeaning, "", aserDate, langSpin, numSpin, OA, OS, OM, OD, WA, WS, createdBy, Util.GetCurrentDate(), IC, 0, StudentUniqID, testT);
                                BackupDatabase.backup(EditStudent.this);
                            } else {
                                // new entry
                                Aser asr = new Aser();
                                asr.StudentId = StudentUniqID;
                                asr.GroupID = GrpID;
                                asr.ChildID = "";
                                asr.TestType = testT;
                                asr.TestDate = aserDate;
                                asr.Lang = langSpin;
                                asr.Num = numSpin;
                                asr.English = engSpin;
                                asr.EnglishSelected = engMeaning;
                                asr.CreatedBy = AppDatabase.getDatabaseInstance(EditStudent.this).getMetaDataDao().getCrlMetaData();
                                asr.CreatedDate = new Utility().GetCurrentDate();
                                asr.DeviceId = Util.GetDeviceID();
                                asr.OAdd = OA;
                                asr.OSub = OS;
                                asr.OMul = OM;
                                asr.ODiv = OD;
                                asr.WAdd = WA;
                                asr.WSub = WS;
                                asr.CreatedOn = new Utility().GetCurrentDateTime(false);
                                asr.sentFlag = 0;
                                AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().insertAser(asr);

                                BackupDatabase.backup(EditStudent.this);
                            }

                            BackupDatabase.backup(EditStudent.this);
                            Toast.makeText(EditStudent.this, "Endline 1 Updated !", Toast.LENGTH_SHORT).show();

                            if (endlineDialog.isShowing())
                                endlineDialog.cancel();

                        } else {
                            Toast.makeText(EditStudent.this, R.string.fillAllFields, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // recall baseline data if cancelled
                endlineDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        AserData = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().GetAllByStudentID(StudentUniqID, 0);
                        if (AserData == null || AserData.size() == 0) {
                            setDefaults();
                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            btn_DatePicker.setText(Util.GetCurrentDate().toString());
                            sp_English.setSelection(0);
                        } else {
                            // fetch baseline aser
                            engSpin = AserData.get(0).English;
                            engMeaning = AserData.get(0).EnglishSelected;
                            AserTestDate = AserData.get(0).TestDate;
                            testT = 0;
                            langSpin = AserData.get(0).Lang;
                            numSpin = AserData.get(0).Num;
                            // set baseline aser
                            sp_BaselineLang.setSelection(langSpin);
                            sp_NumberReco.setSelection(numSpin);
                            sp_English.setSelection(engSpin);
                            btn_DatePicker.setText(AserTestDate);
                            EndlineButtonClicked = false;
                            OA = 0;
                            OS = 0;
                            OM = 0;
                            OD = 0;
                            WA = 0;
                            WS = 0;
                        }
                    }
                });
            }
        });

        btn_Endline2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaults();

                // initialize dialog
                final Dialog endlineDialog = new Dialog(EditStudent.this);
                endlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                endlineDialog.setContentView(R.layout.fragment_endline_dialog);
                endlineDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                // initialize dialog's widgets
                TextView title = endlineDialog.findViewById(R.id.tv_EndlineTitle);
                final Spinner spinner_BaselineLang = endlineDialog.findViewById(R.id.spinner_BaselineLang);
                final Spinner spinner_NumberReco = endlineDialog.findViewById(R.id.spinner_NumberReco);
                final Spinner spinner_English = endlineDialog.findViewById(R.id.spinner_Engish);
                final RadioGroup rg_Meaning = endlineDialog.findViewById(R.id.rg_EngSelected);
                final RadioButton rb_Yes = endlineDialog.findViewById(R.id.rb_Yes);
                final RadioButton rb_No = endlineDialog.findViewById(R.id.rb_No);
                final CheckBox OprAdd = endlineDialog.findViewById(R.id.OprAdd);
                final CheckBox OprSub = endlineDialog.findViewById(R.id.OprSub);
                final CheckBox OprMul = endlineDialog.findViewById(R.id.OprMul);
                final CheckBox OprDiv = endlineDialog.findViewById(R.id.OprDiv);
                final TextView tv_WordProblem = endlineDialog.findViewById(R.id.tv_WordProblem);
                final CheckBox WordAdd = endlineDialog.findViewById(R.id.WordAdd);
                final CheckBox WordSub = endlineDialog.findViewById(R.id.WordSub);
                Button btn_Submit_endline2 = endlineDialog.findViewById(R.id.btn_Submit);
                btn_EndlineDatePicker = endlineDialog.findViewById(R.id.btn_EndlineDatePicker);
                btn_EndlineDatePicker.setText(Util.GetCurrentDate().toString());
                btn_EndlineDatePicker.setPadding(8, 8, 8, 8);
                btn_EndlineDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getFragmentManager(), "EndlineDatePicker");
                    }
                });

                // set values of endline
                title.setText("Endline 2");

                String[] baselineLangAdapter = {"Language", "Beg", "Letter", "Word", "Para", "Story"};
                ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(EditStudent.this, R.layout.custom_spinner, baselineLangAdapter);
                spinner_BaselineLang.setAdapter(baselineAdapter);

                String[] NumberRecoAdapter = {"Number Recognition", "Beg", "0-9", "10-99", "Sub", "Div"};
                ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(EditStudent.this, R.layout.custom_spinner, NumberRecoAdapter);
                spinner_NumberReco.setAdapter(recoAdapter);

                String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
                ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(EditStudent.this, R.layout.custom_spinner, engAdapter);
                spinner_English.setAdapter(EnglishAdapter);

                // show dialog
                endlineDialog.setCanceledOnTouchOutside(false);
                endlineDialog.show();

                // get Aser Data for Endline 2
                AserData = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().GetAllByStudentID(StudentUniqID, 2);
                if (AserData == null || AserData.size() == 0) {
                    setDefaults();
                    testT = 2;
                    btn_EndlineDatePicker.setText(Util.GetCurrentDate().toString());
                    spinner_BaselineLang.setSelection(0);
                    spinner_NumberReco.setSelection(0);
                    spinner_English.setSelection(0);

                    spinner_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            // if Word or Sentence selected
                            if (pos == 4 || pos == 5)
                                rg_Meaning.setVisibility(View.VISIBLE);
                            else
                                rg_Meaning.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                } else {
                    // fetch endline 2 aser
                    engSpin = AserData.get(0).English;
                    engMeaning = AserData.get(0).EnglishSelected;
                    testT = AserData.get(0).TestType;
                    langSpin = AserData.get(0).Lang;
                    numSpin = AserData.get(0).Num;
                    aserDate = AserData.get(0).TestDate;
                    OA = AserData.get(0).OAdd;
                    OS = AserData.get(0).OSub;
                    OM = AserData.get(0).OMul;
                    OD = AserData.get(0).ODiv;
                    WA = AserData.get(0).WAdd;
                    WS = AserData.get(0).WSub;

                    spinner_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            // if Word or Sentence selected
                            if (pos == 4 || pos == 5) {
                                rg_Meaning.setVisibility(View.VISIBLE);
                                if (engMeaning == 1)
                                    rb_Yes.setChecked(true);
                                else
                                    rb_No.setChecked(true);
                            } else {
                                rg_Meaning.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                    // set endline 2 aser
                    spinner_English.setSelection(engSpin);
                    btn_EndlineDatePicker.setText(aserDate);
                    spinner_BaselineLang.setSelection(langSpin);
                    spinner_NumberReco.setSelection(numSpin);

                    if (OA == 1) {
                        OprAdd.setChecked(true);
                    } else {
                        OprAdd.setChecked(false);
                    }
                    if (OS == 1) {
                        OprSub.setChecked(true);
                    } else {
                        OprSub.setChecked(false);
                    }
                    if (OM == 1) {
                        OprMul.setChecked(true);
                    } else {
                        OprMul.setChecked(false);
                    }
                    if (OD == 1) {
                        OprDiv.setChecked(true);
                    } else {
                        OprDiv.setChecked(false);
                    }
                    if (WA == 1) {
                        tv_WordProblem.setVisibility(View.VISIBLE);
                        WordAdd.setChecked(true);
                    } else {
                        WordAdd.setChecked(false);
                    }
                    if (WS == 1) {
                        tv_WordProblem.setVisibility(View.VISIBLE);
                        WordSub.setChecked(true);
                    } else {
                        WordSub.setChecked(false);
                    }
                }

                if (!OprAdd.isChecked() && !OprSub.isChecked()) {
                    tv_WordProblem.setVisibility(View.GONE);
                    WordAdd.setVisibility(View.GONE);
                    WordSub.setVisibility(View.GONE);
                }

                OprAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (OprAdd.isChecked()) {
                            tv_WordProblem.setVisibility(View.VISIBLE);
                            WordAdd.setVisibility(View.VISIBLE);
                        } else {
                            if (!OprAdd.isChecked() && !OprSub.isChecked()) {
                                tv_WordProblem.setVisibility(View.GONE);
                                WordAdd.setVisibility(View.GONE);
                                WordSub.setVisibility(View.GONE);
                            }
                            WordAdd.setChecked(false);
                            WordAdd.setVisibility(View.GONE);
                        }
                    }
                });

                OprSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (OprSub.isChecked()) {
                            tv_WordProblem.setVisibility(View.VISIBLE);
                            WordSub.setVisibility(View.VISIBLE);
                        } else {
                            if (!OprAdd.isChecked() && !OprSub.isChecked()) {
                                tv_WordProblem.setVisibility(View.GONE);
                                WordAdd.setVisibility(View.GONE);
                                WordSub.setVisibility(View.GONE);
                            }
                            WordSub.setChecked(false);
                            WordSub.setVisibility(View.GONE);
                        }
                    }
                });

                btn_Submit_endline2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int BaselineSpinnerValue = spinner_BaselineLang.getSelectedItemPosition();
                        int NumberSpinnerValue = spinner_NumberReco.getSelectedItemPosition();
                        int EngSpinnerValue = spinner_English.getSelectedItemPosition();

                        if (BaselineSpinnerValue > 0 || NumberSpinnerValue > 0 || EngSpinnerValue > 0) {
                            sp_English.setSelection(0);
                            engSpin = EngSpinnerValue;
                            if (engSpin == 4 || engSpin == 5) {
                                int selectedId = rg_Meaning.getCheckedRadioButtonId();
                                RadioButton selectedMeaning = (RadioButton) endlineDialog.findViewById(selectedId);
                                String selected = selectedMeaning.getText().toString();
                                if (selected.equalsIgnoreCase("yes"))
                                    engMeaning = 1;
                                else if (selected.equalsIgnoreCase("no"))
                                    engMeaning = 2;
                                else
                                    engMeaning = 0;
                            } else {
                                engMeaning = 0;
                            }

                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            EndlineButtonClicked = true;
                            aserDate = btn_EndlineDatePicker.getText().toString();

                            testT = 2;
                            langSpin = BaselineSpinnerValue;
                            numSpin = NumberSpinnerValue;

                            OA = OprAdd.isChecked() ? 1 : 0;
                            OS = OprSub.isChecked() ? 1 : 0;
                            OM = OprMul.isChecked() ? 1 : 0;
                            OD = OprDiv.isChecked() ? 1 : 0;
                            WA = WordAdd.isChecked() ? 1 : 0;
                            WS = WordSub.isChecked() ? 1 : 0;

                            // insert or update baseline in db
                            boolean result;
                            result = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().CheckDataExists(StudentUniqID, testT);
                            if (result) {
                                //update
                                AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().UpdateAserData(engSpin, engMeaning, "", aserDate, langSpin, numSpin, OA, OS, OM, OD, WA, WS, createdBy, Util.GetCurrentDate(), IC, 0, StudentUniqID, testT);

                                BackupDatabase.backup(EditStudent.this);

                            } else {
                                // new entry
                                Aser asr = new Aser();
                                asr.StudentId = StudentUniqID;
                                asr.GroupID = GrpID;
                                asr.ChildID = "";
                                asr.TestType = testT;
                                asr.TestDate = aserDate;
                                asr.Lang = langSpin;
                                asr.Num = numSpin;
                                asr.English = engSpin;
                                asr.EnglishSelected = engMeaning;
                                asr.CreatedBy = AppDatabase.getDatabaseInstance(EditStudent.this).getMetaDataDao().getCrlMetaData();
                                asr.CreatedDate = new Utility().GetCurrentDate();
                                asr.DeviceId = Util.GetDeviceID();
                                asr.OAdd = OA;
                                asr.OSub = OS;
                                asr.OMul = OM;
                                asr.ODiv = OD;
                                asr.WAdd = WA;
                                asr.WSub = WS;
                                asr.CreatedOn = new Utility().GetCurrentDateTime(false);
                                asr.sentFlag = 0;
                                AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().insertAser(asr);

                                BackupDatabase.backup(EditStudent.this);
                            }
                            BackupDatabase.backup(EditStudent.this);
                            Toast.makeText(EditStudent.this, "Endline 2 Updated !", Toast.LENGTH_SHORT).show();

                            if (endlineDialog.isShowing())
                                endlineDialog.cancel();
                        } else {
                            Toast.makeText(EditStudent.this, R.string.fillAllFields, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // recall baseline data if cancelled
                endlineDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        AserData = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().GetAllByStudentID(StudentUniqID, 0);
                        if (AserData == null || AserData.size() == 0) {
                            setDefaults();
                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            btn_DatePicker.setText(Util.GetCurrentDate().toString());
                            sp_English.setSelection(0);
                        } else {
                            // fetch baseline aser
                            engSpin = AserData.get(0).English;
                            engMeaning = AserData.get(0).EnglishSelected;
                            testT = 0;
                            langSpin = AserData.get(0).Lang;
                            numSpin = AserData.get(0).Num;
                            AserTestDate = AserData.get(0).TestDate;
                            // set baseline aser
                            sp_BaselineLang.setSelection(langSpin);
                            sp_NumberReco.setSelection(numSpin);
                            btn_DatePicker.setText(AserTestDate);
                            EndlineButtonClicked = false;
                            OA = 0;
                            OS = 0;
                            OM = 0;
                            OD = 0;
                            WA = 0;
                            WS = 0;
                        }
                    }
                });
            }
        });

        btn_Endline3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaults();

                // initialize dialog
                final Dialog endlineDialog = new Dialog(EditStudent.this);
                endlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                endlineDialog.setContentView(R.layout.fragment_endline_dialog);
                endlineDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                // initialize dialog's widgets
                TextView title = endlineDialog.findViewById(R.id.tv_EndlineTitle);
                final Spinner spinner_BaselineLang = endlineDialog.findViewById(R.id.spinner_BaselineLang);
                final Spinner spinner_NumberReco = endlineDialog.findViewById(R.id.spinner_NumberReco);
                final Spinner spinner_English = endlineDialog.findViewById(R.id.spinner_Engish);
                final RadioGroup rg_Meaning = endlineDialog.findViewById(R.id.rg_EngSelected);
                final RadioButton rb_Yes = endlineDialog.findViewById(R.id.rb_Yes);
                final RadioButton rb_No = endlineDialog.findViewById(R.id.rb_No);
                final CheckBox OprAdd = endlineDialog.findViewById(R.id.OprAdd);
                final CheckBox OprSub = endlineDialog.findViewById(R.id.OprSub);
                final CheckBox OprMul = endlineDialog.findViewById(R.id.OprMul);
                final CheckBox OprDiv = endlineDialog.findViewById(R.id.OprDiv);
                final TextView tv_WordProblem = endlineDialog.findViewById(R.id.tv_WordProblem);
                final CheckBox WordAdd = endlineDialog.findViewById(R.id.WordAdd);
                final CheckBox WordSub = endlineDialog.findViewById(R.id.WordSub);
                Button btn_Submit_endline3 = endlineDialog.findViewById(R.id.btn_Submit);
                btn_EndlineDatePicker = endlineDialog.findViewById(R.id.btn_EndlineDatePicker);
                btn_EndlineDatePicker.setText(Util.GetCurrentDate().toString());
                btn_EndlineDatePicker.setPadding(8, 8, 8, 8);
                btn_EndlineDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getFragmentManager(), "EndlineDatePicker");
                    }
                });

                // set values of endline
                title.setText("Endline 3");

                String[] baselineLangAdapter = {"Language", "Beg", "Letter", "Word", "Para", "Story"};
                ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(EditStudent.this, R.layout.custom_spinner, baselineLangAdapter);
                spinner_BaselineLang.setAdapter(baselineAdapter);

                String[] NumberRecoAdapter = {"Number Recognition", "Beg", "0-9", "10-99", "Sub", "Div"};
                ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(EditStudent.this, R.layout.custom_spinner, NumberRecoAdapter);
                spinner_NumberReco.setAdapter(recoAdapter);

                String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
                ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(EditStudent.this, R.layout.custom_spinner, engAdapter);
                spinner_English.setAdapter(EnglishAdapter);

                // show dialog
                endlineDialog.setCanceledOnTouchOutside(false);
                endlineDialog.show();

                // get Aser Data for Endline 1
                AserData = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().GetAllByStudentID(StudentUniqID, 3);
                if (AserData == null || AserData.size() == 0) {
                    setDefaults();
                    testT = 3;
                    btn_EndlineDatePicker.setText(Util.GetCurrentDate().toString());
                    spinner_BaselineLang.setSelection(0);
                    spinner_NumberReco.setSelection(0);
                    spinner_English.setSelection(0);

                    spinner_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            // if Word or Sentence selected
                            if (pos == 4 || pos == 5)
                                rg_Meaning.setVisibility(View.VISIBLE);
                            else
                                rg_Meaning.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                } else {
                    // fetch endline 3 aser
                    engSpin = AserData.get(0).English;
                    engMeaning = AserData.get(0).EnglishSelected;
                    testT = AserData.get(0).TestType;
                    langSpin = AserData.get(0).Lang;
                    numSpin = AserData.get(0).Num;
                    aserDate = AserData.get(0).TestDate;
                    OA = AserData.get(0).OAdd;
                    OS = AserData.get(0).OSub;
                    OM = AserData.get(0).OMul;
                    OD = AserData.get(0).ODiv;
                    WA = AserData.get(0).WAdd;
                    WS = AserData.get(0).WSub;

                    spinner_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            // if Word or Sentence selected
                            if (pos == 4 || pos == 5) {
                                rg_Meaning.setVisibility(View.VISIBLE);
                                if (engMeaning == 1)
                                    rb_Yes.setChecked(true);
                                else
                                    rb_No.setChecked(true);
                            } else {
                                rg_Meaning.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                    // set endline 3 aser
                    spinner_English.setSelection(engSpin);
                    btn_EndlineDatePicker.setText(aserDate);
                    spinner_BaselineLang.setSelection(langSpin);
                    spinner_NumberReco.setSelection(numSpin);

                    if (OA == 1) {
                        OprAdd.setChecked(true);
                    } else {
                        OprAdd.setChecked(false);
                    }
                    if (OS == 1) {
                        OprSub.setChecked(true);
                    } else {
                        OprSub.setChecked(false);
                    }
                    if (OM == 1) {
                        OprMul.setChecked(true);
                    } else {
                        OprMul.setChecked(false);
                    }
                    if (OD == 1) {
                        OprDiv.setChecked(true);
                    } else {
                        OprDiv.setChecked(false);
                    }
                    if (WA == 1) {
                        tv_WordProblem.setVisibility(View.VISIBLE);
                        WordAdd.setChecked(true);
                    } else {
                        WordAdd.setChecked(false);
                    }
                    if (WS == 1) {
                        tv_WordProblem.setVisibility(View.VISIBLE);
                        WordSub.setChecked(true);
                    } else {
                        WordSub.setChecked(false);
                    }
                }

                if (!OprAdd.isChecked() && !OprSub.isChecked()) {
                    tv_WordProblem.setVisibility(View.GONE);
                    WordAdd.setVisibility(View.GONE);
                    WordSub.setVisibility(View.GONE);
                }

                OprAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (OprAdd.isChecked()) {
                            tv_WordProblem.setVisibility(View.VISIBLE);
                            WordAdd.setVisibility(View.VISIBLE);
                        } else {
                            if (!OprAdd.isChecked() && !OprSub.isChecked()) {
                                tv_WordProblem.setVisibility(View.GONE);
                                WordAdd.setVisibility(View.GONE);
                                WordSub.setVisibility(View.GONE);
                            }
                            WordAdd.setChecked(false);
                            WordAdd.setVisibility(View.GONE);
                        }
                    }
                });

                OprSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (OprSub.isChecked()) {
                            tv_WordProblem.setVisibility(View.VISIBLE);
                            WordSub.setVisibility(View.VISIBLE);
                        } else {
                            if (!OprAdd.isChecked() && !OprSub.isChecked()) {
                                tv_WordProblem.setVisibility(View.GONE);
                                WordAdd.setVisibility(View.GONE);
                                WordSub.setVisibility(View.GONE);
                            }
                            WordSub.setChecked(false);
                            WordSub.setVisibility(View.GONE);
                        }
                    }
                });

                btn_Submit_endline3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int BaselineSpinnerValue = spinner_BaselineLang.getSelectedItemPosition();
                        int NumberSpinnerValue = spinner_NumberReco.getSelectedItemPosition();
                        int EngSpinnerValue = spinner_English.getSelectedItemPosition();

                        if (BaselineSpinnerValue > 0 || NumberSpinnerValue > 0 || EngSpinnerValue > 0) {
                            sp_English.setSelection(0);
                            engSpin = EngSpinnerValue;
                            if (engSpin == 4 || engSpin == 5) {
                                int selectedId = rg_Meaning.getCheckedRadioButtonId();
                                RadioButton selectedMeaning = (RadioButton) endlineDialog.findViewById(selectedId);
                                String selected = selectedMeaning.getText().toString();
                                if (selected.equalsIgnoreCase("yes"))
                                    engMeaning = 1;
                                else if (selected.equalsIgnoreCase("no"))
                                    engMeaning = 2;
                                else
                                    engMeaning = 0;
                            } else {
                                engMeaning = 0;
                            }

                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            EndlineButtonClicked = true;

                            testT = 3;
                            langSpin = BaselineSpinnerValue;
                            numSpin = NumberSpinnerValue;
                            aserDate = btn_EndlineDatePicker.getText().toString();

                            OA = OprAdd.isChecked() ? 1 : 0;
                            OS = OprSub.isChecked() ? 1 : 0;
                            OM = OprMul.isChecked() ? 1 : 0;
                            OD = OprDiv.isChecked() ? 1 : 0;
                            WA = WordAdd.isChecked() ? 1 : 0;
                            WS = WordSub.isChecked() ? 1 : 0;

                            // insert or update baseline in db
                            boolean result;
                            result = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().CheckDataExists(StudentUniqID, testT);
                            if (result) {
                                //update
                                AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().UpdateAserData(engSpin, engMeaning, "", aserDate, langSpin, numSpin, OA, OS, OM, OD, WA, WS, createdBy, Util.GetCurrentDate(), IC, 0, StudentUniqID, testT);
                                BackupDatabase.backup(EditStudent.this);
                            } else {
                                // new entry
                                Aser asr = new Aser();
                                asr.StudentId = StudentUniqID;
                                asr.GroupID = GrpID;
                                asr.ChildID = "";
                                asr.TestType = testT;
                                asr.TestDate = aserDate;
                                asr.Lang = langSpin;
                                asr.Num = numSpin;
                                asr.English = engSpin;
                                asr.EnglishSelected = engMeaning;
                                asr.CreatedBy = AppDatabase.getDatabaseInstance(EditStudent.this).getMetaDataDao().getCrlMetaData();
                                asr.CreatedDate = new Utility().GetCurrentDate();
                                asr.DeviceId = Util.GetDeviceID();
                                asr.OAdd = OA;
                                asr.OSub = OS;
                                asr.OMul = OM;
                                asr.ODiv = OD;
                                asr.WAdd = WA;
                                asr.WSub = WS;
                                asr.CreatedOn = new Utility().GetCurrentDateTime(false);
                                asr.sentFlag = 0;
                                AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().insertAser(asr);
                                BackupDatabase.backup(EditStudent.this);
                            }
                            BackupDatabase.backup(EditStudent.this);
                            Toast.makeText(EditStudent.this, "Endline 3 Updated !", Toast.LENGTH_SHORT).show();

                            if (endlineDialog.isShowing())
                                endlineDialog.cancel();
                        } else {
                            Toast.makeText(EditStudent.this, R.string.fillAllFields, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // recall baseline data if cancelled
                endlineDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        AserData = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().GetAllByStudentID(StudentUniqID, 0);
                        if (AserData == null || AserData.size() == 0) {
                            setDefaults();
                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            btn_DatePicker.setText(Util.GetCurrentDate().toString());
                            sp_English.setSelection(0);
                        } else {
                            // fetch baseline aser
                            engSpin = AserData.get(0).English;
                            engMeaning = AserData.get(0).EnglishSelected;
                            testT = 0;
                            langSpin = AserData.get(0).Lang;
                            numSpin = AserData.get(0).Num;
                            AserTestDate = AserData.get(0).TestDate;
                            // set baseline aser
                            sp_BaselineLang.setSelection(langSpin);
                            sp_NumberReco.setSelection(numSpin);
                            btn_DatePicker.setText(AserTestDate);
                            EndlineButtonClicked = false;
                            OA = 0;
                            OS = 0;
                            OM = 0;
                            OD = 0;
                            WA = 0;
                            WS = 0;
                        }
                    }
                });
            }
        });

        btn_Endline4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaults();

                // initialize dialog
                final Dialog endlineDialog = new Dialog(EditStudent.this);
                endlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                endlineDialog.setContentView(R.layout.fragment_endline_dialog);
                endlineDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                // initialize dialog's widgets
                TextView title = endlineDialog.findViewById(R.id.tv_EndlineTitle);
                final Spinner spinner_BaselineLang = endlineDialog.findViewById(R.id.spinner_BaselineLang);
                final Spinner spinner_NumberReco = endlineDialog.findViewById(R.id.spinner_NumberReco);
                final Spinner spinner_English = endlineDialog.findViewById(R.id.spinner_Engish);
                final RadioGroup rg_Meaning = endlineDialog.findViewById(R.id.rg_EngSelected);
                final RadioButton rb_Yes = endlineDialog.findViewById(R.id.rb_Yes);
                final RadioButton rb_No = endlineDialog.findViewById(R.id.rb_No);
                final CheckBox OprAdd = endlineDialog.findViewById(R.id.OprAdd);
                final CheckBox OprSub = endlineDialog.findViewById(R.id.OprSub);
                final CheckBox OprMul = endlineDialog.findViewById(R.id.OprMul);
                final CheckBox OprDiv = endlineDialog.findViewById(R.id.OprDiv);
                final TextView tv_WordProblem = endlineDialog.findViewById(R.id.tv_WordProblem);
                final CheckBox WordAdd = endlineDialog.findViewById(R.id.WordAdd);
                final CheckBox WordSub = endlineDialog.findViewById(R.id.WordSub);
                Button btn_Submit_endline4 = endlineDialog.findViewById(R.id.btn_Submit);
                btn_EndlineDatePicker = endlineDialog.findViewById(R.id.btn_EndlineDatePicker);
                btn_EndlineDatePicker.setText(Util.GetCurrentDate().toString());
                btn_EndlineDatePicker.setPadding(8, 8, 8, 8);
                btn_EndlineDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getFragmentManager(), "EndlineDatePicker");
                    }
                });

                // set values of endline
                title.setText("Endline 4");

                String[] baselineLangAdapter = {"Language", "Beg", "Letter", "Word", "Para", "Story"};
                ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(EditStudent.this, R.layout.custom_spinner, baselineLangAdapter);
                spinner_BaselineLang.setAdapter(baselineAdapter);

                String[] NumberRecoAdapter = {"Number Recognition", "Beg", "0-9", "10-99", "Sub", "Div"};
                ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(EditStudent.this, R.layout.custom_spinner, NumberRecoAdapter);
                spinner_NumberReco.setAdapter(recoAdapter);

                String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
                ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(EditStudent.this, R.layout.custom_spinner, engAdapter);
                spinner_English.setAdapter(EnglishAdapter);

                // show dialog
                endlineDialog.setCanceledOnTouchOutside(false);
                endlineDialog.show();

                // get Aser Data for Endline 4
                AserData = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().GetAllByStudentID(StudentUniqID, 4);
                if (AserData == null || AserData.size() == 0) {
                    setDefaults();
                    testT = 4;
                    btn_EndlineDatePicker.setText(Util.GetCurrentDate().toString());
                    spinner_BaselineLang.setSelection(0);
                    spinner_NumberReco.setSelection(0);
                    spinner_English.setSelection(0);

                    spinner_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            // if Word or Sentence selected
                            if (pos == 4 || pos == 5)
                                rg_Meaning.setVisibility(View.VISIBLE);
                            else
                                rg_Meaning.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                } else {
                    // fetch endline 4 aser
                    engSpin = AserData.get(0).English;
                    engMeaning = AserData.get(0).EnglishSelected;
                    testT = AserData.get(0).TestType;
                    langSpin = AserData.get(0).Lang;
                    numSpin = AserData.get(0).Num;
                    aserDate = AserData.get(0).TestDate;
                    OA = AserData.get(0).OAdd;
                    OS = AserData.get(0).OSub;
                    OM = AserData.get(0).OMul;
                    OD = AserData.get(0).ODiv;
                    WA = AserData.get(0).WAdd;
                    WS = AserData.get(0).WSub;

                    spinner_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            // if Word or Sentence selected
                            if (pos == 4 || pos == 5) {
                                rg_Meaning.setVisibility(View.VISIBLE);
                                if (engMeaning == 1)
                                    rb_Yes.setChecked(true);
                                else
                                    rb_No.setChecked(true);
                            } else {
                                rg_Meaning.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                    // set endline 4 aser
                    spinner_English.setSelection(engSpin);
                    btn_EndlineDatePicker.setText(aserDate);
                    spinner_BaselineLang.setSelection(langSpin);
                    spinner_NumberReco.setSelection(numSpin);

                    if (OA == 1) {
                        OprAdd.setChecked(true);
                    } else {
                        OprAdd.setChecked(false);
                    }
                    if (OS == 1) {
                        OprSub.setChecked(true);
                    } else {
                        OprSub.setChecked(false);
                    }
                    if (OM == 1) {
                        OprMul.setChecked(true);
                    } else {
                        OprMul.setChecked(false);
                    }
                    if (OD == 1) {
                        OprDiv.setChecked(true);
                    } else {
                        OprDiv.setChecked(false);
                    }
                    if (WA == 1) {
                        tv_WordProblem.setVisibility(View.VISIBLE);
                        WordAdd.setChecked(true);
                    } else {
                        WordAdd.setChecked(false);
                    }
                    if (WS == 1) {
                        tv_WordProblem.setVisibility(View.VISIBLE);
                        WordSub.setChecked(true);
                    } else {
                        WordSub.setChecked(false);
                    }
                }

                if (!OprAdd.isChecked() && !OprSub.isChecked()) {
                    tv_WordProblem.setVisibility(View.GONE);
                    WordAdd.setVisibility(View.GONE);
                    WordSub.setVisibility(View.GONE);
                }

                OprAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (OprAdd.isChecked()) {
                            tv_WordProblem.setVisibility(View.VISIBLE);
                            WordAdd.setVisibility(View.VISIBLE);
                        } else {
                            if (!OprAdd.isChecked() && !OprSub.isChecked()) {
                                tv_WordProblem.setVisibility(View.GONE);
                                WordAdd.setVisibility(View.GONE);
                                WordSub.setVisibility(View.GONE);
                            }
                            WordAdd.setChecked(false);
                            WordAdd.setVisibility(View.GONE);
                        }
                    }
                });

                OprSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (OprSub.isChecked()) {
                            tv_WordProblem.setVisibility(View.VISIBLE);
                            WordSub.setVisibility(View.VISIBLE);
                        } else {
                            if (!OprAdd.isChecked() && !OprSub.isChecked()) {
                                tv_WordProblem.setVisibility(View.GONE);
                                WordAdd.setVisibility(View.GONE);
                                WordSub.setVisibility(View.GONE);
                            }
                            WordSub.setChecked(false);
                            WordSub.setVisibility(View.GONE);
                        }
                    }
                });

                btn_Submit_endline4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int BaselineSpinnerValue = spinner_BaselineLang.getSelectedItemPosition();
                        int NumberSpinnerValue = spinner_NumberReco.getSelectedItemPosition();
                        int EngSpinnerValue = spinner_English.getSelectedItemPosition();

                        if (BaselineSpinnerValue > 0 || NumberSpinnerValue > 0 || EngSpinnerValue > 0) {
                            sp_English.setSelection(0);
                            engSpin = EngSpinnerValue;
                            if (engSpin == 4 || engSpin == 5) {
                                int selectedId = rg_Meaning.getCheckedRadioButtonId();
                                RadioButton selectedMeaning = (RadioButton) endlineDialog.findViewById(selectedId);
                                String selected = selectedMeaning.getText().toString();
                                if (selected.equalsIgnoreCase("yes"))
                                    engMeaning = 1;
                                else if (selected.equalsIgnoreCase("no"))
                                    engMeaning = 2;
                                else
                                    engMeaning = 0;
                            } else {
                                engMeaning = 0;
                            }

                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            EndlineButtonClicked = true;
                            aserDate = btn_EndlineDatePicker.getText().toString();

                            testT = 4;
                            langSpin = BaselineSpinnerValue;
                            numSpin = NumberSpinnerValue;

                            OA = OprAdd.isChecked() ? 1 : 0;
                            OS = OprSub.isChecked() ? 1 : 0;
                            OM = OprMul.isChecked() ? 1 : 0;
                            OD = OprDiv.isChecked() ? 1 : 0;
                            WA = WordAdd.isChecked() ? 1 : 0;
                            WS = WordSub.isChecked() ? 1 : 0;

                            // insert or update baseline in db
                            boolean result;
                            result = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().CheckDataExists(StudentUniqID, testT);
                            if (result) {
                                //update
                                AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().UpdateAserData(engSpin, engMeaning, "", aserDate, langSpin, numSpin, OA, OS, OM, OD, WA, WS, createdBy, Util.GetCurrentDate(), IC, 0, StudentUniqID, testT);
                                BackupDatabase.backup(EditStudent.this);
                            } else {
                                // new entry
                                Aser asr = new Aser();
                                asr.StudentId = StudentUniqID;
                                asr.GroupID = GrpID;
                                asr.ChildID = "";
                                asr.TestType = testT;
                                asr.TestDate = aserDate;
                                asr.Lang = langSpin;
                                asr.Num = numSpin;
                                asr.English = engSpin;
                                asr.EnglishSelected = engMeaning;
                                asr.CreatedBy = AppDatabase.getDatabaseInstance(EditStudent.this).getMetaDataDao().getCrlMetaData();
                                asr.CreatedDate = new Utility().GetCurrentDate();
                                asr.DeviceId = Util.GetDeviceID();
                                asr.OAdd = OA;
                                asr.OSub = OS;
                                asr.OMul = OM;
                                asr.ODiv = OD;
                                asr.WAdd = WA;
                                asr.WSub = WS;
                                asr.CreatedOn = new Utility().GetCurrentDateTime(false);
                                asr.sentFlag = 0;
                                AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().insertAser(asr);
                                BackupDatabase.backup(EditStudent.this);
                            }
                            BackupDatabase.backup(EditStudent.this);
                            Toast.makeText(EditStudent.this, "Endline 4 Updated !", Toast.LENGTH_SHORT).show();

                            if (endlineDialog.isShowing())
                                endlineDialog.cancel();
                        } else {
                            Toast.makeText(EditStudent.this, R.string.fillAllFields, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // recall baseline data if cancelled
                endlineDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        AserData = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().GetAllByStudentID(StudentUniqID, 0);
                        if (AserData == null || AserData.size() == 0) {
                            setDefaults();
                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            btn_DatePicker.setText(Util.GetCurrentDate().toString());
                            sp_English.setSelection(0);
                        } else {
                            // fetch baseline aser
                            engSpin = AserData.get(0).English;
                            engMeaning = AserData.get(0).EnglishSelected;
                            testT = 0;
                            langSpin = AserData.get(0).Lang;
                            numSpin = AserData.get(0).Num;
                            AserTestDate = AserData.get(0).TestDate;
                            // set baseline aser
                            sp_BaselineLang.setSelection(langSpin);
                            sp_NumberReco.setSelection(numSpin);
                            btn_DatePicker.setText(AserTestDate);
                            EndlineButtonClicked = false;
                            OA = 0;
                            OS = 0;
                            OM = 0;
                            OD = 0;
                            WA = 0;
                            WS = 0;
                        }
                    }
                });
            }
        });

        btn_Baseline_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check AllSpinners Emptyness
                int BlocksSpinnerValue = blocks_spinner.getSelectedItemPosition();
                int VillagesSpinnerValue = villages_spinner.getSelectedItemPosition();
                int GroupsSpinnerValue = groups_spinner.getSelectedItemPosition();
                int ExistingSpinnerValue = existingStudent_Spinner.getSelectedItemPosition();


                if (BlocksSpinnerValue > 0 && VillagesSpinnerValue > 0 && GroupsSpinnerValue > 0 && ExistingSpinnerValue > 0) {
                    // Photo updated validation
                    if (captureButtonPressed) {
//                        Toast.makeText(EditStudent.this, "Photo Inserted Successfully !!!", Toast.LENGTH_SHORT).show();
                    }

                    testT = 0;
                    langSpin = sp_BaselineLang.getSelectedItemPosition();
                    numSpin = sp_NumberReco.getSelectedItemPosition();
                    engSpin = sp_English.getSelectedItemPosition();
                    int classSpin = sp_Class.getSelectedItemPosition();
                    AserTestDate = btn_DatePicker.getText().toString();

                    if ((langSpin > 0 || numSpin > 0 || engSpin > 0) || classSpin > 0 && !edt_GuardianName.getText().toString().isEmpty() && (rb_Govt.isChecked() || rb_Private.isChecked()) && (rb_Male.isChecked() || rb_Female.isChecked())) {
                        if (!btn_BirthDatePicker.getText().toString().equalsIgnoreCase(getString(R.string.birth_date))) {
                            String moNumber = edt_moNumber.getText().toString();
                            if (validatePhoneDetails()) {
                                String phoneType = getPhoneType();
                                String relation_with_phone_owner = getRelationWithPhoneOwner();
                                String schoolType = "";
                                String gender = "";
                                // get selected radio button from radioGroup
                                int selId = rg_SchoolType.getCheckedRadioButtonId();
                                int genderId = rg_Gender.getCheckedRadioButtonId();

                                if (genderId == rb_Male.getId()) {
                                    gender = "Male";
                                } else if (genderId == rb_Female.getId()) {
                                    gender = "Female";
                                } else {
                                    Student student = AppDatabase.getDatabaseInstance(EditStudent.this).getStudentDao().GetStudentDataByStdID(StudentUniqID);
                                    gender = student.getGender();
                                }

                                // find the radio button by returned id
                                selectedSchoolType = (RadioButton) findViewById(selId);
                                if (!(selId == -1)) { //app was crashing before on submit click(10 oct 20)
                                    schoolType = selectedSchoolType.getText().toString();
                                }
                                String stdSchoolType = "0";
                                if (selId == rb_Govt.getId())
                                    stdSchoolType = "1";
                                else if (selId == rb_Private.getId())
                                    stdSchoolType = "2";

                                String guardianName = edt_GuardianName.getText().toString();

                                String stdClass = "";
                                // get Class
                                if (sp_Class.getSelectedItem().toString().equalsIgnoreCase("Anganwadi"))
                                    stdClass = String.valueOf(-1);
                                else if (sp_Class.getSelectedItem().toString().equalsIgnoreCase("Pre-School"))
                                    stdClass = String.valueOf(-2);
                                else if (sp_Class.getSelectedItem().toString().equalsIgnoreCase("Balwadi"))
                                    stdClass = String.valueOf(-3);
                                else if (sp_Class.getSelectedItem().toString().equalsIgnoreCase("Dropout"))
                                    stdClass = String.valueOf(-4);
                                else if (sp_Class.getSelectedItem().toString().equalsIgnoreCase("Not Enrolled"))
                                    stdClass = String.valueOf(-5);
                                else
                                    stdClass = String.valueOf(sp_Class.getSelectedItemPosition());

                                int sentFlag = 0;

                                String DOB = btn_BirthDatePicker.getText().toString();
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                                String stdAge = "-1";
                                try {
                                    cal.setTime(sdf.parse(btn_BirthDatePicker.getText().toString()));
                                    stdAge = "" + Integer.parseInt(Integer.toString(calculateAge(cal.getTimeInMillis())));
                                } catch (ParseException e) {

                                    try {
                                        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                        cal.setTime(sdf.parse(btn_BirthDatePicker.getText().toString()));
                                        stdAge = "" + Integer.parseInt(Integer.toString(calculateAge(cal.getTimeInMillis())));
                                    } catch (ParseException ex) {
                                        ex.printStackTrace();
                                        Toast.makeText(EditStudent.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                        Student student = AppDatabase.getDatabaseInstance(EditStudent.this).getStudentDao().GetStudentDataByStdID(StudentUniqID);
                                        stdAge = student.Age;
                                    }
                                }


                                // update class, guardian name, SchoolType,birth date ,age
                                AppDatabase.getDatabaseInstance(EditStudent.this).getStudentDao().UpdateStudent(stdClass, guardianName, stdSchoolType, DOB, stdAge, gender, phoneType, relation_with_phone_owner, moNumber, sentFlag, StudentUniqID);
                                Log.d("birth date", DOB + " age " + stdAge);
                                // insert or update aser baseline in db
                                boolean result;
                                result = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().CheckDataExists(StudentUniqID, testT);
                                if (result) {
                                    //update
                                    AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().UpdateAserData(engSpin, engMeaning, "", AserTestDate, langSpin, numSpin, OA, OS, OM, OD, WA, WS, createdBy, Util.GetCurrentDate(), IC, 0, StudentUniqID, testT);
                                    BackupDatabase.backup(EditStudent.this);
                                } else {
                                    // new entry
                                    Aser asr = new Aser();
                                    asr.StudentId = StudentUniqID;
                                    asr.GroupID = GrpID;
                                    asr.ChildID = "";
                                    asr.TestType = testT;
                                    asr.TestDate = AserTestDate;
                                    asr.Lang = langSpin;
                                    asr.English = engSpin;
                                    asr.EnglishSelected = engMeaning;
                                    asr.Num = numSpin;
                                    asr.CreatedBy = AppDatabase.getDatabaseInstance(EditStudent.this).getMetaDataDao().getCrlMetaData();
                                    asr.CreatedDate = new Utility().GetCurrentDate();
                                    asr.DeviceId = Util.GetDeviceID();
                                    asr.OAdd = OA;
                                    asr.OSub = OS;
                                    asr.OMul = OM;
                                    asr.ODiv = OD;
                                    asr.WAdd = WA;
                                    asr.WSub = WS;
                                    asr.CreatedOn = new Utility().GetCurrentDateTime(false);
                                    asr.sentFlag = 0;
                                    try {
                                        AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().insertAser(asr);
                                        BackupDatabase.backup(EditStudent.this);
                                    } catch (Exception e) {
                                        Modal_Log log = new Modal_Log();
                                        log.setCurrentDateTime(new Utility().GetCurrentDate());
                                        log.setErrorType("ERROR");
                                        log.setExceptionMessage(e.getMessage());
                                        log.setExceptionStackTrace(e.getStackTrace().toString());
                                        log.setMethodName("EditStudent" + "_" + "Submit");
                                        log.setDeviceId("");
                                        AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                                        BackupDatabase.backup(ApplicationController.getInstance());

                                        e.printStackTrace();
                                        Toast.makeText(EditStudent.this, R.string.baselineInsertFailed, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                BackupDatabase.backup(EditStudent.this);
                                resetFormPartially();
                                Toast.makeText(EditStudent.this, R.string.baselineUpdated, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(EditStudent.this, R.string.selectBirthDate, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditStudent.this, R.string.fillAllBaselineFld, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditStudent.this, R.string.fillAllFields, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormReset();
            }
        });
    }

    int calculateAge(long date) {
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }
        return age;
    }

    private void initializeStudBirthDate() {
        btn_BirthDatePicker.setText(R.string.birth_date);
        btn_BirthDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new BirthDatePickerFragment();
                newFragment.show(getFragmentManager(), "BirthDatePicker");
            }
        });
    }

    private void initializeAserDate() {
        btn_DatePicker.setText(Util.GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);
        btn_DatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
    }

    private void initializeNumberRecoSpinner() {
        sp_NumberReco = findViewById(R.id.spinner_NumberReco);
        String[] NumberRecoAdapter = {"Baseline (Number Reco)", "Beg", "0-9", "10-99", "Sub", "Div"};
        ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, NumberRecoAdapter);
        //sp_NumberReco.setPrompt("Number Reco Level");
        sp_NumberReco.setAdapter(recoAdapter);
    }

    private void initializeBaselineSpinner() {
        sp_BaselineLang = findViewById(R.id.spinner_BaselineLang);
        //sp_BaselineLang.setPrompt("Baseline Level");
        String[] baselineLangAdapter = {"Baseline (Lang)", "Beg", "Letter", "Word", "Para", "Story"};
        ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, baselineLangAdapter);
        sp_BaselineLang.setAdapter(baselineAdapter);
    }

    private void initializeEnglishSpinner() {
        String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
        ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, engAdapter);
        sp_English.setAdapter(EnglishAdapter);
    }

    //Intialize State
    private void initializeState() {
        //Get Villages Data for States
        List<String> States = new ArrayList<>();
        States.clear();
        States = AppDatabase.getDatabaseInstance(EditStudent.this).getVillageDao().getState();
        populateBlock(States.get(0));
    }

    private String getRelationWithPhoneOwner() {
        int pos = relation_with_phone_owner.getSelectedItemPosition();
        String[] relation_with_phone_owner = getResources().getStringArray(R.array.relation_with_phone_owner_constants);
        if (pos > 0) {
            return relation_with_phone_owner[pos];
        } else {
            return "";
        }
    }

    private String getPhoneType() {
        int pos = phone_type.getSelectedItemPosition();
        String[] phoneTypeConstants = getResources().getStringArray(R.array.phone_type_constants);
        if (pos > 0) {
            return phoneTypeConstants[pos];
        } else {
            return "";
        }
    }

    private boolean validatePhoneDetails() {
        int position = phone_type.getSelectedItemPosition();
        if (position != 0) {
            if (position == 4) {
                // if phone not available
                return true;
            } else {
                String moNumber = edt_moNumber.getText().toString();
                if (isValidRelationWithPhoneOwner() && isValidMobile(moNumber)) {
                    // if phone  available and details also filled
                    return true;
                } else {
                    // if phone  available but details also not filled
                    return false;
                }
            }
        } else {
            Toast.makeText(this, R.string.select_phone_type, Toast.LENGTH_SHORT).show();
            // if item not selected
            return false;
        }
    }

    private boolean isValidRelationWithPhoneOwner() {
        int pos = relation_with_phone_owner.getSelectedItemPosition();
        if (pos > 0) {
            return true;
        } else {
            Toast.makeText(this,  R.string.select_phone_owner, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void initializePhoneNumberSpinner() {
        phone_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position > 0 && position != 4) {
                    enablePhoneFields();
                } else {
                    disablePhoneFields();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void enablePhoneFields() {
        // relation_with_phone_owner.setSelection(0);
        relation_with_phone_owner.setEnabled(true);
        edt_moNumber.setEnabled(true);
    }

    private void disablePhoneFields() {
        edt_moNumber.setText("");
        edt_moNumber.setEnabled(false);
        relation_with_phone_owner.setSelection(0);
        relation_with_phone_owner.setEnabled(false);
    }

    private boolean isValidMobile(String phone) {
        if (phone.length() == 10) {
            return true;
        } else {
            Toast.makeText(this, R.string.validationMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void initializeVariables() {
        edt_GuardianName = (EditText) findViewById(R.id.edt_GuardianName);
        btn_BirthDatePicker = findViewById(R.id.btn_BirthDatePicker);
        sp_Class = (Spinner) findViewById(R.id.sp_Class);
        rg_SchoolType = (RadioGroup) findViewById(R.id.rg_SchoolType);
        rg_Gender = (RadioGroup) findViewById(R.id.rg_Gender);
        rb_Male = (RadioButton) findViewById(R.id.rb_Male);
        rb_Female = (RadioButton) findViewById(R.id.rb_Female);
        rb_Govt = (RadioButton) findViewById(R.id.rb_Govt);
        rb_Private = (RadioButton) findViewById(R.id.rb_Private);
        sp_English = (Spinner) findViewById(R.id.spinner_Engish);
        groups_spinner = (Spinner) findViewById(R.id.spinner_SelectGroups);
        villages_spinner = (Spinner) findViewById(R.id.spinner_selectVillage);
        blocks_spinner = (Spinner) findViewById(R.id.spinner_SelectBlock);
        edt_Fname = (TextView) findViewById(R.id.edt_FirstName);
        edt_Mname = (TextView) findViewById(R.id.edt_MiddleName);
        edt_Lname = (TextView) findViewById(R.id.edt_LastName);
        phone_type = (Spinner) findViewById(R.id.phone_type);
        relation_with_phone_owner = (Spinner) findViewById(R.id.relation_with_phone_owner);
        edt_moNumber = (EditText) findViewById(R.id.edt_moNumber);
        edt_Age = (TextView) findViewById(R.id.edt_Age);
        //   tv_Gender = (TextView) findViewById(R.id.tv_Gender);
        btn_Capture = (Button) findViewById(R.id.btn_Capture);
        imgView = (ImageView) findViewById(R.id.imageView);
        btn_Capture.setVisibility(View.GONE);
        btn_Baseline_Submit = (Button) findViewById(R.id.btn_Submit);
        btn_Clear = (Button) findViewById(R.id.btn_Clear);
        existingStudent_Spinner = (Spinner) findViewById(R.id.spinner_existingStudent);
        Util = new Utility();
        btn_DatePicker = findViewById(R.id.btn_DatePicker);
        btn_Endline1 = findViewById(R.id.btn_Endline1);
        btn_Endline2 = findViewById(R.id.btn_Endline2);
        btn_Endline3 = findViewById(R.id.btn_Endline3);
        btn_Endline4 = findViewById(R.id.btn_Endline4);
        AserForm = findViewById(R.id.AserForm);
        createdBy = AppDatabase.getDatabaseInstance(EditStudent.this).getMetaDataDao().getCrlMetaData();
    }

    private void initializeClassSpinner() {
        ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_Class));
        sp_Class.setAdapter(classAdapter);
    }


    public void populateBlock(String selectedState) {
        //Get Villages Data for Blocks AllSpinners
        Blocks.clear();
        Blocks = AppDatabase.getDatabaseInstance(EditStudent.this).getVillageDao().GetStatewiseBlock(selectedState);
        Blocks.add(0, getString(R.string.selectblock));
        //Creating the ArrayAdapter instance having the Villages list
        ArrayAdapter<String> BlockAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, Blocks);
        // Hint for AllSpinners
        blocks_spinner.setPrompt(getString(R.string.selectblock));
        blocks_spinner.setAdapter(BlockAdapter);

        blocks_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBlock = blocks_spinner.getSelectedItem().toString();
                populateVillage(selectedBlock);
                btn_Capture.setVisibility(View.GONE);
                resetFormPartially();
                AserForm.setVisibility(View.GONE);
                groups_spinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void populateVillage(String selectedBlock) {
        //Get Villages Data for Villages filtered by block for Spinners
        List<Village> BlocksVillages = new ArrayList<>();
        BlocksVillages.clear();
        Village v = new Village();
        v.VillageId = "0";
        v.VillageName = getString(R.string.selectvillage);
        BlocksVillages = AppDatabase.getDatabaseInstance(EditStudent.this).getVillageDao().GetVillages(selectedBlock);
        BlocksVillages.add(0, v);

        List<Village> SpinnerBlocksVillages = new ArrayList<>();
        for (int i = 0; i < BlocksVillages.size(); i++)
            SpinnerBlocksVillages.add(new Village(Integer.parseInt(BlocksVillages.get(i).VillageId), BlocksVillages.get(i).VillageName));

        //Creating the ArrayAdapter instance having the Villages list
        ArrayAdapter<Village> VillagesAdapter = new ArrayAdapter<Village>(this, R.layout.custom_spinner, SpinnerBlocksVillages);
        // Hint for AllSpinners
        villages_spinner.setPrompt(getString(R.string.selectvillage));
        villages_spinner.setAdapter(VillagesAdapter);
        villages_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Village village = (Village) parent.getItemAtPosition(position);
                vilID = Integer.parseInt(village.getVillageId());
                populateGroups(vilID);
                btn_Capture.setVisibility(View.GONE);
                AserForm.setVisibility(View.GONE);
                groups_spinner.setSelection(0);
                resetFormPartially();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void populateGroups(int villageID) {
        //Get Groups Data for Villages filtered by Villages for Spinners
        GroupsVillages.clear();
        Groups grp = new Groups();
        grp.GroupId = "0";
        grp.GroupName = getString(R.string.selectgroup);
        GroupsVillages = AppDatabase.getDatabaseInstance(EditStudent.this).getGroupDao().GetGroups(villageID);
        GroupsVillages.add(0, grp);
        //Creating the ArrayAdapter instance having the Villages list
        List<Groups> SpinnerGroups = new ArrayList<>();
        for (int i = 0; i < GroupsVillages.size(); i++)
            SpinnerGroups.add(new Groups(GroupsVillages.get(i).GroupId, GroupsVillages.get(i).GroupName));

        final ArrayAdapter<Groups> GroupsAdapter = new ArrayAdapter<Groups>(this, R.layout.custom_spinner, SpinnerGroups);
        // Hint for AllSpinners
        groups_spinner.setPrompt(getString(R.string.selectgroup));
        groups_spinner.setAdapter(GroupsAdapter);
        groups_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                AserForm.setVisibility(View.GONE);
                btn_Capture.setVisibility(View.GONE);
                existingStudent_Spinner.setSelection(0);
                edt_Fname.setText("");
                edt_Mname.setText("");
                edt_Lname.setText("");
                edt_Age.setText("");
                edt_GuardianName.setText("");
                rg_Gender.clearCheck();
                sp_Class.setSelection(0);
                rb_Govt.setChecked(false);
                rb_Private.setChecked(false);
                imgView.setImageDrawable(null);
                btn_Capture.setVisibility(View.GONE);
                EndlineButtonClicked = false;
                btn_DatePicker.setText(Util.GetCurrentDate().toString());
                sp_BaselineLang.setSelection(0);
                sp_NumberReco.setSelection(0);
                sp_English.setSelection(0);
                setDefaults();
                AserForm.setVisibility(View.GONE);

                String GroupName = groups_spinner.getSelectedItem().toString();
                //GrpID = GroupsVillages.get(0).getGroupId();
                Groups SelectedGroupData = GroupsAdapter.getItem(groups_spinner.getSelectedItemPosition());
                GrpID = SelectedGroupData.getGroupId();
                String Id = GrpID;
                populateExistingStudents(Id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void populateExistingStudents(String GroupID) {
        ExistingStudents.clear();
        Student std = new Student();
        std.StudentId = "0";
        std.FirstName = getString(R.string.selectstudent);
        ExistingStudents = AppDatabase.getDatabaseInstance(EditStudent.this).getStudentDao().GetAllStudentsByGroupID(GroupID);
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
        //existingStudent_Spinner.setPrompt("Select Existing Student");
        existingStudent_Spinner.setAdapter(ExistingStudentAdapter);
        existingStudent_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String StdID = existingStudent_Spinner.getSelectedItem().toString();
                Student SelectedStudentData = ExistingStudentAdapter.getItem(existingStudent_Spinner.getSelectedItemPosition());
                StudentUniqID = SelectedStudentData.getStudentId();

                try {
                    FirstName = "";
                    MiddleName = "";
                    LastName = "";
                    edt_Fname.setText("");
                    edt_Mname.setText("");
                    edt_Lname.setText("");
                    edt_GuardianName.setText("");
                    rg_Gender.clearCheck();
                    btn_BirthDatePicker.setText(R.string.birth_date);
                    rb_Govt.setChecked(false);
                    rb_Private.setChecked(false);
                    rg_SchoolType.clearCheck();
                    sp_Class.setSelection(0);
                    phone_type.setSelection(0);
                    relation_with_phone_owner.setSelection(0);
                    edt_moNumber.setText("");
                    populateStudentData(StudentUniqID);
                } catch (Exception e) {
                    Modal_Log log = new Modal_Log();
                    log.setCurrentDateTime(new Utility().GetCurrentDate());
                    log.setErrorType("ERROR");
                    log.setExceptionMessage(e.getMessage());
                    log.setExceptionStackTrace(e.getStackTrace().toString());
                    log.setMethodName("EditStudent" + "_" + "populateExistingStudents");
                    log.setDeviceId("");
                    AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                    BackupDatabase.backup(ApplicationController.getInstance());

                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void populateStudentData(String studentUniqID) {
        // fetch student data
        Student SelectedStudent = AppDatabase.getDatabaseInstance(EditStudent.this).getStudentDao().GetStudentDataByStdID(studentUniqID);

        if (SelectedStudent == null && existingStudent_Spinner.getSelectedItemPosition() == 0) {
        } else if (SelectedStudent == null && existingStudent_Spinner.getSelectedItemPosition() > 0) {
            Toast.makeText(EditStudent.this, "Sorry !!! No Data Found !!!", Toast.LENGTH_SHORT).show();
        } else {
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

            edt_Mname.setVisibility(View.VISIBLE);
            edt_Lname.setVisibility(View.VISIBLE);
            edt_Fname.setText(getString(R.string.firstname) + " " + FirstName);
            edt_Mname.setText(getString(R.string.middlename) + " " + MiddleName);
            edt_Lname.setText(getString(R.string.lastname) + " " + LastName);

            if (SelectedStudent.FullName.contains(" ")) {
                edt_Mname.setVisibility(View.GONE);
                edt_Lname.setVisibility(View.GONE);
                edt_Fname.setText("Student Name : " + SelectedStudent.FullName);
            }

            Age = Integer.parseInt(SelectedStudent.Age);
            String gen = SelectedStudent.Gender;
            if (gen.equals("Male") || gen.equals("M") || gen.equals("1")) {
                Gender = "Male";
                rg_Gender.clearCheck();
                rb_Male.setChecked(true);
            } else if (gen.equals("Female") || gen.equals("F") || gen.equals("2")) {
                Gender = "Female";
                rg_Gender.clearCheck();
                rb_Female.setChecked(true);
            }

            try {
                if (SelectedStudent.GuardianName == null) {
                    edt_GuardianName.setText("");
                    guardian = "";
                    edt_GuardianName.setText(guardian);
                } else {
                    guardian = SelectedStudent.GuardianName;
                    edt_GuardianName.setText(guardian);
                }
            } catch (Exception e) {
                edt_GuardianName.setText("");
                guardian = "";
                edt_GuardianName.setText(guardian);
            }

            String cls = String.valueOf(SelectedStudent.Stud_Class);
            if (!cls.isEmpty()) {
                Class = Integer.parseInt(SelectedStudent.Stud_Class);
                if (Class == -1)
                    sp_Class.setSelection(13);
                else if (Class == -2)
                    sp_Class.setSelection(14);
                else if (Class == -3)
                    sp_Class.setSelection(15);
                else if (Class == -4)
                    sp_Class.setSelection(16);
                else if (Class == -5)
                    sp_Class.setSelection(17);
                else
                    try {
                        // in some cases class was 100 so try catch added
                        if (Class <= sp_Class.getAdapter().getCount()) {
                            sp_Class.setSelection(Class);
                        } else {
                            Toast.makeText(this, R.string.Invalid_class, Toast.LENGTH_SHORT).show();
                            sp_Class.setSelection(0);
                        }
                    } catch (Exception e) {
                        sp_Class.setSelection(0);
                    }

            } else {
                sp_Class.setSelection(0);
            }

            try {
                if (SelectedStudent.SchoolType != null) {
                    if (SelectedStudent.SchoolType.equalsIgnoreCase("1")) {
                        rg_SchoolType.clearCheck();
                        rb_Govt.setChecked(true);
                    } else if (SelectedStudent.SchoolType.equalsIgnoreCase("2")) {
                        rg_SchoolType.clearCheck();
                        rb_Private.setChecked(true);
                    } else {
                        rg_SchoolType.clearCheck();
                    }
                } else {
                    rg_SchoolType.clearCheck();
                }
            } catch (Exception e) {
                rb_Govt.setChecked(false);
                rb_Private.setChecked(false);
            }
            if (SelectedStudent.phoneType != null) {
                String[] phoneTypeConstants = getResources().getStringArray(R.array.phone_type_constants);
                int PhoneType = -1;
                for (int i = 0; i < phoneTypeConstants.length; i++) {
                    if (SelectedStudent.phoneType.trim().equalsIgnoreCase(phoneTypeConstants[i])) {
                        PhoneType = i;
                        break;
                    }
                }
                if (PhoneType != -1)
                    phone_type.setSelection(PhoneType);
            }

            if (SelectedStudent.relation_with_phone_owner != null) {
                String[] relation_with_phone_ownerArray = getResources().getStringArray(R.array.relation_with_phone_owner_constants);
                int PhoneOwner = -1;
                for (int i = 0; i < relation_with_phone_ownerArray.length; i++) {
                    if (SelectedStudent.relation_with_phone_owner.trim().equalsIgnoreCase(relation_with_phone_ownerArray[i])) {
                        PhoneOwner = i;
                        break;
                    }
                }
                if (PhoneOwner != -1)
                    relation_with_phone_owner.setSelection(PhoneOwner);
            }
            if (SelectedStudent.getPhoneNo() != null) {
                edt_moNumber.setText(SelectedStudent.getPhoneNo());
            }
        }

        // set student data
        if (FirstName == null) {
            btn_Capture.setVisibility(View.GONE);
        } else {
            AserForm.setVisibility(View.VISIBLE);

            edt_Mname.setVisibility(View.VISIBLE);
            edt_Lname.setVisibility(View.VISIBLE);
            edt_Fname.setText(getString(R.string.firstname) + " " + FirstName);
            edt_Mname.setText(getString(R.string.middlename) + " " + MiddleName);
            edt_Lname.setText(getString(R.string.lastname) + " " + LastName);

            if (SelectedStudent.FullName.contains(" ")) {
                edt_Mname.setVisibility(View.GONE);
                edt_Lname.setVisibility(View.GONE);
                edt_Fname.setText("Student Name : " + SelectedStudent.FullName);
            }
            edt_Age.setText(getString(R.string.age) + " : " + String.valueOf(Age));


            // tv_Gender.setText(getString(R.string.gender) + " : " + Gender);
            btn_Capture.setVisibility(View.VISIBLE);
            btn_Capture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_Thumbnail);
                }
            });
        }


        String dob = SelectedStudent.getDOB();
        try {
            if (dob != null && !dob.isEmpty()) {
                btn_BirthDatePicker.setText(dob);
            } else {
                btn_BirthDatePicker.setText(R.string.birth_date);
            }
        } catch (Exception e) {

        }


        // fetch aser data for baseline i.e TestType = 0
        AserData = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().GetAllByStudentID(studentUniqID, 0);
        if (AserData == null || AserData.size() == 0) {
            setDefaults();
            sp_BaselineLang.setSelection(0);
            sp_NumberReco.setSelection(0);
            sp_English.setSelection(0);
            btn_DatePicker.setText(Util.GetCurrentDate().toString());
        } else {
            // fetch baseline aser
            testT = 0;
            langSpin = AserData.get(0).Lang;
            numSpin = AserData.get(0).Num;
            engSpin = AserData.get(0).English;
            engMeaning = AserData.get(0).EnglishSelected;
            AserTestDate = AserData.get(0).TestDate;
            // set baseline aser
            sp_BaselineLang.setSelection(langSpin);
            sp_NumberReco.setSelection(numSpin);
            sp_English.setSelection(engSpin);
            btn_DatePicker.setText(AserTestDate);
            EndlineButtonClicked = false;
            OA = 0;
            OS = 0;
            OM = 0;
            OD = 0;
            WA = 0;
            WS = 0;
        }


    }

    public void FormReset() {
        rb_Govt.setChecked(false);
        rb_Private.setChecked(false);
        rg_SchoolType.clearCheck();
        rg_Gender.clearCheck();
        phone_type.setSelection(0);
        relation_with_phone_owner.setSelection(0);
        edt_moNumber.setText("");
        sp_Class.setSelection(0);
        edt_GuardianName.setText("");
        blocks_spinner.setSelection(0);
        villages_spinner.setSelection(0);
        groups_spinner.setSelection(0);
        existingStudent_Spinner.setSelection(0);
        edt_Fname.setText("");
        edt_Mname.setText("");
        edt_Lname.setText("");
        edt_Age.setText("");
        sp_BaselineLang.setSelection(0);
        sp_NumberReco.setSelection(0);
        btn_DatePicker.setText(Util.GetCurrentDate().toString());
        imgView.setImageDrawable(null);
        EndlineButtonClicked = false;
        btn_Capture.setVisibility(View.GONE);
        setDefaults();
        AserForm.setVisibility(View.GONE);
    }

    private void resetFormPartially() {
        rb_Govt.setChecked(false);
        rb_Private.setChecked(false);
        rg_Gender.clearCheck();
        phone_type.setSelection(0);
        relation_with_phone_owner.setSelection(0);
        edt_moNumber.setText("");
        sp_Class.setSelection(0);
        edt_GuardianName.setText("");
        existingStudent_Spinner.setSelection(0);
        edt_Fname.setText("");
        edt_Mname.setText("");
        edt_Lname.setText("");
        edt_Age.setText("");
        imgView.setImageDrawable(null);
        btn_Capture.setVisibility(View.GONE);
        EndlineButtonClicked = false;
        btn_DatePicker.setText(Util.GetCurrentDate().toString());
        sp_BaselineLang.setSelection(0);
        sp_NumberReco.setSelection(0);
        sp_English.setSelection(0);
        btn_BirthDatePicker.setText(R.string.birth_date);
        setDefaults();
        AserForm.setVisibility(View.GONE);
    }

    public void setDefaults() {
        testT = 0;
        langSpin = 0;
        numSpin = 0;
        OA = 0;
        OS = 0;
        OM = 0;
        OD = 0;
        WA = 0;
        WS = 0;
        engSpin = 0;
        engMeaning = 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TAKE_Thumbnail) {
            if (data != null) {
                if (data.hasExtra("data")) {
                    Bitmap thumbnail1 = data.getParcelableExtra("data");
                    imgView.setImageBitmap(thumbnail1);
                    try {
                        captureButtonPressed = true;
                        Context cnt;
                        cnt = this;
                        File folder = new File(Environment.getExternalStorageDirectory() + "/.POSinternal/StudentProfiles/");
                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdir();
                        }
                        if (success) {
                            // Do something on success
                            File outputFile = new File(folder, StudentUniqID + ".jpg");
                            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                            thumbnail1.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            data = null;
                            thumbnail1 = null;
                            // To Refresh Contents of sharableFolder
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outputFile)));
                        } else {
                            // Do something else on failure
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

