package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.Objects;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;


public class CallListActivity extends AppCompatActivity {

    Toolbar mToolBar;
    RecyclerView allMessageFriends;
    DatabaseReference FriendsReference, UserReff;
    String currentUserId;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    ImageView AppBarIcon;
    TextView AppBarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_list);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        FriendsReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("Message Friends");
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");


        mToolBar = findViewById(R.id.call_list_ap_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");

        AppBarIcon = findViewById(R.id.call_friend_app_bar_icon);
        AppBarTitle = findViewById(R.id.call_friend_app_bar_title);

        AppBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        AppBarTitle.setText("Chat Friends");

        allMessageFriends = findViewById(R.id.all_messages_friends);
        allMessageFriends.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        allMessageFriends.setLayoutManager(linearLayoutManager);

        DisplayAllUser();




    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void DisplayAllUser()
    {
        Query query = FriendsReference.orderByChild("permission");
        // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User,AllUserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CallListActivity.AllUserViewHolder allUserViewHolder, final int position, @NonNull final User user) {

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

                           allUserViewHolder.AudioCall.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v)
                               {
                                   Intent chatIntent = new Intent(CallListActivity.this,ChatActivity.class);
                                   chatIntent.putExtra("visit_user_id",userId);
                                   chatIntent.putExtra("userId",currentUserId);
                                   chatIntent.putExtra("userName",name);
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

                allUserViewHolder.VideoCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Toast.makeText(CallListActivity.this,"Coming Soon",Toast.LENGTH_SHORT).show();
                    }
                });



            }


            public AllUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_message_friends, parent ,false);
                return new AllUserViewHolder(view);
            }
        };
        adapter.startListening();
        allMessageFriends.setAdapter(adapter);

    }

    public static class AllUserViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView VideoCall, AudioCall;



        AllUserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            VideoCall = itemView.findViewById(R.id.video_call_btn);
            AudioCall = itemView.findViewById(R.id.call_naw_btn);


        }

        public void setProfileImage(String profileImage) {
            CircleImageView images = mView.findViewById(R.id.message_friend_profile_image);
            Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(images);
        }

        public void setFullName(String fullName){
            TextView myName = mView.findViewById(R.id.message_friend_name);
            myName.setText(fullName);
        }


    }

}