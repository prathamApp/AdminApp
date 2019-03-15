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

import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Aser;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.BirthDatePickerFragment;
import com.pratham.admin.util.DatePickerFragment;
import com.pratham.admin.util.Utility;

import org.greenrobot.eventbus.EventBus;
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

public class AddNewStudent extends BaseActivity/* implements ConnectionReceiverListener */ {

    private static final int TAKE_Thumbnail = 1;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static String TAG = "PermissionDemo";
    public boolean EndlineButtonClicked = false;
    Spinner states_spinner, blocks_spinner, villages_spinner, groups_spinner, sp_Class;
    EditText edt_Fname, edt_Mname, edt_Lname, edt_GuardianName;
    RadioGroup rg_Gender, rg_SchoolType;
    Button btn_Submit_Baseline, btn_Clear, btn_Capture, btn_BirthDatePicker;
    RadioButton rb_Male, rb_Female, rb_Govt, rb_Private;
    String GrpID;
    List<String> Blocks = new ArrayList<>();
    int vilID;
    String gender;
    List<String> ExistingStudents;
    String StudentID, FirstName, MiddleName, LastName, Age, Class, UpdatedDate, Gender;
    String randomUUIDStudent;
    ImageView imgView;
    Uri uriSavedImage;
    UUID uuStdid;
    RadioButton selectedGender, selectedSchoolType;
    List<Groups> GroupsVillages = new ArrayList<Groups>();
    boolean timer;
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


//    boolean internetIsAvailable = false;

    @Subscribe
    public void onEvent(String msg) {
        if (!msg.isEmpty()) {
            btn_EndlineDatePicker.setText(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_student);
        getSupportActionBar().hide();
//        checkConnection();

        EventBus.getDefault().register(AddNewStudent.this);

        initializeVariables();
        initializeClassSpinner();
        initializeStatesSpinner();
        initializeBaselineSpinner();
        initializeNumberRecoSpinner();
        initializeAserDate();
        initializeBirthDate();
        initializeEnglishSpinner();

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
                title.setText("Endline 1");

                String[] baselineLangAdapter = {"Language", "Beg", "Letter", "Word", "Para", "Story"};
                ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, baselineLangAdapter);
                spinner_BaselineLang.setAdapter(baselineAdapter);

                String[] NumberRecoAdapter = {"Number Recognition", "Beg", "0-9", "10-99", "Sub", "Div"};
                ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, NumberRecoAdapter);
                spinner_NumberReco.setAdapter(recoAdapter);

                String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
                ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, engAdapter);
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
                title.setText("Endline 2");

                String[] baselineLangAdapter = {"Language", "Beg", "Letter", "Word", "Para", "Story"};
                ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, baselineLangAdapter);
                spinner_BaselineLang.setAdapter(baselineAdapter);

                String[] NumberRecoAdapter = {"Number Recognition", "Beg", "0-9", "10-99", "Sub", "Div"};
                ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, NumberRecoAdapter);
                spinner_NumberReco.setAdapter(recoAdapter);

                String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
                ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, engAdapter);
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
                title.setText("Endline 3");

                String[] baselineLangAdapter = {"Language", "Beg", "Letter", "Word", "Para", "Story"};
                ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, baselineLangAdapter);
                spinner_BaselineLang.setAdapter(baselineAdapter);

                String[] NumberRecoAdapter = {"Number Recognition", "Beg", "0-9", "10-99", "Sub", "Div"};
                ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, NumberRecoAdapter);
                spinner_NumberReco.setAdapter(recoAdapter);

                String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
                ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, engAdapter);
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
                            Toast.makeText(AddNewStudent.this, "Please fill all the fields !!!", Toast.LENGTH_SHORT).show();
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
                title.setText("Endline 4");

                String[] baselineLangAdapter = {"Language", "Beg", "Letter", "Word", "Para", "Story"};
                ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, baselineLangAdapter);
                spinner_BaselineLang.setAdapter(baselineAdapter);

                String[] NumberRecoAdapter = {"Number Recognition", "Beg", "0-9", "10-99", "Sub", "Div"};
                ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, NumberRecoAdapter);
                spinner_NumberReco.setAdapter(recoAdapter);

                String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
                ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(AddNewStudent.this, R.layout.custom_spinner, engAdapter);
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
                            Toast.makeText(AddNewStudent.this, "Please fill all the fields !!!", Toast.LENGTH_SHORT).show();
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
                // find the radio button by returned id
                selectedGender = (RadioButton) findViewById(selectedId);
                gender = selectedGender.getText().toString();

                // get selected radio button from radioGroup
                int selId = rg_SchoolType.getCheckedRadioButtonId();
                // find the radio button by returned id
                selectedSchoolType = (RadioButton) findViewById(selId);
                String schoolType = selectedSchoolType.getText().toString();
                int stdSchoolType = 0;
                if (schoolType.equalsIgnoreCase("Government"))
                    stdSchoolType = 1;
                else if (schoolType.equalsIgnoreCase("Private"))
                    stdSchoolType = 2;

                // Check AllSpinners Emptyness
                int StatesSpinnerValue = states_spinner.getSelectedItemPosition();
                int ClassSpinnerValue = sp_Class.getSelectedItemPosition();
                int BlocksSpinnerValue = blocks_spinner.getSelectedItemPosition();
                int VillagesSpinnerValue = villages_spinner.getSelectedItemPosition();
                int GroupsSpinnerValue = groups_spinner.getSelectedItemPosition();

                // Spinners Selection
                if (StatesSpinnerValue > 0 && BlocksSpinnerValue > 0 && VillagesSpinnerValue > 0 && GroupsSpinnerValue > 0 && ClassSpinnerValue > 0) {
                    // Checking Emptyness
                    if ((!edt_Fname.getText().toString().isEmpty() || !edt_Lname.getText().toString().isEmpty())) {
                        // Validations
                        if ((edt_Fname.getText().toString().matches("[a-zA-Z.? ]*")) && (edt_Lname.getText().toString().matches("[a-zA-Z.? ]*"))
                                && (edt_Mname.getText().toString().matches("[a-zA-Z.? ]*"))
                                && (!btn_BirthDatePicker.getText().toString().contains("Birth")) && (!edt_GuardianName.getText().toString().isEmpty())) {

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

//                                checkConnection();

                            try {
                                AppDatabase.getDatabaseInstance(AddNewStudent.this).getStudentDao().insertStudent(stdObj);
                                AppDatabase.getDatabaseInstance(AddNewStudent.this).getAserDao().insertAser(asr);
                                Toast.makeText(AddNewStudent.this, "Record Inserted Successfully !!!", Toast.LENGTH_SHORT).show();
                                BackupDatabase.backup(AddNewStudent.this);
                                resetFormPartially();

                                // Push To Server
                                    /*try {
                                        if (internetIsAvailable) {
                                            Gson gson = new Gson();
                                            String StudentJSON = gson.toJson(Collections.singletonList(stdObj));
                                            String AserJSON = gson.toJson(asr);

                                            MetaData metaData = new MetaData();
                                            metaData.setKeys("pushDataTime");
                                            metaData.setValue(DateFormat.getDateTimeInstance().format(new Date()));
                                            List<MetaData> metaDataList = AppDatabase.getDatabaseInstance(AddNewStudent.this).getMetaDataDao().getAllMetaData();
                                            String metaDataJSON = customParse(metaDataList);
                                            AppDatabase.getDatabaseInstance(AddNewStudent.this).getMetaDataDao().insertMetadata(metaData);

                                            String json = "{ \"StudentJSON\":" + StudentJSON + ",\"AserJSON\":" + AserJSON + ",\"metadata\":" + metaDataJSON + "}";
                                            Log.d("json :::", json);

                                            final ProgressDialog dialog = new ProgressDialog(AddNewStudent.this);
                                            dialog.setTitle("UPLOADING ... ");
                                            dialog.setCancelable(false);
                                            dialog.setCanceledOnTouchOutside(false);
                                            dialog.show();

                                            AndroidNetworking.post(PushForms).setContentType("application/json").addStringBody(json).build().getAsString(new StringRequestListener() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Log.d("responce", response);
                                                    AppDatabase.getDatabaseInstance(AddNewStudent.this).getStudentDao().updateSentFlag(1, randomUUIDStudent);
                                                    Toast.makeText(AddNewStudent.this, "Form Data Pushed to Server !!!", Toast.LENGTH_SHORT).show();
                                                    resetFormPartially();
                                                    dialog.dismiss();
                                                }

                                                @Override
                                                public void onError(ANError anError) {
                                                    Toast.makeText(AddNewStudent.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                                                    AppDatabase.getDatabaseInstance(AddNewStudent.this).getCoachDao().updateSentFlag(0, randomUUIDStudent);
                                                    resetFormPartially();
                                                    dialog.dismiss();
                                                }
                                            });

                                        } else {
                                            resetFormPartially();
                                            Toast.makeText(AddNewStudent.this, "Form Data not Pushed to Server as Internet isn't connected !!! ", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }*/

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(AddNewStudent.this, "Record Insertion Failed !!!", Toast.LENGTH_SHORT).show();
                            }

                            // either baseline spinners are fully filled or not filled at all
                            /*} else {
                                Toast.makeText(AddNewStudent.this, "Please Fill All Fields !", Toast.LENGTH_SHORT).show();
                            }*/

                        } else {
                            Toast.makeText(AddNewStudent.this, "Please Enter Valid Input !!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddNewStudent.this, "Please Fill all fields !!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddNewStudent.this, "Please Fill all fields !!!", Toast.LENGTH_SHORT).show();
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

    private void resetFormPartially() {
        edt_Fname.getText().clear();
        edt_Mname.getText().clear();
        edt_Lname.getText().clear();
        edt_GuardianName.getText().clear();
        imgView.setImageDrawable(null);
        UUID uuStdid = UUID.randomUUID();
        randomUUIDStudent = uuStdid.toString();
        EndlineButtonClicked = false;
        btn_BirthDatePicker.setText("Birth Date");
        btn_DatePicker.setText(util.GetCurrentDate().toString());
        sp_Class.setSelection(0);
        sp_BaselineLang.setSelection(0);
        sp_NumberReco.setSelection(0);
        sp_English.setSelection(0);
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
        states_spinner = (Spinner) findViewById(R.id.spinner_SelectState);
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
        String[] NumberRecoAdapter = {"Baseline (Number Recognition)", "Beg", "0-9", "10-99", "Sub", "Div"};
        ArrayAdapter<String> recoAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, NumberRecoAdapter);
        //sp_NumberReco.setPrompt("Number Reco Level");
        sp_NumberReco.setAdapter(recoAdapter);
    }

    private void initializeEnglishSpinner() {
        String[] engAdapter = {"Baseline (English)", "Beg", "Capital Letter", "Small Letter", "Word", "Sentence"};
        ArrayAdapter<String> EnglishAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, engAdapter);
        sp_English.setAdapter(EnglishAdapter);
    }

    private void initializeBaselineSpinner() {
        //sp_BaselineLang.setPrompt("Baseline Level");
        String[] baselineLangAdapter = {"Baseline (Lang)", "Beg", "Letter", "Word", "Para", "Story"};
        ArrayAdapter<String> baselineAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, baselineLangAdapter);
        sp_BaselineLang.setAdapter(baselineAdapter);
    }

    private void initializeStatesSpinner() {
        //Get Villages Data for States AllSpinners
        List<String> States = new ArrayList<>();
        States.clear();
        States = AppDatabase.getDatabaseInstance(AddNewStudent.this).getVillageDao().getState();
        States.add(0, "Select State");
        //Creating the ArrayAdapter instance having the Villages list
        ArrayAdapter<String> StateAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, States);
        // Hint for AllSpinners
        states_spinner.setPrompt("Select State");
        states_spinner.setAdapter(StateAdapter);

        states_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = states_spinner.getSelectedItem().toString();
                populateBlock(selectedState);
                groups_spinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void populateBlock(String selectedState) {
        //Get Villages Data for Blocks AllSpinners
        Blocks.clear();
        Blocks = AppDatabase.getDatabaseInstance(AddNewStudent.this).getVillageDao().GetStatewiseBlock(selectedState);
        Blocks.add(0, "Select Block");
        //Creating the ArrayAdapter instance having the Villages list
        ArrayAdapter<String> BlockAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, Blocks);
        // Hint for AllSpinners
        blocks_spinner.setPrompt("Select Block");
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
        v.VillageName = "Select Village";
        BlocksVillages = AppDatabase.getDatabaseInstance(AddNewStudent.this).getVillageDao().GetVillages(selectedBlock);
        BlocksVillages.add(0, v);

        List<Village> SpinnerBlocksVillages = new ArrayList<>();
        for (int i = 0; i < BlocksVillages.size(); i++)
            SpinnerBlocksVillages.add(new Village(Integer.parseInt(BlocksVillages.get(i).VillageId), BlocksVillages.get(i).VillageName));

        //Creating the ArrayAdapter instance having the Villages list
        ArrayAdapter<Village> VillagesAdapter = new ArrayAdapter<Village>(this, R.layout.custom_spinner, SpinnerBlocksVillages);
        // Hint for AllSpinners
        villages_spinner.setPrompt("Select Village");
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
        grp.GroupName = "Select Group";
        GroupsVillages = AppDatabase.getDatabaseInstance(AddNewStudent.this).getGroupDao().GetGroups(villageID);
        GroupsVillages.add(0, grp);
        //GroupsVillages.get(0).getGroupId();
        //Creating the ArrayAdapter instance having the Villages list
        List<Groups> SpinnerGroups = new ArrayList<>();
        for (int i = 0; i < GroupsVillages.size(); i++)
            SpinnerGroups.add(new Groups(GroupsVillages.get(i).GroupId, GroupsVillages.get(i).GroupName));

        ArrayAdapter<Groups> GroupsAdapter = new ArrayAdapter<Groups>(this, R.layout.custom_spinner, SpinnerGroups);
        // Hint for AllSpinners
        groups_spinner.setPrompt("Select Group");
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
        states_spinner.setSelection(0);
        blocks_spinner.setSelection(0);
        sp_Class.setSelection(0);
        villages_spinner.setSelection(0);
        groups_spinner.setSelection(0);
        edt_Fname.getText().clear();
        edt_Mname.getText().clear();
        edt_Lname.getText().clear();
        edt_GuardianName.getText().clear();
        sp_BaselineLang.setSelection(0);
        sp_NumberReco.setSelection(0);
        btn_DatePicker.setText(util.GetCurrentDate().toString());
        btn_BirthDatePicker.setText("Birth Date");
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
                        e.printStackTrace();
                    }
                }
            }
        }
    }

  /*  @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        ApplicationController.getInstance().setConnectionListener(this);
    }

    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }

    private String customParse(List<MetaData> metaDataList) {
        String json = "{";

        for (int i = 0; i < metaDataList.size(); i++) {
            json = json + "\"" + metaDataList.get(i).getKeys() + "\":\"" + metaDataList.get(i).getValue() + "\"";
            if (i < metaDataList.size() - 1) {
                json = json + ",";
            }
        }
        json = json + "}";

        return json;
    }*/

}

