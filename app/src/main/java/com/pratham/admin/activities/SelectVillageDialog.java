package com.pratham.admin.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pratham.admin.R;
import com.pratham.admin.interfaces.VillageListLisner;
import com.pratham.admin.modalclasses.Village;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectVillageDialog extends Dialog {

    @BindView(R.id.txt_clear_changes_village)
    TextView clear_changes;
    @BindView(R.id.btn_close_village)
    ImageButton btn_close;
    /*  @BindView(R.id.txt_count_village)
      TextView txt_count_village;*/
    @BindView(R.id.txt_message_village)
    TextView txt_message_village;
    @BindView(R.id.flowLayout)
    GridLayout flowLayout;

    Context context;
    List<Village> VillageNameList;
    List<CheckBox> checkBoxes = new ArrayList<>();
    VillageListLisner villageListLisner;

    public SelectVillageDialog(@NonNull Context context, List tempList) {
        super(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        this.VillageNameList = tempList;
        this.context = context;
        this.villageListLisner = (VillageListLisner) context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_village_dialog);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        for (int i = 0; i < VillageNameList.size(); i++) {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(VillageNameList.get(i).getVillageName());
            checkBox.setTag(VillageNameList.get(i).getVillageId());
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;
            param.setGravity(Gravity.FILL_HORIZONTAL);
            checkBox.setLayoutParams(param);
            flowLayout.addView(checkBox);
            checkBoxes.add(checkBox);
        }
    }


    @OnClick(R.id.btn_close_village)
    public void closeDialog() {
        dismiss();
    }

    @OnClick(R.id.txt_clear_changes_village)
    public void clearChanges() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxes.get(i).setChecked(false);
        }
    }

    @OnClick(R.id.txt_ok_village)
    public void ok() {
        List villageIdList = new ArrayList();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isChecked()) {
                villageIdList.add(checkBoxes.get(i).getTag());
            }
        }
        villageListLisner.getSelectedVillage(villageIdList);
        dismiss();
    }

}

