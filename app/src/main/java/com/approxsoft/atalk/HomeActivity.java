package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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

import Model.MessageData;
import Model.Permission;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeActivity extends AppCompatActivity {


    DrawerLayout MainLayout;
    NavigationView navigationView;
    Toolbar mToolBer;
    ImageView ShareBtn;
    TextView navUserName, navEmail, ChatHomeGroupBtn, ChatHomeChannelBtn, GroupHomeChatBtn, GroupHomeChannelBtn, ChannelHomeChatBtn, ChannelHomeGroupBtn;
    ActionBarDrawerToggle actionBarDrawerToggle;
    RelativeLayout HomeChatRelativeLayout,HomeGroupRelativeLayout,HomeChannelRelativeLayout,relativeLayout4,relativeLayout5,relativeLayout6,relativeLayout7,relativeLayout8,relativeLayout9,relativeLayout10,relativeLayout11,relativeLayout12,relativeLayout13,relativeLayout14,relativeLayout15,relativeLayout16,relativeLayout17,relativeLayout18,relativeLayout19;
    LinearLayout profileLinearLayout,editProfileLinearLayout,saveNoteLinearLayout,GroupLinerLayout,ChannelLinearLayout,chatRequestLinearLayout,CallNawLinearLayout,SettingsLinearLayout,TermsConditionLayout,AboutLinearLayout,FindRequestLayout,SecratChatLayout,linearLayout13,linearLayout14,linearLayout15,linearLayout16,linearLayout17,linearLayout18,linearLayout19,linearLayout20,linearLayout21,linearLayout22,linearLayout23;

    LinearLayout TopDownLayout;
    ImageView SearchIcon, upIcon,downIcon,AddProfileImageIcon;
    CircleImageView ProfileImage;
    FirebaseAuth mAuth;

    private DatabaseReference userReference,ChannelUserReff;
    private StorageReference ImageReff;
    String currentUserId;
    public static final int Gallery_pick = 1;
    private ProgressDialog loadingBar;
    Bitmap thumb_bitmap = null;

    private RecyclerView myFriendList;
    private RecyclerView friendsMessageList, groupMessageList, channelMessageList;
    private CircleImageView messageProfileImage;
    private TextView messageUserName;

    private String userId, userType;
    private DatabaseReference userReff, FriendsReference, MessageReff,UserReff, messageReff, GroupMessageReff, ChannelMessageReff;
    private Toolbar mToolBar;
    SwipeRefreshLayout refreshLayout, channelRefresh, groupRefresh;
    MediaPlayer mediaPlayer;
    String Condition = "";

    private static final int RC_VIDEO_APP_PREM = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        requestPermission();


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("All Users");
        messageReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        ImageReff = FirebaseStorage.getInstance().getReference().child("User Images");
        FriendsReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("Message Friends");
        MessageReff = FirebaseDatabase.getInstance().getReference().child("messages").child(currentUserId);
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("All Group");
        ChannelUserReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("All Channel");
        GroupMessageReff = FirebaseDatabase.getInstance().getReference().child("All Group");
        ChannelMessageReff = FirebaseDatabase.getInstance().getReference().child("All Channel");

        mToolBer = findViewById(R.id.home_ap_bar);
        ///setSupportActionBar(mToolBer);
        // getSupportActionBar().setTitle("Make money ideas 2020");

        MainLayout = findViewById(R.id.main_layout);

        mediaPlayer = MediaPlayer.create(this,R.raw.pristine);



        navigationView = findViewById(R.id.main_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(HomeActivity.this,MainLayout,mToolBer,R.string.navigation_open,R.string.navigation_close);
        MainLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        refreshLayout = findViewById(R.id.message_refresh);
        channelRefresh = findViewById(R.id.channel_message_refresh);
        groupRefresh = findViewById(R.id.group_message_refresh);


        ShareBtn = findViewById(R.id.application_share_btn);

        ShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.utalksoft.utalk");
                startActivity(Intent.createChooser(intent,"Share Application Link"));
            }
        });


        //////////////////////////////////////////////




        HomeChatRelativeLayout = findViewById(R.id.home_chat_relative_layout);
        HomeGroupRelativeLayout = findViewById(R.id.home_group_relative_layout);
        HomeChannelRelativeLayout = findViewById(R.id.home_channel_relative_layout);

        ChatHomeGroupBtn = findViewById(R.id.chat_home_group_btn);
        ChatHomeChannelBtn = findViewById(R.id.chat_home_channel_btn);

        GroupHomeChatBtn = findViewById(R.id.group_home_chat_btn);
        GroupHomeChannelBtn = findViewById(R.id.group_home_Channel_btn);

        ChannelHomeChatBtn  = findViewById(R.id.channel_home_chat_btn);
        ChannelHomeGroupBtn = findViewById(R.id.channel_home_group_btn);




        myFriendList = findViewById(R.id.all_friend_online_status);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();



        ////////////////////////////////////////////////////

        ///////////////////////////////////////////////////

        friendsMessageList = findViewById(R.id.all_friend_message_list);
        friendsMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);
        linearLayoutManager2.setOrientation(RecyclerView.VERTICAL);
        friendsMessageList.setLayoutManager(linearLayoutManager2);

        DisplayAllFriendsMessage();



        ///////////////////////////////////////////////////



        groupMessageList = findViewById(R.id.all_friend_group_message_list);



        channelMessageList = findViewById(R.id.all_friend_channel_message_list);
        channelMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(HomeActivity.this);
        linearLayoutManager4.setReverseLayout(true);
        linearLayoutManager4.setStackFromEnd(true);
        linearLayoutManager4.setOrientation(RecyclerView.VERTICAL);
        channelMessageList.setLayoutManager(linearLayoutManager4);


        ChatHomeGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                HomeChatRelativeLayout.setVisibility(View.GONE);
                HomeGroupRelativeLayout.setVisibility(View.VISIBLE);

                groupMessageList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(HomeActivity.this);
                linearLayoutManager3.setReverseLayout(true);
                linearLayoutManager3.setStackFromEnd(true);
                linearLayoutManager3.setOrientation(RecyclerView.VERTICAL);
                groupMessageList.setLayoutManager(linearLayoutManager3);
                DisplayAllGroupMessage();

            }
        });

        ChatHomeChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                HomeChatRelativeLayout.setVisibility(View.GONE);
                HomeChannelRelativeLayout.setVisibility(View.VISIBLE);

                DisplayAllChannelMessageList();
            }
        });

        GroupHomeChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeGroupRelativeLayout.setVisibility(View.GONE);
                HomeChatRelativeLayout.setVisibility(View.VISIBLE);

                DisplayAllFriendsMessage();
                DisplayAllFriends();

            }
        });

        GroupHomeChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeGroupRelativeLayout.setVisibility(View.GONE);
                HomeChannelRelativeLayout.setVisibility(View.VISIBLE);

                DisplayAllChannelMessageList();
            }
        });

        ChannelHomeChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeChannelRelativeLayout.setVisibility(View.GONE);
                HomeChatRelativeLayout.setVisibility(View.VISIBLE);

                DisplayAllFriendsMessage();
                DisplayAllFriends();


            }
        });

        ChannelHomeGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeChannelRelativeLayout.setVisibility(View.GONE);
                HomeGroupRelativeLayout.setVisibility(View.VISIBLE);

                groupMessageList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(HomeActivity.this);
                linearLayoutManager3.setReverseLayout(true);
                linearLayoutManager3.setStackFromEnd(true);
                linearLayoutManager3.setOrientation(RecyclerView.VERTICAL);
                groupMessageList.setLayoutManager(linearLayoutManager3);
                DisplayAllGroupMessage();
            }
        });








        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Intent intent = new Intent(HomeActivity.this,HomeActivity.class);
                // startActivity(intent);
                //Animatoo.animateFade(HomeActivity.this);
                DisplayAllFriends();

                DisplayAllFriendsMessage();
                refreshLayout.setRefreshing(false);

            }
        });

        groupRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                DisplayAllGroupMessage();
                groupRefresh.setRefreshing(false);

            }
        });

        channelRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                DisplayAllChannelMessageList();
                channelRefresh.setRefreshing(false);

            }
        });





        SearchIcon = findViewById(R.id.home_search_icon);

        SearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, MessageRequestActivity.class);
                intent.putExtra("title","User");
                startActivity(intent);
            }
        });

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        navUserName = navView.findViewById(R.id.nav_profile_name);
        navEmail = navView.findViewById(R.id.nav_email_or_number);
        TopDownLayout = navView.findViewById(R.id.header_top_down_linear_layout);
        upIcon = navView.findViewById(R.id.nav_up_icon);
        downIcon = navView.findViewById(R.id.nav_down_icon);
        profileLinearLayout = navView.findViewById(R.id.nav_profile_linear_layout);
        editProfileLinearLayout = navView.findViewById(R.id.nav_profile_edit_linear_layout);
        saveNoteLinearLayout = navView.findViewById(R.id.nav_save_note_linear_layout);
        ProfileImage = navView.findViewById(R.id.nav_profile_image);
        AddProfileImageIcon = navView.findViewById(R.id.nav_add_profile_image_icon);
        GroupLinerLayout = navView.findViewById(R.id.nav_group_linear_layout);
        ChannelLinearLayout = navView.findViewById(R.id.nav_create_channel_linear_layout);
        chatRequestLinearLayout = navView.findViewById(R.id.nav_chat_request_linear_layout);
        CallNawLinearLayout = navView.findViewById(R.id.nav_call_naw_linear_layout);
        SettingsLinearLayout = navView.findViewById(R.id.nav_setting_linear_layout);
        TermsConditionLayout = navView.findViewById(R.id.nav_terms_condition_linear_layout);
        AboutLinearLayout = navView.findViewById(R.id.nav_about_linear_layout);
        FindRequestLayout = navView.findViewById(R.id.nav_message_request_linear_layout);
        SecratChatLayout = navView.findViewById(R.id.nav_secret_chat_linear_layout);
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

                    navUserName.setText(fullName);
                    navEmail.setText(email);
                    Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(ProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        updateUserStatus("online");

        downIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                downIcon.setVisibility(View.GONE);
                TopDownLayout.setVisibility(View.VISIBLE);
                upIcon.setVisibility(View.VISIBLE);
            }
        });

        upIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                upIcon.setVisibility(View.GONE);
                TopDownLayout.setVisibility(View.GONE);
                downIcon.setVisibility(View.VISIBLE);
            }
        });

        profileLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
            }
        });

        editProfileLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HomeActivity.this, EditProfileActivity.class);
                intent.putExtra("type","Edit Profile");
                startActivity(intent);
            }
        });

        saveNoteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(HomeActivity.this,SaveNoteActivity.class));
            }
        });

        GroupLinerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HomeActivity.this, GroupAndChannel_activity.class);
                intent.putExtra("type","Group");
                startActivity(intent);
            }
        });

        ChannelLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HomeActivity.this, GroupAndChannel_activity.class);
                intent.putExtra("type","Channel");
                startActivity(intent);
            }
        });

        chatRequestLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MessageRequestActivity.class);
                intent.putExtra("title","User");
                startActivity(intent);
            }
        });

        CallNawLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CallListActivity.class));
            }
        });



        AddProfileImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_pick);

            }
        });

        SettingsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                intent.putExtra("type","Settings");
                startActivity(intent);
            }
        });
        TermsConditionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                intent.putExtra("type","Terms and condition");
                startActivity(intent);
            }
        });
        AboutLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                intent.putExtra("type","About");
                startActivity(intent);
            }
        });

        FindRequestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                intent.putExtra("type","Message Request");
                startActivity(intent);
            }
        });

        SecratChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this,"Coming Soon",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DisplayAllChannelMessageList()
    {
        Query query2 = ChannelUserReff.orderByChild("permission"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Permission> options = new FirebaseRecyclerOptions.Builder<Permission>().setQuery(query2, Permission.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Permission, HomeActivity.ChannelMessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HomeActivity.ChannelMessageViewHolder messageViewHolder, final int position, @NonNull final Permission messageData) {



                final String usersIDs = getRef(position).getKey();



                ChannelMessageReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        //final String id = messageViewHolder.setFrom(messageData.getFrom());
                        if (dataSnapshot.exists()) {
                            final String userName = Objects.requireNonNull(dataSnapshot.child("channelName").getValue()).toString();
                            final String profileimage = Objects.requireNonNull(dataSnapshot.child("channelProfileImage").getValue()).toString();

                            messageViewHolder.groupName(userName);
                            Picasso.get().load(profileimage).placeholder(R.drawable.profile_icon).into(messageViewHolder.groupImage);


                            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //MessageReff.child(usersIDs).child("condition").setValue("true");

                                    Intent chatIntent = new Intent(HomeActivity.this, ChannelChatActivity.class);
                                    chatIntent.putExtra("visit_user_id", usersIDs);
                                    chatIntent.putExtra("userId",currentUserId);
                                    startActivity(chatIntent);
                                }


                            });



                        }
                        if (dataSnapshot.hasChild("messages")) {
                            String time = Objects.requireNonNull(dataSnapshot.child("messages").child("time").getValue()).toString();
                            String date = Objects.requireNonNull(dataSnapshot.child("messages").child("date").getValue()).toString();
                            String text = Objects.requireNonNull(dataSnapshot.child("messages").child("message").getValue()).toString();

                            messageViewHolder.groupText(text);
                            //messageViewHolder.setDate(messageData.getDate());

                            Calendar calForDate = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                            String saveCurrentDate = currentDate.format(calForDate.getTime());

                            Calendar calForTime = Calendar.getInstance();
                            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                            String saveCurrentTime = currentTime.format(calForDate.getTime());

                            if (time.equals(saveCurrentTime))
                            {
                                messageViewHolder.Time("Just Now");
                            }
                            else
                            if (date.equals(saveCurrentDate))
                            {
                                messageViewHolder.Time(("Today: "+time));
                            }
                            else
                            {
                                messageViewHolder.Time(date);
                            }

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





            }

            public ChannelMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_message_layout, parent ,false);
                return new ChannelMessageViewHolder(view);
            }
        };
        adapter.startListening();
        channelMessageList.setAdapter(adapter);
    }

    public static class ChannelMessageViewHolder extends RecyclerView.ViewHolder {

        View mView;
        CircleImageView groupImage;



        public ChannelMessageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            groupImage = mView.findViewById(R.id.group_message_profile_image);


        }


        public void groupName(String userName) {
            TextView myName = mView.findViewById(R.id.group_message_profile_full_name);
            myName.setText(userName);
        }

        public void groupText(String text) {
            TextView grouptext = mView.findViewById(R.id.group_message_text);
            grouptext.setText(text);
        }

        public void Time(String just_now) {
            TextView Time = mView.findViewById(R.id.group_message_time);
            Time.setText(just_now);
        }
    }

    private void DisplayAllGroupMessage()
    {

        Query query2 = UserReff.orderByChild("permission"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Permission> options = new FirebaseRecyclerOptions.Builder<Permission>().setQuery(query2, Permission.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Permission, HomeActivity.GroupMessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HomeActivity.GroupMessageViewHolder messageViewHolder, final int position, @NonNull final Permission messageData) {



                final String usersIDs = getRef(position).getKey();



                GroupMessageReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        //final String id = messageViewHolder.setFrom(messageData.getFrom());
                        if (dataSnapshot.exists()) {
                            final String userName = Objects.requireNonNull(dataSnapshot.child("groupName").getValue()).toString();
                            final String profileimage = Objects.requireNonNull(dataSnapshot.child("groupProfileImage").getValue()).toString();

                            messageViewHolder.groupName(userName);
                            Picasso.get().load(profileimage).placeholder(R.drawable.profile_icon).into(messageViewHolder.groupImage);


                            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //MessageReff.child(usersIDs).child("condition").setValue("true");

                                    Intent chatIntent = new Intent(HomeActivity.this, GroupChatActivity.class);
                                    chatIntent.putExtra("visit_user_id", usersIDs);
                                    chatIntent.putExtra("userId",currentUserId);
                                    startActivity(chatIntent);
                                }


                            });



                        }
                        if (dataSnapshot.hasChild("messages")) {
                            String time = Objects.requireNonNull(dataSnapshot.child("messages").child("time").getValue()).toString();
                            String date = Objects.requireNonNull(dataSnapshot.child("messages").child("date").getValue()).toString();
                            String text = Objects.requireNonNull(dataSnapshot.child("messages").child("message").getValue()).toString();

                            messageViewHolder.groupText(text);
                            //messageViewHolder.setDate(messageData.getDate());

                            Calendar calForDate = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                            String saveCurrentDate = currentDate.format(calForDate.getTime());

                            Calendar calForTime = Calendar.getInstance();
                            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                            String saveCurrentTime = currentTime.format(calForDate.getTime());

                            if (time.equals(saveCurrentTime))
                            {
                                messageViewHolder.Time("Just Now");
                            }
                            else
                            if (date.equals(saveCurrentDate))
                            {
                                messageViewHolder.Time(("Today: "+time));
                            }
                            else
                            {
                                messageViewHolder.Time(date);
                            }

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





            }

            public GroupMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_message_layout, parent ,false);
                return new GroupMessageViewHolder(view);
            }
        };
        adapter.startListening();
        groupMessageList.setAdapter(adapter);

    }

    public static class GroupMessageViewHolder extends RecyclerView.ViewHolder {

        View mView;
        CircleImageView groupImage;



        public GroupMessageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            groupImage = mView.findViewById(R.id.group_message_profile_image);


        }


        public void groupName(String userName) {
            TextView myName = mView.findViewById(R.id.group_message_profile_full_name);
            myName.setText(userName);
        }

        public void groupText(String text) {
           TextView grouptext = mView.findViewById(R.id.group_message_text);
           grouptext.setText(text);
        }

        public void Time(String just_now) {
            TextView Time = mView.findViewById(R.id.group_message_time);
            Time.setText(just_now);
        }
    }

    private void DisplayAllFriendsMessage() {

        Query query2 = MessageReff.orderByChild("time"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<MessageData> options = new FirebaseRecyclerOptions.Builder<MessageData>().setQuery(query2, MessageData.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<MessageData, HomeActivity.MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HomeActivity.MessageViewHolder messageViewHolder, final int position, @NonNull final MessageData messageData) {



                final String usersIDs = getRef(position).getKey();



                MessageReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        //final String id = messageViewHolder.setFrom(messageData.getFrom());
                        if (dataSnapshot.exists())
                        {
                            String condition = dataSnapshot.child("condition").getValue().toString();
                            String type = dataSnapshot.child("type").getValue().toString();

                            if (type.equals("user"))
                            {


                                userReference.child(usersIDs).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            final String userName = dataSnapshot.child("firstName").getValue().toString();
                                            final String profileimage = dataSnapshot.child("profileImage").getValue().toString();
                                            final String type;

                                            messageViewHolder.setFullName(userName);
                                            messageViewHolder.setProfileImage(getApplicationContext(), profileimage);

                                            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v)
                                                {
                                                    MessageReff.child(usersIDs).child("condition").setValue("true");

                                                    Intent chatIntent = new Intent(HomeActivity.this,ChatActivity.class);
                                                    chatIntent.putExtra("visit_user_id",usersIDs);
                                                    chatIntent.putExtra("userId",currentUserId);
                                                    chatIntent.putExtra("type","user");
                                                    chatIntent.putExtra("from","user");
                                                    startActivity(chatIntent);
                                                }


                                            });


                                            if (dataSnapshot.hasChild("userState"))
                                            {
                                                type = dataSnapshot.child("userState").child("type").getValue().toString();

                                                if (type.equals("online"))
                                                {
                                                    messageViewHolder.OnlineStatus.setVisibility(View.VISIBLE);
                                                }
                                                else
                                                {
                                                    messageViewHolder.OnlineStatus.setVisibility(View.INVISIBLE);
                                                }

                                            }
                                        }

                                    }



                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                if (condition.equals("false"))
                                {
                                    mediaPlayer.start();
                                    messageViewHolder.MessageNotificationIcon.setVisibility(View.VISIBLE);
                                    messageViewHolder.setMessage(messageData.getMessage());
                                    //messageViewHolder.setDate(messageData.getDate());

                                    Calendar calForDate = Calendar.getInstance();
                                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                    String saveCurrentDate = currentDate.format(calForDate.getTime());

                                    Calendar calForTime = Calendar.getInstance();
                                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                                    String saveCurrentTime = currentTime.format(calForDate.getTime());

                                    if (messageData.getTime().equals(saveCurrentTime))
                                    {
                                        messageViewHolder.setTime("Just Now");
                                    }
                                    else
                                    if (messageData.getDate().equals(saveCurrentDate))
                                    {
                                        messageViewHolder.setTime(("Today: "+messageData.getTime()));
                                    }
                                    else
                                    {
                                        messageViewHolder.setTime(messageData.getDate());
                                    }


                                }
                                else if (condition.equals("true"))
                                {

                                    messageViewHolder.MessageNotificationIcon.setVisibility(View.GONE);
                                    messageViewHolder.setMessage(messageData.getMessage());
                                    // messageViewHolder.setDate(messageData.getDate());


                                    Calendar calForDate = Calendar.getInstance();
                                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                    String saveCurrentDate = currentDate.format(calForDate.getTime());

                                    Calendar calForTime = Calendar.getInstance();
                                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                                    String saveCurrentTime = currentTime.format(calForDate.getTime());

                                        if (messageData.getTime().equals(saveCurrentTime))
                                        {
                                            messageViewHolder.setTime("Just Now");
                                        }
                                        else
                                        if (messageData.getDate().equals(saveCurrentDate))
                                        {
                                            messageViewHolder.setTime(("Today: "+messageData.getTime()));
                                        }
                                        else
                                        {
                                            messageViewHolder.setTime(messageData.getDate());
                                        }
                                }

                                userReference.child(currentUserId).child("Message Friends").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(usersIDs))
                                        {
                                            messageViewHolder.messageDeleteBtn.setVisibility(View.GONE);
                                            messageViewHolder.messageDeleteBtn.setEnabled(false);
                                        }
                                        else
                                        {
                                            messageViewHolder.Time.setVisibility(View.GONE);
                                            messageViewHolder.messageDeleteBtn.setVisibility(View.VISIBLE);
                                            messageViewHolder.messageDeleteBtn.setEnabled(true);


                                            messageViewHolder.messageDeleteBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    messageReff.child(currentUserId).child(usersIDs).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if (task.isSuccessful())
                                                            {
                                                                messageReff.child(usersIDs).child(currentUserId).removeValue();

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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





            }

            public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_message_layout, parent ,false);
                return new MessageViewHolder(view);
            }
        };
        adapter.startListening();
        friendsMessageList.setAdapter(adapter);
    }
    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView messageDeleteBtn, Time;
        ImageView MessageNotificationIcon, OnlineStatus;



        public MessageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            messageDeleteBtn = mView.findViewById(R.id.message_Delete_Btn);
            MessageNotificationIcon = mView.findViewById(R.id.message_notification);
            Time = mView.findViewById(R.id.friend_message_time);
            OnlineStatus = mView.findViewById(R.id.online_status);

        }

        public void setProfileImage(Context applicationContext, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.friend_message_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile_icon).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = (TextView) mView.findViewById(R.id.friend_message_profile_full_name);
            myName.setText(fullName);
        }
        public void setMessage(String message){
            TextView messages = (TextView) mView.findViewById(R.id.friend_message_text);
            messages.setText(message);
        }

        public void setTime(String time){
            TextView times = (TextView) mView.findViewById(R.id.friend_message_time);
            times.setText(time);
        }




    }

    private void DisplayAllFriends() {

        Query query = FriendsReference.orderByChild("permission"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User, HomeActivity.FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HomeActivity.FriendViewHolder friendsViewHolder, final int position, @NonNull User friends) {


                final String usersIDs = getRef(position).getKey();
                //updateUserStatus("online");




                userReference.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String userName = dataSnapshot.child("fullName").getValue().toString();
                            final String profileimage = dataSnapshot.child("profileImage").getValue().toString();
                            final String type;

                            if (dataSnapshot.hasChild("userState"))
                            {
                                type = dataSnapshot.child("userState").child("type").getValue().toString();

                                if (type.equals("online"))
                                {
                                    friendsViewHolder.onlineStatus.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    friendsViewHolder.onlineStatus.setVisibility(View.INVISIBLE);
                                }

                            }

                            friendsViewHolder.setFullName(userName);
                            friendsViewHolder.setProfileImage(getApplicationContext(), profileimage);

                            friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent chatIntent = new Intent(HomeActivity.this,ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id",usersIDs);
                                    chatIntent.putExtra("userId",currentUserId);
                                    chatIntent.putExtra("userName",userName);
                                    chatIntent.putExtra("type","user");
                                    chatIntent.putExtra("from","user");
                                    startActivity(chatIntent);
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent ,false);
                return new FriendViewHolder(view);
            }
        };
        adapter.startListening();
        myFriendList.setAdapter(adapter);
    }
    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView onlineStatus;

        public FriendViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            onlineStatus = itemView.findViewById(R.id.online_status);
            //onlineStatus.setVisibility(View.VISIBLE);
        }

        public void setProfileImage(Context applicationContext, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.message_friends_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile_icon).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = (TextView) mView.findViewById(R.id.message_friends_name);
            myName.setText(fullName);
        }

    }


    private void updateUserStatus(String state)
    {
        String SaveCurrentDate, SaveCurrentTime;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyy");
        SaveCurrentDate =currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        SaveCurrentTime =currentTime.format(callForTime.getTime());


        Map CurrentStatemap = new HashMap();
        CurrentStatemap.put("time", SaveCurrentTime);
        CurrentStatemap.put("date", SaveCurrentDate);
        CurrentStatemap.put("type", state);

        userReference.child(currentUserId).child("userState")
                .updateChildren(CurrentStatemap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_pick && resultCode == RESULT_OK && data!=null) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);


        }


        Calendar calForDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());


        final  String postRandomName = saveCurrentDate + saveCurrentTime;


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                File thumb_filePathUri = new File(resultUri.getPath());


                try
                {
                    thumb_bitmap = new Compressor(HomeActivity.this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(80)
                            .compressToBitmap(thumb_filePathUri);

                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,80, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                StorageReference thumb_path = ImageReff.child(currentUserId).child("ProfileImage").child(resultUri.getLastPathSegment()+postRandomName+".jpg");
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Your profile picture uploading ...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                thumb_path.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                        Task<Uri> thumbDownloadUrl = thumb_task.getResult().getMetadata().getReference().getDownloadUrl();
                        if (thumb_task.isSuccessful())
                        {
                            thumbDownloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    userReference.child(currentUserId).child("profileImage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        // Toast.makeText(HomeActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(HomeActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(HomeActivity.this, "Error: Image not crop.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, HomeActivity.class);
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