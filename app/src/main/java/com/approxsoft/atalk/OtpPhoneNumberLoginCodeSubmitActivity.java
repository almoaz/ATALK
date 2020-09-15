package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpPhoneNumberLoginCodeSubmitActivity extends AppCompatActivity {

    TextView OTPCodeSubmitNumber;

    private EditText code;
    private Button OTPcodeSubmitBtn;
    String  userVerificationId;
    private FirebaseAuth mAuth;
    private String number,id;
    DatabaseReference database;
    ProgressBar signUpProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_phone_number_login_code_submit);


        userVerificationId = getIntent().getStringExtra("AuthCredentials");

        database = FirebaseDatabase.getInstance().getReference();

        OTPCodeSubmitNumber = findViewById(R.id.login_otp_submit_number);
        code = findViewById(R.id.login_code);

        signUpProgressBar = findViewById(R.id.login_Otp_progress_bar);
        OTPcodeSubmitBtn = findViewById(R.id.login_otp_submit_code_btn);

        number = getIntent().getStringExtra("number");
        mAuth = FirebaseAuth.getInstance();

        sendVerificationCode();

        OTPCodeSubmitNumber.setText("code to your phone  " + number);



        OTPcodeSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(code.getText().toString())){
                    Toast.makeText(OtpPhoneNumberLoginCodeSubmitActivity.this, "Enter Otp", Toast.LENGTH_SHORT).show();
                }
                else if(code.getText().toString().replace(" ","").length()!=6){
                    Toast.makeText(OtpPhoneNumberLoginCodeSubmitActivity.this, "Enter right otp", Toast.LENGTH_SHORT).show();
                }
                else {
                    //loader.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, code.getText().toString().replace(" ",""));
                    signInWithPhoneAuthCredential(credential);
                    signUpProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void sendVerificationCode() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                OtpPhoneNumberLoginCodeSubmitActivity.this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        OtpPhoneNumberLoginCodeSubmitActivity.this.id = id;

                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                        signUpProgressBar.setVisibility(View.VISIBLE);
                        OTPcodeSubmitBtn.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OtpPhoneNumberLoginCodeSubmitActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //loader.setVisibility(View.GONE);
                        if (task.isSuccessful()) {



                            FirebaseUser user = task.getResult().getUser();

                            final String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


                            database.child("All Users").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if (Objects.requireNonNull(dataSnapshot.child(currentUserId).child("email").getValue()).toString().equals(number))
                                    {
                                        startActivity(new Intent(OtpPhoneNumberLoginCodeSubmitActivity.this,HomeActivity.class));
                                        finish();
                                    }
                                    else
                                    {
                                        startActivity(new Intent(OtpPhoneNumberLoginCodeSubmitActivity.this,SignUpActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            Toast.makeText(OtpPhoneNumberLoginCodeSubmitActivity.this, "Verification Filed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}