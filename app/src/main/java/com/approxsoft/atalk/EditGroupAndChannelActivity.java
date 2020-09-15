package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
import java.util.HashMap;
import java.util.Objects;

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditGroupAndChannelActivity extends AppCompatActivity {

    Toolbar mToolBar;
    RelativeLayout editGroupLayout, editChannelLayout, inviteFriendsGroupLayout, inviteFriendsChannelLayout;
    RecyclerView AllFriends, AllFriend;
    DatabaseReference groupReference, channelReference,FriendsReference,UserReff;
    String groupUrlorChannrlUrl, Type, currentUserId;
    CircleImageView groupImageView, channelImageView;
    ImageView addGroupImage, addChannelImage;
    Button groupDataSaveBtn, channelDataSaveBtn;
    EditText groupNameText, channelNameText;
    Spinner groupSpinner, channelSpinner;

    private StorageReference ImageReff;
    public final int Gallery_pick = 1;
    private ProgressDialog loadingBar;
    Bitmap thumb_bitmap = null;

    FirebaseAuth mAuth;
    ImageView AppBarIcon;
    TextView AppBarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_and_channel);

        Type = getIntent().getExtras().get("type").toString();
        groupUrlorChannrlUrl = getIntent().getExtras().get("url").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        groupReference = FirebaseDatabase.getInstance().getReference().child("All Group");
        channelReference = FirebaseDatabase.getInstance().getReference().child("All Channel");
        ImageReff = FirebaseStorage.getInstance().getReference().child("User Images");
        FriendsReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("Message Friends");
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");

        mToolBar = findViewById(R.id.edit_group_and_channel_activity);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");

        AppBarIcon = findViewById(R.id.edit_group_app_bar_icon);
        AppBarTitle = findViewById(R.id.edit_group_app_bar_title);
        AppBarTitle.setText(Type);


        editGroupLayout = findViewById(R.id.edit_group_relative_layout);
        inviteFriendsGroupLayout = findViewById(R.id.invite_group_friends_list_layout);
        AllFriends = findViewById(R.id.add_friends_list);
        groupImageView = findViewById(R.id.edit_group_image);
        addGroupImage = findViewById(R.id.add_group_image_icon);
        groupNameText = findViewById(R.id.edit_group_name_text);
        groupSpinner = findViewById(R.id.edit_group_privacy);
        groupDataSaveBtn = findViewById(R.id.edit_group_btn);

        editChannelLayout = findViewById(R.id.edit_channel_relative_layout);
        inviteFriendsChannelLayout = findViewById(R.id.invite_channel_friends_list_layout);
        AllFriend = findViewById(R.id.add_friend_list);
        channelImageView = findViewById(R.id.edit_channel_image);
        addChannelImage = findViewById(R.id.add_channel_image_icon);
        channelNameText = findViewById(R.id.edit_channel_name_text);
        channelSpinner = findViewById(R.id.edit_channel_privacy);
        channelDataSaveBtn = findViewById(R.id.edit_channel_btn);

        loadingBar = new ProgressDialog(this);

        AppBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        switch (Type) {
            case "Edit Group":
                editGroupLayout.setVisibility(View.VISIBLE);


                groupReference.child(groupUrlorChannrlUrl).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String groupName = dataSnapshot.child("groupName").getValue().toString();
                            String groupImage = dataSnapshot.child("groupProfileImage").getValue().toString();
                            groupNameText.setText(groupName);
                            Picasso.get().load(groupImage).placeholder(R.drawable.profile_icon).into(groupImageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            case "Add Friends": {
                inviteFriendsGroupLayout.setVisibility(View.VISIBLE);

                AllFriends.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EditGroupAndChannelActivity.this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                AllFriends.setLayoutManager(linearLayoutManager);

                DisplayAllUser();

                AppBarIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
                break;
            }
            case "Edit Channel":
                editChannelLayout.setVisibility(View.VISIBLE);

                channelReference.child(groupUrlorChannrlUrl).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String channelName = dataSnapshot.child("channelName").getValue().toString();
                            String channelImage = dataSnapshot.child("channelProfileImage").getValue().toString();

                            channelNameText.setText(channelName);
                            Picasso.get().load(channelImage).placeholder(R.drawable.profile_icon).into(channelImageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            case "Add Friend": {
                inviteFriendsChannelLayout.setVisibility(View.VISIBLE);


                AllFriend.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EditGroupAndChannelActivity.this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                AllFriend.setLayoutManager(linearLayoutManager);

                DisplayAllUsers();

                AppBarIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       onBackPressed();
                    }
                });
                break;
            }
        }

        groupDataSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (groupSpinner.getSelectedItem().equals("Select Your Group Privacy"))
                {

                }
                else
                    if (groupNameText.getText().toString().isEmpty())
                    {

                    }
                    else
                    {
                        HashMap groupMap = new HashMap();
                        groupMap.put("groupName",groupNameText.getText().toString());
                        groupMap.put("privacy",groupSpinner.getSelectedItem().toString());

                        groupReference.child(groupUrlorChannrlUrl).updateChildren(groupMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task)
                            {
                                if (task.isSuccessful())
                                {
                                    onBackPressed();
                                }
                            }
                        });
                    }
            }
        });

        channelDataSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (channelSpinner.getSelectedItem().equals("Select Your Channel Privacy"))
                {

                }
                else
                if (channelNameText.getText().toString().isEmpty())
                {

                }
                else
                {
                    HashMap groupMap = new HashMap();
                    groupMap.put("channelName",channelNameText.getText().toString());
                    groupMap.put("privacy",channelSpinner.getSelectedItem().toString());

                    channelReference.child(groupUrlorChannrlUrl).updateChildren(groupMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if (task.isSuccessful())
                            {
                                onBackPressed();
                            }
                        }
                    });
                }
            }
        });

        addGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_pick);
            }
        });


    }

    private void DisplayAllUsers() {
        Query query = FriendsReference.orderByChild("permission");
        // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User, EditGroupAndChannelActivity.AllUsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final EditGroupAndChannelActivity.AllUsersViewHolder allUserViewHolder, final int position, @NonNull final User user) {

                final String userId = getRef(position).getKey();


                UserReff.child(Objects.requireNonNull(userId)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            final String name = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                            String image = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();

                            allUserViewHolder.setFullName(name);
                            allUserViewHolder.setProfileImage(image);



                        }

                        if (dataSnapshot.child("All Channel").hasChild(groupUrlorChannrlUrl))
                        {
                            allUserViewHolder.AddGroupBtn.setVisibility(View.GONE);
                            allUserViewHolder.DeleteGroupBtn.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            allUserViewHolder.AddGroupBtn.setVisibility(View.VISIBLE);
                            allUserViewHolder.DeleteGroupBtn.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                allUserViewHolder.AddGroupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        UserReff.child(userId).child("All Channel").child(groupUrlorChannrlUrl).child("permission").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    channelReference.child(groupUrlorChannrlUrl).child("Channel Member").child(userId).child("permission").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                allUserViewHolder.AddGroupBtn.setVisibility(View.GONE);
                                                allUserViewHolder.DeleteGroupBtn.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

                allUserViewHolder.DeleteGroupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        UserReff.child(userId).child("All Channel").child(groupUrlorChannrlUrl).child("permission").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    channelReference.child(groupUrlorChannrlUrl).child("Channel Member").child(userId).child("permission").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                allUserViewHolder.AddGroupBtn.setVisibility(View.VISIBLE);
                                                allUserViewHolder.DeleteGroupBtn.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });



            }


            public AllUsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_add_to_group_or_channel, parent ,false);
                return new AllUsersViewHolder(view);
            }
        };
        adapter.startListening();
        AllFriend.setAdapter(adapter);
    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        Button AddGroupBtn , DeleteGroupBtn;


        AllUsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            AddGroupBtn = itemView.findViewById(R.id.add_group_btn);
            DeleteGroupBtn = itemView.findViewById(R.id.leave_group_btn);



        }

        public void setProfileImage(String profileImage) {
            CircleImageView images = mView.findViewById(R.id.invite_message_friend_profile_image);
            Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(images);
        }

        public void setFullName(String fullName){
            TextView myName = mView.findViewById(R.id.invite_message_friend_name);
            myName.setText(fullName);
        }


    }


    private void DisplayAllUser()
    {
        Query query = FriendsReference.orderByChild("permission");
        // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User, EditGroupAndChannelActivity.AllUserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final EditGroupAndChannelActivity.AllUserViewHolder allUserViewHolder, final int position, @NonNull final User user) {

                final String userId = getRef(position).getKey();


                UserReff.child(Objects.requireNonNull(userId)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            final String name = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                            String image = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();

                            allUserViewHolder.setFullName(name);
                            allUserViewHolder.setProfileImage(image);



                        }

                        if (dataSnapshot.child("All Group").hasChild(groupUrlorChannrlUrl))
                        {
                            allUserViewHolder.AddGroupBtn.setVisibility(View.GONE);
                            allUserViewHolder.DeleteGroupBtn.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            allUserViewHolder.AddGroupBtn.setVisibility(View.VISIBLE);
                            allUserViewHolder.DeleteGroupBtn.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                allUserViewHolder.AddGroupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        UserReff.child(userId).child("All Group").child(groupUrlorChannrlUrl).child("permission").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    groupReference.child(groupUrlorChannrlUrl).child("Group Member").child(userId).child("permission").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                allUserViewHolder.AddGroupBtn.setVisibility(View.GONE);
                                                allUserViewHolder.DeleteGroupBtn.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

                allUserViewHolder.DeleteGroupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        UserReff.child(userId).child("All Group").child(groupUrlorChannrlUrl).child("permission").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    groupReference.child(groupUrlorChannrlUrl).child("Group Member").child(userId).child("permission").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                allUserViewHolder.AddGroupBtn.setVisibility(View.VISIBLE);
                                                allUserViewHolder.DeleteGroupBtn.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });



            }


            public AllUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_add_to_group_or_channel, parent ,false);
                return new AllUserViewHolder(view);
            }
        };
        adapter.startListening();
        AllFriends.setAdapter(adapter);
    }

    public static class AllUserViewHolder extends RecyclerView.ViewHolder {

        View mView;

        Button AddGroupBtn , DeleteGroupBtn;


        AllUserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            AddGroupBtn = itemView.findViewById(R.id.add_group_btn);
            DeleteGroupBtn = itemView.findViewById(R.id.leave_group_btn);



        }

        public void setProfileImage(String profileImage) {
            CircleImageView images = mView.findViewById(R.id.invite_message_friend_profile_image);
            Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(images);
        }

        public void setFullName(String fullName){
            TextView myName = mView.findViewById(R.id.invite_message_friend_name);
            myName.setText(fullName);
        }


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
                    thumb_bitmap = new Compressor(EditGroupAndChannelActivity.this)
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


                StorageReference thumb_path = ImageReff.child(currentUserId).child("GroupImage").child(resultUri.getLastPathSegment()+ ".jpg");

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

                                    if (Type.equals("Edit Group"))
                                    {
                                        groupReference.child(groupUrlorChannrlUrl).child("groupProfileImage").setValue(downloadUrl);
                                    }else if (Type.equals("Edit Channel"))
                                    {
                                        channelReference.child(groupUrlorChannrlUrl).child("channelProfileImage").setValue(downloadUrl);
                                    }
                                    loadingBar.dismiss();

                                }
                            });
                        }

                    }
                });


            } else {
                Toast.makeText(EditGroupAndChannelActivity.this, "Error: Image not crop.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }
}