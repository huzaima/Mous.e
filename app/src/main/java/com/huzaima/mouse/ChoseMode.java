package com.huzaima.mouse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.dd.processbutton.iml.ActionProcessButton;

/**
 * Created by Marib on 4/19/2016.
 */
public class ChoseMode extends AppCompatActivity {

    ActionProcessButton wifi_button,bluetooth_button;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chose_mode);
//
//        this.overridePendingTransition(R.anim.left_to_right,
//                R.anim.right_to_left);

        wifi_button = (ActionProcessButton) findViewById(R.id.wifi_button);
        bluetooth_button = (ActionProcessButton) findViewById(R.id.bluetooth_button);


    }

    @Override
    protected void onResume() {
        super.onResume();

        wifi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChoseMode.this,InputAddressAndPort.class);
                startActivity(i);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(ChoseMode.this, findViewById(R.id.wifi_button), "profile");
                                startActivity(i, options.toBundle());

            }
        });

    }
}
