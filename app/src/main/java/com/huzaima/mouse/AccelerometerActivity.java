package com.huzaima.mouse;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.dd.processbutton.iml.ActionProcessButton;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private int readings = 0;
    private KalmanFilter xFilter, yFilter;
    private Queue<String> data;
    private ArrayList<Byte> dataToWrite;
    private boolean exit;
    private CircularFillableLoaders progress;
    private Snackbar snackbar;
    ActionProcessButton l,r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        progress = (CircularFillableLoaders) findViewById(R.id.progressbar);
        progress.setProgress(100);

        data = new LinkedList<>();
        dataToWrite = new ArrayList<>();
        exit = false;
        findViewById(R.id.dotted_line).setVisibility(View.INVISIBLE);
        findViewById(R.id.swipe).setVisibility(View.INVISIBLE);
        findViewById(R.id.swipe_text).setVisibility(View.INVISIBLE);
        l = (ActionProcessButton) findViewById(R.id.left_click);
        r = (ActionProcessButton) findViewById(R.id.right_click);
        l.setVisibility(View.INVISIBLE);
        r.setVisibility(View.INVISIBLE);

        new ConnectionThread(getIntent().getStringExtra("IP"), "5000").start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_LINEAR_ACCELERATION)
            return;

        if (readings == 0) {
            xFilter = new KalmanFilter(event.values[0]);
            yFilter = new KalmanFilter(event.values[1]);
            snackbar = Snackbar.make(findViewById(R.id.rootview),"Calibrating...",Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }

        if (readings > 0) {
            xFilter.update(event.values[0]);
            yFilter.update(event.values[1]);
            String temp;
            if (readings > 1000) {
                if (xFilter.filteredValue > xFilter.last+0.025 || xFilter.filteredValue < xFilter.last-0.025)//device moved left/right
                    if (xFilter.filteredValue > xFilter.last) {//movement relative to last movement
                        temp = "\n" + "Direction: Left            " + xFilter.filteredValue;
                        if (data.size() > 25)
                            data.poll();
                        data.add(temp);
                        dataToWrite.add((byte) 3);
                    } else {
                        temp = "\n" + "Direction: Right	       " + xFilter.filteredValue;
                        if (data.size() > 25)
                            data.poll();
                        data.add(temp);
                        dataToWrite.add((byte) 4);
                    }
                if (yFilter.filteredValue > yFilter.last+0.025 || yFilter.filteredValue < yFilter.last-0.025)//device moved up/down
                    if (yFilter.filteredValue > yFilter.last) {//movement relative to lase movement
                        temp = "\n" + "Direction: Down	           " + yFilter.filteredValue;
                        if (data.size() > 25)
                            data.poll();
                        data.add(temp);
                        dataToWrite.add((byte) 2);
                    } else {
                        temp = "\n" + "Direction: Up	           " + xFilter.filteredValue;
                        if (data.size() > 25)
                            data.poll();
                        data.add(temp);
                        dataToWrite.add((byte) 1);
                    }
            } else if (readings < 1000)//1000 readings to bring filtered value down to ~0
                progress.setProgress(100 - readings/10);
            else {
                progress.setVisibility(View.GONE);
                l.setVisibility(View.VISIBLE);
                r.setVisibility(View.VISIBLE);
                findViewById(R.id.dotted_line).setVisibility(View.VISIBLE);
                findViewById(R.id.swipe).setVisibility(View.VISIBLE);
                findViewById(R.id.swipe_text).setVisibility(View.VISIBLE);
                findViewById(R.id.rootview).setBackgroundColor(Color.WHITE);
                snackbar.dismiss();
            }
        }
        readings++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {

        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataToWrite.add((byte) 5);
            }
        });
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataToWrite.add((byte) 6);
            }
        });
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        exit = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        exit = true;
    }

    private void showSnackbar(String text, int duration) {
        View view = findViewById(R.id.rootview);
        snackbar = Snackbar.make(view,text,duration==0?Snackbar.LENGTH_SHORT:Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public class ConnectionThread extends Thread {

        private Socket clientSocket;
        private BufferedWriter clientOutput;
        private String IP_ADDRESS, PORT;

        ConnectionThread(String ip, String po) {
            IP_ADDRESS = ip;
            PORT = po;
        }

        @Override
        public void run() {
            try {
                clientOutput = new BufferedWriter(new OutputStreamWriter(SocketHandler.getSocket().getOutputStream()));
            } catch (final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSnackbar("Error establishing connection\n" + e.getMessage(),1);
                    }
                });
                return;
            }

            while (!exit) {
                if (dataToWrite.size() > 0)
                    try {
                        clientOutput.write(dataToWrite.remove(0));
                        clientOutput.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            try {
                clientOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}