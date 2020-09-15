package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import Model.Channel;
import Model.Group;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAndChannel_activity extends AppCompatActivity {

    Toolbar mToolBar;
    RelativeLayout GroupRelativeLayout, ChannelRelativeLayout;
    TextView CreateGroupBtn, AllGroupBtn, MyGroupBtn, CreateChannelBtn, AllChannelBtn, MyChannelBtn;
    RecyclerView MyGroupList, MyChannelList;
    TextView  groupText, channelText;
    ImageView GroupOrChannelSearchIcon;
    DatabaseReference GroupReference, ChannelReference;
    FirebaseAuth mAuth;
    String currentUserId, Type;
    ImageView AppBarIcon;
    TextView AppBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_and_channel_activity);

        Type = getIntent().getExtras().get("type").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        mToolBar = findViewById(R.id.group_app_bar);
        setSupportActionBar(mToolBar);
        AppBarIcon = findViewById(R.id.group_channel_app_bar_icon);
        AppBarTitle = findViewById(R.id.group_channel_app_bar_title);
        getSupportActionBar().setTitle("");


        AppBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        GroupRelativeLayout = findViewById(R.id.group_relative_layout);
        CreateGroupBtn = findViewById(R.id.create_group);
        AllGroupBtn = findViewById(R.id.all_group);
        MyGroupBtn = findViewById(R.id.your_group);
        groupText = findViewById(R.id.group_text);

        ChannelRelativeLayout = findViewById(R.id.channel_relative_layout);
        CreateChannelBtn = findViewById(R.id.create_channel);
        AllChannelBtn = findViewById(R.id.all_channel);
        MyChannelBtn = findViewById(R.id.your_channel);
        channelText = findViewById(R.id.channel_text);

        GroupOrChannelSearchIcon = findViewById(R.id.group_and_channel_search);



        CreateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAndChannel_activity.this,CreateGroupAndChannelActivity.class);
                intent.putExtra("title","Create Group");
                startActivity(intent);
            }
        });

        CreateChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAndChannel_activity.this,CreateGroupAndChannelActivity.class);
                intent.putExtra("title","Create Channel");
                startActivity(intent);
            }
        });

        AllGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAndChannel_activity.this,CreateGroupAndChannelActivity.class);
                intent.putExtra("title","All Group");
                startActivity(intent);
            }
        });

        AllChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAndChannel_activity.this,CreateGroupAndChannelActivity.class);
                intent.putExtra("title","All Channel");
                startActivity(intent);
            }
        });

        MyGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAndChannel_activity.this,CreateGroupAndChannelActivity.class);
                intent.putExtra("title","My Group");
                startActivity(intent);
            }
        });

        MyChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAndChannel_activity.this,CreateGroupAndChannelActivity.class);
                intent.putExtra("title","My Channel");
                startActivity(intent);
            }
        });

        if (Type.equals("Group"))
        {
            GroupRelativeLayout.setVisibility(View.VISIBLE);
            AppBarTitle.setText(Type);
            MyGroupList = findViewById(R.id.all_group_list);

            GroupReference = FirebaseDatabase.getInstance().getReference().child("All Group");

            GroupOrChannelSearchIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GroupAndChannel_activity.this,MessageRequestActivity.class);
                    intent.putExtra("title","Group");
                    startActivity(intent);
                }
            });

            MyGroupList.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            MyGroupList.setLayoutManager(linearLayoutManager);

            DisplayMyGroup();

        }
        else if (Type.equals("Channel"))
        {

            ChannelRelativeLayout.setVisibility(View.VISIBLE);
            AppBarTitle.setText(Type);
            MyChannelList = findViewById(R.id.all_channel_list);

            ChannelReference = FirebaseDatabase.getInstance().getReference().child("All Channel");

              GroupOrChannelSearchIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GroupAndChannel_activity.this,MessageRequestActivity.class);
                    intent.putExtra("title","Channel");
                    startActivity(intent);
                }
            });

            MyChannelList.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            MyChannelList.setLayoutManager(linearLayoutManager);

            DisplayMyChannel();

        }

    }

    private void DisplayMyChannel()
    {
        Query query = ChannelReference.orderByChild("admin1")
                .startAt(currentUserId).endAt(currentUserId + " \uf8ff");// haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Channel> options = new FirebaseRecyclerOptions.Builder<Channel>().setQuery(query, Channel.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Channel, GroupAndChannel_activity.ChannelViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final GroupAndChannel_activity.ChannelViewHolder channelViewHolder, final int position, @NonNull final Channel channel) {

                final String channelId = getRef(position).getKey();

                channelText.setVisibility(View.VISIBLE);

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
                    channelViewHolder.date.setText("Channel Since "+channel.getDate());
                }

                channelViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent chatIntent = new Intent(GroupAndChannel_activity.this,ChannelChatActivity.class);
                        chatIntent.putExtra("visit_user_id", channelId);
                        chatIntent.putExtra("userId",currentUserId);
                        startActivity(chatIntent);
                        finish();
                    }
                });




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
        Query query = GroupReference.orderByChild("admin1")
                .startAt(currentUserId).endAt(currentUserId + " \uf8ff");// haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Group> options = new FirebaseRecyclerOptions.Builder<Group>().setQuery(query, Group.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Group, GroupAndChannel_activity.GroupViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final GroupAndChannel_activity.GroupViewHolder groupViewHolder, final int position, @NonNull final Group group) {

                final String groupId = getRef(position).getKey();

                groupText.setVisibility(View.VISIBLE);

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
                    groupViewHolder.date.setText("Group Since "+group.getDate());
                }

                groupViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent chatIntent = new Intent(GroupAndChannel_activity.this, GroupChatActivity.class);
                        chatIntent.putExtra("visit_user_id", groupId);
                        chatIntent.putExtra("userId",currentUserId);
                        startActivity(chatIntent);
                        finish();
                    }
                });


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
}