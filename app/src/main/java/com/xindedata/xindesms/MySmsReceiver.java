package com.xindedata.xindesms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by wenbing on 9/23/18.
 */

public class MySmsReceiver extends BroadcastReceiver {

    public static final String TAG = MainActivity.TAG;

    public static final String IntentAction = "android.provider.Telephony.SMS_RECEIVED";

    public void onReceive(Context context, Intent intent) {
        try {
            if (!intent.getAction().equals(IntentAction)) {
                return;
            }

            Bundle bundle  = intent.getExtras();

            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] msgs = new SmsMessage[pdus.length];
                for(int i=0; i<msgs.length; i++){
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    String msgBody = msgs[i].getMessageBody();
                    //check if the sms has verification code
                    Log.d(TAG, msgs[i].getOriginatingAddress());
                    Log.d(MainActivity.TAG, msgBody);
                    JSONObject obj = new JSONObject();
                    obj.put("sender_num", msgs[i].getOriginatingAddress());
                    String code = getCodeFromMessage(msgBody);
                    Log.d(TAG, "code="+code);
                    if(code == null || code.isEmpty()) {
                        Log.d(TAG, "This SMS does not contain verification code!");
                        continue;
                    }
                    obj.put("code", code);
                    String phoneNum = MyUtil.getPreference(context, "phone_num", "");
                    if(phoneNum != null && !phoneNum.isEmpty()) {
                        obj.put("receiver_num", phoneNum);
                    }
                    String phoneNum2 = MyUtil.getPreference(context, "phone_num2", "");
                    if(phoneNum2 != null && !phoneNum2.isEmpty()) {
                        obj.put("receiver_num2", phoneNum2);
                    }
                    (new SmsThread(context, obj.toString())).start();
                }
            }
        } catch (Exception ex) {
            //Toast.MakeText (context, ex.Message, ToastLength.Long).Show ();
            ex.printStackTrace();
        }

    }

    private String getCodeFromMessage(String body) {
        //get verification code
        int len = body.length();
        int index = body.indexOf("验证码");
        if(index<0) return null;
        while(index<len) {
            //find the first digit
            while(index < len && (body.charAt(index) > '9' || body.charAt(index) < '0')) {
                index++;
            }
            Log.d(TAG, "index1 = " + index);
            if(index < len) {
                int start = index;
                while(index < len && (body.charAt(index) <= '9' && body.charAt(index) >= '0')) {
                    index++;
                }
                if(index-start >= 4) {
                    Log.d(TAG, "found sms code!");
                    return body.substring(start, index);
                }
            }
        }
        Log.d(TAG,"does not find sms code!");
        return null;
    }

    class SmsThread extends Thread {
        private String mContent = null;
        private Context mContext = null;
        public SmsThread(Context context, String content) {
            mContent = content;
            mContext = context;
        }
        public void run() {
            String server_url = MyUtil.getPreference(mContext, "server_url", MainActivity.DEFAULT_URL);
            MyUtil.sendSms(server_url, mContent);
        }
    }
}
