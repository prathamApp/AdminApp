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
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.CustomGroup;
import com.pratham.admin.util.DatePickerFragmentOne;
import com.pratham.admin.util.Utility;

import java.util.ArrayList;
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
    List registeredVGGRPs;
    private boolean[] selectedVGItems;
    String[] selectedVGArray;
    String[] VG;
    String selectedVG = "";

    // GrpWithTheirGrp GWTG
    List registeredGWTGGRPs;
    private boolean[] selectedGWTGItems;
    String[] GWTG;
    String[] selectedGWTGArray;
    String selectedGWTG = "";

    // WorkCrosscheckedGrps WCCG
    List registeredWCCGGRPs;
    private boolean[] selectedWCCGItems;
    String[] WCCG;
    String[] selectedWCCGArray;
    String selectedWCCG = "";

    // Present Coaches PC
    List registeredPCGRPs;
    private boolean[] selectedPCItems;
    String[] PC;
    String[] selectedPCArray;
    String selectedPC = "";

    // PresentCoachesWithTheirGrp  PCWG
    List registeredPCWGGRPs;
    private boolean[] selectedPCWGItems;
    String[] PCWG;
    String[] selectedPCWGArray;
    String selectedPCWG = "";

    java.util.UUID UUID;
    String uniqueVisitID = "";

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
        populateCoachesWithTheirGroup();

    }


    @OnClick(R.id.btn_Submit)
    public void submitForm(View view) {
        if ((sp_Village.getSelectedItemPosition() > 0) && (selectedVG.trim().length() > 0)
                && (selectedGWTG.trim().length() > 0) && (selectedWCCG.trim().length() > 0)
                && (selectedPC.trim().length() > 0) && (selectedPCWG.trim().length() > 0)
                && (edt_PresentStdCount.getText().toString().trim().length() > 0)) {
            try {


                Toast.makeText(this, "Form Submitted !!!", Toast.LENGTH_SHORT).show();
                resetForm();
            } catch (Exception e) {

            }

        } else {
            Toast.makeText(this, "Please fill all the fields !!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetForm() {
        populateVillages();
        populatePresentCoaches();
        populateCoachesWithTheirGroup();
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
                String vid = customGroup.getId();

                // Populate Visited Groups Spinner
                populateVisitedGroups(vid);

                // Populate GrpWithTheirGrp
                populateGrpWithTheirGrp(vid);

                // Populate WorkCrosscheckedGrps
                populateWorkCrosscheckedGrps(vid);


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
            VG = new String[AllGroupsInDB.size()];
            for (int i = 0; i < AllGroupsInDB.size(); i++) {
                if (AllGroupsInDB.get(i).getVillageId().equals(villageID)) {
                    registeredVGGRPs.add(new CustomGroup(AllGroupsInDB.get(i).getGroupName(), AllGroupsInDB.get(i).getGroupId()));
                    VG[i] = AllGroupsInDB.get(i).getGroupName();
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
            selectedVGArray = new String[selected.length];
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedVGArray[i] = VG[i];
                    selectedVG = selectedVG + "," + selectedVGArray[i];
                }
            }
            selectedVG = selectedVG.replaceFirst(",", "");

        }
    };


    // VISITED GROUPS
    private void populateGrpWithTheirGrp(String villageID) {
        // todo get registered grps
        registeredGWTGGRPs = new ArrayList();
        if (AllGroupsInDB != null) {
            GWTG = new String[AllGroupsInDB.size()];
            for (int i = 0; i < AllGroupsInDB.size(); i++) {
                if (AllGroupsInDB.get(i).getVillageId().equals(villageID)) {
                    registeredGWTGGRPs.add(new CustomGroup(AllGroupsInDB.get(i).getGroupName(), AllGroupsInDB.get(i).getGroupId()));
                    GWTG[i] = AllGroupsInDB.get(i).getGroupName();
                }
            }
        }

        ArrayAdapter grpAdapter = new ArrayAdapter(CrlVisitForm.this, android.R.layout.simple_spinner_dropdown_item, registeredGWTGGRPs);
        sp_GrpWithTheirGrp_multiselect.setAdapter(grpAdapter, false, onGWTGSelectedListener);
        // set initial selection
        selectedGWTGItems = new boolean[grpAdapter.getCount()];
        sp_GrpWithTheirGrp_multiselect.setHint("Select Groups with Their Groups");
        sp_GrpWithTheirGrp_multiselect.setHintTextColor(Color.BLACK);

    }

    // VG Listener
    private MultiSpinner.MultiSpinnerListener onGWTGSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            selectedGWTGArray = new String[selected.length];
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedGWTGArray[i] = GWTG[i];
                    selectedGWTG = selectedGWTG + "," + selectedGWTGArray[i];
                }
            }
            selectedGWTG = selectedGWTG.replaceFirst(",", "");

        }
    };


    // WorkCrosscheckedGrps GROUPS
    private void populateWorkCrosscheckedGrps(String villageID) {
        // todo get registered grps
        registeredWCCGGRPs = new ArrayList();
        if (AllGroupsInDB != null) {
            WCCG = new String[AllGroupsInDB.size()];
            for (int i = 0; i < AllGroupsInDB.size(); i++) {
                if (AllGroupsInDB.get(i).getVillageId().equals(villageID)) {
                    registeredWCCGGRPs.add(new CustomGroup(AllGroupsInDB.get(i).getGroupName(), AllGroupsInDB.get(i).getGroupId()));
                    WCCG[i] = AllGroupsInDB.get(i).getGroupName();
                }
            }
        }

        ArrayAdapter grpAdapter = new ArrayAdapter(CrlVisitForm.this, android.R.layout.simple_spinner_dropdown_item, registeredWCCGGRPs);
        sp_WorkCrosscheckedGrps_multiselect.setAdapter(grpAdapter, false, onWCCGSelectedListener);
        // set initial selection
        selectedWCCGItems = new boolean[grpAdapter.getCount()];
        sp_WorkCrosscheckedGrps_multiselect.setHint("Select Work Crosschecked Groups");
        sp_WorkCrosscheckedGrps_multiselect.setHintTextColor(Color.BLACK);

    }

    // WorkCrosscheckedGrps Listener
    private MultiSpinner.MultiSpinnerListener onWCCGSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            selectedWCCGArray = new String[selected.length];
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedWCCGArray[i] = WCCG[i];
                    selectedWCCG = selectedWCCG + "," + selectedWCCGArray[i];
                }
            }
            selectedWCCG = selectedWCCG.replaceFirst(",", "");

        }
    };


    // Present Groups
    private void populatePresentCoaches() {
        registeredPCGRPs = new ArrayList();
        for (int j = 0; j < coachList.size(); j++) {
            CustomGroup customGroup = new CustomGroup(coachList.get(j).getCoachName(), coachList.get(j).getCoachID());
            registeredPCGRPs.add(customGroup);
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
            selectedPCArray = new String[selected.length];
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedPCArray[i] = PC[i];
                    selectedPC = selectedPC + "," + selectedPCArray[i];
                }
            }
            selectedPC = selectedPC.replaceFirst(",", "");

        }
    };

    // Present Groups with their grps
    private void populateCoachesWithTheirGroup() {
        List registeredPCWGGRPs = new ArrayList();
        for (int j = 0; j < coachList.size(); j++) {
            CustomGroup customGroup = new CustomGroup(coachList.get(j).getCoachName(), coachList.get(j).getCoachID());
            registeredPCWGGRPs.add(customGroup);
        }

        ArrayAdapter coachAdapter = new ArrayAdapter(CrlVisitForm.this, android.R.layout.simple_spinner_dropdown_item, registeredPCWGGRPs);
        sp_CoachesWithGrp_multiselect.setAdapter(coachAdapter, false, onPCWGSelectedListener);
        // set initial selection
        selectedPCWGItems = new boolean[coachAdapter.getCount()];
        sp_CoachesWithGrp_multiselect.setHint("Select Coach with Their Group");
        sp_CoachesWithGrp_multiselect.setHintTextColor(Color.BLACK);

    }

    // PCWG Listener
    private MultiSpinner.MultiSpinnerListener onPCWGSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            selectedPCWGArray = new String[selected.length];
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedPCWGArray[i] = PCWG[i];
                    selectedPCWG = selectedPCWG + "," + selectedPCWGArray[i];
                }
            }
            selectedPCWG = selectedPCWG.replaceFirst(",", "");

        }
    };


}
