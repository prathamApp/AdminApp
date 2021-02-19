package com.pratham.admin.activities;

//http://www.deboma.com/article/mobile-development/22/android-datepicker-and-age-calculation

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
import java.util.UUID;

public class AddNewStudent extends BaseActivity {

    private static final int TAKE_Thumbnail = 1;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static String TAG = "PermissionDemo";
    public boolean EndlineButtonClicked = false;
    Spinner blocks_spinner, villages_spinner, groups_spinner, sp_Class, phone_type, relation_with_phone_owner;
    EditText edt_Fname, edt_Mname, edt_Lname, edt_GuardianName, edt_moNumber;
    RadioGroup rg_Gender, rg_SchoolType;
    Button btn_Submit_Baseline, btn_Clear, btn_Capture, btn_BirthDatePicker;
    RadioButton rb_Male, rb_Female, rb_Govt, rb_Private;
    TextView edt_Age;
    String GrpID;
    List<String> Blocks = new ArrayList<>();
    int vilID;
    String gender;
    String randomUUIDStudent;
    ImageView imgView;
    UUID uuStdid;
    RadioButton selectedGender, selectedSchoolType;
    List<Groups> GroupsVillages = new ArrayList<Groups>();
    int stdAge = 0;
    Utility util;
    Spinner sp_BaselineLang, sp_NumberReco, sp_English;
    Button btn_EndlineDatePicker, btn_DatePicker, btn_Endline1, btn_Endline2, btn_Endline3, btn_Endline4;
    LinearLayout AserForm;
    int testT, langSpin, numSpin, engSpin;
    int OA = 0;
    int OS = 0;
    int OM = 0;
    int OD = 0;
    int WA = 0;
    int WS = 0;
    String aserDate;
    private String GrpName = "";
    int engMeaning = 0;
    AlertDialog meaningDialog;
    CharSequence[] values = {" Yes", "No"};

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
                Toast.makeText(AddNewStudent.this, "something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            edt_Age.setText("Age : " + stdAge);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_student);
        getSupportActionBar().hide();

        //EventBus.getDefault().register(AddNewStudent.this);

        initializeVariables();
        initializeClassSpinner();
        initializeState();
        initializeBaselineSpinner();
        initializeNumberRecoSpinner();
        initializeAserDate();
        initializeBirthDate();
        initializeEnglishSpinner();
        initializePhoneNumberSpinner();
        sp_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                // if Word or Sentence selected
                if (pos == 4 || pos == 5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddNewStudent.this);
                    builder.setTitle("Meaning : ");
                    builder.setSingleChoiceItems(values, 1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0:
                                    engMeaning = 1;
                                    break;
                                case 1:
                                    engMeaning = 2;
                                    break;
                            }
                        }
                    });
                    builder.setPositiveButton(R.string.Okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
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

        btn_Capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_Thumbnail);
            }
        });

        btn_Endline1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaults();

                // initialize dialog
                final Dialog endlineDialog = new Dialog(AddNewStudent.this);
                endlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                endlineDialog.setContentView(R.layout.fragment_endline_dialog);
                endlineDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                // initialize dialog's widgets
                TextView title = endlineDialog.findViewById(R.id.tv_EndlineTitle);
                final Spinner spinner_BaselineLang = endlineDialog.findViewById(R.id.spinner_BaselineLang);
                final Spinner spinner_NumberReco = endlineDialog.findViewById(R.id.spinner_NumberReco);
                final Spinner spinner_English = endlineDialog.findViewById(R.id.spinner_Engish);
                final RadioGroup rg_Meaning = endlineDialog.findViewById(R.id.rg_EngSelected);
                final CheckBox OprAdd = endlineDialog.findViewById(R.id.OprAdd);
                final CheckBox OprSub = endlineDialog.findViewById(R.id.OprSub);
                final CheckBox OprMul = endlineDialog.findViewById(R.id.OprMul);
                final CheckBox OprDiv = endlineDialog.findViewById(R.id.OprDiv);
                final TextView tv_WordProblem = endlineDialog.findViewById(R.id.tv_WordProblem);
                final CheckBox WordAdd = endlineDialog.findViewById(R.id.WordAdd);
                final CheckBox WordSub = endlineDialog.findViewById(R.id.WordSub);
                Button btn_Submit_endline1 = endlineDialog.findViewById(R.id.btn_Submit);
                btn_EndlineDatePicker = endlineDialog.findViewById(R.id.btn_EndlineDatePicker);
                btn_EndlineDatePicker.setText(util.GetCurrentDate().toString());
                btn_EndlineDatePicker.setPadding(8, 8, 8, 8);
                btn_EndlineDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getFragmentManager(), "EndlineDatePicker");
                    }
                });

                // set values of endline
                title.setText(R.string.endline1);

                String[] baselineLangAdapter = {"Language", "Beg", "Letter", "Word", "Para", "Story"};
                ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, baselineLangAdapter);
                spinner_BaselineLang.setAdapter(baselineAdapter);

                //String[] NumberRecoAdapter = {"Number Recognition", "Beg", "0-9", "10-99", "Sub", "Div"};
                ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_el_numRecoSSpinner));
                spinner_NumberReco.setAdapter(recoAdapter);

                //String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
                ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_englishAdapter));
                spinner_English.setAdapter(EnglishAdapter);

                spinner_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                        // if Word or Sentence selected
                        if (pos == 4 || pos == 5) {
                            rg_Meaning.setVisibility(View.VISIBLE);
                        } else {
                            rg_Meaning.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                // show dialog
                endlineDialog.setCanceledOnTouchOutside(false);
                endlineDialog.show();

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
                        int EnglishSpinnerValue = spinner_English.getSelectedItemPosition();

//                        if (BaselineSpinnerValue > 0 && NumberSpinnerValue > 0 && EnglishSpinnerValue > 0) {
                        if (BaselineSpinnerValue > 0 || NumberSpinnerValue > 0 || EnglishSpinnerValue > 0) {
                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            sp_English.setSelection(0);
                            EndlineButtonClicked = true;

                            testT = 1;
                            langSpin = BaselineSpinnerValue;
                            numSpin = NumberSpinnerValue;
                            engSpin = EnglishSpinnerValue;

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
                            aserDate = btn_EndlineDatePicker.getText().toString();
                            OA = OprAdd.isChecked() ? 1 : 0;
                            OS = OprSub.isChecked() ? 1 : 0;
                            OM = OprMul.isChecked() ? 1 : 0;
                            OD = OprDiv.isChecked() ? 1 : 0;
                            WA = WordAdd.isChecked() ? 1 : 0;
                            WS = WordSub.isChecked() ? 1 : 0;

                            Toast.makeText(AddNewStudent.this, "Endline 1 is filled.", Toast.LENGTH_SHORT).show();

                            if (endlineDialog.isShowing())
                                endlineDialog.dismiss();
                        } else {
                            Toast.makeText(AddNewStudent.this, "Please fill all the fields !!!", Toast.LENGTH_SHORT).show();
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
                final Dialog endlineDialog = new Dialog(AddNewStudent.this);
                endlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                endlineDialog.setContentView(R.layout.fragment_endline_dialog);
                endlineDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                // initialize dialog's widgets
                TextView title = endlineDialog.findViewById(R.id.tv_EndlineTitle);
                final Spinner spinner_BaselineLang = endlineDialog.findViewById(R.id.spinner_BaselineLang);
                final Spinner spinner_NumberReco = endlineDialog.findViewById(R.id.spinner_NumberReco);
                final Spinner spinner_English = endlineDialog.findViewById(R.id.spinner_Engish);
                final RadioGroup rg_Meaning = endlineDialog.findViewById(R.id.rg_EngSelected);
                final CheckBox OprAdd = endlineDialog.findViewById(R.id.OprAdd);
                final CheckBox OprSub = endlineDialog.findViewById(R.id.OprSub);
                final CheckBox OprMul = endlineDialog.findViewById(R.id.OprMul);
                final CheckBox OprDiv = endlineDialog.findViewById(R.id.OprDiv);
                final TextView tv_WordProblem = endlineDialog.findViewById(R.id.tv_WordProblem);
                final CheckBox WordAdd = endlineDialog.findViewById(R.id.WordAdd);
                final CheckBox WordSub = endlineDialog.findViewById(R.id.WordSub);
                Button btn_Submit_endline2 = endlineDialog.findViewById(R.id.btn_Submit);
                btn_EndlineDatePicker = endlineDialog.findViewById(R.id.btn_EndlineDatePicker);
                btn_EndlineDatePicker.setText(util.GetCurrentDate().toString());
                btn_EndlineDatePicker.setPadding(8, 8, 8, 8);
                btn_EndlineDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getFragmentManager(), "EndlineDatePicker");
                    }
                });

                // set values of endline
                title.setText(R.string.endline2);

                String[] baselineLangAdapter = {"Language", "Beg", "Letter", "Word", "Para", "Story"};
                ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, baselineLangAdapter);
                spinner_BaselineLang.setAdapter(baselineAdapter);

                //String[] NumberRecoAdapter = {"Number Recognition", "Beg", "0-9", "10-99", "Sub", "Div"};
                ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_el_numRecoSSpinner));
                spinner_NumberReco.setAdapter(recoAdapter);

                //String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
                ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_englishAdapter));
                spinner_English.setAdapter(EnglishAdapter);

                spinner_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                        // if Word or Sentence selected
                        if (pos == 4 || pos == 5) {
                            rg_Meaning.setVisibility(View.VISIBLE);
                        } else {
                            rg_Meaning.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });


                // show dialog
                endlineDialog.setCanceledOnTouchOutside(false);
                endlineDialog.show();

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
                        int EnglishSpinnerValue = spinner_English.getSelectedItemPosition();

                        if (BaselineSpinnerValue > 0 || NumberSpinnerValue > 0 || EnglishSpinnerValue > 0) {
                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            sp_English.setSelection(0);
                            EndlineButtonClicked = true;

                            testT = 2;
                            langSpin = BaselineSpinnerValue;
                            numSpin = NumberSpinnerValue;
                            engSpin = EnglishSpinnerValue;

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

                            aserDate = btn_EndlineDatePicker.getText().toString();

                            OA = OprAdd.isChecked() ? 1 : 0;
                            OS = OprSub.isChecked() ? 1 : 0;
                            OM = OprMul.isChecked() ? 1 : 0;
                            OD = OprDiv.isChecked() ? 1 : 0;
                            WA = WordAdd.isChecked() ? 1 : 0;
                            WS = WordSub.isChecked() ? 1 : 0;

                            Toast.makeText(AddNewStudent.this, "Endline 2 is filled.", Toast.LENGTH_SHORT).show();

                            if (endlineDialog.isShowing())
                                endlineDialog.dismiss();
                        } else {
                            Toast.makeText(AddNewStudent.this, "Please fill all the fields !!!", Toast.LENGTH_SHORT).show();
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
                final Dialog endlineDialog = new Dialog(AddNewStudent.this);
                endlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                endlineDialog.setContentView(R.layout.fragment_endline_dialog);
                endlineDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                // initialize dialog's widgets
                TextView title = endlineDialog.findViewById(R.id.tv_EndlineTitle);
                final Spinner spinner_BaselineLang = endlineDialog.findViewById(R.id.spinner_BaselineLang);
                final Spinner spinner_NumberReco = endlineDialog.findViewById(R.id.spinner_NumberReco);
                final Spinner spinner_English = endlineDialog.findViewById(R.id.spinner_Engish);
                final RadioGroup rg_Meaning = endlineDialog.findViewById(R.id.rg_EngSelected);
                final CheckBox OprAdd = endlineDialog.findViewById(R.id.OprAdd);
                final CheckBox OprSub = endlineDialog.findViewById(R.id.OprSub);
                final CheckBox OprMul = endlineDialog.findViewById(R.id.OprMul);
                final CheckBox OprDiv = endlineDialog.findViewById(R.id.OprDiv);
                final TextView tv_WordProblem = endlineDialog.findViewById(R.id.tv_WordProblem);
                final CheckBox WordAdd = endlineDialog.findViewById(R.id.WordAdd);
                final CheckBox WordSub = endlineDialog.findViewById(R.id.WordSub);
                Button btn_Submit_endline3 = endlineDialog.findViewById(R.id.btn_Submit);
                btn_EndlineDatePicker = endlineDialog.findViewById(R.id.btn_EndlineDatePicker);
                btn_EndlineDatePicker.setText(util.GetCurrentDate().toString());
                btn_EndlineDatePicker.setPadding(8, 8, 8, 8);
                btn_EndlineDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getFragmentManager(), "EndlineDatePicker");
                    }
                });

                // set values of endline
                title.setText(R.string.endline3);

                String[] baselineLangAdapter = {"Language", "Beg", "Letter", "Word", "Para", "Story"};
                ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, baselineLangAdapter);
                spinner_BaselineLang.setAdapter(baselineAdapter);

                //String[] NumberRecoAdapter = {"Number Recognition", "Beg", "0-9", "10-99", "Sub", "Div"};
                ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_el_numRecoSSpinner));
                spinner_NumberReco.setAdapter(recoAdapter);

                //String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
                ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_englishAdapter));
                spinner_English.setAdapter(EnglishAdapter);

                spinner_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                        // if Word or Sentence selected
                        if (pos == 4 || pos == 5) {
                            rg_Meaning.setVisibility(View.VISIBLE);
                        } else {
                            rg_Meaning.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });


                // show dialog
                endlineDialog.setCanceledOnTouchOutside(false);
                endlineDialog.show();

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
                        int EnglishSpinnerValue = spinner_English.getSelectedItemPosition();

                        if (BaselineSpinnerValue > 0 || NumberSpinnerValue > 0 || EnglishSpinnerValue > 0) {
                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            sp_English.setSelection(0);
                            EndlineButtonClicked = true;

                            testT = 3;
                            langSpin = BaselineSpinnerValue;
                            numSpin = NumberSpinnerValue;
                            engSpin = EnglishSpinnerValue;
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

                            aserDate = btn_EndlineDatePicker.getText().toString();

                            OA = OprAdd.isChecked() ? 1 : 0;
                            OS = OprSub.isChecked() ? 1 : 0;
                            OM = OprMul.isChecked() ? 1 : 0;
                            OD = OprDiv.isChecked() ? 1 : 0;
                            WA = WordAdd.isChecked() ? 1 : 0;
                            WS = WordSub.isChecked() ? 1 : 0;

                            Toast.makeText(AddNewStudent.this, "Endline 3 is filled.", Toast.LENGTH_SHORT).show();

                            if (endlineDialog.isShowing())
                                endlineDialog.dismiss();
                        } else {
                            Toast.makeText(AddNewStudent.this, R.string.fillAllFields, Toast.LENGTH_SHORT).show();
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
                final Dialog endlineDialog = new Dialog(AddNewStudent.this);
                endlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                endlineDialog.setContentView(R.layout.fragment_endline_dialog);
                endlineDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                // initialize dialog's widgets
                TextView title = endlineDialog.findViewById(R.id.tv_EndlineTitle);
                final Spinner spinner_BaselineLang = endlineDialog.findViewById(R.id.spinner_BaselineLang);
                final Spinner spinner_NumberReco = endlineDialog.findViewById(R.id.spinner_NumberReco);
                final Spinner spinner_English = endlineDialog.findViewById(R.id.spinner_Engish);
                final RadioGroup rg_Meaning = endlineDialog.findViewById(R.id.rg_EngSelected);
                final CheckBox OprAdd = endlineDialog.findViewById(R.id.OprAdd);
                final CheckBox OprSub = endlineDialog.findViewById(R.id.OprSub);
                final CheckBox OprMul = endlineDialog.findViewById(R.id.OprMul);
                final CheckBox OprDiv = endlineDialog.findViewById(R.id.OprDiv);
                final TextView tv_WordProblem = endlineDialog.findViewById(R.id.tv_WordProblem);
                final CheckBox WordAdd = endlineDialog.findViewById(R.id.WordAdd);
                final CheckBox WordSub = endlineDialog.findViewById(R.id.WordSub);
                Button btn_Submit_endline4 = endlineDialog.findViewById(R.id.btn_Submit);
                btn_EndlineDatePicker = endlineDialog.findViewById(R.id.btn_EndlineDatePicker);
                btn_EndlineDatePicker.setText(util.GetCurrentDate().toString());
                btn_EndlineDatePicker.setPadding(8, 8, 8, 8);
                btn_EndlineDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getFragmentManager(), "EndlineDatePicker");
                    }
                });

                // set values of endline
                title.setText(R.string.end);

                String[] baselineLangAdapter = {"Language", "Beg", "Letter", "Word", "Para", "Story"};
                ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, baselineLangAdapter);
                spinner_BaselineLang.setAdapter(baselineAdapter);

                //String[] NumberRecoAdapter = {"Number Recognition", "Beg", "0-9", "10-99", "Sub", "Div"};
                ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_el_numRecoSSpinner));
                spinner_NumberReco.setAdapter(recoAdapter);

                //String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
                ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_englishAdapter));
                spinner_English.setAdapter(EnglishAdapter);

                spinner_English.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                        // if Word or Sentence selected
                        if (pos == 4 || pos == 5) {
                            rg_Meaning.setVisibility(View.VISIBLE);
                        } else {
                            rg_Meaning.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });


                // show dialog
                endlineDialog.setCanceledOnTouchOutside(false);
                endlineDialog.show();

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
                        int EnglishSpinnerValue = spinner_English.getSelectedItemPosition();

                        if (BaselineSpinnerValue > 0 || NumberSpinnerValue > 0 || EnglishSpinnerValue > 0) {
                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            sp_English.setSelection(0);
                            EndlineButtonClicked = true;

                            testT = 4;
                            langSpin = BaselineSpinnerValue;
                            numSpin = NumberSpinnerValue;
                            engSpin = EnglishSpinnerValue;
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

                            aserDate = btn_EndlineDatePicker.getText().toString();

                            OA = OprAdd.isChecked() ? 1 : 0;
                            OS = OprSub.isChecked() ? 1 : 0;
                            OM = OprMul.isChecked() ? 1 : 0;
                            OD = OprDiv.isChecked() ? 1 : 0;
                            WA = WordAdd.isChecked() ? 1 : 0;
                            WS = WordSub.isChecked() ? 1 : 0;

                            Toast.makeText(AddNewStudent.this, "Endline 4 is filled.", Toast.LENGTH_SHORT).show();

                            if (endlineDialog.isShowing())
                                endlineDialog.dismiss();
                        } else {
                            Toast.makeText(AddNewStudent.this, R.string.fillAllFields, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btn_Submit_Baseline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get selected radio button from radioGroup
                int selectedId = rg_Gender.getCheckedRadioButtonId();
                int genderId = rg_Gender.getCheckedRadioButtonId();
                // find the radio button by returned id
                selectedGender = (RadioButton) findViewById(selectedId);
               // gender = selectedGender.getText().toString();

                if (genderId == rb_Male.getId()) {
                    gender = "Male";
                } else if (genderId == rb_Female.getId()) {
                    gender = "Female";
                }

               /* if (gender.equals(getString(R.string.male)))
                    gender = "Male";
                else if (gender.equals(getString(R.string.female)))
                    gender = "Female";*/

                // get selected radio button from radioGroup
                int selId = rg_SchoolType.getCheckedRadioButtonId();
                // find the radio button by returned id
                selectedSchoolType = (RadioButton) findViewById(selId);
                String schoolType = selectedSchoolType.getText().toString();
                String stdSchoolType = "0";
               /* if (schoolType.equalsIgnoreCase(getString(R.string.government)))
                    stdSchoolType = 1;
                else if (schoolType.equalsIgnoreCase(getString(R.string.privat)))
                    stdSchoolType = 2;*/


                if (selId==rb_Govt.getId())
                    stdSchoolType = "1";
                else if (selId==rb_Private.getId())
                    stdSchoolType = "2";





                // Check AllSpinners Emptyness
                int ClassSpinnerValue = sp_Class.getSelectedItemPosition();
                int BlocksSpinnerValue = blocks_spinner.getSelectedItemPosition();
                int VillagesSpinnerValue = villages_spinner.getSelectedItemPosition();
                int GroupsSpinnerValue = groups_spinner.getSelectedItemPosition();

                // Spinners Selection
                if (BlocksSpinnerValue > 0 && VillagesSpinnerValue > 0 && GroupsSpinnerValue > 0 && ClassSpinnerValue > 0) {
                    // Checking Emptyness
                    if ((!edt_Fname.getText().toString().isEmpty() || !edt_Lname.getText().toString().isEmpty())) {
                        // Validations
                        if ((edt_Fname.getText().toString().matches("[a-zA-Z.? ]*")) && (edt_Lname.getText().toString().matches("[a-zA-Z.? ]*"))
                                && (edt_Mname.getText().toString().matches("[a-zA-Z.? ]*"))
                                && (!btn_BirthDatePicker.getText().toString().equalsIgnoreCase(getString(R.string.birth_date))) && (!edt_GuardianName.getText().toString().isEmpty())) {

                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                            try {
                                cal.setTime(sdf.parse(btn_BirthDatePicker.getText().toString()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            stdAge = Integer.parseInt(Integer.toString(calculateAge(cal.getTimeInMillis())));

                            /*// either baseline spinners are fully filled or not filled at all
                            if ((sp_BaselineLang.getSelectedItemPosition() > 0 && sp_NumberReco.getSelectedItemPosition() > 0 && sp_English.getSelectedItemPosition() > 0)
                                    || (sp_BaselineLang.getSelectedItemPosition() == 0 && sp_NumberReco.getSelectedItemPosition() == 0 && sp_English.getSelectedItemPosition() == 0)) {
                            */    // Populate Std Data
                            Student stdObj = new Student();
                            stdObj.StudentId = randomUUIDStudent;
                            stdObj.FirstName = edt_Fname.getText().toString();
                            stdObj.MiddleName = edt_Mname.getText().toString();
                            stdObj.LastName = edt_Lname.getText().toString();
                            stdObj.FullName = edt_Fname.getText().toString() + " " + edt_Mname.getText().toString() + " " + edt_Lname.getText().toString();
                            stdObj.Age = String.valueOf(stdAge);

                            // get Class
                            if (sp_Class.getSelectedItem().toString().equalsIgnoreCase("Anganwadi"))
                                stdObj.Stud_Class = String.valueOf(-1);
                            else if (sp_Class.getSelectedItem().toString().equalsIgnoreCase("Pre-School"))
                                stdObj.Stud_Class = String.valueOf(-2);
                            else if (sp_Class.getSelectedItem().toString().equalsIgnoreCase("Balwadi"))
                                stdObj.Stud_Class = String.valueOf(-3);
                            else if (sp_Class.getSelectedItem().toString().equalsIgnoreCase("Dropout"))
                                stdObj.Stud_Class = String.valueOf(-4);
                            else if (sp_Class.getSelectedItem().toString().equalsIgnoreCase("Not Enrolled"))
                                stdObj.Stud_Class = String.valueOf(-5);
                            else
                                stdObj.Stud_Class = String.valueOf(ClassSpinnerValue);

                            stdObj.GuardianName = edt_GuardianName.getText().toString();
                            stdObj.SchoolType = stdSchoolType;

                            stdObj.UpdatedDate = util.GetCurrentDateTime(false);
                            stdObj.Gender = gender;
                            stdObj.GroupId = GrpID;
                            stdObj.GroupName = GrpName;
                            stdObj.CreatedBy = AppDatabase.getDatabaseInstance(AddNewStudent.this).getMetaDataDao().getCrlMetaData();
                            stdObj.CreatedOn = util.GetCurrentDateTime(false).toString();
                            stdObj.DOB = btn_BirthDatePicker.getText().toString();
                            stdObj.sentFlag = 0;

                            if (sp_BaselineLang.getSelectedItemPosition() > 0 || sp_NumberReco.getSelectedItemPosition() > 0 || sp_English.getSelectedItemPosition() > 0)
                                EndlineButtonClicked = false;

                            if (!EndlineButtonClicked) {
                                // Baseline
                                testT = 0;
                                langSpin = sp_BaselineLang.getSelectedItemPosition();
                                numSpin = sp_NumberReco.getSelectedItemPosition();
                                engSpin = sp_English.getSelectedItemPosition();
                                aserDate = btn_DatePicker.getText().toString();
                                OA = 0;
                                OS = 0;
                                OM = 0;
                                OD = 0;
                                WA = 0;
                                WS = 0;
                                // get yes or not
                                if (engSpin == 0)
                                    engMeaning = 0;
                            }
                            // Populate Aser Data
                            Aser asr = new Aser();
                            asr.StudentId = randomUUIDStudent;
                            asr.GroupID = GrpID;
                            asr.ChildID = "";
                            asr.TestType = testT;
                            asr.TestDate = aserDate;
                            asr.Lang = langSpin;
                            asr.Num = numSpin;
                            asr.English = engSpin;
                            asr.EnglishSelected = engMeaning;
                            asr.CreatedBy = AppDatabase.getDatabaseInstance(AddNewStudent.this).getMetaDataDao().getCrlMetaData();
                            asr.CreatedDate = new Utility().GetCurrentDate();
                            asr.DeviceId = util.GetDeviceID();
                            asr.OAdd = OA;
                            asr.OSub = OS;
                            asr.OMul = OM;
                            asr.ODiv = OD;
                            asr.WAdd = WA;
                            asr.WSub = WS;
                            asr.sentFlag = 0;
                            asr.CreatedOn = new Utility().GetCurrentDateTime(false);
                            String moNumber = edt_moNumber.getText().toString();
                            if (validatePhoneDetails()) {
                                stdObj.phoneNo = moNumber;
                                stdObj.phoneType = getPhoneType();
                                stdObj.relation_with_phone_owner = getRelationWithPhoneOwner();
                                try {
                                    AppDatabase.getDatabaseInstance(AddNewStudent.this).getStudentDao().insertStudent(stdObj);
                                    AppDatabase.getDatabaseInstance(AddNewStudent.this).getAserDao().insertAser(asr);
                                    Toast.makeText(AddNewStudent.this, R.string.recInsertSuccess, Toast.LENGTH_SHORT).show();
                                    BackupDatabase.backup(AddNewStudent.this);
                                    resetFormPartially();

                                } catch (Exception e) {
                                    Modal_Log log = new Modal_Log();
                                    log.setCurrentDateTime(new Utility().GetCurrentDate());
                                    log.setErrorType("ERROR");
                                    log.setExceptionMessage(e.getMessage());
                                    log.setExceptionStackTrace(e.getStackTrace().toString());
                                    log.setMethodName("AddNewStudent" + "_" + "Submit");
                                    log.setDeviceId("");
                                    AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                                    BackupDatabase.backup(ApplicationController.getInstance());

                                    e.printStackTrace();
                                    Toast.makeText(AddNewStudent.this, R.string.recInsertFailed, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(AddNewStudent.this, R.string.enterValidInput, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddNewStudent.this, R.string.fillAllFields, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddNewStudent.this, R.string.fillAllFields, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, R.string.select_phone_owner, Toast.LENGTH_SHORT).show();
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
        relation_with_phone_owner.setSelection(0);
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

    private void resetFormPartially() {
        edt_Fname.getText().clear();
        edt_Mname.getText().clear();
        edt_Lname.getText().clear();
        edt_moNumber.getText().clear();
        edt_Age.setText("");
        edt_GuardianName.getText().clear();
        imgView.setImageDrawable(null);
        UUID uuStdid = UUID.randomUUID();
        randomUUIDStudent = uuStdid.toString();
        EndlineButtonClicked = false;
        btn_BirthDatePicker.setText(getString(R.string.birth_date));
        btn_DatePicker.setText(util.GetCurrentDate().toString());
        sp_Class.setSelection(0);
        sp_BaselineLang.setSelection(0);
        sp_NumberReco.setSelection(0);
        sp_English.setSelection(0);
        relation_with_phone_owner.setSelection(0);
        phone_type.setSelection(0);
        setDefaults();
    }


    public void setDefaults() {
        testT = 0;
        langSpin = 0;
        numSpin = 0;
        engSpin = 0;
        engMeaning = 0;
        OA = 0;
        OS = 0;
        OM = 0;
        OD = 0;
        WA = 0;
        WS = 0;
    }

    private void initializeVariables() {
        sp_NumberReco = findViewById(R.id.spinner_NumberReco);
        sp_BaselineLang = findViewById(R.id.spinner_BaselineLang);
        sp_English = findViewById(R.id.spinner_Engish);
        sp_Class = findViewById(R.id.sp_Class);
        blocks_spinner = (Spinner) findViewById(R.id.spinner_SelectBlock);
        villages_spinner = (Spinner) findViewById(R.id.spinner_selectVillage);
        groups_spinner = (Spinner) findViewById(R.id.spinner_SelectGroups);
        rb_Male = (RadioButton) findViewById(R.id.rb_Male);
        rb_Female = (RadioButton) findViewById(R.id.rb_Female);
        rb_Govt = (RadioButton) findViewById(R.id.rb_Govt);
        rb_Private = (RadioButton) findViewById(R.id.rb_Private);
        uuStdid = UUID.randomUUID();
        randomUUIDStudent = uuStdid.toString();
        edt_Fname = (EditText) findViewById(R.id.edt_FirstName);
        edt_Mname = (EditText) findViewById(R.id.edt_MiddleName);
        edt_Lname = (EditText) findViewById(R.id.edt_LastName);
        phone_type = (Spinner) findViewById(R.id.phone_type);
        relation_with_phone_owner = (Spinner) findViewById(R.id.relation_with_phone_owner);
        edt_moNumber = (EditText) findViewById(R.id.edt_moNumber);
        edt_GuardianName = (EditText) findViewById(R.id.edt_GuardianName);
        rg_Gender = (RadioGroup) findViewById(R.id.rg_Gender);
        rg_SchoolType = (RadioGroup) findViewById(R.id.rg_SchoolType);
        btn_Capture = (Button) findViewById(R.id.btn_Capture);
        imgView = (ImageView) findViewById(R.id.imageView);
        btn_Submit_Baseline = (Button) findViewById(R.id.btn_Submit);
        btn_Clear = (Button) findViewById(R.id.btn_Clear);
        util = new Utility();
        btn_DatePicker = findViewById(R.id.btn_DatePicker);
        btn_BirthDatePicker = findViewById(R.id.btn_BirthDatePicker);
        btn_Endline1 = findViewById(R.id.btn_Endline1);
        btn_Endline2 = findViewById(R.id.btn_Endline2);
        btn_Endline3 = findViewById(R.id.btn_Endline3);
        btn_Endline4 = findViewById(R.id.btn_Endline4);
        AserForm = findViewById(R.id.AserForm);
        edt_Age = findViewById(R.id.age);
    }

    private void initializeAserDate() {
        btn_DatePicker.setText(util.GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);
        btn_DatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "DatePicker");

            }
        });
    }


    private void initializeBirthDate() {
        btn_BirthDatePicker.setPadding(8, 8, 8, 8);
        btn_BirthDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new BirthDatePickerFragment();
                newFragment.show(getFragmentManager(), "BirthDatePicker");
            }
        });
    }

    private void initializeClassSpinner() {
        ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_Class));
        sp_Class.setAdapter(classAdapter);
    }

    private void initializeNumberRecoSpinner() {
//        String[] NumberRecoAdapter = {"Baseline (Number Recognition)", "Beg", "0-9", "10-99", "Sub", "Div"};
        ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_numRecoSSpinner));
        //sp_NumberReco.setPrompt("Number Reco Level");
        sp_NumberReco.setAdapter(recoAdapter);
    }

    private void initializeEnglishSpinner() {
        //String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
        ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, getResources().getStringArray(R.array.array_englishAdapter));
        sp_English.setAdapter(EnglishAdapter);
    }

    private void initializeBaselineSpinner() {
        //sp_BaselineLang.setPrompt("Baseline Level");
        String[] baselineLangAdapter = {"Baseline (Lang)", "Beg", "Letter", "Word", "Para", "Story"};
        ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, baselineLangAdapter);
        sp_BaselineLang.setAdapter(baselineAdapter);
    }

    //Intialize State
    private void initializeState() {
        //Get Villages Data for States
        List<String> States = new ArrayList<>();
        States.clear();
        States = AppDatabase.getDatabaseInstance(AddNewStudent.this).getVillageDao().getState();
        populateBlock(States.get(0));
    }

    public void populateBlock(String selectedState) {
        //Get Villages Data for Blocks AllSpinners
        Blocks.clear();
        Blocks = AppDatabase.getDatabaseInstance(AddNewStudent.this).getVillageDao().GetStatewiseBlock(selectedState);
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
        BlocksVillages = AppDatabase.getDatabaseInstance(AddNewStudent.this).getVillageDao().GetVillages(selectedBlock);
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
                groups_spinner.setSelection(0);
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
        GroupsVillages = AppDatabase.getDatabaseInstance(AddNewStudent.this).getGroupDao().GetGroups(villageID);
        GroupsVillages.add(0, grp);
        //GroupsVillages.get(0).getGroupId();
        //Creating the ArrayAdapter instance having the Villages list
        List<Groups> SpinnerGroups = new ArrayList<>();
        for (int i = 0; i < GroupsVillages.size(); i++)
            SpinnerGroups.add(new Groups(GroupsVillages.get(i).GroupId, GroupsVillages.get(i).GroupName));

        ArrayAdapter<Groups> GroupsAdapter = new ArrayAdapter<Groups>(this, R.layout.custom_spinner, SpinnerGroups);
        // Hint for AllSpinners
        groups_spinner.setPrompt(getString(R.string.selectgroup));
        groups_spinner.setAdapter(GroupsAdapter);
        groups_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ID = String.valueOf(groups_spinner.getSelectedItemId());
                GrpID = GroupsVillages.get(Integer.parseInt(ID)).getGroupId();
                try {
                    GrpName = GroupsVillages.get(Integer.parseInt(ID)).getGroupName();
                } catch (NumberFormatException e) {
                    GrpName = "";
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void FormReset() {
        UUID uuStdid = UUID.randomUUID();
        randomUUIDStudent = uuStdid.toString();
        blocks_spinner.setSelection(0);
        sp_Class.setSelection(0);
        villages_spinner.setSelection(0);
        groups_spinner.setSelection(0);
        edt_Age.setText("");
        edt_Fname.getText().clear();
        edt_Mname.getText().clear();
        edt_Lname.getText().clear();
        edt_moNumber.getText().clear();
        edt_GuardianName.getText().clear();
        sp_BaselineLang.setSelection(0);
        sp_NumberReco.setSelection(0);
        btn_DatePicker.setText(util.GetCurrentDate().toString());
        btn_BirthDatePicker.setText(getString(R.string.birth_date));
        imgView.setImageDrawable(null);
        EndlineButtonClicked = false;
        setDefaults();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_Thumbnail) {
            if (data != null) {
                if (data.hasExtra("data")) {
                    Bitmap thumbnail1 = data.getParcelableExtra("data");
                    imgView.setImageBitmap(thumbnail1);
                    try {

                        Context cnt;
                        cnt = this;
                        File folder = new File(Environment.getExternalStorageDirectory() + "/.POSinternal/StudentProfiles/");
                        //  File folder = new File(splashScreenVideo.fpath + "/MyClicks/");
                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdir();
                        }
                        if (success) {
                            // Do something on success
                            File outputFile = new File(folder, randomUUIDStudent + ".jpg");
                            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                            thumbnail1.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            data = null;
                            thumbnail1 = null;
                            // To Refresh Contents of sharableFolder
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outputFile)));
                        } else {
                        }

                    } catch (Exception e) {
                        Modal_Log log = new Modal_Log();
                        log.setCurrentDateTime(new Utility().GetCurrentDate());
                        log.setErrorType("ERROR");
                        log.setExceptionMessage(e.getMessage());
                        log.setExceptionStackTrace(e.getStackTrace().toString());
                        log.setMethodName("AddNewStudent" + "_" + "onActivityResult");
                        log.setDeviceId("");
                        AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                        BackupDatabase.backup(ApplicationController.getInstance());

                        e.printStackTrace();
                    }
                }
            }
        }
    }

}

