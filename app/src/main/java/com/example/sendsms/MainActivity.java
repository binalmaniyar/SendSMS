package com.example.sendsms;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etMessage, etPhnNo;
    Button btnSendSMS;

    int MY_PERMISSION_REQUEST_SEND_SMS = 1;

    String sent = "SMS_SENT";
    String delivered = "SMS_DELIVERED";
    PendingIntent sentPI, deliverPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMessage = findViewById(R.id.etMessage);
        etPhnNo = findViewById(R.id.etPhnNo);
        btnSendSMS = findViewById(R.id.btnSendSms);

        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(sent),0);
        deliverPI = PendingIntent.getBroadcast(this, 0,new Intent(delivered),0);

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = etMessage.getText().toString().trim();
                String phnNo = etPhnNo.getText().toString().trim();

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSION_REQUEST_SEND_SMS);
                }
                else
                {
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(phnNo,null,message,sentPI,deliverPI);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(MainActivity.this,"SMS Sent", Toast.LENGTH_LONG).show();
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(MainActivity.this,"Generic failure", Toast.LENGTH_LONG).show();
                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(MainActivity.this,"No service",Toast.LENGTH_LONG).show();
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(MainActivity.this,"Null PDU",Toast.LENGTH_LONG).show();
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(MainActivity.this,"Radio Off",Toast.LENGTH_LONG).show();
                        break;
                }

            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(MainActivity.this,"SMS Delievered",Toast.LENGTH_LONG).show();
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(MainActivity.this,"SMS not delivered",Toast.LENGTH_LONG).show();
                        break;
                }

            }
        };

        registerReceiver(smsSentReceiver, new IntentFilter(sent));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(delivered));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }
}



























