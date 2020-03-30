package com.pratham.admin.activities.AddNewGroup;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
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
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.OnClick;

@EActivity(R.layout.activity_add_new_group)
public class AddNewGroup extends BaseActivity implements AddNewGroupContract.AddNewGroupView{

    @ViewById(R.id.spinner_SelectBlock)
    Spinner blocks_spinner;
    @ViewById(R.id.edt_NewGroupName)
    EditText edt_NewGroupName;
    @ViewById(R.id.btn_Submit)
    Button btn_Submit;
    @ViewById(R.id.btn_Clear)
    Button btn_Clear;

    Spinner villages_spinner;
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
//    boolean internetIsAvailable = false;

    @Bean(AddNewGroupPresenter.class)
    AddNewGroupContract.AddNewGroupPresenter addNewGroupPresenter;

    @AfterViews
    public void initilaze() {

        addNewGroupPresenter.setView(AddNewGroup.this);
        // Hide Actionbar
        getSupportActionBar().hide();

//        checkConnection();

        villages_spinner = (Spinner) findViewById(R.id.spinner_selectVillage);
        sessionContex = this;
        villageContext = this;
        grpContext = this;
        util = new Utility();
        // Unique ID For GroupID
        uuid = UUID.randomUUID();
        randomUUIDGroup = uuid.toString();
        initializeState();
        // Generate Unique Device ID
        deviceID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        /*//Device ID from Assign Groups
        TelephonyManager tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        deviceIMEI = tManager.getDeviceId();
        final String devID = deviceIMEI;
*/

    }

    @Click(R.id.btn_Submit)
    public void SubmitData() {
        // Check AllSpinners Emptyness
        int BlocksSpinnerValue = blocks_spinner.getSelectedItemPosition();
        int VillagesSpinnerValue = villages_spinner.getSelectedItemPosition();

        if (BlocksSpinnerValue > 0 && VillagesSpinnerValue > 0) {

            String GroupName = edt_NewGroupName.getText().toString();
            if ((GroupName.matches("[a-zA-Z0-9 ]*")) && (GroupName.length() > 0 && GroupName.length() < 21)) {

                Groups grpobj = new Groups();

                grpobj.GroupId = randomUUIDGroup;
                grpobj.GroupCode = "";
                grpobj.GroupName = edt_NewGroupName.getText().toString();
                grpobj.DeviceId = deviceID.equals(null) ? "0000" : deviceID;
                grpobj.VillageId = String.valueOf(vilID);
                //grpobj.ProgramId = Integer.parseInt(AppDatabase.getDatabaseInstance(AddNewGroup.this).getMetaDataDao().getProgramID());
                //grpobj.CreatedBy = AppDatabase.getDatabaseInstance(AddNewGroup.this).getMetaDataDao().getCrlMetaData();
                grpobj.CreatedOn = util.GetCurrentDateTime(false).toString();
                grpobj.sentFlag = 0;

                addNewGroupPresenter.addGroupToDB(grpobj);
                BackupDatabase.backup(AddNewGroup.this);
                FormReset();
            } else {
                Toast.makeText(AddNewGroup.this, "Please Enter less than 21 Alphabets only as Group Name !!!", Toast.LENGTH_SHORT).show();
            }

        } else {
            showToast(R.string.fillAllDetails);
        }
    }

    @Click(R.id.btn_Clear)
    public void clearData() {
        FormReset();
    }

    private void initializeState() {
        //Get Villages Data for States
        List<String> States = new ArrayList<>();
        States.clear();
        States = AppDatabase.getDatabaseInstance(AddNewGroup.this).getVillageDao().getState();
        addNewGroupPresenter.populateBlock(States.get(0));
    }

    public void FormReset() {

        blocks_spinner.setSelection(0);
        villages_spinner.setSelection(0);
        edt_NewGroupName.getText().clear();
        // Unique ID For GroupID
        uuid = UUID.randomUUID();
        randomUUIDGroup = uuid.toString();
    }

    @Override
    public void showBlock(List<String> Blocks) {
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
                addNewGroupPresenter.populateVillage(selectedBlock);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showVillage(List<Village> Village) {
        Village v = new Village();
        v.VillageId = "0";
        v.VillageName = getString(R.string.selectvillage);
        Village.add(0,v);
        List<Village> SpinnerBlocksVillages = new ArrayList<>();
        for (int i = 0; i < Village.size(); i++)
            SpinnerBlocksVillages.add(new Village(Integer.parseInt(Village.get(i).VillageId), Village.get(i).VillageName));

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void showToast(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}