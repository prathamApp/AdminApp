package com.pratham.admin.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.Button;

import com.pratham.admin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckInternetDialog extends Dialog {
    @BindView(R.id.btn_exit)
    Button exit;
    Context context;

    public CheckInternetDialog(Context context) {
        super(context);
        setContentView(R.layout.no_internet_dialog);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);
        this.context = context;
    }

    @OnClick({R.id.btn_exit})
    public void onExit() {
        dismiss();
        ((Activity) context).finish();
    }

}
