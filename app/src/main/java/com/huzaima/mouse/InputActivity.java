package com.huzaima.mouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dd.processbutton.iml.ActionProcessButton;

import java.io.IOException;
import java.net.Socket;

public class InputActivity extends AppCompatActivity {

    ActionProcessButton btnSignIn;
    EditText IP_Address, Port_Number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        btnSignIn = (ActionProcessButton) findViewById(R.id.btnConnect);
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        btnSignIn.setProgress(0);

        IP_Address = (EditText) findViewById(R.id.IP_Address);
        Port_Number = (EditText) findViewById(R.id.Port_Number);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ip_address = String.valueOf(IP_Address.getText());
                int port;

                if (String.valueOf(Port_Number.getText()).equals("")) {
                    port = 0;
                } else {
                    port = Integer.parseInt(String.valueOf(Port_Number.getText()));
                }

                Log.i("IP = ", "" + ip_address);
                Log.i("Port = ", "" + port);
                EstablishConnection ec = new EstablishConnection(ip_address, port);
                ec.execute();
            }
        });
    }

    public class EstablishConnection extends AsyncTask {

        String ip;
        int port;
        Socket cursorSocket, leftClickSocket, rightClickSocket, swipeSocket, calibrationSocket;

        EstablishConnection(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        boolean success = false;

        @Override
        protected Object doInBackground(Object[] params) {

            try {

                cursorSocket = new Socket(ip, port);
                leftClickSocket = new Socket(ip, port + 1);
                rightClickSocket = new Socket(ip, port + 2);
                swipeSocket = new Socket(ip, port + 3);
                calibrationSocket = new Socket(ip, port + 4);

                success = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < 101; i++) {
                            btnSignIn.setProgress(i);
                        }//for loop

                        SocketHandler.setCursorSocket(cursorSocket);
                        SocketHandler.setLeftClickSocket(leftClickSocket);
                        SocketHandler.setRightClickSocket(rightClickSocket);
                        SocketHandler.setSwipeSocket(swipeSocket);
                        SocketHandler.setCalibrationSocket(calibrationSocket);

                        Intent i = new Intent(getApplicationContext(), UserCalibrationActivity.class);

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
}
