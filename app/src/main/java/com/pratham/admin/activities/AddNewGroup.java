package com.pratham.admin.activities;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddNewGroup extends AppCompatActivity {

    Spinner states_spinner, blocks_spinner, villages_spinner;
    EditText edt_NewGroupName;
    Button btn_Submit, btn_Clear;
    List<String> Blocks = new ArrayList<>();
    int vilID;
    String deviceID = "";
    String deviceIMEI = "";
    UUID uuid;
    String randomUUIDGroup;
    Context villageContext, grpContext;

    Context sessionContex;
    boolean timer;
    Utility util;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group);

        // Hide Actionbar
        getSupportActionBar().hide();

        sessionContex = this;

        villageContext = this;

        grpContext = this;
        util = new Utility();

        // Unique ID For GroupID
        uuid = UUID.randomUUID();
        randomUUIDGroup = uuid.toString();

        states_spinner = (Spinner) findViewById(R.id.spinner_SelectState);
        //Get Villages Data for States AllSpinners
        List<String> States = new ArrayList<>();
        States.clear();
        States = AppDatabase.getDatabaseInstance(AddNewGroup.this).getVillageDao().getState();
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        edt_NewGroupName = (EditText) findViewById(R.id.edt_NewGroupName);

        // Generate Unique Device ID
        deviceID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        /*//Device ID from Assign Groups
        TelephonyManager tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        deviceIMEI = tManager.getDeviceId();
        final String devID = deviceIMEI;
*/
        btn_Submit = (Button) findViewById(R.id.btn_Submit);
        btn_Clear = (Button) findViewById(R.id.btn_Clear);

        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check AllSpinners Emptyness
                int StatesSpinnerValue = states_spinner.getSelectedItemPosition();
                int BlocksSpinnerValue = blocks_spinner.getSelectedItemPosition();
                int VillagesSpinnerValue = villages_spinner.getSelectedItemPosition();

                if (StatesSpinnerValue > 0 && BlocksSpinnerValue > 0 && VillagesSpinnerValue > 0) {

                    String GroupName = edt_NewGroupName.getText().toString();
                    if ((GroupName.matches("[a-zA-Z0-9 ]*")) && (GroupName.length() > 0 && GroupName.length() < 21)) {

                        Groups grpobj = new Groups();

                        grpobj.GroupId = randomUUIDGroup;
                        grpobj.GroupCode = "";
                        grpobj.GroupName = edt_NewGroupName.getText().toString();
                        grpobj.DeviceId = deviceID.equals(null) ? "0000" : deviceID;
                        grpobj.VillageId = String.valueOf(vilID);
                        grpobj.ProgramId = Integer.parseInt(AppDatabase.getDatabaseInstance(AddNewGroup.this).getMetaDataDao().getProgramID());
                        grpobj.CreatedBy = AppDatabase.getDatabaseInstance(AddNewGroup.this).getMetaDataDao().getCrlMetaData();
                        grpobj.CreatedOn = util.GetCurrentDateTime(false).toString();
                        grpobj.sentFlag = 0;

                        AppDatabase.getDatabaseInstance(AddNewGroup.this).getGroupDao().insertGroup(grpobj);

                        Toast.makeText(AddNewGroup.this, "Record Inserted Successfully !!!", Toast.LENGTH_SHORT).show();
                        BackupDatabase.backup(grpContext);
                        FormReset();
                    } else {
                        Toast.makeText(AddNewGroup.this, "Please Enter less than 21 Alphabets only as Group Name !!!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(AddNewGroup.this, "Please Fill all fields !!!", Toast.LENGTH_SHORT).show();
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

    public void populateBlock(String selectedState) {
        blocks_spinner = (Spinner) findViewById(R.id.spinner_SelectBlock);
        //Get Villages Data for Blocks AllSpinners
        Blocks.clear();
        Blocks = AppDatabase.getDatabaseInstance(AddNewGroup.this).getVillageDao().GetStatewiseBlock(selectedState);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,CrlAddEditScreen.class);
        this.finish();
        startActivity(intent);
    }*/

    public void populateVillage(String selectedBlock) {
        villages_spinner = (Spinner) findViewById(R.id.spinner_selectVillage);
        //Get Villages Data for Villages filtered by block for Spinners
        List<Village> BlocksVillages = new ArrayList<>();
        BlocksVillages.clear();
        Village v = new Village();
        v.VillageId = "0";
        v.VillageName = "Select Village";
        BlocksVillages = AppDatabase.getDatabaseInstance(AddNewGroup.this).getVillageDao().GetVillages(selectedBlock);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void FormReset() {

        states_spinner.setSelection(0);
        blocks_spinner.setSelection(0);
        villages_spinner.setSelection(0);
        edt_NewGroupName.getText().clear();
        // Unique ID For GroupID
        uuid = UUID.randomUUID();
        randomUUIDGroup = uuid.toString();
    }


}