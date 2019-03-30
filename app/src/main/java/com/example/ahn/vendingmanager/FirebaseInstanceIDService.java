package com.example.ahn.vendingmanager;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Ahn on 2017. 11. 16..
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private String token;

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        token = FirebaseInstanceId.getInstance().getToken();
        Log.e("--- token_fcm", token);

        sendRegistrationToServer(token);
    }

    public static void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("getToken", token)
                .build();

        //request
        Request request = new Request.Builder()
                .url(MainActivity.DB_URL + "token_register.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
//            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
