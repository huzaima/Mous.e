package com.huzaima.mouse;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.dd.processbutton.iml.ActionProcessButton;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Marib on 4/19/2016.
 */
public class InputAddressAndPort extends AppCompatActivity {

    ActionProcessButton btnSignIn;
    EditText IP_Address,Port_Number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_address_and_port);
        btnSignIn = (ActionProcessButton) findViewById(R.id.btnConnect);
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        btnSignIn.setProgress(0);

        IP_Address = (EditText) findViewById(R.id.IP_Address);
        Port_Number= (EditText) findViewById(R.id.Port_Number);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ip_address = String.valueOf(IP_Address.getText());
                int port;

                if(String.valueOf(Port_Number.getText()).equals("")){
                    port = 0;
                } else {
                    port = Integer.parseInt(String.valueOf(Port_Number.getText()));
                }

                Log.i("IP = ",""+ip_address);
                Log.i("Port = ", ""+port);
                EstablishConnection ec = new EstablishConnection(ip_address,port);
                ec.execute();

            }
        });

    }//onResume

    public class EstablishConnection extends AsyncTask{

        String ip;
        int port;
        Socket socket;
        EstablishConnection(String ip,int port){this.ip = ip; this.port = port;}
        boolean success = false;

        @Override
        protected Object doInBackground(Object[] params) {

            try {

                socket = new Socket(ip, port);
                success = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for(int i=0;i<101;i++){
                            btnSignIn.setProgress(i);
                        }//for loop

                        Intent i = new Intent(getApplicationContext(),ChoseSensor.class);
                        SocketHandler.setSocket(socket);
                        startActivity(i);
                    }
                });

            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSignIn.setEnabled(false);
                        btnSignIn.setProgress(-1);
                    }
                });

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSignIn.setEnabled(true);
                        btnSignIn.setProgress(0);
                    }
                });

                e.printStackTrace();
            }//Catch


            return null;
        }//doInBackGround

    }

}//Class
