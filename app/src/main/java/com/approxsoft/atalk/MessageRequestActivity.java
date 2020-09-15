package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import Model.Channel;
import Model.Group;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageRequestActivity extends AppCompatActivity {

    EditText searchBox;
    ImageView SearchBtn;
    RecyclerView userList, groupList, channelList;
    FirebaseAuth mAuth;
    String currentUserId, Type;
    DatabaseReference UserReference, GroupReference, ChannelReference, UserReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_request);

        Type = getIntent().getExtras().get("title").toString();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        UserReference = FirebaseDatabase.getInstance().getReference().child("All Users");


        searchBox = findViewById(R.id.find_friends_search);
        SearchBtn = findViewById(R.id.find_friends_btn);

        userList = findViewById(R.id.search_user_list);
        groupList = findViewById(R.id.search_group_list);
        channelList = findViewById(R.id.search_channel_list);

        switch (Type) {
            case "User": {
                userList.setVisibility(View.VISIBLE);

                UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");

                userList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                userList.setLayoutManager(linearLayoutManager);

                searchBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        SearchBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String searchBoxInputs = searchBox.getText().toString();
                                SearchAllUser(searchBoxInputs);
                            }
                        });
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                break;
            }
            case "Group": {
                groupList.setVisibility(View.VISIBLE);

                GroupReference = FirebaseDatabase.getInstance().getReference().child("All Group");

                groupList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                groupList.setLayoutManager(linearLayoutManager);

                searchBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        SearchBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String searchBoxInputs = searchBox.getText().toString();
                                SearchAllGroup(searchBoxInputs);
                            }
                        });
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                break;
            }
            case "Channel": {
                channelList.setVisibility(View.VISIBLE);

                ChannelReference = FirebaseDatabase.getInstance().getReference().child("All Channel");

                channelList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                channelList.setLayoutManager(linearLayoutManager);

                searchBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        SearchBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String searchBoxInputs = searchBox.getText().toString();
                                SearchAllChannel(searchBoxInputs);
                            }
                        });
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                break;
            }
        }

    }

    private void SearchAllUser(String searchBoxInputs)
    {
        Query query = UserReff.orderByChild("fullName")
                .startAt(searchBoxInputs).endAt(searchBoxInputs + "\uf8ff");// haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User, MessageRequestActivity.AllUserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MessageRequestActivity.AllUserViewHolder allUserViewHolder, final int position, @NonNull final User user) {

                final String userId = getRef(position).getKey();

                allUserViewHolder.userName.setText(user.getFullName());
                Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.profile_icon).into(allUserViewHolder.userImage);

                allUserViewHolder.AddMessageRequestBtn.setVisibility(View.GONE);
                allUserViewHolder.CancelMessageRequestBtn.setVisibility(View.GONE);
                allUserViewHolder.AcceptMessageRequestBtn.setVisibility(View.GONE);
                allUserViewHolder.DeleteMessageRequestBtn.setVisibility(View.GONE);
                allUserViewHolder.UnfriendBtn.setVisibility(View.GONE);

                if (userId.equals(currentUserId))
                {
                    allUserViewHolder.userLayout.setVisibility(View.GONE);
                }
                else
                {
                    allUserViewHolder.userLayout.setVisibility(View.VISIBLE);
                }



                UserReference.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("Message Friends").hasChild(currentUserId))
                        {

                            allUserViewHolder.AddMessageRequestBtn.setVisibility(View.GONE);
                            allUserViewHolder.CancelMessageRequestBtn.setVisibility(View.GONE);
                            allUserViewHolder.AcceptMessageRequestBtn.setVisibility(View.GONE);
                            allUserViewHolder.DeleteMessageRequestBtn.setVisibility(View.GONE);
                            allUserViewHolder.UnfriendBtn.setVisibility(View.VISIBLE);


                        }

                        else

                        if (dataSnapshot.child("Message Request").hasChild(currentUserId))
                        {


                            if (dataSnapshot.child("Message Request").child(currentUserId).child("type").getValue().equals("sender"))
                            {


                                allUserViewHolder.AddMessageRequestBtn.setVisibility(View.GONE);
                                allUserViewHolder.CancelMessageRequestBtn.setVisibility(View.GONE);
                                allUserViewHolder.AcceptMessageRequestBtn.setVisibility(View.VISIBLE);
                                allUserViewHolder.DeleteMessageRequestBtn.setVisibility(View.VISIBLE);
                                allUserViewHolder.UnfriendBtn.setVisibility(View.GONE);




                            }
                            else if (dataSnapshot.child("Message Request").child(currentUserId).child("type").getValue().equals("receiver"))
                            {
                                allUserViewHolder.AddMessageRequestBtn.setVisibility(View.GONE);
                                allUserViewHolder.AcceptMessageRequestBtn.setVisibility(View.GONE);
                                allUserViewHolder.DeleteMessageRequestBtn.setVisibility(View.GONE);
                                allUserViewHolder.UnfriendBtn.setVisibility(View.GONE);
                                allUserViewHolder.CancelMessageRequestBtn.setVisibility(View.VISIBLE);


                            }
                        }
                        else {
                            allUserViewHolder.AddMessageRequestBtn.setVisibility(View.VISIBLE);
                            allUserViewHolder.CancelMessageRequestBtn.setVisibility(View.GONE);
                            allUserViewHolder.AcceptMessageRequestBtn.setVisibility(View.GONE);
                            allUserViewHolder.DeleteMessageRequestBtn.setVisibility(View.GONE);
                            allUserViewHolder.UnfriendBtn.setVisibility(View.GONE);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                allUserViewHolder.AddMessageRequestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        UserReff.child(currentUserId).child("Message Request").child(userId).child("type").setValue("sender").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    UserReff.child(userId).child("Message Request").child(currentUserId).child("type").setValue("receiver").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                allUserViewHolder.AddMessageRequestBtn.setVisibility(View.GONE);
                                                allUserViewHolder.CancelMessageRequestBtn.setVisibility(View.VISIBLE);

                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });


                allUserViewHolder.CancelMessageRequestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        UserReff.child(userId).child("Message Request").child(currentUserId).child("type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {

                                    UserReff.child(currentUserId).child("Message Request").child(userId).child("type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {

                                                allUserViewHolder.AddMessageRequestBtn.setVisibility(View.VISIBLE);
                                                allUserViewHolder.CancelMessageRequestBtn.setVisibility(View.GONE);
                                            }

                                        }
                                    });
                                }
                            }
                        });



                    }
                });

                allUserViewHolder.AcceptMessageRequestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {


                        Task<Void> voidTask = UserReff.child(currentUserId).child("Message Friends").child(userId).child("permission").setValue("true");

                        UserReff.child(userId).child("Message Friends").child(currentUserId).child("permission").setValue("true");

                        UserReff.child(currentUserId).child("Message Request").child(userId).removeValue();

                        UserReff.child(userId).child("Message Request").child(currentUserId).removeValue();

                        allUserViewHolder.AcceptMessageRequestBtn.setVisibility(View.GONE);
                        allUserViewHolder.DeleteMessageRequestBtn.setVisibility(View.GONE);
                        allUserViewHolder.UnfriendBtn.setVisibility(View.VISIBLE);


                    }
                });

                allUserViewHolder.UnfriendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        UserReff.child(currentUserId).child("Message Friends").child(userId).removeValue();
                        UserReff.child(userId).child("Message Friends").child(currentUserId).removeValue();

                        allUserViewHolder.AddMessageRequestBtn.setVisibility(View.VISIBLE);
                        allUserViewHolder.CancelMessageRequestBtn.setVisibility(View.GONE);
                        allUserViewHolder.UnfriendBtn.setVisibility(View.GONE);


                    }
                });


                allUserViewHolder.DeleteMessageRequestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        UserReff.child(currentUserId).child("Message Request").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    UserReff.child(userId).child("Message Request").child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                allUserViewHolder.AddMessageRequestBtn.setVisibility(View.VISIBLE);
                                                allUserViewHolder.CancelMessageRequestBtn.setVisibility(View.GONE);
                                                allUserViewHolder.AcceptMessageRequestBtn.setVisibility(View.GONE);
                                                allUserViewHolder.DeleteMessageRequestBtn.setVisibility(View.GONE);
                                                allUserViewHolder.UnfriendBtn.setVisibility(View.GONE);
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_user_layout, parent ,false);
                return new AllUserViewHolder(view);
            }
        };
        adapter.startListening();
        userList.setAdapter(adapter);
    }

    public static class AllUserViewHolder extends RecyclerView.ViewHolder {

        View mView;

        CircleImageView userImage;
        TextView userName;
        TextView AddMessageRequestBtn, CancelMessageRequestBtn, AcceptMessageRequestBtn, DeleteMessageRequestBtn, UnfriendBtn;
        LinearLayout userLayout;

        AllUserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            userImage = itemView.findViewById(R.id.search_user_profile_image);
            userName = itemView.findViewById(R.id.search_user_name);
            AddMessageRequestBtn = itemView.findViewById(R.id.add_request_btn);
            CancelMessageRequestBtn = itemView.findViewById(R.id.cancel_request_btn);
            AcceptMessageRequestBtn = itemView.findViewById(R.id.accept_chat_request);
            DeleteMessageRequestBtn = itemView.findViewById(R.id.decline_chat_request);
            UnfriendBtn = itemView.findViewById(R.id.un_friend_user);
            userLayout = itemView.findViewById(R.id.linear_layout);


        }


    }

    private void SearchAllChannel(String searchBoxInputs)
    {

        Query query = ChannelReference.orderByChild("channelName")
                .startAt(searchBoxInputs).endAt(searchBoxInputs + "\uf8ff");// haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Channel> options = new FirebaseRecyclerOptions.Builder<Channel>().setQuery(query, Channel.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Channel, MessageRequestActivity.AllChannelViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MessageRequestActivity.AllChannelViewHolder channelViewHolder, final int position, @NonNull final Channel channel) {

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
                            channelViewHolder.RemoveChannelBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    ChannelReference.child(channelId).child("Channel Member").child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                                            channelViewHolder.ChannelAddBtn.setVisibility(View.VISIBLE);
                                                            channelViewHolder.RemoveChannelBtn.setVisibility(View.GONE);
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
                            ChannelReference.child(channelId).addListenerForSingleValueEvent(new ValueEventListener() {
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

                                        channelViewHolder.ChannelAddBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                Calendar calForDate = Calendar.getInstance();
                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                final String saveCurrentDate = currentDate.format(calForDate.getTime());

                                                ChannelReference.child(channelId).child("Channel Member").child(currentUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            UserReference.child("All Channel").child(channelId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
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

            public AllChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_search_layout, parent ,false);
                return new AllChannelViewHolder(view);
            }
        };
        adapter.startListening();
        channelList.setAdapter(adapter);
    }

    private void SearchAllGroup(String searchBoxInputs)
    {
        Query query = GroupReference.orderByChild("groupName")
                .startAt(searchBoxInputs).endAt(searchBoxInputs + "\uf8ff");// haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Group> options = new FirebaseRecyclerOptions.Builder<Group>().setQuery(query, Group.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Group, MessageRequestActivity.AllGroupViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MessageRequestActivity.AllGroupViewHolder groupViewHolder, final int position, @NonNull final Group group) {

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
                                                GroupReference.child(groupId).child("Group Member").child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                            GroupReference.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                                GroupReference.child(groupId).child("UserGroupRequest").child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                                GroupReference.child(groupId).child("UserGroupRequest").child(currentUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        groupList.setAdapter(adapter);
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

}