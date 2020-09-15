package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText resetEmail;
    private Button resetBtn;
    String emailPattern ="[a-z0-9._-]+@[a-z]+.[a-z]+";
    FirebaseAuth firebaseAuth;
    private TextView ImportantText;
    ImageView forgotPasswordBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        firebaseAuth = FirebaseAuth.getInstance();


        resetEmail = findViewById(R.id.forgot_password_email);
        resetBtn = findViewById(R.id.reser_password_btn);
        ImportantText = findViewById(R.id.message_box);
        forgotPasswordBackBtn = findViewById(R.id.forgot_password_back_btn);

        forgotPasswordBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        resetBtn.setEnabled(false);

        resetEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (resetEmail.getText().toString().matches(emailPattern))
                {
                    resetBtn.setEnabled(false);

                    firebaseAuth.sendPasswordResetEmail(resetEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        ImportantText.setVisibility(View.VISIBLE);
                                        ImportantText.setText("Email send successfully! Check Your Inbox");
                                        ImportantText.setTextColor(Color.WHITE);
                                        resetEmail.setText("");
                                    }
                                    else
                                    {
                                        resetBtn.setEnabled(true);
                                        ImportantText.setVisibility(View.VISIBLE);
                                        ImportantText.setText("Something wrong ! you try again");
                                        ImportantText.setTextColor(Color.RED);
                                        resetEmail.setText("");
                                    }
                                }
                            });
                }
                else
                {
                    ImportantText.setVisibility(View.VISIBLE);
                    ImportantText.setText("Email bad format");
                    ImportantText.setTextColor(Color.RED);
                    resetEmail.setText("");
                }
            }
        });


    }
    private void checkInputs() {
        if (!TextUtils.isEmpty(resetEmail.getText().toString().trim())){
            resetBtn.setEnabled(true);
        }else {
            resetBtn.setEnabled(false);



        }


    }

}
