package com.approxsoft.atalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.hbb20.CountryCodePicker;

public class OtpPhoneNumberCodeSendActivity extends AppCompatActivity {

    private EditText OtpNumber;
    CountryCodePicker OtpCountryCode;
    private ImageView OtpCodeSendBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    String phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_phone_number_code_send);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        phoneNumber = getIntent().getExtras().get("phoneNumber").toString();



        OtpNumber = findViewById(R.id.login_otp_phone_number);
        OtpCountryCode = findViewById(R.id.login_otp_country_code);
        OtpCodeSendBtn = findViewById(R.id.login_otp_send_code_btn);

        OtpNumber.setText(phoneNumber);

        OtpCountryCode.registerCarrierNumberEditText((EditText) OtpNumber);



        OtpCodeSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(OtpNumber.getText().toString())){
                    Toast.makeText(OtpPhoneNumberCodeSendActivity.this, "Enter No ....", Toast.LENGTH_SHORT).show();
                }
                else if(OtpNumber.getText().toString().replace(" ","").length()!=10){
                    Toast.makeText(OtpPhoneNumberCodeSendActivity.this, "Enter Correct No ...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(OtpPhoneNumberCodeSendActivity.this,OtpPhoneNumberLoginCodeSubmitActivity.class);
                    intent.putExtra("number",OtpCountryCode.getFullNumberWithPlus().replace(" ",""));
                    intent.putExtra("phoneNumber",OtpNumber.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser != null){
            sendUserToHome();
        }
    }


    private void sendUserToHome() {
        Intent homeIntent = new Intent(OtpPhoneNumberCodeSendActivity.this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }
}