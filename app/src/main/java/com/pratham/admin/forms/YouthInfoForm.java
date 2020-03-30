package com.pratham.admin.forms;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.activities.AddNewStudent;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Modal_Log;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.modalclasses.Youth;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.BirthDatePickerFragment;
import com.pratham.admin.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YouthInfoForm extends BaseActivity {

    Spinner blocks_spinner, villages_spinner, groups_spinner, education_spinner, occupation_spinner;

    @BindView(R.id.edt_FirstName)
    EditText edt_Fname;
    @BindView(R.id.edt_MiddleName)
    EditText edt_Mname;
    @BindView(R.id.edt_LastName)
    EditText edt_Lname;
    @BindView(R.id.edt_GuardianName)
    EditText edt_GuardianName;
    @BindView(R.id.edt_Phonenumber)
    EditText edt_PhoneNumber;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;
    @BindView(R.id.btn_Clear)
    Button btn_Clear;
    @BindView(R.id.btn_BirthDatePicker)
    Button btn_BirthDatePicker;

    @BindView(R.id.rg_Gender)
    RadioGroup rg_Gender;
    @BindView(R.id.rg_marital_status)
    RadioGroup rg_marital;
    @BindView(R.id.rg_rustudy)
    RadioGroup rg_rustudy;
    @BindView(R.id.rg_haveSmartphone)
    RadioGroup rg_q1;
    @BindView(R.id.rg_useSmartphone)
    RadioGroup rg_q2;
    @BindView(R.id.rg_iwanttojoin)
    RadioGroup rg_q3;

    @BindView(R.id.rb_Male)
    RadioButton rb_Male;
    @BindView(R.id.rb_Female)
    RadioButton rb_Female;

    RadioButton selectedGender, selectedMarital, selectedrus, selectedHasPhone, selectedUsePhone, selectedWantjoin;
    String gender, selectedEdu, maritalStatus, ruStudy, haveSmartphone, useSmartphone, wantToJoin;
    int ruStudyVal, haveSmartphoneVal, useSmartphoneVal, wantToJoinVal;
    private String education = "";
    private String occupation = "";

    int vilID;
    List<String> Blocks = new ArrayList<>();
    String GrpID;
    private String GrpName = "";
    List<Groups> GroupsVillages = new ArrayList<Groups>();

    String randomUUIDYouth;
    UUID uuYthid;
    Utility util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youth_info_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();

        initializeVariables();
        initializeBirthDate();
        initializeStatesSpinner();

        // Populate Education Spinner
        populateEducation();

        // Populate Occupation Spinner
        populateOccupation();

        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getRadioVals();
                // Check AllSpinners Emptyness
                int BlocksSpinnerValue = blocks_spinner.getSelectedItemPosition();
                int VillagesSpinnerValue = villages_spinner.getSelectedItemPosition();
                int GroupsSpinnerValue = groups_spinner.getSelectedItemPosition();

                // Spinners Selection
                if (BlocksSpinnerValue > 0 && VillagesSpinnerValue > 0 && GroupsSpinnerValue > 0) {
                    // Checking Emptyness
                    if ((!edt_Fname.getText().toString().isEmpty() || !edt_Lname.getText().toString().isEmpty())) {
                        // Validations
                        if ((edt_Fname.getText().toString().matches("[a-zA-Z.? ]*")) && (edt_Lname.getText().toString().matches("[a-zA-Z.? ]*"))
                                && (edt_Mname.getText().toString().matches("[a-zA-Z.? ]*"))
                                && (!btn_BirthDatePicker.getText().toString().equals(getString(R.string.birthdate))) && (!edt_GuardianName.getText().toString().isEmpty())) {


                            Youth youthObj = new Youth();
                            youthObj.youthId = randomUUIDYouth;
                            youthObj.groupId = GrpID;
                            youthObj.groupName = GrpName;
                            youthObj.firstName = edt_Fname.getText().toString();
                            youthObj.middleName = edt_Mname.getText().toString();
                            youthObj.lastName = edt_Lname.getText().toString();
                            youthObj.phoneNumber = edt_PhoneNumber.getText().toString();
                            youthObj.guardianName = edt_GuardianName.getText().toString();
                            youthObj.birthDate = btn_BirthDatePicker.getText().toString();
                            youthObj.gender = gender;
                            youthObj.maritalStatus = maritalStatus;
                            youthObj.areyoustudying = ruStudyVal;
                            youthObj.education = education;
                            youthObj.occupation = occupation;
                            youthObj.haveSmartphone = haveSmartphoneVal;
                            youthObj.useSmartphone = useSmartphoneVal;
                            youthObj.wantToJoin = wantToJoinVal;
                            youthObj.createdBy = AppDatabase.getDatabaseInstance(YouthInfoForm.this).getMetaDataDao().getCrlMetaData();
                            youthObj.createdOn = util.GetCurrentDateTime(false).toString();
                            youthObj.sentFlag = 0;

                            try {
                                AppDatabase.getDatabaseInstance(YouthInfoForm.this).getYouthDao().insertYouth(youthObj);
                                Toast.makeText(YouthInfoForm.this, R.string.recInsertSuccess, Toast.LENGTH_SHORT).show();
                                BackupDatabase.backup(YouthInfoForm.this);
                                resetForm();

                            } catch (Exception e) {
                                Modal_Log log = new Modal_Log();
                                log.setCurrentDateTime(new Utility().GetCurrentDate());
                                log.setErrorType("ERROR");
                                log.setExceptionMessage(e.getMessage());
                                log.setExceptionStackTrace(e.getStackTrace().toString());
                                log.setMethodName("AddYouth" + "_" + "Submit");
                                log.setDeviceId("");
                                AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                                BackupDatabase.backup(ApplicationController.getInstance());

                                e.printStackTrace();
                                Toast.makeText(YouthInfoForm.this, R.string.recInsertFailed, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(YouthInfoForm.this, R.string.enterValidInput, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(YouthInfoForm.this, R.string.fillAllFields, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(YouthInfoForm.this, R.string.fillAllFields, Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetForm();
            }
        });
    }

    public void getRadioVals() {
        // get selected radio button from radioGroup
        int selectedId = rg_Gender.getCheckedRadioButtonId();
        // find the radio button by returned id
        selectedGender = (RadioButton) findViewById(selectedId);
        gender = selectedGender.getText().toString();
        if (gender.equals(getString(R.string.male)))
            gender = "Male";
        else if (gender.equals(getString(R.string.female)))
            gender = "Female";

        // get selected Marital value
        int selectedIdMarital = rg_marital.getCheckedRadioButtonId();
        selectedMarital = (RadioButton) findViewById(selectedIdMarital);
        maritalStatus = selectedMarital.getText().toString();
        if (maritalStatus.equals(getString(R.string.married)))
            maritalStatus = "Married";
        else if (maritalStatus.equals(getString(R.string.unmarried)))
            maritalStatus = "Unmarried";

        //get selected Are You Study value (yes = 1 and no = 0)
        int selectedIdrus = rg_rustudy.getCheckedRadioButtonId();
        selectedrus = (RadioButton) findViewById(selectedIdrus);
        ruStudy = selectedrus.getText().toString();
        if (ruStudy.equals(getString(R.string.yes)))
            ruStudyVal = 1;
        else if (ruStudy.equals(getString(R.string.no)))
            ruStudyVal = 0;

        //get selected haveSmartphone value (yes = 1 and no = 0)
        int havePhoneId = rg_q1.getCheckedRadioButtonId();
        selectedHasPhone = (RadioButton) findViewById(havePhoneId);
        haveSmartphone = selectedHasPhone.getText().toString();
        if (haveSmartphone.equals(getString(R.string.yes)))
            haveSmartphoneVal = 1;
        else
            haveSmartphoneVal = 0;

        //get selected Use Phone value (yes = 1 and no = 0)
        int usePhoneId = rg_q2.getCheckedRadioButtonId();
        selectedUsePhone = (RadioButton) findViewById(usePhoneId);
        useSmartphone = selectedUsePhone.getText().toString();
        if (useSmartphone.equals(getString(R.string.yes)))
            useSmartphoneVal = 1;
        else
            useSmartphoneVal = 0;

        //get selected I want to join course value (yes = 1 and no = 0)
        int iwantCourseId = rg_q3.getCheckedRadioButtonId();
        selectedWantjoin = (RadioButton) findViewById(iwantCourseId);
        wantToJoin = selectedWantjoin.getText().toString();
        if (wantToJoin.equals(getString(R.string.personalandcomm)))
            wantToJoinVal = 1;
        else
            wantToJoinVal = 0;
    }

    private void initializeVariables() {
        blocks_spinner = (Spinner) findViewById(R.id.spinner_SelectBlock);
        villages_spinner = (Spinner) findViewById(R.id.spinner_selectVillage);
        groups_spinner = (Spinner) findViewById(R.id.spinner_SelectGroups);
        education_spinner = (Spinner) findViewById(R.id.spinner_education);
        occupation_spinner = (Spinner) findViewById(R.id.spinner_occupation);
        uuYthid = UUID.randomUUID();
        randomUUIDYouth = uuYthid.toString();
        //btn_Capture = (Button) findViewById(R.id.btn_Capture);
        util = new Utility();
        btn_BirthDatePicker = findViewById(R.id.btn_BirthDatePicker);
    }

    private void initializeStatesSpinner() {
        //Get Villages Data for States AllSpinners
        List<String> States = new ArrayList<>();
        States.clear();
        States = AppDatabase.getDatabaseInstance(YouthInfoForm.this).getVillageDao().getState();
        populateBlock(States.get(0));
    }

    //Spinner Block Value
    public void populateBlock(String selectedState) {
        //Get Villages Data for Blocks AllSpinners
        Blocks.clear();
        Blocks = AppDatabase.getDatabaseInstance(YouthInfoForm.this).getVillageDao().GetStatewiseBlock(selectedState);
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

    //Spinner Village Values
    public void populateVillage(String selectedBlock) {
        //Get Villages Data for Villages filtered by block for Spinners
        List<Village> BlocksVillages = new ArrayList<>();
        BlocksVillages.clear();
        Village v = new Village();
        v.VillageId = "0";
        v.VillageName = getString(R.string.selectvillage);
        BlocksVillages = AppDatabase.getDatabaseInstance(YouthInfoForm.this).getVillageDao().GetVillages(selectedBlock);
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

    //Spinner Groups Value
    public void populateGroups(int villageID) {
        //Get Groups Data for Villages filtered by Villages for Spinners
        GroupsVillages.clear();
        Groups grp = new Groups();
        grp.GroupId = "0";
        grp.GroupName = getString(R.string.selectgroup);
        GroupsVillages = AppDatabase.getDatabaseInstance(YouthInfoForm.this).getGroupDao().GetGroups(villageID);
        GroupsVillages.add(0, grp);
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

    //Spinner Education Value
    private void populateEducation() {
        ArrayAdapter specialityAdapter = new ArrayAdapter(YouthInfoForm.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_Education));
        education_spinner.setAdapter(specialityAdapter);
        education_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                selectedEdu = education_spinner.getSelectedItem().toString();
                if (selectedEdu.contains(getString(R.string.select))) {
                } else {
                    if (selectedEdu.equalsIgnoreCase("No"))
                        education = String.valueOf(0);
                    else if (selectedEdu.equalsIgnoreCase(getString(R.string.sp_clas5)))
                        education = String.valueOf(5);
                    else if (selectedEdu.equalsIgnoreCase(getString(R.string.sp_clas8)))
                        education = String.valueOf(8);
                    else if (selectedEdu.equalsIgnoreCase(getString(R.string.sp_highSec)))
                        education = String.valueOf(10);
                    else if (selectedEdu.equalsIgnoreCase(getString(R.string.sp_intmd)))
                        education = String.valueOf(12);
                    else if (selectedEdu.equalsIgnoreCase(getString(R.string.sp_grad)))
                        education = String.valueOf(15);
                    else if (selectedEdu.equalsIgnoreCase(getString(R.string.sp_postgrad)))
                        education = String.valueOf(17);
                    else if (selectedEdu.equalsIgnoreCase("Others"))
                        education = String.valueOf(18);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Spinner Occupation Value
    private void populateOccupation() {
        ArrayAdapter occupationAdapter = new ArrayAdapter(YouthInfoForm.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_Occupation));
        occupation_spinner.setAdapter(occupationAdapter);
        occupation_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedOccupation = occupation_spinner.getSelectedItem().toString();
                if (selectedOccupation.contains(getString(R.string.select))) {
                } else if (selectedOccupation.equalsIgnoreCase("Others")) {
                    // Dialog
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(YouthInfoForm.this);
                    LayoutInflater inflater = YouthInfoForm.this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.others_dialog, null);
                    dialogBuilder.setView(dialogView);
                    final EditText edt = (EditText) dialogView.findViewById(R.id.edt_Others);
                    dialogBuilder.setCancelable(false);
                    edt.setHint("e.g. Service");
                    dialogBuilder.setTitle(R.string.plzmentionOther);
                    dialogBuilder.setPositiveButton(R.string.Okay, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            occupation = edt.getText().toString().trim();
                            if (edt.getText().toString().trim().equalsIgnoreCase("")) {
                                populateOccupation();
                            } else {
                                Toast.makeText(YouthInfoForm.this, "Occupation = " + occupation, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //pass
                            populateOccupation();
                        }
                    });
                    AlertDialog b = dialogBuilder.create();
                    b.show();
                } else {
                    occupation = selectedOccupation;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void resetForm() {
        edt_Fname.getText().clear();
        edt_Mname.getText().clear();
        edt_Lname.getText().clear();
        edt_GuardianName.getText().clear();
        edt_PhoneNumber.getText().clear();
        UUID uuStdid = UUID.randomUUID();
        randomUUIDYouth = uuStdid.toString();
        btn_BirthDatePicker.setText(R.string.birthdate);
        blocks_spinner.setSelection(0);
        villages_spinner.setSelection(0);
        groups_spinner.setSelection(0);
        education_spinner.setSelection(0);
        occupation_spinner.setSelection(0);
    }

}
