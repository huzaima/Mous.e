package com.huzaima.mouse;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class EntryActivity extends AppCompatActivity {

    private TextView text;
    private ImageView image;
    private Runnable r1, r2;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        text = (TextView) findViewById(R.id.text);
        image = (ImageView) findViewById(R.id.image);

        r1 = new Runnable() {
            @Override
            public void run() {
                text.setVisibility(View.VISIBLE);
                handler.postDelayed(r2,750);
            }
        };

        r2 =new Runnable() {
            @Override
            public void run() {
                text.setVisibility(View.INVISIBLE);
                handler.postDelayed(r1,750);
            }
        };

        handler = new Handler();
        handler.post(r1);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                Intent intent = new Intent(getApplicationContext(),ChoseMode.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
            }
        });
    }
}
