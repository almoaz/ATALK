package com.approxsoft.atalk;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

public class OTPCodeSendActivity extends AppCompatActivity {

    private EditText OtpNumber;
    CountryCodePicker OtpCountryCode;
    Spinner OtpCountrySpinner;
    private ImageView OtpCodeSendBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    String firstName, lastName, phoneNumber, password;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_code_send);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        firstName = getIntent().getExtras().get("firstName").toString();
        lastName = getIntent().getExtras().get("lastName").toString();
        phoneNumber = getIntent().getExtras().get("phoneNumber").toString();
        password = getIntent().getExtras().get("password").toString();


        OtpNumber = findViewById(R.id.otp_phone_number);
        OtpCountryCode = findViewById(R.id.otp_country_code);
        OtpCodeSendBtn = findViewById(R.id.otp_send_code_btn);

        OtpNumber.setText(phoneNumber);

        OtpCountryCode.registerCarrierNumberEditText((EditText) OtpNumber);



        OtpCodeSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(OtpNumber.getText().toString())){
                    Toast.makeText(OTPCodeSendActivity.this, "Enter No ....", Toast.LENGTH_SHORT).show();
                }
                else if(OtpNumber.getText().toString().replace(" ","").length()!=10){
                    Toast.makeText(OTPCodeSendActivity.this, "Enter Correct No ...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(OTPCodeSendActivity.this,OTPCodeSubmitActivity.class);
                    intent.putExtra("number",OtpCountryCode.getFullNumberWithPlus().replace(" ",""));
                    intent.putExtra("firstName",firstName);
                    intent.putExtra("lastName",lastName);
                    intent.putExtra("phoneNumber",OtpNumber.getText().toString());
                    intent.putExtra("password",password);
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
        Intent homeIntent = new Intent(OTPCodeSendActivity.this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }
}
