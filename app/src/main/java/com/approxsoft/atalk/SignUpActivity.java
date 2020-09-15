package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    TextView AllReadyHaveAdAccount;
    Button SignUpBtn;
    EditText signUpFirstName, signUpLastName, signUpEmailOrPhone, signUpPassword;
    ProgressBar signUpProgressBar;

    String emailPattern ="[a-z0-9._-]+@[a-z]+.[a-z]+";
    String numberPattern = "[0-9]+";
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        signUpFirstName = findViewById(R.id.sign_up_first_name);
        signUpLastName = findViewById(R.id.sign_up_last_name);
        signUpEmailOrPhone = findViewById(R.id.sign_up_email_or_phone);
        signUpPassword = findViewById(R.id.sign_up_password);
        signUpProgressBar = findViewById(R.id.sign_up_progress_bar);
        AllReadyHaveAdAccount = findViewById(R.id.all_ready_have_an_account_btn);
        SignUpBtn = findViewById(R.id.sign_up_btn);

        //SignUpBtn.setEnabled(false);

        AllReadyHaveAdAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });


        signUpFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpEmailOrPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkCondition();
            }
        });


    }

    private void checkCondition()
    {
        if (signUpEmailOrPhone.getText().toString().matches(numberPattern))
        {

            Intent intent = new Intent(SignUpActivity.this,OTPCodeSendActivity.class);
            intent.putExtra("firstName",signUpFirstName.getText().toString());
            intent.putExtra("lastName",signUpLastName.getText().toString());
            intent.putExtra("phoneNumber",signUpEmailOrPhone.getText().toString());
            intent.putExtra("password",signUpPassword.getText().toString());
            startActivity(intent);
        }
        else
        if (signUpEmailOrPhone.getText().toString().matches(emailPattern))
        {
            if (signUpPassword.getText().length() >= 8)
            {
                signUpProgressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(signUpEmailOrPhone.getText().toString(),signUpPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful())
                                {
                                    String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


                                    HashMap userMap = new HashMap();
                                    userMap.put("firstName",signUpFirstName.getText().toString());
                                    userMap.put("lastName",signUpLastName.getText().toString());
                                    userMap.put("email",signUpEmailOrPhone.getText().toString());
                                    userMap.put("fullName",signUpFirstName.getText().toString()+" "+signUpLastName.getText().toString());
                                    userMap.put("Password",signUpPassword.getText().toString());
                                    userMap.put("profileImage","None");
                                    userMap.put("coverImage","None");
                                    userMap.put("dateOfBirth","None");
                                    userMap.put("gender","None");
                                    userMap.put("location","None");
                                    userMap.put("country","None");
                                    userMap.put("bio","Write about yourself");
                                    userMap.put("favoritePlace","Write your favorite place name");
                                    userMap.put("userName","Add your username");
                                    userMap.put("uid",currentUserId);




                                    databaseReference.child("All Users").child(currentUserId).updateChildren(userMap).addOnCompleteListener( new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendEmailVerification();
                                            }
                                            else
                                            {
                                                String error = Objects.requireNonNull(task.getException()).getMessage();
                                                Toast.makeText(SignUpActivity.this,error,Toast.LENGTH_SHORT).show();
                                                signUpProgressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        }
    }

    private void sendEmailVerification()
    {
        FirebaseUser firebaseUser =mAuth.getCurrentUser();
        if (firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this,"Email Verification send to your email",Toast.LENGTH_SHORT).show();
                        sendUserToSignInActivity();
                        SignUpActivity.this.finish();
                        mAuth.signOut();

                    }else {
                        Toast.makeText(SignUpActivity.this,"Database id Down Please wait",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

    private void sendUserToSignInActivity() {
        Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkInputs()
    {
        if (!TextUtils.isEmpty(signUpFirstName.getText().toString().trim()))
        {
            if (!TextUtils.isEmpty(signUpLastName.getText().toString().trim()))
            {
                if (!TextUtils.isEmpty(signUpEmailOrPhone.getText().toString().trim()))
                {
                    if (!TextUtils.isEmpty(signUpPassword.getText().toString().trim()))
                    {
                        SignUpBtn.setEnabled(true);
                    }else
                    {
                        SignUpBtn.setEnabled(true);
                    }
                }else
                {
                    SignUpBtn.setEnabled(true);
                }
            }else
            {
                SignUpBtn.setEnabled(true);
            }
        }else
        {
            SignUpBtn.setEnabled(true);
        }
    }
}
