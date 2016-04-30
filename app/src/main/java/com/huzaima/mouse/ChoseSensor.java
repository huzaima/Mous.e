package com.huzaima.mouse;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dd.processbutton.iml.ActionProcessButton;

public class ChoseSensor extends AppCompatActivity {

    ActionProcessButton gyro_button, acc_button;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_sensor);
//
//        this.overridePendingTransition(R.anim.left_to_right,
//                R.anim.right_to_left);

        gyro_button = (ActionProcessButton) findViewById(R.id.wifi_button);
        acc_button = (ActionProcessButton) findViewById(R.id.bluetooth_button);


    }

    @Override
    protected void onResume() {
        super.onResume();

        gyro_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChoseSensor.this, UserCalibrationActivity.class);
                startActivity(i);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(ChoseSensor.this, findViewById(R.id.wifi_button), "profile");
                startActivity(i, options.toBundle());

            }
        });

        acc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChoseSensor.this, AccelerometerActivity.class);
                startActivity(i);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(ChoseSensor.this, findViewById(R.id.bluetooth_button), "profile");
                startActivity(i, options.toBundle());

            }
        });


    }
}
