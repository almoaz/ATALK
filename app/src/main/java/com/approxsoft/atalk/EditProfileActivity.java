package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    Toolbar mToolBar;
    RelativeLayout userRelativeLayout, bioRelativeLayout, favoritePlaceRelativeLayout, countryRelativeLayout, locationRelativeLayout, allEditLayout;
    EditText userNameEditText, userNameEditText1, bioEditText, bioEditText1, favoriteEditText, favoriteEditText1, countryEditText, countryEditText1, location, location1;
    Button AllTextSaveBtn, userTextBtn, bioTextBtn, favoriteTextBtn, countryTextBtn, locationTextBtn;
    String Type;
    String currentUserId;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ImageView AppBarIcon;
    TextView AppBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);

        Type = getIntent().getExtras().get("type").toString();
        mToolBar = findViewById(R.id.edit_profile_ap_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");
        AppBarIcon = findViewById(R.id.edit_profile_app_bar_icon);
        AppBarTitle = findViewById(R.id.edit_profile_app_bar_title);

        AppBarTitle.setText(Type);

        AppBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });


        userRelativeLayout = findViewById(R.id.user_name1_relative_layout);
        bioRelativeLayout = findViewById(R.id.bio_text1_relative_layout);
        favoritePlaceRelativeLayout = findViewById(R.id.favorite_pace_name1_relative_layout);
        countryRelativeLayout = findViewById(R.id.country_name1_relative_layout);
        locationRelativeLayout = findViewById(R.id.location_name1_relative_layout);
        allEditLayout = findViewById(R.id.all_edit_relativeLayout);

        userNameEditText = findViewById(R.id.user_name_text);
        userNameEditText1 = findViewById(R.id.user_name_text1);
        userTextBtn = findViewById(R.id.user_name_text1_btn);

        bioEditText = findViewById(R.id.bio_text);
        bioEditText1  = findViewById(R.id.bio_text1);
        bioTextBtn = findViewById(R.id.bio_text1_btn);

        favoriteEditText = findViewById(R.id.favorite_pace_name);
        favoriteEditText1 = findViewById(R.id.favorite_pace_name1);
        favoriteTextBtn = findViewById(R.id.favorite_pace_name1_btn);

        countryEditText = findViewById(R.id.country_name);
        countryEditText1 = findViewById(R.id.country_name1);
        countryTextBtn = findViewById(R.id.country_name1_btn);

        location = findViewById(R.id.location_name);
        location1 = findViewById(R.id.location_name1);
        locationTextBtn = findViewById(R.id.location_name1_btn);

        AllTextSaveBtn = findViewById(R.id.edit_profile_save_changes_btn);


        switch (Type) {
            case "Edit Profile":
                allEditLayout.setVisibility(View.VISIBLE);

                break;
            case "Edit Username":
                userRelativeLayout.setVisibility(View.VISIBLE);

                break;
            case "Edit Your About":
                bioRelativeLayout.setVisibility(View.VISIBLE);

                break;
            case "Edit Your Favorite Place":
                favoritePlaceRelativeLayout.setVisibility(View.VISIBLE);

                break;
            case "Edit Your Country Name":
                countryRelativeLayout.setVisibility(View.VISIBLE);

                break;
            case "Edit Location":
                locationRelativeLayout.setVisibility(View.VISIBLE);

                break;
        }

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String bio = dataSnapshot.child("bio").getValue().toString();
                            String Location = dataSnapshot.child("location").getValue().toString();
                            String country = dataSnapshot.child("country").getValue().toString();
                            String favoritePlace = dataSnapshot.child("favoritePlace").getValue().toString();
                            String userName = dataSnapshot.child("userName").getValue().toString();

                            userNameEditText.setText(userName);
                            userNameEditText1.setText(userName);

                            bioEditText.setText(bio);
                            bioEditText1.setText(bio);

                            location.setText(Location);
                            location1.setText(Location);

                            countryEditText.setText(country);
                            countryEditText1.setText(country);

                            favoriteEditText.setText(favoritePlace);
                            favoriteEditText1.setText(favoritePlace);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

         AllTextSaveBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v)
             {
                 AllDataSaveChanges();
             }
         });

         userTextBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v)
             {
                if (userNameEditText1.getText().toString().isEmpty())
                {

                }
                else
                {
                    databaseReference.child("userName").setValue(userNameEditText1.getText().toString());
                    sendUserToProfileActivity();
                }
             }
         });

        bioTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (bioEditText1.getText().toString().isEmpty())
                {

                }
                else
                {
                    databaseReference.child("bio").setValue(bioEditText1.getText().toString());
                    sendUserToProfileActivity();
                }
            }
        });

        favoriteTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (favoriteEditText1.getText().toString().isEmpty())
                {

                }
                else
                {
                    databaseReference.child("favoritePlace").setValue(favoriteEditText1.getText().toString());
                    sendUserToProfileActivity();
                }
            }
        });

        countryTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (countryEditText1.getText().toString().isEmpty())
                {

                }
                else
                {
                    databaseReference.child("country").setValue(countryEditText1.getText().toString());
                    sendUserToProfileActivity();
                }
            }
        });

        locationTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (location1.getText().toString().isEmpty())
                {

                }
                else
                {
                    databaseReference.child("location").setValue(location1.getText().toString());
                    sendUserToProfileActivity();
                }
            }
        });
    }

    private void sendUserToProfileActivity()
    {
        Intent intent = new Intent(EditProfileActivity.this,ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void AllDataSaveChanges()
    {
        HashMap updateMap = new HashMap();
        updateMap.put("userName",userNameEditText.getText().toString());
        updateMap.put("bio",bioEditText.getText().toString());
        updateMap.put("favoritePlace",favoriteEditText.getText().toString());
        updateMap.put("country",countryEditText.getText().toString());
        updateMap.put("location",location.getText().toString());

        databaseReference.updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
               if (task.isSuccessful())
               {
                   Toast.makeText(EditProfileActivity.this,"Save",Toast.LENGTH_SHORT).show();
               }
            }
        });

    }
}