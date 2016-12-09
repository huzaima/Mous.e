package com.huzaima.mouse;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Marib on 4/17/2016.
 */
public class UserCalibrationActivity extends AppCompatActivity implements
        View.OnClickListener,
        SensorEventListener {

    private ActionProcessButton calibrate;
    private int count = 0;
    private Boolean startReadingSensor = false;
    private ArrayList<Float> sensorData = new ArrayList<>();
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Intent intent;
    private TextView tv;
    private float up, down, left, right;
    private final int CALIBRATION_READINGS = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_calibration);

        calibrate = (ActionProcessButton) findViewById(R.id.calibrate_button);
        calibrate.setMode(ActionProcessButton.Mode.ENDLESS);

        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        tv = (TextView) findViewById(R.id.textaaa);
        tv.setText(getText(R.string.calibrationtexttop));

        intent = new Intent(getApplicationContext(), MainActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        calibrate.setOnClickListener(this);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mSensor);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.calibrate_button:
                switch (count) {
                    case 0:
                        indexToRead = 0;
                        startReadingSensor = true;
                        calibrate.setEnabled(false);
                        calibrate.setAlpha(0.75f);
                        calibrate.setProgress(0);
                        break;
                    case 1:
                        indexToRead = 0;
                        startReadingSensor = true;
                        calibrate.setEnabled(false);
                        calibrate.setAlpha(0.75f);
                        calibrate.setProgress(0);
                        break;
                    case 2:
                        indexToRead = 2;
                        startReadingSensor = true;
                        calibrate.setEnabled(false);
                        calibrate.setAlpha(0.75f);
                        calibrate.setProgress(0);
                        break;
                    case 3:
                        indexToRead = 2;
                        startReadingSensor = true;
                        calibrate.setEnabled(false);
                        calibrate.setAlpha(0.75f);
                        calibrate.setProgress(0);
                }
                break;
        }
    }

    private int indexToRead;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            if (startReadingSensor) {
                if (sensorData.size() < CALIBRATION_READINGS) {
                    calibrate.setProgress((int) (sensorData.size() / (CALIBRATION_READINGS / 100f)));
                    sensorData.add(event.values[indexToRead]);
                    Log.v("calibration", "Reading: " + Arrays.toString(event.values));
                } else {
                    float mean = mean(sensorData);
                    switch (count) {
                        case 0:
                            Log.v("calibration", "Up: " + mean);
                            up = mean;
                            tv.setText(getText(R.string.calibrationtextbottom));
                            calibrate.setProgress(0);
                            break;
                        case 1:
                            Log.v("calibration", "Down: " + mean);
                            down = mean;
                            tv.setText(getText(R.string.calibrationtextleft));
                            calibrate.setProgress(0);
                            break;
                        case 2:
                            Log.v("calibration", "Left: " + mean);
                            left = mean;
                            tv.setText(getText(R.string.calibrationtextright));
                            calibrate.setProgress(0);
                            break;
                        case 3:
                            Log.v("calibration", "Right: " + mean);
                            right = mean;
                            mSensorManager.unregisterListener(this, mSensor);
                            calibrate.setProgress(100);
                            calibrate.setText("Calibrated Successfully");
                            writeCalibrationData.start();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(intent);
                                    finish();
                                }
                            }, 2 * 1000);
                            return;
                    }
                    count++;
                    sensorData.clear();
                    startReadingSensor = false;
                    calibrate.setEnabled(true);
                    calibrate.setAlpha(1f);
                    final MediaPlayer player = MediaPlayer.create(getApplicationContext(), R.raw.success);
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            player.release();
                        }
                    });
                    player.start();
                }
            }
        }
    }

    private Thread writeCalibrationData = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(SocketHandler.getCalibrationSocket().getOutputStream()));
                writer.write(up + " " + down + " " + left + " " + right + "\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private float mean(ArrayList<Float> readings) {

        float mean = 0f;

        for (int c = 0; c < readings.size(); c++) {
            mean += readings.get(c);
        }

        return mean / (float) readings.size();
    }
}