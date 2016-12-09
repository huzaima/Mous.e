package com.huzaima.mouse;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.dd.processbutton.iml.ActionProcessButton;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    boolean connected = false;
    private SensorManager senSensorManager;
    private Sensor senRotation;
    PrintWriter cursorOut, leftClickOut, rightClickOut, swipeOut = null;
    ImageView swipeView;
    ActionProcessButton leftClick, rightClick;
    BlockingQueue leftClickQueue = new LinkedBlockingQueue();
    BlockingQueue rightClickQueue = new LinkedBlockingQueue();
    BlockingQueue swipeQueue = new LinkedBlockingQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mouse_activity);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senRotation = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        leftClick = (ActionProcessButton) findViewById(R.id.left_click);
        rightClick = (ActionProcessButton) findViewById(R.id.right_click);
        swipeView = (ImageView) findViewById(R.id.swipeView);

        leftClick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    leftClickQueue.add("LC");
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    leftClickQueue.add("LCR");
                }
                return false;
            }
        });

        rightClick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    rightClickQueue.add("RC");
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    rightClickQueue.add("RCR");
                }
                return false;
            }
        });

        swipeView.setOnTouchListener(new View.OnTouchListener() {

            float startX, startY;
            float currX, currY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startY = event.getY();
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startY = 0;
                }

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    currY = startY - event.getY();
                    startY = event.getY();
                    swipeQueue.add(currY);
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senRotation, SensorManager.SENSOR_DELAY_GAME);

        try {
            cursorOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(SocketHandler.getCursorSocket().getOutputStream())), true);
            leftClickOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(SocketHandler.getLeftClickSocket().getOutputStream())), true);
            rightClickOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(SocketHandler.getRightClickSocket().getOutputStream())), true);
            swipeOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(SocketHandler.getSwipeSocket().getOutputStream())), true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("onResume", "Successfully created Out object");

        registerTheListener();
        clickThread(leftClickOut, leftClickQueue);
        clickThread(rightClickOut, rightClickQueue);
        clickThread(swipeOut, swipeQueue);
    }

    void clickThread(final PrintWriter outSocket, final BlockingQueue queue) {

        new Thread(new Runnable() {
            public void run() {

                while (true) {
                    try {
                        outSocket.println(queue.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    float filtered[];

    @Override
    public void onSensorChanged(SensorEvent event) {

        filtered = lowPassFilter(event.values, filtered);
        cursorOut.println(filtered[0] * 1000 + " " + filtered[2] * 1000);
        cursorOut.flush();

    }//OnSensorChanged

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void registerTheListener() {
        senSensorManager.registerListener(this, senRotation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private static final float ALPHA = 0.25f;

    private float[] lowPassFilter(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }//LowPassFilter
}
