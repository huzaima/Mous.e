package com.huzaima.mouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dd.processbutton.iml.ActionProcessButton;

/**
 * Created by Marib on 4/17/2016.
 */
public class UserCalibrationActivity extends Activity implements SensorEventListener{

    ActionProcessButton calibrate_button;

    private SensorManager senSensorManager;
    private Sensor senRotation;
    float mid, up, down, left, right;
    float[] tempvalues = new float[10];
    ImageView circle;

    int onScreen = 1;
    boolean buttonPressed = false;

    @Override
    protected void onResume() {
        super.onResume();

        calibrate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed = true;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.user_calibration_activity);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senRotation = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        senSensorManager.registerListener(this, senRotation , SensorManager.SENSOR_DELAY_NORMAL);

        calibrate_button = (ActionProcessButton) findViewById(R.id.calibrate_button);
        circle = (ImageView) findViewById(R.id.circle);
    }//onCreate


    int iteration = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor mySensor = event.sensor;

        if(mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            switch (onScreen) {

                case 1 :
                    if(buttonPressed) {

                        if(iteration == 10){

                            iteration = 0;
                            mid = mean(tempvalues);
                            Log.i("mid = ",""+mid);
                            onScreen = 2;
                            buttonPressed = false;

                            break;
                        }


                        tempvalues[iteration] = event.values[2];
                        iteration++;


                    }//If Button one pressed

                    break;

                case 2:
                    if(buttonPressed) {

                        if(iteration == 10){
                            iteration = 0;
                            up = mean(tempvalues);
                            Log.i("UP = ",""+ up);
                            onScreen = 3;
                            buttonPressed = false;

                            break;
                        }
                            tempvalues[iteration] = event.values[2];
                            iteration++;


                    }//If

                    break;
                case 3:
                    if(buttonPressed) {

                        if(iteration == 10){
                            iteration = 0;
                            down = mean(tempvalues);
                            Log.i("DOWN = ",""+ down);
                            onScreen = 4;
                            buttonPressed = false;

                            break;
                        }
                        tempvalues[iteration] = event.values[2];
                        iteration++;


                    }//If

                    break;
                case 4:
                    if(buttonPressed) {

                        if(iteration == 10){
                            iteration = 0;
                            left = mean(tempvalues);
                            Log.i("LEFT = ",""+ left);
                            onScreen = 5;
                            buttonPressed = false;

                            break;
                        }
                        tempvalues[iteration] = event.values[2];
                        iteration++;


                    }//If

                    break;

                case 5:
                    if(buttonPressed) {

                        if(iteration == 10){

                            right = mean(tempvalues);
                            Log.i("Right = ",""+ right);

                            finishedCalibration();
                            break;
                        }
                            tempvalues[iteration] = event.values[2];
                            iteration++;


                    }//If
                    break;

            }//Switch

        }//if

    }//onSensorChanged

    private void finishedCalibration() {

        senSensorManager.unregisterListener(this);


        Intent i = new Intent(UserCalibrationActivity.this,MainActivity.class);
        i.putExtra("MID",mid);
        i.putExtra("UP", up);
        i.putExtra("DOWN", down);
        i.putExtra("LEFT", left);
        i.putExtra("RIGHT", right);

        startActivity(i);
        finish();

    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }//onAccuracyChanged


    public static float mean(float[] temp) {
        float sum = 0;
        for (int i = 0; i < temp.length; i++) {
            sum += temp[i];
        }
        return sum / temp.length;
    }//mean
}
