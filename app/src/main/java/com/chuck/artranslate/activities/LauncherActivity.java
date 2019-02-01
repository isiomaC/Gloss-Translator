package com.chuck.artranslate.activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chuck.artranslate.R;
import com.chuck.artranslate.authentication.authSActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    MainActivity.start(LauncherActivity.this);
                }else{
                    authSActivity.start(LauncherActivity.this);
                }
            }
        }, 2000);

    }
}
