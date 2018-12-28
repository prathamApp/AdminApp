package com.pratham.admin.activities;

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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.pratham.admin.util.DatePickerFragment;
import com.pratham.admin.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EditStudent extends AppCompatActivity {

    Spinner states_spinner, blocks_spinner, villages_spinner, groups_spinner, existingStudent_Spinner;
    TextView edt_Fname, edt_Mname, edt_Lname, edt_Age, edt_Class, tv_Gender;
    Button btn_Baseline_Submit, btn_Clear, btn_Capture;
    String GrpID;
    List<String> Blocks = new ArrayList<>();
    int vilID;
    Utility Util;
    String gender;
    List<Student> ExistingStudents = new ArrayList<>();
    String StudentID;
    String FirstName;
    String MiddleName;
    String LastName;
    int Age;
    int Class;
    String Gender;
    private static final int TAKE_Thumbnail = 1;
    ImageView imgView;
    private static String TAG = "PermissionDemo";
    private static final int REQUEST_WRITE_STORAGE = 112;
    Uri uriSavedImage;
    String StudentUniqID;
    boolean timer;
    String createdBy = "";

    Spinner sp_BaselineLang, sp_NumberReco;
    Button btn_EndlineDatePicker, btn_DatePicker, btn_Endline1, btn_Endline2, btn_Endline3, btn_Endline4;
    LinearLayout AserForm;
    public boolean EndlineButtonClicked = false;

    int testT = 0, langSpin, numSpin;
    int OA = 0;
    int OS = 0;
    int OM = 0;
    int OD = 0;
    int WA = 0;
    int WS = 0;
    int IC = 0;
    String AserTestDate;

    List<Aser> AserData;
    private boolean captureButtonPressed = false;
    String aserDate;
    List<Groups> GroupsVillages = new ArrayList<Groups>();

    @Subscribe
    public void onEvent(String msg) {
        if (!msg.isEmpty()) {
            btn_EndlineDatePicker.setText(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);
        getSupportActionBar().hide();

        EventBus.getDefault().register(EditStudent.this);

        initializeVariables();
        populateStatesSpinner();
        initializeBaselineSpinner();
        initializeNumberRecoSpinner();
        initializeAserDate();

        btn_Endline1.setOnClickListener(new View.OnClickListener() {
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
                final CheckBox OprAdd = endlineDialog.findViewById(R.id.OprAdd);
                final CheckBox OprSub = endlineDialog.findViewById(R.id.OprSub);
                final CheckBox OprMul = endlineDialog.findViewById(R.id.OprMul);
                final CheckBox OprDiv = endlineDialog.findViewById(R.id.OprDiv);
                final TextView tv_WordProblem = endlineDialog.findViewById(R.id.tv_WordProblem);
                final CheckBox WordAdd = endlineDialog.findViewById(R.id.WordAdd);
                final CheckBox WordSub = endlineDialog.findViewById(R.id.WordSub);
                Button btn_Submit = endlineDialog.findViewById(R.id.btn_Submit);
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
                } else {
                    // fetch baseline aser
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

                    btn_EndlineDatePicker.setText(aserDate);
                    // set baseline aser
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

                btn_Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int BaselineSpinnerValue = spinner_BaselineLang.getSelectedItemPosition();
                        int NumberSpinnerValue = spinner_NumberReco.getSelectedItemPosition();

                        if (BaselineSpinnerValue > 0 && NumberSpinnerValue > 0) {
                            sp_BaselineLang.setSelection(0);
                            sp_NumberReco.setSelection(0);
                            EndlineButtonClicked = true;
                            aserDate = btn_EndlineDatePicker.getText().toString();
                            testT = 1;
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
                                AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().UpdateAserData("", aserDate, langSpin, numSpin, OA, OS, OM, OD, WA, WS, createdBy, Util.GetCurrentDate(), IC, 0, StudentUniqID, testT);
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
                            }

                            BackupDatabase.backup(EditStudent.this);
                            Toast.makeText(EditStudent.this, "Endline 1 Updated !", Toast.LENGTH_SHORT).show();

                            if (endlineDialog.isShowing())
                                endlineDialog.cancel();

                        } else {
                            Toast.makeText(EditStudent.this, "Please fill all the fields !!!", Toast.LENGTH_SHORT).show();
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
                        } else {
                            // fetch baseline aser
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
                final CheckBox OprAdd = endlineDialog.findViewById(R.id.OprAdd);
                final CheckBox OprSub = endlineDialog.findViewById(R.id.OprSub);
                final CheckBox OprMul = endlineDialog.findViewById(R.id.OprMul);
                final CheckBox OprDiv = endlineDialog.findViewById(R.id.OprDiv);
                final TextView tv_WordProblem = endlineDialog.findViewById(R.id.tv_WordProblem);
                final CheckBox WordAdd = endlineDialog.findViewById(R.id.WordAdd);
                final CheckBox WordSub = endlineDialog.findViewById(R.id.WordSub);
                Button btn_Submit = endlineDialog.findViewById(R.id.btn_Submit);
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
                } else {
                    // fetch baseline aser
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

                    btn_EndlineDatePicker.setText(aserDate);
                    // set baseline aser
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

                btn_Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int BaselineSpinnerValue = spinner_BaselineLang.getSelectedItemPosition();
                        int NumberSpinnerValue = spinner_NumberReco.getSelectedItemPosition();

                        if (BaselineSpinnerValue > 0 && NumberSpinnerValue > 0) {
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
                                AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().UpdateAserData("", aserDate, langSpin, numSpin, OA, OS, OM, OD, WA, WS, createdBy, Util.GetCurrentDate(), IC, 0, StudentUniqID, testT);
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
                            }
                            BackupDatabase.backup(EditStudent.this);
                            Toast.makeText(EditStudent.this, "Endline 2 Updated !", Toast.LENGTH_SHORT).show();

                            if (endlineDialog.isShowing())
                                endlineDialog.cancel();
                        } else {
                            Toast.makeText(EditStudent.this, "Please fill all the fields !!!", Toast.LENGTH_SHORT).show();
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
                        } else {
                            // fetch baseline aser
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
                final CheckBox OprAdd = endlineDialog.findViewById(R.id.OprAdd);
                final CheckBox OprSub = endlineDialog.findViewById(R.id.OprSub);
                final CheckBox OprMul = endlineDialog.findViewById(R.id.OprMul);
                final CheckBox OprDiv = endlineDialog.findViewById(R.id.OprDiv);
                final TextView tv_WordProblem = endlineDialog.findViewById(R.id.tv_WordProblem);
                final CheckBox WordAdd = endlineDialog.findViewById(R.id.WordAdd);
                final CheckBox WordSub = endlineDialog.findViewById(R.id.WordSub);
                Button btn_Submit = endlineDialog.findViewById(R.id.btn_Submit);
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
                } else {
                    // fetch baseline aser
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

                    btn_EndlineDatePicker.setText(aserDate);
                    // set baseline aser
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

                btn_Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int BaselineSpinnerValue = spinner_BaselineLang.getSelectedItemPosition();
                        int NumberSpinnerValue = spinner_NumberReco.getSelectedItemPosition();

                        if (BaselineSpinnerValue > 0 && NumberSpinnerValue > 0) {
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
                                AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().UpdateAserData("", aserDate, langSpin, numSpin, OA, OS, OM, OD, WA, WS, createdBy, Util.GetCurrentDate(), IC, 0, StudentUniqID, testT);
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
                            }
                            BackupDatabase.backup(EditStudent.this);
                            Toast.makeText(EditStudent.this, "Endline 3 Updated !", Toast.LENGTH_SHORT).show();

                            if (endlineDialog.isShowing())
                                endlineDialog.cancel();
                        } else {
                            Toast.makeText(EditStudent.this, "Please fill all the fields !!!", Toast.LENGTH_SHORT).show();
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
                        } else {
                            // fetch baseline aser
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
                final CheckBox OprAdd = endlineDialog.findViewById(R.id.OprAdd);
                final CheckBox OprSub = endlineDialog.findViewById(R.id.OprSub);
                final CheckBox OprMul = endlineDialog.findViewById(R.id.OprMul);
                final CheckBox OprDiv = endlineDialog.findViewById(R.id.OprDiv);
                final TextView tv_WordProblem = endlineDialog.findViewById(R.id.tv_WordProblem);
                final CheckBox WordAdd = endlineDialog.findViewById(R.id.WordAdd);
                final CheckBox WordSub = endlineDialog.findViewById(R.id.WordSub);
                Button btn_Submit = endlineDialog.findViewById(R.id.btn_Submit);
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
                } else {
                    // fetch baseline aser
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

                    btn_EndlineDatePicker.setText(aserDate);
                    // set baseline aser
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

                btn_Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int BaselineSpinnerValue = spinner_BaselineLang.getSelectedItemPosition();
                        int NumberSpinnerValue = spinner_NumberReco.getSelectedItemPosition();

                        if (BaselineSpinnerValue > 0 && NumberSpinnerValue > 0) {
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
                                AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().UpdateAserData("", aserDate, langSpin, numSpin, OA, OS, OM, OD, WA, WS, createdBy, Util.GetCurrentDate(), IC, 0, StudentUniqID, testT);
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
                            }
                            BackupDatabase.backup(EditStudent.this);
                            Toast.makeText(EditStudent.this, "Endline 4 Updated !", Toast.LENGTH_SHORT).show();

                            if (endlineDialog.isShowing())
                                endlineDialog.cancel();
                        } else {
                            Toast.makeText(EditStudent.this, "Please fill all the fields !!!", Toast.LENGTH_SHORT).show();
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
                        } else {
                            // fetch baseline aser
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
                int StatesSpinnerValue = states_spinner.getSelectedItemPosition();
                int BlocksSpinnerValue = blocks_spinner.getSelectedItemPosition();
                int VillagesSpinnerValue = villages_spinner.getSelectedItemPosition();
                int GroupsSpinnerValue = groups_spinner.getSelectedItemPosition();
                int ExistingSpinnerValue = existingStudent_Spinner.getSelectedItemPosition();


                if (StatesSpinnerValue > 0 && BlocksSpinnerValue > 0 && VillagesSpinnerValue > 0 && GroupsSpinnerValue > 0 && ExistingSpinnerValue > 0) {
                    // Photo updated validation
                    if (captureButtonPressed) {
//                        Toast.makeText(EditStudent.this, "Photo Inserted Successfully !!!", Toast.LENGTH_SHORT).show();
                    }

                    testT = 0;
                    langSpin = sp_BaselineLang.getSelectedItemPosition();
                    numSpin = sp_NumberReco.getSelectedItemPosition();
                    AserTestDate = btn_DatePicker.getText().toString();

                    if (langSpin > 0 && numSpin > 0) {
                        // insert or update baseline in db
                        boolean result;
                        result = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().CheckDataExists(StudentUniqID, testT);
                        if (result) {
                            //update
                            AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().UpdateAserData("", AserTestDate, langSpin, numSpin, OA, OS, OM, OD, WA, WS, createdBy, Util.GetCurrentDate(), IC, 0, StudentUniqID, testT);
                        } else {
                            // new entry
                            Aser asr = new Aser();
                            asr.StudentId = StudentUniqID;
                            asr.GroupID = GrpID;
                            asr.ChildID = "";
                            asr.TestType = testT;
                            asr.TestDate = AserTestDate;
                            asr.Lang = langSpin;
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
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(EditStudent.this, "Baseline Insertion Failed !", Toast.LENGTH_SHORT).show();
                            }
                        }
                        BackupDatabase.backup(EditStudent.this);
                        resetFormPartially();
                        Toast.makeText(EditStudent.this, "Baseline Updated !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditStudent.this, "Please fill all Baseline fields !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditStudent.this, "Please Select Fill all fields !!!", Toast.LENGTH_SHORT).show();
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
        String[] NumberRecoAdapter = {"Baseline (Number Recognition)", "Beg", "0-9", "10-99", "Sub", "Div"};
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

    private void populateStatesSpinner() {
        //Get Villages Data for States AllSpinners
        List<String> States = new ArrayList<>();
        States.clear();
        States = AppDatabase.getDatabaseInstance(EditStudent.this).getVillageDao().getState();
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
                btn_Capture.setVisibility(View.GONE);
                groups_spinner.setSelection(0);
                AserForm.setVisibility(View.GONE);
                resetFormPartially();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeVariables() {
        groups_spinner = (Spinner) findViewById(R.id.spinner_SelectGroups);
        states_spinner = (Spinner) findViewById(R.id.spinner_SelectState);
        villages_spinner = (Spinner) findViewById(R.id.spinner_selectVillage);
        blocks_spinner = (Spinner) findViewById(R.id.spinner_SelectBlock);
        edt_Fname = (TextView) findViewById(R.id.edt_FirstName);
        edt_Mname = (TextView) findViewById(R.id.edt_MiddleName);
        edt_Lname = (TextView) findViewById(R.id.edt_LastName);
        edt_Age = (TextView) findViewById(R.id.edt_Age);
        edt_Class = (TextView) findViewById(R.id.edt_Class);
        tv_Gender = (TextView) findViewById(R.id.tv_Gender);
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

    public void populateBlock(String selectedState) {
        //Get Villages Data for Blocks AllSpinners
        Blocks.clear();
        Blocks = AppDatabase.getDatabaseInstance(EditStudent.this).getVillageDao().GetStatewiseBlock(selectedState);
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
        v.VillageName = "Select Village";
        BlocksVillages = AppDatabase.getDatabaseInstance(EditStudent.this).getVillageDao().GetVillages(selectedBlock);
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
        grp.GroupName = "Select Group";
        GroupsVillages = AppDatabase.getDatabaseInstance(EditStudent.this).getGroupDao().GetGroups(villageID);
        GroupsVillages.add(0, grp);
        //Creating the ArrayAdapter instance having the Villages list
        List<Groups> SpinnerGroups = new ArrayList<>();
        for (int i = 0; i < GroupsVillages.size(); i++)
            SpinnerGroups.add(new Groups(GroupsVillages.get(i).GroupId, GroupsVillages.get(i).GroupName));

        final ArrayAdapter<Groups> GroupsAdapter = new ArrayAdapter<Groups>(this, R.layout.custom_spinner, SpinnerGroups);
        // Hint for AllSpinners
        groups_spinner.setPrompt("Select Group");
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
                edt_Class.setText("");
                imgView.setImageDrawable(null);
                btn_Capture.setVisibility(View.GONE);
                EndlineButtonClicked = false;
                btn_DatePicker.setText(Util.GetCurrentDate().toString());
                sp_BaselineLang.setSelection(0);
                sp_NumberReco.setSelection(0);
                setDefaults();
                AserForm.setVisibility(View.GONE);

                String GroupName = groups_spinner.getSelectedItem().toString();
                //GrpID = GroupsVillages.get(0).getGroupId();
                Groups SelectedGroupData = GroupsAdapter.getItem(groups_spinner.getSelectedItemPosition());
                GrpID = SelectedGroupData.getGroupId();
                String Id = GrpID;
                //Toast.makeText(EditStudent.this, "Group ID is "+Id, Toast.LENGTH_SHORT).show();
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
        std.FirstName = "Select Student";
        ExistingStudents = AppDatabase.getDatabaseInstance(EditStudent.this).getStudentDao().GetAllStudentsByGroupID(GroupID);
        ExistingStudents.add(0, std);

        List<Student> SpinnerStudents = new ArrayList<>();
        for (int i = 0; i < ExistingStudents.size(); i++)
            SpinnerStudents.add(new Student(ExistingStudents.get(i).StudentId, ExistingStudents.get(i).FirstName));

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
                    populateStudentData(StudentUniqID);
                } catch (Exception e) {
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
        if (SelectedStudent == null && existingStudent_Spinner.getSelectedItemPosition() > 0) {
            Toast.makeText(EditStudent.this, "Sorry !!! No Data Found !!!", Toast.LENGTH_SHORT).show();
        } else {
            FirstName = SelectedStudent.FirstName;
            MiddleName = SelectedStudent.MiddleName;
            LastName = SelectedStudent.LastName;
            Age = Integer.parseInt(SelectedStudent.Age);
            String gen = SelectedStudent.Gender;
            if (gen.equals("Male") || gen.equals("M") || gen.equals("1")) {
                Gender = "Male";
            } else if (gen.equals("Female") || gen.equals("F") || gen.equals("2")) {
                Gender = "Female";
            } else {
                // Default
                Gender = "Male";
            }
            String cls = String.valueOf(SelectedStudent.Stud_Class);
            if (cls.length() > 0) {
                Class = Integer.parseInt(SelectedStudent.Stud_Class);
            } else {
                Class = 0;
            }
        }

        // set student data
        if (FirstName == null) {
            btn_Capture.setVisibility(View.GONE);
        } else {
            AserForm.setVisibility(View.VISIBLE);
            edt_Fname.setText("First Name : " + FirstName);
            edt_Mname.setText("Middle Name : " + MiddleName);
            edt_Lname.setText("Last Name : " + LastName);
            edt_Age.setText("Age : " + String.valueOf(Age));
            edt_Class.setText("Class : " + String.valueOf(Class));
            tv_Gender.setText("Gender : " + Gender);
            btn_Capture.setVisibility(View.VISIBLE);
            btn_Capture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_Thumbnail);
                }
            });
        }

        // fetch aser data for baseline i.e TestType = 0
        AserData = AppDatabase.getDatabaseInstance(EditStudent.this).getAserDao().GetAllByStudentID(studentUniqID, 0);
        if (AserData == null || AserData.size() == 0) {
            setDefaults();
            sp_BaselineLang.setSelection(0);
            sp_NumberReco.setSelection(0);
            btn_DatePicker.setText(Util.GetCurrentDate().toString());
        } else {
            // fetch baseline aser
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

    public void FormReset() {
        states_spinner.setSelection(0);
        blocks_spinner.setSelection(0);
        villages_spinner.setSelection(0);
        groups_spinner.setSelection(0);
        existingStudent_Spinner.setSelection(0);
        edt_Fname.setText("");
        edt_Mname.setText("");
        edt_Lname.setText("");
        edt_Age.setText("");
        edt_Class.setText("");
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
        existingStudent_Spinner.setSelection(0);
        edt_Fname.setText("");
        edt_Mname.setText("");
        edt_Lname.setText("");
        edt_Age.setText("");
        edt_Class.setText("");
        imgView.setImageDrawable(null);
        btn_Capture.setVisibility(View.GONE);
        EndlineButtonClicked = false;
        btn_DatePicker.setText(Util.GetCurrentDate().toString());
        sp_BaselineLang.setSelection(0);
        sp_NumberReco.setSelection(0);
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

