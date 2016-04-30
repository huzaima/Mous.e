package com.huzaima.mouse;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;


public class MainActivity extends Activity implements SensorEventListener {


    boolean connected = false;
    private SensorManager senSensorManager;
    private Sensor senRotation;
    PrintWriter out = null;
    TextView tv ;
    float mid, up, down, left, right;
    double sensitivity = 4.3;
    boolean greaterToTheRight = false;
    boolean greaterToTheTop = false;

    Queue originalX = new LinkedList();
    Queue originalY = new LinkedList();

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("MID  = ",""+mid);
        Log.i("UP = ",""+up);
        Log.i("DOWN = ",""+down);
        Log.i("LEFT = ",""+left);
        Log.i("RIGHT = ",""+right);


        DataTransferThread ct = new DataTransferThread();
        registerTheListener();
        ct.execute();

        if(right > mid){greaterToTheRight = true;}
        if(up > down ){greaterToTheTop = true;}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mouse_activity);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senRotation = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);


        mid = getIntent().getFloatExtra("MID", (float) 0.0);
        up = getIntent().getFloatExtra("UP", (float) 0.0);
        down = getIntent().getFloatExtra("DOWN", (float) 0.0);
        left = getIntent().getFloatExtra("LEFT", (float) 0.0);
        right = getIntent().getFloatExtra("RIGHT", (float) 0.0);

        mid     *=10000;
        up      *=10000;
        down    *=10000;
        left    *=10000;
        right   *=10000;


    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

                Log.i("in while","Adding ");
                originalX.add(event.values);


        }//If sensor


    }

    public class DataTransferThread extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] params) {

            Log.i("In DO in background"," ok");


            float[] event_values;
            float mx,yx;
            boolean flag = true;

            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(SocketHandler.getSocket().getOutputStream())), true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while(flag) {


                if (originalX.size() > 0) {

                    Log.i("in While","If true");

                    event_values = (float[]) originalX.remove();

                    mx = event_values[2];
                    mx *= 10000;

                    yx = event_values[0];
                    yx *= 10000;

                    String output;

                    if (mx > mid) {
                        if (greaterToTheRight) {
                            mx = (float) (mx / sensitivity);
  //                          out.println(mx);
                            output = Float.toString(mx);
                        }//If Greater to the Right
                        else {
                            mx = (float) (mx / sensitivity);
                            mx *= -1;
//                            out.println(mx);
                            output = Float.toString(mx);
                        }//Else greater to the left
                    }//if Z > midMean
                    else {
                        if (greaterToTheRight) {
                            mx = (float) (mx / sensitivity);
  //                          out.println(mx);
                            output = Float.toString(mx);

                        } else {
                            mx = (float) (mx / sensitivity);
                            mx *= -1;
//                            out.println(mx);
                            output = Float.toString(mx);

                        }// greater to the right

                    }//Z < Mean

                    if (yx > mid) {
                        if (greaterToTheRight) {
                            yx = (float) (yx / sensitivity);
                            //out.println(yx);
                            output = output+"^"+Float.toString(yx);
                        }//If Greater to the Right
                        else {
                            yx = (float) (yx / sensitivity);
                            yx *= -1;
                            output = output+"^"+Float.toString(yx);

                            //out.println(yx);
                        }//Else greater to the left
                    }//if Z > midMean
                    else {
                        if (greaterToTheRight) {
                            yx = (float) (yx / sensitivity);
                            output = output+"^"+Float.toString(yx);

                            //out.println(mx);
                        } else {
                            yx = (float) (yx / sensitivity);
                            yx *= -1;
                            output = output+"^"+Float.toString(yx);
                            //out.println(mx);
                        }// greater to the right

                    }//Z < Mean

                    out.println(output);
                    out.flush();

                }//of Size >0

            }//while



                return null;
        }//DoInBackground

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            Log.i("InPostExecute", " ok");
        }
    }//ConnectionThread


    public void registerTheListener(){
        senSensorManager.registerListener(this, senRotation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        Log.i("In","OnPuase");
        super.onPause();
        if(connected)
            senSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}//main Activity
