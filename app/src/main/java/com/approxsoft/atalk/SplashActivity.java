package com.approxsoft.atalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        SystemClock.sleep(2000);
    }

    @Override
    protected void onStart() {

        if (mUser == null)
        {
            Intent registrationIntent = new Intent(SplashActivity.this,RegistrationSplashActivity.class);
            registrationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            registrationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(registrationIntent);
            finish();
        }
        else
        {
            Intent registrationIntent = new Intent(SplashActivity.this,HomeActivity.class);
            startActivity(registrationIntent);
            finish();
        }

        super.onStart();
    }
}
