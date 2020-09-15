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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AddNoteActivity extends AppCompatActivity {

    Toolbar mToolBar;
    EditText noteEditText;
    Button NoteSaveBtn;
    FirebaseAuth mAuth;
    String currentUserId;
    DatabaseReference databaseReference;

    ImageView AppBarIcon;
    TextView AppBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);

        mToolBar = findViewById(R.id.add_note_ap_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");

        AppBarIcon = findViewById(R.id.add_note_app_bar_icon);
        AppBarTitle = findViewById(R.id.add_note_app_bar_title);

        AppBarTitle.setText("Save Important Note");

        AppBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        noteEditText = findViewById(R.id.add_note_text);
        NoteSaveBtn = findViewById(R.id.save_note_btn);

        NoteSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (TextUtils.isEmpty(noteEditText.getText().toString()))
                {

                }
                else
                {
                    Calendar calForDate = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                    final String saveCurrentDate = currentDate.format(calForDate.getTime());

                    Calendar calForTime = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                    final String saveCurrentTime = currentTime.format(calForTime.getTime());

                    HashMap noteMap = new HashMap();
                    noteMap.put("note",noteEditText.getText().toString());
                    noteMap.put("time",saveCurrentTime);
                    noteMap.put("date",saveCurrentDate);

                    databaseReference.child("All Note").push().updateChildren(noteMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if (task.isSuccessful())
                            {
                                Intent intent = new Intent(AddNoteActivity.this,SaveNoteActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }
}