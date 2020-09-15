package com.approxsoft.atalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegistrationSplashActivity extends AppCompatActivity {

    Button TalkNowBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_splash);

        TalkNowBtn = findViewById(R.id.splash_screen_talk_now_btn);

        TalkNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationSplashActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
