package com.xindedata.xindesms;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "xindesms";
    public static final String DEFAULT_URL = "http://192.168.3.14:9000/xms";

    private Button mSave = null;
    private Button mSend = null;
    private EditText mEditUrl = null;
    private EditText mEditNum = null;
    private EditText mEditNum2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditUrl = findViewById(R.id.server_url_edit);
        mEditNum = findViewById(R.id.phone_num_edit);
        mEditNum2 = findViewById(R.id.phone_num2_edit);
        mSave = findViewById(R.id.save_btn);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, mEditUrl.getText().toString());
                MyUtil.savePreference(MainActivity.this,"server_url", mEditUrl.getText().toString());
                MyUtil.savePreference(MainActivity.this,"phone_num", mEditNum.getText().toString());
                MyUtil.savePreference(MainActivity.this,"phone_num2", mEditNum2.getText().toString());
            }
        });

        mSend = findViewById(R.id.send_btn);
        mSend.setVisibility(View.INVISIBLE);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Send button clicked");
                (new SmsThread()).start();
            }
        });
    }

    protected void onResume() {
        super.onResume();
        mEditUrl.setText(MyUtil.getPreference(MainActivity.this,"server_url", DEFAULT_URL));
        mEditNum.setText(MyUtil.getPreference(MainActivity.this,"phone_num", ""));
        mEditNum2.setText(MyUtil.getPreference(MainActivity.this,"phone_num2", ""));
    }

    class SmsThread extends Thread {
        public void run() {
            //sendSms();
        }
    }
}
