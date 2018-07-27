package com.pratham.admin.forms;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pratham.admin.R;
import com.pratham.admin.custom.MultiSpinner;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.CRLVisit;
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.CustomGroup;
import com.pratham.admin.util.DatePickerFragmentOne;
import com.pratham.admin.util.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CrlVisitForm extends AppCompatActivity {

    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.sp_VisitedGroups_multiselect)
    MultiSpinner sp_VisitedGroups_multiselect;
    @BindView(R.id.sp_PresentCoaches_multiselect)
    MultiSpinner sp_PresentCoaches_multiselect;
    @BindView(R.id.sp_CoachesWithGrp_multiselect)
    MultiSpinner sp_CoachesWithGrp_multiselect;
    @BindView(R.id.sp_GrpWithTheirGrp_multiselect)
    MultiSpinner sp_GrpWithTheirGrp_multiselect;
    @BindView(R.id.sp_WorkCrosscheckedGrps_multiselect)
    MultiSpinner sp_WorkCrosscheckedGrps_multiselect;
    @BindView(R.id.edt_PresentStdCount)
    EditText edt_PresentStdCount;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;

    List<Village> villageList = new ArrayList<>();
    List<Coach> coachList = new ArrayList<>();
    List<Groups> AllGroupsInDB;

    // Visited Groups VG
    List<String> selectedVGArray;
    List registeredVGGRPs;
    private boolean[] selectedVGItems;
    List<String> VG;
    String selectedVG = "";

    // GrpWithTheirGrp GWTG
    List<String> selectedGWTGArray;
    private boolean[] selectedGWTGItems;
    List<String> GWTG;
    String selectedGWTG = "";

    // WorkCrosscheckedGrps WCCG
    List<String> selectedWCCGArray;
    private boolean[] selectedWCCGItems;
    List<String> WCCG;
    String selectedWCCG = "";

    // Present Coaches PC
    List<CustomGroup> registeredPCGRPs;
    private boolean[] selectedPCItems;
    List<String> PC = new ArrayList<>();
    String selectedPC = "";

    // PresentCoachesWithTheirGrp  PCWG
    private boolean[] selectedPCWGItems;
    List<String> PCWG;
    String[] selectedPCWGArray;
    String selectedPCWG = "";

    java.util.UUID UUID;
    String uniqueVisitID = "";
    String vid;
    private String vName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crl_visit_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();

        // Generate Random UUID
        uniqueVisitID = UUID.randomUUID().toString();

        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);

        //retrive all groups from  DB
        AllGroupsInDB = AppDatabase.getDatabaseInstance(this).getGroupDao().getAllGroups();

        // Populate Village Spinner
        villageList = AppDatabase.getDatabaseInstance(this).getVillageDao().getAllVillages();
        populateVillages();

        // Populate Coach Spinner
        coachList = AppDatabase.getDatabaseInstance(this).getCoachDao().getAllCoaches();
        populatePresentCoaches();

    }


    @OnClick(R.id.btn_Submit)
    public void submitForm(View view) {
        if ((sp_Village.getSelectedItemPosition() > 0) && (selectedVG.trim().length() > 0)
                && (selectedGWTG.trim().length() > 0) && (selectedWCCG.trim().length() > 0)
                && (selectedPC.trim().length() > 0) && (selectedPCWG.trim().length() > 0)
                && (edt_PresentStdCount.getText().toString().trim().length() > 0)) {
            try {

                String date = btn_DatePicker.getText().toString().trim();

                CRLVisit cvObj = new CRLVisit();

                cvObj.VisitID = uniqueVisitID;
                cvObj.VillageID = Integer.parseInt(vid);
                cvObj.DateVisited = date;
                cvObj.GroupIDVisited = selectedVG;
                cvObj.CoachPresentInVillage = selectedPC;
                cvObj.CoachPresentWithGroup = selectedPCWG;
                cvObj.PresentGroupIDs = selectedGWTG; // Select group with their grp = present grp id
                cvObj.WorkCrosscheckedGroupIDs = selectedWCCG;
                cvObj.PresentStudents = edt_PresentStdCount.getText().toString().trim();
                cvObj.Village = vName;
                cvObj.Group = "";

                AppDatabase.getDatabaseInstance(this).getCRLVisitdao().insertCRLVisit(Collections.singletonList(cvObj));
                resetForm();
                Toast.makeText(this, "Form Submitted !!!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }

        } else {
            Toast.makeText(this, "Please fill all the fields !!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetForm() {
        populateVillages();
        populatePresentCoaches();
        edt_PresentStdCount.getText().clear();
        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);
        uniqueVisitID = UUID.randomUUID().toString();
    }

    @OnClick(R.id.btn_DatePicker)
    public void visitDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentOne();
        newFragment.show(getFragmentManager(), "DatePicker");
    }


    private void populateVillages() {
        final List VillageName = new ArrayList();
        if (!villageList.isEmpty()) {
            VillageName.add(new CustomGroup("Select Village"));
            for (int j = 0; j < villageList.size(); j++) {
                CustomGroup customGroup = new CustomGroup(villageList.get(j).getVillageName(), villageList.get(j).getVillageId());
                VillageName.add(customGroup);
            }
            ArrayAdapter villageAdapter = new ArrayAdapter(CrlVisitForm.this, android.R.layout.simple_spinner_dropdown_item, VillageName);
            sp_Village.setAdapter(villageAdapter);
        }

        sp_Village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomGroup customGroup = (CustomGroup) VillageName.get(pos);
                vid = customGroup.getId();
                vName = customGroup.getName();

                // Populate Visited Groups Spinner
                populateVisitedGroups(vid);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    // VISITED GROUPS
    private void populateVisitedGroups(String villageID) {
        // todo get registered grps
        registeredVGGRPs = new ArrayList();
        if (AllGroupsInDB != null) {
//            VG = new String[AllGroupsInDB.size()];
            VG = new ArrayList<>();
            for (int i = 0; i < AllGroupsInDB.size(); i++) {
                if (AllGroupsInDB.get(i).getVillageId().equals(villageID)) {
                    registeredVGGRPs.add(new CustomGroup(AllGroupsInDB.get(i).getGroupName(), AllGroupsInDB.get(i).getGroupId()));
//                    VG[i] = AllGroupsInDB.get(i).getGroupId();
                    VG.add(AllGroupsInDB.get(i).getGroupId());
                }
            }
        }

        ArrayAdapter grpAdapter = new ArrayAdapter(CrlVisitForm.this, android.R.layout.simple_spinner_dropdown_item, registeredVGGRPs);
        sp_VisitedGroups_multiselect.setAdapter(grpAdapter, false, onVGSelectedListener);
        // set initial selection
        selectedVGItems = new boolean[grpAdapter.getCount()];
        sp_VisitedGroups_multiselect.setHint("Select Visited Groups");
        sp_VisitedGroups_multiselect.setHintTextColor(Color.BLACK);

    }

    // VG Listener
    private MultiSpinner.MultiSpinnerListener onVGSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            List<String> grp_sel = new ArrayList<>();
            selectedVGArray = new ArrayList<>();
            selectedVG = "";
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedVGArray.add(VG.get(i));
                    selectedVG = selectedVG + "," + VG.get(i);
                }
            }
            selectedVG = selectedVG.replaceFirst(",", "");

            // Populate GrpWithTheirGrp
            populateGrpWithTheirGrp(selectedVGArray);

            // Populate WorkCrosscheckedGrps
            populateWorkCrosscheckedGrps(selectedVGArray);

//            Toast.makeText(CrlVisitForm.this, "" + selectedVG, Toast.LENGTH_SHORT).show();
        }
    };


    // VISITED GROUPS
    private void populateGrpWithTheirGrp(final List<String> selectedgrpID) {
        // todo get registered grps
        List<String> registeredGWTGGRPs = new ArrayList<>();

        for (int i = 0; i < AllGroupsInDB.size(); i++) {
            for (int j = 0; j < selectedgrpID.size(); j++) {
                if (AllGroupsInDB.get(i).getGroupId().equalsIgnoreCase(selectedgrpID.get(j))) {
                    registeredGWTGGRPs.add(AllGroupsInDB.get(i).getGroupName());
                }
            }
        }
        ArrayAdapter grpAdapter = new ArrayAdapter(CrlVisitForm.this, android.R.layout.simple_spinner_dropdown_item, registeredGWTGGRPs);
        sp_GrpWithTheirGrp_multiselect.setAdapter(grpAdapter, false, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                selectedGWTGArray = new ArrayList<>();
                selectedGWTG = "";
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i]) {
                        selectedGWTGArray.add(selectedgrpID.get(i));
                        selectedGWTG = selectedGWTG + "," + selectedgrpID.get(i);
                    }
                }
                selectedGWTG = selectedGWTG.replaceFirst(",", "");
//                Toast.makeText(CrlVisitForm.this, "" + selectedGWTG, Toast.LENGTH_SHORT).show();
            }
        });
        // set initial selection
        selectedGWTGItems = new boolean[grpAdapter.getCount()];
        sp_GrpWithTheirGrp_multiselect.setHint("Select Groups with Their Groups");
        sp_GrpWithTheirGrp_multiselect.setHintTextColor(Color.BLACK);

    }


    // WorkCrosscheckedGrps GROUPS
    private void populateWorkCrosscheckedGrps(final List<String> selectedGrpID) {
        // todo get registered grps
        List registeredWCCGGRPs = new ArrayList();

        for (int i = 0; i < AllGroupsInDB.size(); i++) {
            for (int j = 0; j < selectedGrpID.size(); j++) {
                if (AllGroupsInDB.get(i).getGroupId().equalsIgnoreCase(selectedGrpID.get(j))) {
                    registeredWCCGGRPs.add(AllGroupsInDB.get(i).getGroupName());
                }
            }
        }

        ArrayAdapter grpAdapter = new ArrayAdapter(CrlVisitForm.this, android.R.layout.simple_spinner_dropdown_item, registeredWCCGGRPs);
        sp_WorkCrosscheckedGrps_multiselect.setAdapter(grpAdapter, false, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                selectedWCCGArray = new ArrayList<>();
                selectedWCCG = "";
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i]) {
                        selectedWCCGArray.add(selectedGrpID.get(i));
                        selectedWCCG = selectedWCCG + "," + selectedGrpID.get(i);
                    }
                }
                selectedWCCG = selectedWCCG.replaceFirst(",", "");
//                Toast.makeText(CrlVisitForm.this, "" + selectedWCCG, Toast.LENGTH_SHORT).show();
            }
        });
        // set initial selection
        selectedWCCGItems = new boolean[grpAdapter.getCount()];
        sp_WorkCrosscheckedGrps_multiselect.setHint("Select Work Crosschecked Groups");
        sp_WorkCrosscheckedGrps_multiselect.setHintTextColor(Color.BLACK);
    }

    // Present Groups
    private void populatePresentCoaches() {
        registeredPCGRPs = new ArrayList();
        for (int j = 0; j < coachList.size(); j++) {
            CustomGroup customGroup = new CustomGroup(coachList.get(j).getCoachName(), coachList.get(j).getCoachID());
            registeredPCGRPs.add(customGroup);
            PC.add(coachList.get(j).getCoachID());
        }

        ArrayAdapter coachAdapter = new ArrayAdapter(CrlVisitForm.this, android.R.layout.simple_spinner_dropdown_item, registeredPCGRPs);
        sp_PresentCoaches_multiselect.setAdapter(coachAdapter, false, onPCSelectedListener);
        // set initial selection
        selectedPCItems = new boolean[coachAdapter.getCount()];
        sp_PresentCoaches_multiselect.setHint("Select Present Coach");
        sp_PresentCoaches_multiselect.setHintTextColor(Color.BLACK);

    }

    // PC Listener
    private MultiSpinner.MultiSpinnerListener onPCSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            List<String> selectedPCArray = new ArrayList<>();
            selectedPC = "";
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedPCArray.add(PC.get(i));
                    selectedPC = selectedPC + "," + PC.get(i);
                }
            }
            selectedPC = selectedPC.replaceFirst(",", "");
            populateCoachesWithTheirGroup(selectedPCArray);
        }
    };

    // Present Groups with their grps
    private void populateCoachesWithTheirGroup(final List<String> selectedPCArray) {
        List<String> registeredPCWGGRPs = new ArrayList();
        for (int i = 0; i < registeredPCGRPs.size(); i++) {
            for (int j = 0; j < selectedPCArray.size(); j++) {
                if (registeredPCGRPs.get(i).getId().equalsIgnoreCase(selectedPCArray.get(j))) {
                    registeredPCWGGRPs.add(registeredPCGRPs.get(i).getName());
                }
            }
        }

        ArrayAdapter coachAdapter = new ArrayAdapter(CrlVisitForm.this, android.R.layout.simple_spinner_dropdown_item, registeredPCWGGRPs);
        sp_CoachesWithGrp_multiselect.setAdapter(coachAdapter, false, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                selectedPCWGArray = new String[selected.length];
                selectedPCWG = "";
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i]) {
                        selectedPCWGArray[i] = selectedPCArray.get(i);
                        selectedPCWG = selectedPCWG + "," + selectedPCArray.get(i);
                    }
                }
                selectedPCWG = selectedPCWG.replaceFirst(",", "");
            }
        });
        // set initial selection
        selectedPCWGItems = new boolean[coachAdapter.getCount()];
        sp_CoachesWithGrp_multiselect.setHint("Select Coach with Their Group");
        sp_CoachesWithGrp_multiselect.setHintTextColor(Color.BLACK);

    }
}
