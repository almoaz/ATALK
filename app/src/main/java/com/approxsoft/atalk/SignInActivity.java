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

import java.util.Objects;


public class SignInActivity extends AppCompatActivity {

    TextView DontHaveAnAccount, forgotPassword;
    EditText signInEmailAndNumber, signInPassword;
    Button signInBtn;
    ProgressBar signInProgressBar;
    String emailPattern ="[a-z0-9._-]+@[a-z]+.[a-z]+";
    String numberPattern = "[+0-9]+";
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();


        signInEmailAndNumber = findViewById(R.id.sign_in_email_or_phone);
        signInPassword = findViewById(R.id.sign_in_password);
        signInBtn = findViewById(R.id.sign_in_btn);
        signInProgressBar = findViewById(R.id.sign_in_progressBar);
        forgotPassword = findViewById(R.id.forgot_password);
        DontHaveAnAccount = findViewById(R.id.dont_have_an_account);

        signInBtn.setEnabled(false);


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SignInActivity.this,ForgotPasswordActivity.class);
                intent.putExtra("value",signInEmailAndNumber.getText().toString());
                startActivity(intent);
            }
        });

        DontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        signInEmailAndNumber.addTextChangedListener(new TextWatcher() {
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

        signInPassword.addTextChangedListener(new TextWatcher() {
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

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checkCondition();

            }
        });




    }




    private void checkCondition()
    {
        if (signInEmailAndNumber.getText().toString().matches(numberPattern))
        {
            if (signInPassword.length() >=8)
            {
                signInBtn.setEnabled(false);

                Intent intent = new Intent(SignInActivity.this,OtpPhoneNumberCodeSendActivity.class);
                intent.putExtra("phoneNumber",signInEmailAndNumber.getText().toString());
                startActivity(intent);

            }
        }else

        if (signInEmailAndNumber.getText().toString().matches(emailPattern))
        {
            if (signInPassword.length() >=8)
            {
                signInBtn.setEnabled(false);
                signInProgressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(signInEmailAndNumber.getText().toString(),signInPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful())
                                {
                                    verifyEmailFirst();
                                }
                                else
                                {
                                    String error = Objects.requireNonNull(task.getException()).getMessage();
                                    Toast.makeText(SignInActivity.this,error,Toast.LENGTH_SHORT).show();
                                    signInProgressBar.setVisibility(View.INVISIBLE);
                                    signInBtn.setEnabled(true);
                                }
                            }
                        });
            }
        }else
        {
            Toast.makeText(SignInActivity.this,"Email or phone number bad format",Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyEmailFirst()
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailVerify = Objects.requireNonNull(firebaseUser).isEmailVerified();
        if (emailVerify){
            sendUserToMainActivity();

        }else {
            Toast.makeText(SignInActivity.this,"Verify Your Email",Toast.LENGTH_SHORT).show();
            signInProgressBar.setVisibility(View.INVISIBLE);
            mAuth.signOut();
        }

    }

    private void sendUserToMainActivity()
    {
        Intent intent = new Intent(SignInActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkInputs()
    {
        if (!TextUtils.isEmpty(signInEmailAndNumber.getText().toString().trim()))
        {
            if (!TextUtils.isEmpty(signInPassword.getText().toString().trim()))
            {
                signInBtn.setEnabled(true);
            }else
            {
                signInBtn.setEnabled(false);
            }
        }else
        {
            signInBtn.setEnabled(false);
        }
    }
}
