package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class OTPCodeSubmitActivity extends AppCompatActivity {

    TextView OTPCodeSubmitNumber;

    private EditText code1;
    private Button OTPcodeSubmitBtn;
    String firstName, lastName, phoneNumber, password, userVerificationId;
    private FirebaseAuth mAuth;
    private String number,id;
    DatabaseReference database;
    ProgressBar signUpProgressBar;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_code_submit);

       firstName = getIntent().getExtras().get("firstName").toString();
       lastName = getIntent().getExtras().get("lastName").toString();
       password = getIntent().getExtras().get("password").toString();
        phoneNumber = getIntent().getExtras().get("phoneNumber").toString();

       userVerificationId = getIntent().getStringExtra("AuthCredentials");

       database = FirebaseDatabase.getInstance().getReference();

        OTPCodeSubmitNumber = findViewById(R.id.otp_submit_number);
        code1 = findViewById(R.id.code1);
        signUpProgressBar = findViewById(R.id.Otp_progress_bar);
        OTPcodeSubmitBtn = findViewById(R.id.otp_submit_code_btn);

        number = getIntent().getStringExtra("number");
        mAuth = FirebaseAuth.getInstance();

        sendVerificationCode();

        OTPCodeSubmitNumber.setText("code to your phone  " + number);

        if (code1.getText().toString().isEmpty())
        {

        }
        else
        {
            OTPcodeSubmitBtn.setEnabled(true);
        }

        final String code = code1.getText().toString();

        OTPcodeSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(code)){
                    Toast.makeText(OTPCodeSubmitActivity.this, "Enter Otp", Toast.LENGTH_SHORT).show();
                }
                else if(code.replace(" ","").length()!=6){
                    Toast.makeText(OTPCodeSubmitActivity.this, "Enter right otp", Toast.LENGTH_SHORT).show();
                }
                else {
                    //loader.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, code1.getText().toString().replace(" ",""));
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
                OTPCodeSubmitActivity.this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        OTPCodeSubmitActivity.this.id = id;

                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                        signUpProgressBar.setVisibility(View.VISIBLE);
                        OTPcodeSubmitBtn.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OTPCodeSubmitActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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


                            database.child("All User").child(currentUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if (dataSnapshot.child("email").getValue().equals(number))
                                    {
                                        startActivity(new Intent(OTPCodeSubmitActivity.this,HomeActivity.class));
                                        finish();
                                    }
                                    else
                                    {
                                        HashMap userMap = new HashMap();
                                        userMap.put("firstName",firstName);
                                        userMap.put("lastName",lastName);
                                        userMap.put("email",number);
                                        userMap.put("fullName",firstName+" "+lastName);
                                        userMap.put("Password",password);
                                        userMap.put("profileImage","None");
                                        userMap.put("coverImage","None");
                                        userMap.put("bio","Write about yourself");
                                        userMap.put("dateOfBirth","None");
                                        userMap.put("gender","None");
                                        userMap.put("favoritePlace","Write your favorite place name");
                                        userMap.put("userName","Add your username");
                                        userMap.put("location","None");
                                        userMap.put("country","None");
                                        userMap.put("uid",currentUserId);



                                        database.child("All Users").child(currentUserId).updateChildren(userMap).addOnCompleteListener( new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    startActivity(new Intent(OTPCodeSubmitActivity.this,HomeActivity.class));
                                                    finish();
                                                }
                                                else
                                                {
                                                    String error = Objects.requireNonNull(task.getException()).getMessage();
                                                    Toast.makeText(OTPCodeSubmitActivity.this,error,Toast.LENGTH_SHORT).show();
                                                    signUpProgressBar.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



                            // ...
                        } else {
                            Toast.makeText(OTPCodeSubmitActivity.this, "Verification Filed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    }
