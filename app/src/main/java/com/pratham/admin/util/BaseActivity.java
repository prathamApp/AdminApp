package com.pratham.admin.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pratham.admin.activities.CatchoTransparentActivity;

import net.alhazmy13.catcho.library.Catcho;


/**
 * Created by Ameya on 15-Mar-18.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Catcho.Builder(this)
                .activity(CatchoTransparentActivity.class)
                .recipients("your-email@domain.com")
                .build();

    }
}