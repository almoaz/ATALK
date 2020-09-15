package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ProfileActivity extends AppCompatActivity {

    Toolbar mToolBer;
    CircleImageView profileImage;
    ImageView addProfileImage;
    TextView profileUserName, profileFullName, profileBio, profileEmail, profilefavoritePlace, profileCountryName, profileLocation;
    private DatabaseReference userReference;
    private StorageReference ImageReff;
    String currentUserId;
    public final int Gallery_pick = 1;
    private ProgressDialog loadingBar;
    Bitmap thumb_bitmap = null;
    FirebaseAuth mAuth;
    ImageView AppBarIcon;
    TextView AppBarTitle;
    private static final int RC_VIDEO_APP_PREM = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        requestPermission();


        mToolBer = findViewById(R.id.profile_ap_bar);
        setSupportActionBar(mToolBer);
        getSupportActionBar().setTitle("");

        AppBarIcon = findViewById(R.id.profile_app_bar_icon);
        AppBarTitle = findViewById(R.id.profile_app_bar_title);

        AppBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("All Users");
        ImageReff = FirebaseStorage.getInstance().getReference().child("User Images");

        profileImage = findViewById(R.id.profile_image);
        addProfileImage = findViewById(R.id.add_profile_image_icon);
        profileFullName = findViewById(R.id.profile_full_name);
        profileEmail = findViewById(R.id.profile_email);
        profileUserName = findViewById(R.id.profile_user_name);
        profileBio = findViewById(R.id.profile_bio);
        profileCountryName = findViewById(R.id.profile_country_name);
        profileLocation = findViewById(R.id.profile_location);
        profilefavoritePlace = findViewById(R.id.profile_favorite_pace_name);

        loadingBar = new ProgressDialog(this);

        userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String image = dataSnapshot.child("profileImage").getValue().toString();
                    String bio = dataSnapshot.child("bio").getValue().toString();
                    String location = dataSnapshot.child("location").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();
                    String favoritePlace = dataSnapshot.child("favoritePlace").getValue().toString();
                    String userName = dataSnapshot.child("userName").getValue().toString();



                    profileFullName.setText(fullName);
                    AppBarTitle.setText(fullName);
                    profileEmail.setText(email);
                    profileUserName.setText(userName);
                    profileBio.setText(bio);
                    profilefavoritePlace.setText(favoritePlace);
                    profileLocation.setText(location);
                    profileCountryName.setText(country);
                    Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("type","Edit Username");
                startActivity(intent);
            }
        });

        profileBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("type","Edit Your About");
                startActivity(intent);
            }
        });

        profilefavoritePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("type","Edit Your Favorite Place");
                startActivity(intent);
            }
        });

        profileCountryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("type","Edit Your Country Name");
                startActivity(intent);
            }
        });

        profileLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("type","Edit Location");
                startActivity(intent);
            }
        });

        addProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_pick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Gallery_pick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);


        }



        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                File thumb_filePathUri = new File(resultUri.getPath());


                try {
                    thumb_bitmap = new Compressor(ProfileActivity.this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(80)
                            .compressToBitmap(thumb_filePathUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

                StorageReference thumb_path = ImageReff.child(currentUserId).child("ProfileImage").child(resultUri.getLastPathSegment()+ ".jpg");
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Profile picture uploading ...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                thumb_path.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                        Task<Uri> thumbDownloadUrl = thumb_task.getResult().getMetadata().getReference().getDownloadUrl();
                        if (thumb_task.isSuccessful()) {
                            thumbDownloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();

                                    userReference.child(currentUserId).child("profileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(ProfileActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();

                                            } else {
                                                String message = Objects.requireNonNull(task.getException()).getMessage();
                                                Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });

                                }
                            });
                        }

                    }
                });


            } else {
                Toast.makeText(ProfileActivity.this, "Error: Image not crop.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, ProfileActivity.class);
    }
    @AfterPermissionGranted(RC_VIDEO_APP_PREM)
    private void requestPermission()
    {
        String[] perms = {Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms))
        {

        }
        else
        {
            EasyPermissions.requestPermissions(this,"Hi this app need some permission, Please Allow.", RC_VIDEO_APP_PREM, perms);
        }
    }
}