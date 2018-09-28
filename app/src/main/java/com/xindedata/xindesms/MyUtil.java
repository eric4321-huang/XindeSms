package com.xindedata.xindesms;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wenbing on 9/24/18.
 */

public class MyUtil {
    public static final String TAG = MainActivity.TAG;

    public static void savePreference(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences("my_pref",0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPreference(Context context, String key, String defaultValue) {
        SharedPreferences pref = context.getSharedPreferences("my_pref",0);
        String server_url = pref.getString(key, defaultValue);
        return server_url;
    }

    public static void sendSms(String server_url, String content) {
        Log.d(TAG, "enter sendSms()");
        URL url = null;
        HttpURLConnection client = null;
        try {
            url = new URL(server_url);
            client = (HttpURLConnection) url.openConnection();
            client.setReadTimeout(10000);
            client.setConnectTimeout(15000);
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type","application/json;charset=utf-8");
            String body = content;
            //"{\"from_num\":\"10086\", \"phone_num\": \"hwb_geny\", \"code\": \"hwb_geny\"}";
            byte[] data = body.getBytes("UTF-8");
            client.setRequestProperty("Content-Length", "" + data.length);
            client.setDoOutput(true);
            OutputStream output = client.getOutputStream();
            output.write(body.getBytes());
            output.flush();
            output.close();
            int status = client.getResponseCode();
            //InputStream input = client.getInputStream();
            Log.d(TAG, "status="+status);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public static String getPhoneNumber(Context context) {
        Log.d(TAG, "enter getPhoneNumber");
        TelephonyManager manager =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            String myFirstNum = manager.getLine1Number();
            return myFirstNum;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
