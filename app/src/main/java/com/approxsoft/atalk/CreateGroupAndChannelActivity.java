package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Model.Channel;
import Model.Group;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class CreateGroupAndChannelActivity extends AppCompatActivity {

    Toolbar mToolBar;
    RelativeLayout CreateGroupLayout, CreateChannelLayout, MyGroupLayout, AllGroupListLayout, MyChannelLayout, AllChannelLayout, InviteFriendsToGroupListLayout, InviteChannelListLayout;
    RecyclerView MyChannelList, AllChannelList, MyGroupList, AllGroupList,allFriends,allFriend;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference, dataReference, groupReference, channelReference, UserReference,FriendsReference,UserReff;
    String currentUserId, Type,downloadUrl = "None",url;
    EditText GroupNameText, ChannelNameText;
    Button CreateGroupBtn, CreateChannelBtn, groupFriendsSubmitBtn, channelFriendsSubmitBtn;
    CircleImageView GroupImage, ChannelImage;
    ImageView AddGroupImage, AddChannelImage;
    TextView SaveText;
    private StorageReference ImageReff;
    public final int Gallery_pick = 1;
    private ProgressDialog loadingBar;
    Bitmap thumb_bitmap = null;
    Spinner GroupPrivacySpinner, ChannelPrivacySpinner;
    ImageView AppBarIcon;
    TextView AppBarTitle;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_and_channel);

        Type = getIntent().getExtras().get("title").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);
        UserReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);
        FriendsReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("Message Friends");
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        dataReference = FirebaseDatabase.getInstance().getReference();
        ImageReff = FirebaseStorage.getInstance().getReference().child("User Images");

        mToolBar = findViewById(R.id.create_group_ap_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");

        AppBarIcon = findViewById(R.id.create_group_app_bar_icon);
        AppBarTitle = findViewById(R.id.create_group_app_bar_title);

        AppBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CreateGroupLayout = findViewById(R.id.create_group_relative_layout);
        InviteFriendsToGroupListLayout = findViewById(R.id.invite_group_friends_list);
        InviteChannelListLayout = findViewById(R.id.invite_channel_friends_list);
        MyGroupLayout = findViewById(R.id.my_group_list_relative_layout);
        AllGroupListLayout = findViewById(R.id.all_group_list_relative_layout);
        MyGroupList = findViewById(R.id.my_group_list_view);
        AllGroupList = findViewById(R.id.all_group_list_view);
        GroupImage = findViewById(R.id.create_group_image);
        AddGroupImage = findViewById(R.id.add_group_image_icon);
        GroupNameText = findViewById(R.id.create_group_name_text);
        CreateGroupBtn = findViewById(R.id.create_group_btn);
        GroupPrivacySpinner = findViewById(R.id.create_group_privacy);

        CreateChannelLayout = findViewById(R.id.create_channel_relative_layout);
        MyChannelLayout = findViewById(R.id.my_channel_list_relative_layout);
        AllChannelLayout = findViewById(R.id.all_channel_list_relative_layout);
        MyChannelList = findViewById(R.id.my_channel_list_view);
        AllChannelList = findViewById(R.id.all_channel_list_view);
        ChannelImage = findViewById(R.id.create_channel_image);
        AddChannelImage = findViewById(R.id.add_channel_image_icon);
        ChannelNameText = findViewById(R.id.create_channel_name_text);
        CreateChannelBtn = findViewById(R.id.create_channel_btn);
        ChannelPrivacySpinner = findViewById(R.id.create_channel_privacy);

        groupFriendsSubmitBtn = findViewById(R.id.group_invite_friend_submit_btn);
        channelFriendsSubmitBtn = findViewById(R.id.channel_invite_friend_submit_btn);

        loadingBar = new ProgressDialog(this);
        SaveText = findViewById(R.id.save_text);



        groupFriendsSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                String saveCurrentDate = currentDate.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                String saveCurrentTime = currentTime.format(calForDate.getTime());

                Map reciverMap = new HashMap();
                reciverMap.put("message", "Welcome to new group");
                reciverMap.put("time", saveCurrentTime);
                reciverMap.put("date", saveCurrentDate);
                reciverMap.put("condition", "true");
                reciverMap.put("from", currentUserId);

                dataReference.child("All Group").child(SaveText.getText().toString()).child("messages").updateChildren(reciverMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent chatIntent = new Intent(CreateGroupAndChannelActivity.this, GroupChatActivity.class);
                            chatIntent.putExtra("visit_user_id", SaveText.getText().toString());
                            chatIntent.putExtra("userId",currentUserId);
                            startActivity(chatIntent);
                            finish();
                        }
                    }
                });
            }
        });

        channelFriendsSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                String saveCurrentDate = currentDate.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                String saveCurrentTime = currentTime.format(calForDate.getTime());

                Map reciverMap = new HashMap();
                reciverMap.put("message", "Welcome to new channel");
                reciverMap.put("time", saveCurrentTime);
                reciverMap.put("date", saveCurrentDate);
                reciverMap.put("condition", "true");
                reciverMap.put("from", currentUserId);

                dataReference.child("All Channel").child(SaveText.getText().toString()).child("messages").updateChildren(reciverMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent chatIntent = new Intent(CreateGroupAndChannelActivity.this,ChannelChatActivity.class);
                            chatIntent.putExtra("visit_user_id", SaveText.getText().toString());
                            chatIntent.putExtra("userId",currentUserId);
                            startActivity(chatIntent);
                            finish();
                        }
                    }
                });
            }
        });


        switch (Type) {
            case "Create Group":
                CreateGroupLayout.setVisibility(View.VISIBLE);
                AppBarTitle.setText(Type);


                AddGroupImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent();
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, Gallery_pick);
                    }
                });

                CreateGroupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GroupNameText.getText().toString().isEmpty()) {
                            Toast.makeText(CreateGroupAndChannelActivity.this, "Write your group name", Toast.LENGTH_SHORT).show();
                        } else if (GroupPrivacySpinner.getSelectedItem().toString().equals("Select Your Group Privacy")) {
                            Toast.makeText(CreateGroupAndChannelActivity.this, "Select your group privacy", Toast.LENGTH_SHORT).show();
                        } else {
                            Calendar calForDate = Calendar.getInstance();
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                            final String saveCurrentDate = currentDate.format(calForDate.getTime());

                            Calendar calForTime = Calendar.getInstance();
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                            final String saveCurrentTime = currentTime.format(calForTime.getTime());

                            final String randomKey = saveCurrentDate + saveCurrentTime + currentUserId;

                            if (downloadUrl.equals("None")) {
                                url = "None";
                            } else {
                                url = downloadUrl;
                            }

                            HashMap groupMap = new HashMap();
                            groupMap.put("groupName", GroupNameText.getText().toString());
                            groupMap.put("groupProfileImage", url);
                            groupMap.put("time", saveCurrentTime);
                            groupMap.put("date", saveCurrentDate);
                            groupMap.put("privacy", GroupPrivacySpinner.getSelectedItem().toString());
                            groupMap.put("admin1", currentUserId);

                            SaveText.setText(randomKey);

                            dataReference.child("All Group").child(randomKey).updateChildren(groupMap).addOnCompleteListener(new OnCompleteListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {

                                        dataReference.child("All Users").child(currentUserId).child("All Group").child(randomKey).child("permission").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Type = "Invite Group";
                                                    CreateGroupLayout.setVisibility(View.GONE);
                                                    InviteFriendsToGroupListLayout.setVisibility(View.VISIBLE);

                                                    AppBarTitle.setText("Add Groups");


                                                    allFriends = findViewById(R.id.all_friends);
                                                    allFriends.setHasFixedSize(true);
                                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CreateGroupAndChannelActivity.this);
                                                    linearLayoutManager.setReverseLayout(true);
                                                    linearLayoutManager.setStackFromEnd(true);
                                                    allFriends.setLayoutManager(linearLayoutManager);

                                                    DisplayAllUser();

                                                    AppBarIcon.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(CreateGroupAndChannelActivity.this, GroupAndChannel_activity.class);
                                                            intent.putExtra("type", "Group");
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });

                break;
            case "Create Channel":

                CreateChannelLayout.setVisibility(View.VISIBLE);
                AppBarTitle.setText(Type);


                AddChannelImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent();
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, Gallery_pick);
                    }
                });

                CreateChannelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ChannelNameText.getText().toString().isEmpty()) {
                            Toast.makeText(CreateGroupAndChannelActivity.this, "Write your channel name", Toast.LENGTH_SHORT).show();
                        } else if (ChannelPrivacySpinner.getSelectedItem().toString().equals("Select Your channel Privacy")) {
                            Toast.makeText(CreateGroupAndChannelActivity.this, "Select your channel privacy", Toast.LENGTH_SHORT).show();
                        } else {
                            Calendar calForDate = Calendar.getInstance();
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                            final String saveCurrentDate = currentDate.format(calForDate.getTime());

                            Calendar calForTime = Calendar.getInstance();
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                            final String saveCurrentTime = currentTime.format(calForTime.getTime());

                            final String randomKey = saveCurrentDate + saveCurrentTime + currentUserId;

                            if (downloadUrl.equals("None")) {
                                url = "None";
                            } else {
                                url = downloadUrl;
                            }

                            HashMap channelMap = new HashMap();
                            channelMap.put("channelName", ChannelNameText.getText().toString());
                            channelMap.put("channelProfileImage", url);
                            channelMap.put("time", saveCurrentTime);
                            channelMap.put("date", saveCurrentDate);
                            channelMap.put("privacy", ChannelPrivacySpinner.getSelectedItem().toString());
                            channelMap.put("admin1", currentUserId);

                            SaveText.setText(randomKey);

                            dataReference.child("All Channel").child(randomKey).updateChildren(channelMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        dataReference.child("All Users").child(currentUserId).child("All Channel").child(randomKey).child("permission").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Type = "Invite Channel";
                                                    CreateChannelLayout.setVisibility(View.GONE);
                                                    InviteChannelListLayout.setVisibility(View.VISIBLE);

                                                    AppBarTitle.setText("Add Channel");


                                                    allFriend = findViewById(R.id.all_friend);
                                                    allFriend.setHasFixedSize(true);
                                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CreateGroupAndChannelActivity.this);
                                                    linearLayoutManager.setReverseLayout(true);
                                                    linearLayoutManager.setStackFromEnd(true);
                                                    allFriend.setLayoutManager(linearLayoutManager);

                                                    DisplayAllUsers();

                                                    AppBarIcon.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(CreateGroupAndChannelActivity.this, GroupAndChannel_activity.class);
                                                            intent.putExtra("type", "Channel");
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });

                break;
            case "My Group": {

                MyGroupLayout.setVisibility(View.VISIBLE);
                AppBarTitle.setText(Type);

                groupReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("My Group");

                MyGroupList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                MyGroupList.setLayoutManager(linearLayoutManager);

                DisplayMyGroup();

                AppBarIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CreateGroupAndChannelActivity.this, GroupAndChannel_activity.class);
                        intent.putExtra("type", "Group");
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            }
            case "My Channel": {
                MyChannelLayout.setVisibility(View.VISIBLE);
                AppBarTitle.setText(Type);

                channelReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("My Channel");

                MyChannelList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                MyChannelList.setLayoutManager(linearLayoutManager);

                DisplayMyChannel();

                AppBarIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CreateGroupAndChannelActivity.this, GroupAndChannel_activity.class);
                        intent.putExtra("type", "Channel");
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            }
            case "All Group": {
                AllGroupListLayout.setVisibility(View.VISIBLE);
                AppBarTitle.setText(Type);

                groupReference = FirebaseDatabase.getInstance().getReference().child("All Group");


                AllGroupList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                AllGroupList.setLayoutManager(linearLayoutManager);

                DisplayAllGroups();

                AppBarIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CreateGroupAndChannelActivity.this, GroupAndChannel_activity.class);
                        intent.putExtra("type", "Group");
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            }
            case "All Channel": {

                AllChannelLayout.setVisibility(View.VISIBLE);
                AppBarTitle.setText(Type);

                channelReference = FirebaseDatabase.getInstance().getReference().child("All Channel");

                AllChannelList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                AllChannelList.setLayoutManager(linearLayoutManager);

                DisplayAllChannel();

                AppBarIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CreateGroupAndChannelActivity.this, GroupAndChannel_activity.class);
                        intent.putExtra("type", "Channel");
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            }
            case "Invite Group":
                CreateGroupLayout.setVisibility(View.GONE);
                InviteFriendsToGroupListLayout.setVisibility(View.VISIBLE);

                break;
            case "Invite Channel":
                CreateChannelLayout.setVisibility(View.GONE);
                InviteChannelListLayout.setVisibility(View.VISIBLE);

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + Type);
        }




        

    }

    private void DisplayAllUsers() {
        Query query = FriendsReference.orderByChild("permission");
        // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User, AllUsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CreateGroupAndChannelActivity.AllUsersViewHolder allUserViewHolder, final int position, @NonNull final User user) {

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

                        if (dataSnapshot.child("All Channel").hasChild(SaveText.getText().toString()))
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
                        UserReff.child(userId).child("All Channel").child(SaveText.getText().toString()).child("permission").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    dataReference.child("All Channel").child(SaveText.getText().toString()).child("Channel Member").child(userId).child("permission").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        UserReff.child(userId).child("All Channel").child(SaveText.getText().toString()).child("permission").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    dataReference.child("All Channel").child(SaveText.getText().toString()).child("Channel Member").child(userId).child("permission").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
        allFriend.setAdapter(adapter);
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

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User, AllUserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CreateGroupAndChannelActivity.AllUserViewHolder allUserViewHolder, final int position, @NonNull final User user) {

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

                        if (dataSnapshot.child("All Group").hasChild(SaveText.getText().toString()))
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
                        UserReff.child(userId).child("All Group").child(SaveText.getText().toString()).child("permission").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    dataReference.child("All Group").child(SaveText.getText().toString()).child("Group Member").child(userId).child("permission").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        UserReff.child(userId).child("All Group").child(SaveText.getText().toString()).child("permission").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    dataReference.child("All Group").child(SaveText.getText().toString()).child("Group Member").child(userId).child("permission").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
        allFriends.setAdapter(adapter);
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

    private void DisplayAllChannel()
    {

        Query query = channelReference.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Channel> options = new FirebaseRecyclerOptions.Builder<Channel>().setQuery(query, Channel.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Channel, AllChannelViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AllChannelViewHolder channelViewHolder, final int position, @NonNull final Channel channel) {

                final String channelId = getRef(position).getKey();

                channelViewHolder.channelName.setText(channel.getChannelName());
                Picasso.get().load(channel.getChannelProfileImage()).placeholder(R.drawable.profile_icon).into(channelViewHolder.channelImage);

                UserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("All Channel").hasChild(channelId))
                        {
                            channelViewHolder.ChannelAddBtn.setVisibility(View.GONE);
                            channelViewHolder.RemoveChannelBtn.setVisibility(View.VISIBLE);

                        }
                        else
                        {
                            channelReference.child(channelId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if (dataSnapshot.child("Channel Member").hasChild(currentUserId))
                                    {
                                        channelViewHolder.ChannelAddBtn.setVisibility(View.GONE);
                                        channelViewHolder.RemoveChannelBtn.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        channelViewHolder.ChannelAddBtn.setVisibility(View.VISIBLE);
                                        channelViewHolder.RemoveChannelBtn.setVisibility(View.GONE);


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                channelViewHolder.RemoveChannelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        channelReference.child(channelId).child("Channel Member").child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    UserReference.child("All Channel").child(channelId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                channelViewHolder.RemoveChannelBtn.setVisibility(View.GONE);
                                                channelViewHolder.ChannelAddBtn.setVisibility(View.VISIBLE);

                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

                channelViewHolder.ChannelAddBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Calendar calForDate = Calendar.getInstance();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                        final String saveCurrentDate = currentDate.format(calForDate.getTime());

                        channelReference.child(channelId).child("Channel Member").child(currentUserId).child("permission").child("true").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    UserReference.child("All Channel").child(channelId).child("permission").child("true").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                channelViewHolder.ChannelAddBtn.setVisibility(View.GONE);
                                                channelViewHolder.RemoveChannelBtn.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });


            }

            public AllChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_search_layout, parent ,false);
                return new AllChannelViewHolder(view);
            }
        };
        adapter.startListening();
        AllChannelList.setAdapter(adapter);

    }

    private void DisplayAllGroups()
    {
        Query query = groupReference.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Group> options = new FirebaseRecyclerOptions.Builder<Group>().setQuery(query, Group.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Group, AllGroupViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AllGroupViewHolder groupViewHolder, final int position, @NonNull final Group group) {

                final String groupId = getRef(position).getKey();

                groupViewHolder.groupName.setText(group.getGroupName());
                Picasso.get().load(group.getGroupProfileImage()).placeholder(R.drawable.profile_icon).into(groupViewHolder.groupImage);

                UserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("All Group").hasChild(groupId))
                        {
                            groupViewHolder.AddGroupBtn.setVisibility(View.GONE);
                            groupViewHolder.CancelGroupBtn.setVisibility(View.GONE);
                            groupViewHolder.RemoveGroupBtn.setVisibility(View.VISIBLE);

                            groupViewHolder.RemoveGroupBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    UserReference.child("All Group").child(groupId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                groupReference.child(groupId).child("Group Member").child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            groupViewHolder.AddGroupBtn.setVisibility(View.VISIBLE);
                                                            groupViewHolder.CancelGroupBtn.setVisibility(View.GONE);
                                                            groupViewHolder.RemoveGroupBtn.setVisibility(View.GONE);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            groupReference.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if (dataSnapshot.child("UserGroupRequest").hasChild(currentUserId))
                                    {
                                        groupViewHolder.AddGroupBtn.setVisibility(View.GONE);
                                        groupViewHolder.CancelGroupBtn.setVisibility(View.VISIBLE);
                                        groupViewHolder.RemoveGroupBtn.setVisibility(View.GONE);

                                        groupViewHolder.CancelGroupBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                groupReference.child(groupId).child("UserGroupRequest").child(currentUserId).child("date").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            groupViewHolder.AddGroupBtn.setVisibility(View.VISIBLE);
                                                            groupViewHolder.CancelGroupBtn.setVisibility(View.GONE);
                                                            groupViewHolder.RemoveGroupBtn.setVisibility(View.GONE);
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else
                                    {
                                        groupViewHolder.AddGroupBtn.setVisibility(View.VISIBLE);
                                        groupViewHolder.CancelGroupBtn.setVisibility(View.GONE);
                                        groupViewHolder.RemoveGroupBtn.setVisibility(View.GONE);

                                        groupViewHolder.AddGroupBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                Calendar calForDate = Calendar.getInstance();
                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                final String saveCurrentDate = currentDate.format(calForDate.getTime());

                                                groupReference.child(groupId).child("UserGroupRequest").child(currentUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            groupViewHolder.AddGroupBtn.setVisibility(View.GONE);
                                                            groupViewHolder.CancelGroupBtn.setVisibility(View.VISIBLE);
                                                            groupViewHolder.RemoveGroupBtn.setVisibility(View.GONE);
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            public AllGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_search_layout, parent ,false);
                return new AllGroupViewHolder(view);
            }
        };
        adapter.startListening();
        AllGroupList.setAdapter(adapter);

    }

    private void DisplayMyChannel()
    {

        Query query = channelReference.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Channel> options = new FirebaseRecyclerOptions.Builder<Channel>().setQuery(query, Channel.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Channel, ChannelViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChannelViewHolder channelViewHolder, final int position, @NonNull final Channel channel) {

                final String channelId = getRef(position).getKey();

                channelViewHolder.channelName.setText(channel.getChannelName());
                Picasso.get().load(channel.getChannelProfileImage()).placeholder(R.drawable.profile_icon).into(channelViewHolder.channelImage);

                Calendar calForDate = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                final String saveCurrentDate = currentDate.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                final String saveCurrentTime = currentTime.format(calForTime.getTime());

                if (channel.getTime().equals(saveCurrentTime))
                {
                    channelViewHolder.date.setText("Just Now");
                }
                else
                if (channel.getTime().equals(saveCurrentDate))
                {
                    channelViewHolder.date.setText("Today at "+ channel.getTime());
                }
                else
                {
                    channelViewHolder.date.setText(channel.getDate());
                }




            }

            public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_channel_layout, parent ,false);
                return new ChannelViewHolder(view);
            }
        };
        adapter.startListening();
        MyChannelList.setAdapter(adapter);
    }

    private void DisplayMyGroup()
    {

        Query query = groupReference.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Group> options = new FirebaseRecyclerOptions.Builder<Group>().setQuery(query, Group.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Group, GroupViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final GroupViewHolder groupViewHolder, final int position, @NonNull final Group group) {

                final String groupId = getRef(position).getKey();

                groupViewHolder.groupName.setText(group.getGroupName());
                Picasso.get().load(group.getGroupProfileImage()).placeholder(R.drawable.profile_icon).into(groupViewHolder.groupImage);

                Calendar calForDate = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                final String saveCurrentDate = currentDate.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                final String saveCurrentTime = currentTime.format(calForTime.getTime());

                if (group.getTime().equals(saveCurrentTime))
                {
                    groupViewHolder.date.setText("Just Now");
                }
                else
                if (group.getTime().equals(saveCurrentDate))
                {
                    groupViewHolder.date.setText("Today at "+ group.getTime());
                }
                else
                {
                    groupViewHolder.date.setText(group.getDate());
                }


            }

            public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_group_layout, parent ,false);
                return new GroupViewHolder(view);
            }
        };
        adapter.startListening();
        MyGroupList.setAdapter(adapter);

    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        View mView;

        CircleImageView groupImage;
        TextView groupName, date;

        GroupViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            groupImage = itemView.findViewById(R.id.group_profile_image);
            groupName = itemView.findViewById(R.id.group_name);
            date = itemView.findViewById(R.id.group_extra_text);

        }


    }

    public static class ChannelViewHolder extends RecyclerView.ViewHolder {

        View mView;

        CircleImageView channelImage;
        TextView channelName, date;

        ChannelViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            channelImage = itemView.findViewById(R.id.channel_profile_image);
            channelName = itemView.findViewById(R.id.channel_name);
            date = itemView.findViewById(R.id.channel_extra_text);

        }


    }

    public static class AllGroupViewHolder extends RecyclerView.ViewHolder {

        View mView;

        CircleImageView groupImage;
        TextView groupName;
        Button AddGroupBtn, CancelGroupBtn, RemoveGroupBtn;

        AllGroupViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            groupImage = itemView.findViewById(R.id.search_group_profile_image);
            groupName = itemView.findViewById(R.id.search_group_name);
            AddGroupBtn = itemView.findViewById(R.id.add_group_btn);
            CancelGroupBtn = itemView.findViewById(R.id.cancel_group_btn);
            RemoveGroupBtn = itemView.findViewById(R.id.leave_group_btn);


        }


    }

    public static class AllChannelViewHolder extends RecyclerView.ViewHolder {

        View mView;

        CircleImageView channelImage;
        TextView channelName;
        Button ChannelAddBtn, RemoveChannelBtn;

        AllChannelViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            channelImage = itemView.findViewById(R.id.search_channel_profile_image);
            channelName = itemView.findViewById(R.id.search_channel_name);
            ChannelAddBtn = itemView.findViewById(R.id.add_channel_btn);
            RemoveChannelBtn = itemView.findViewById(R.id.leave_channel_btn);


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
                    thumb_bitmap = new Compressor(CreateGroupAndChannelActivity.this)
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
                                    downloadUrl = uri.toString();

                                    if (Type.equals("Create Group"))
                                    {
                                        Picasso.get().load(downloadUrl).placeholder(R.drawable.profile_icon).into(GroupImage);
                                    }else
                                    {
                                        Picasso.get().load(downloadUrl).placeholder(R.drawable.profile_icon).into(ChannelImage);
                                    }
                                    loadingBar.dismiss();

                                }
                            });
                        }

                    }
                });


            } else {
                Toast.makeText(CreateGroupAndChannelActivity.this, "Error: Image not crop.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }
}