package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Adapter.MessagesAdapter;
import Model.Messages;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar chatToolBar;
    private ImageView sendMessagebtn, ImageSendView, ImageDelete, VideoCallBtn;
    private EditText userMessageInputs;
    private RecyclerView userMessageList;
    private final List<Messages> messagesList = new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    String messageReciverID, messageReciverName, messageSenderID,saveCurrentDate,saveCurrentTime, type,typeCondition, From, typeFrom,Image1DownloadUrl;
    private LinearLayout linearLayout1, linearLayout2,linearLayout3,linearLayout4,linearLayout5;
    Uri uri1;
    private ProgressDialog loadingBar;
    private RelativeLayout relativeLayout;
    private int message = 0;
    public static final int Gallery_pick = 1;
    TextView reciverName, userLastSeen, DownloadUrl1,DownloadUrl;
    private CircleImageView reciverProfileimage;
    private DatabaseReference RootReff,UserReff, reference,reference2,RootRef, PageReference;
    private FirebaseAuth mAuth;
    ValueEventListener seenListener;
    StorageReference postImagesReff;
    ConstraintLayout constraintLayout;
    RelativeLayout ImojiListLayout;
    ImageView imojiListDelete,imoji1,imoji2,imoji3,imoji4,imoji5,imoji6,imoji7,imoji8,imoji9,imoji10,imoji11,imoji12,imoji13,imoji14,imoji15,imoji16;
    ImageView AppBarIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mAuth = FirebaseAuth.getInstance();
        messageSenderID = getIntent().getExtras().get("userId").toString();

        RootReff = FirebaseDatabase.getInstance().getReference();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages");
        postImagesReff = FirebaseStorage.getInstance().getReference();
        messageReciverID = getIntent().getExtras().get("visit_user_id").toString();
        type = getIntent().getExtras().get("type").toString();
        From = getIntent().getExtras().get("from").toString();

        updateUserStatus("online");




        IntializeFields();




        DisplayReceiverInfo();
        typeCondition = "user";
        typeFrom = From;

        sendMessagebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (userMessageInputs.getText().toString().isEmpty())
                {

                }else
                {
                    DownloadUrl.setText(Image1DownloadUrl);
                    SendMessage();
                }

            }
        });



        RootRef.child("All Users").child(messageReciverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String type = dataSnapshot.child("userState").child("type").getValue().toString();
                    final String lastDate = dataSnapshot.child("userState").child("date").getValue().toString();
                    final String lastTime = dataSnapshot.child("userState").child("time").getValue().toString();

                    if (dataSnapshot.hasChild("VisitId"))
                    {

                        String visitor = dataSnapshot.child("VisitId").getValue().toString();

                        if (visitor.equals(messageSenderID))
                        {
                            userLastSeen.setText("Typing...");

                        }
                    }else if (type.equals("online"))
                    {
                        userLastSeen.setText("online");

                    }
                    else
                    {
                        userLastSeen.setText("last seen: " + lastTime + " " + lastDate);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        FetchMessages();
        //seenMessage();

    }


    private void FetchMessages()
    {
        RootReff.child("Messages").child(messageReciverID).child(messageSenderID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {
                        if (dataSnapshot.exists())
                        {
                            Messages messages = dataSnapshot.getValue(Messages.class);

                            messagesList.add(messages);
                            //MessagesAdapter messagesAdapter = new MessagesAdapter(messagesList);
                            messagesAdapter.notifyDataSetChanged();
                            userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());

                            if (messages.getFrom().equals(messageSenderID)){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("seenType", "true");
                                dataSnapshot.getRef().updateChildren(hashMap);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void SendMessage()
    {



        constraintLayout.setVisibility(View.GONE);
        final String messageText = userMessageInputs.getText().toString();

        final String message_reciver_reff = "Messages/" + messageReciverID + "/" + messageSenderID;
        final String message_sender_reff = "Messages/" + messageSenderID + "/" + messageReciverID;


        DatabaseReference user_message_key = RootReff.child("Messages").child(messageSenderID).child(messageReciverID).child(messageReciverID).push();

        String message_pust_id = user_message_key.getKey();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final String downloadurl = DownloadUrl.getText().toString();
        final String Type = type;
        Map messageTextBody = new HashMap();
        messageTextBody.put("message", messageText);
        messageTextBody.put("time", saveCurrentTime);
        messageTextBody.put("date", saveCurrentDate);
        messageTextBody.put("type", typeCondition);
        messageTextBody.put("seenType", "false");
        messageTextBody.put("fileUrl",downloadurl);
        messageTextBody.put("from", messageSenderID);

        Map messageTextBody2 = new HashMap();
        messageTextBody2.put("message", messageText);
        messageTextBody2.put("time", saveCurrentTime);
        messageTextBody2.put("date", saveCurrentDate);
        messageTextBody2.put("type", typeFrom);
        messageTextBody2.put("seenType", "false");
        messageTextBody2.put("fileUrl",downloadurl);
        messageTextBody2.put("from", messageSenderID);

        UserReff.child(messageSenderID).child("VisitId").removeValue();

        Map messageBodyDetails = new HashMap();


        messageBodyDetails.put(message_reciver_reff + "/" + message_pust_id , messageTextBody);
        messageBodyDetails.put(message_sender_reff + "/" + message_pust_id , messageTextBody2);

        RootReff.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful()){
                    userMessageInputs.setText("");
                    DownloadUrl.setText("");

                    Map sendingMap = new HashMap();
                    sendingMap.put("message", messageText);
                    sendingMap.put("time", saveCurrentTime);
                    sendingMap.put("date", saveCurrentDate);
                    sendingMap.put("condition", "true");
                    sendingMap.put("type", typeCondition);

                    RootReff.child("messages").child(messageSenderID).child(messageReciverID).updateChildren(sendingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                Map reciverMap = new HashMap();
                                reciverMap.put("message", messageText);
                                reciverMap.put("time", saveCurrentTime);
                                reciverMap.put("date", saveCurrentDate);
                                reciverMap.put("condition", "false");
                                reciverMap.put("type", typeFrom);
                                RootReff.child("messages").child(messageReciverID).child(messageSenderID).updateChildren(reciverMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {

                                            if (Type.equals("user"))
                                            {
                                                Map notificationMap = new HashMap();
                                                notificationMap.put("time",saveCurrentTime);
                                                notificationMap.put("message", messageText);
                                                notificationMap.put("type", Type);

                                                RootReff.child("All Users").child(messageReciverID).child("MessageNotification").child(messageReciverID).child(messageSenderID).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            //Toast.makeText(ChatActivity.this,"Message send successfully...",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                            else if (Type.equals("page"))
                                            {
                                                Map notificationMap = new HashMap();
                                                notificationMap.put("time",saveCurrentTime);
                                                notificationMap.put("message", messageText);
                                                notificationMap.put("type", Type);

                                                RootReff.child("All Pages").child(messageReciverID).child("MessageNotification").child(messageReciverID).child(messageSenderID).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            //Toast.makeText(ChatActivity.this,"Message send successfully...",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }

                                        }
                                        else
                                        {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(ChatActivity.this,"Error:"+message,Toast.LENGTH_SHORT).show();
                                            userMessageInputs.setText("");
                                        }
                                    }
                                });
                            }

                        }
                    });
                }

            }
        });

    }

    private void SendImoji()
    {



        final String message_reciver_reff = "Messages/" + messageReciverID + "/" + messageSenderID;
        final String message_sender_reff = "Messages/" + messageSenderID + "/" + messageReciverID;


        DatabaseReference user_message_key = RootReff.child("Messages").child(messageSenderID).child(messageReciverID).child(messageReciverID).push();

        String message_pust_id = user_message_key.getKey();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final String downloadurl1 = DownloadUrl1.getText().toString();
        final String Type = type;
        Map messageTextBody = new HashMap();
        messageTextBody.put("message", "");
        messageTextBody.put("time", saveCurrentTime);
        messageTextBody.put("date", saveCurrentDate);
        messageTextBody.put("type", typeCondition);
        messageTextBody.put("seenType", "false");
        messageTextBody.put("fileUrl",downloadurl1);
        messageTextBody.put("from", messageSenderID);

        Map messageTextBody2 = new HashMap();
        messageTextBody2.put("message", "");
        messageTextBody2.put("time", saveCurrentTime);
        messageTextBody2.put("date", saveCurrentDate);
        messageTextBody2.put("type", typeFrom);
        messageTextBody2.put("seenType", "false");
        messageTextBody2.put("fileUrl",downloadurl1);
        messageTextBody2.put("from", messageSenderID);

        UserReff.child(messageSenderID).child("VisitId").removeValue();

        Map messageBodyDetails = new HashMap();


        messageBodyDetails.put(message_reciver_reff + "/" + message_pust_id , messageTextBody);
        messageBodyDetails.put(message_sender_reff + "/" + message_pust_id , messageTextBody2);

        RootReff.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful()){
                    DownloadUrl1.setText("");

                    Map sendingMap = new HashMap();
                    sendingMap.put("message", "Imoji");
                    sendingMap.put("time", saveCurrentTime);
                    sendingMap.put("date", saveCurrentDate);
                    sendingMap.put("condition", "true");
                    sendingMap.put("type", typeCondition);

                    RootReff.child("messages").child(messageSenderID).child(messageReciverID).updateChildren(sendingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                Map reciverMap = new HashMap();
                                reciverMap.put("message", "Imoji");
                                reciverMap.put("time", saveCurrentTime);
                                reciverMap.put("date", saveCurrentDate);
                                reciverMap.put("condition", "false");
                                reciverMap.put("type", typeFrom);
                                RootReff.child("messages").child(messageReciverID).child(messageSenderID).updateChildren(reciverMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {

                                            if (Type.equals("user"))
                                            {
                                                Map notificationMap = new HashMap();
                                                notificationMap.put("time",saveCurrentTime);
                                                notificationMap.put("message", "imoji");
                                                notificationMap.put("type", Type);

                                                RootReff.child("All Users").child(messageReciverID).child("MessageNotification").child(messageReciverID).child(messageSenderID).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            //Toast.makeText(ChatActivity.this,"Message send successfully...",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                            else if (Type.equals("page"))
                                            {
                                                Map notificationMap = new HashMap();
                                                notificationMap.put("time",saveCurrentTime);
                                                notificationMap.put("message", "imoji");
                                                notificationMap.put("type", Type);

                                                RootReff.child("All Pages").child(messageReciverID).child("MessageNotification").child(messageReciverID).child(messageSenderID).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            //Toast.makeText(ChatActivity.this,"Message send successfully...",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }

                                        }
                                        else
                                        {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(ChatActivity.this,"Error:"+message,Toast.LENGTH_SHORT).show();
                                            userMessageInputs.setText("");
                                        }
                                    }
                                });
                            }

                        }
                    });
                }

            }
        });

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

        UserReff.child(messageSenderID).child("userState")
                .updateChildren(CurrentStatemap);
    }

    private void DisplayReceiverInfo()
    {


        RootReff.child("All Users").child(messageReciverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                    final String name = dataSnapshot.child("fullName").getValue().toString();

                    reciverName.setText(name);


                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(reciverProfileimage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void IntializeFields() {

        chatToolBar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);

        AppBarIcon = findViewById(R.id.chat_app_bar_icon);
        AppBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        reciverName = findViewById(R.id.custom_profile_name);
        userLastSeen = findViewById(R.id.custom_user_last_seen);
        reciverProfileimage = findViewById(R.id.custom_profile_image);

        loadingBar = new ProgressDialog(this);
        constraintLayout = findViewById(R.id.image_constraint_layout);
        ImageDelete = findViewById(R.id.image_delete);
        //StarBtn = findViewById(R.id.send_star_icon);
        DownloadUrl1 = findViewById(R.id.download_url1);
        DownloadUrl = findViewById(R.id.download_url);
        ImageSendView = findViewById(R.id.message_image_view);
        VideoCallBtn = findViewById(R.id.video_call_btn);

        ImojiListLayout = findViewById(R.id.emoji_relative_layout);

        relativeLayout = findViewById(R.id.chat_relative_layout);
        linearLayout1 = findViewById(R.id.chat_linerLayout1);
        linearLayout2 = findViewById(R.id.chat_linerLayout2);
        linearLayout3 = findViewById(R.id.chat_linerLayout3);
        linearLayout4 = findViewById(R.id.chat_linerLayout5);
        linearLayout5 = findViewById(R.id.chat_linerLayout4);
       // imojiListDelete = findViewById(R.id.imoji_list_delete);
        imoji1 = findViewById(R.id.imoji1);
        imoji2 = findViewById(R.id.imoji2);
        imoji3 = findViewById(R.id.imoji3);
        imoji4 = findViewById(R.id.imoji4);
        imoji5 = findViewById(R.id.imoji5);
        imoji6 = findViewById(R.id.imoji6);
        imoji7 = findViewById(R.id.imoji7);
        imoji8 = findViewById(R.id.imoji8);
        imoji9 = findViewById(R.id.imoji9);
        imoji10 = findViewById(R.id.imoji10);
        imoji11 = findViewById(R.id.imoji11);
        imoji12 = findViewById(R.id.imoji12);
        imoji13 = findViewById(R.id.imoji13);
        imoji14 = findViewById(R.id.imoji14);
        imoji15 = findViewById(R.id.imoji15);
        imoji16 = findViewById(R.id.imoji16);


        messagesAdapter = new MessagesAdapter(messagesList);
        userMessageList = findViewById(R.id.messages_list_of_users);
        userMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messagesAdapter);


        sendMessagebtn = findViewById(R.id.send_message_btn);
        userMessageInputs = findViewById(R.id.message_text);



        userMessageInputs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        linearLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.VISIBLE);
                linearLayout3.setVisibility(View.VISIBLE);
                linearLayout4.setVisibility(View.GONE);
                relativeLayout.setBackgroundResource(R.drawable.message_text_box_background);
            }
        });

        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_pick);
            }
        });

        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImojiListLayout.setVisibility(View.VISIBLE);
                linearLayout3.setVisibility(View.GONE);
                linearLayout5.setVisibility(View.VISIBLE);


            }
        });
        linearLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImojiListLayout.setVisibility(View.GONE);
                linearLayout3.setVisibility(View.VISIBLE);
                linearLayout5.setVisibility(View.GONE);


            }
        });


        ImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                postImagesReff.child("User Images").child(messageSenderID).child("MessageSendImage").child(uri1.getLastPathSegment()+ ".jpg").delete();
                DownloadUrl.setText("");
                constraintLayout.setVisibility(View.GONE);
                if (userMessageInputs.getText().toString().equals(""))
                {
                   // relativeLayout.setBackgroundResource(R.drawable.post_btn_background);
                    linearLayout1.setVisibility(View.VISIBLE);
                    linearLayout2.setVisibility(View.VISIBLE);
                    linearLayout3.setVisibility(View.VISIBLE);
                    linearLayout4.setVisibility(View.GONE);
                    sendMessagebtn.setVisibility(View.GONE);
                }
                else
                {
                    //relativeLayout.setBackgroundResource(R.drawable.registration_input_background);
                    linearLayout1.setVisibility(View.GONE);
                    linearLayout2.setVisibility(View.GONE);
                    linearLayout3.setVisibility(View.GONE);
                    linearLayout4.setVisibility(View.VISIBLE);
                }
            }
        });

        imoji1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji1.png?alt=media&token=c5ca89e6-c331-47a6-8ed3-15c09154327b");
                SendImoji();
            }
        });

        imoji2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji2.png?alt=media&token=22d36b20-9711-47d2-9b5e-e9ad934e7c5d");
                SendImoji();
            }
        });

        imoji3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji3.png?alt=media&token=381e5637-9c05-4440-bcfb-277f37fc2d6c");
                SendImoji();
            }
        });

        imoji4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji4.png?alt=media&token=eae872d9-5fe6-4f15-b23c-14bdf646f56b");
                SendImoji();
            }
        });

        imoji5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji5.png?alt=media&token=c5153165-e6b8-4258-a347-41887a86ef32");
                SendImoji();
            }
        });

        imoji6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji6.png?alt=media&token=d36023f8-443b-46d7-b489-89ce296699cb");
                SendImoji();
            }
        });

        imoji7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji7.png?alt=media&token=72dd3ba3-3c73-4756-afa2-c44f83b73b0b");
                SendImoji();
            }
        });

        imoji8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji8.png?alt=media&token=1f19181e-05b5-47c3-b163-42fa6a587b33");
                SendImoji();
            }
        });

        imoji9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji9.png?alt=media&token=c2a01677-7ae6-4cac-a849-a4930686d74b");
                SendImoji();
            }
        });

        imoji10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji10.png?alt=media&token=2939c363-0d5c-4e94-a3bb-722121b2fa6b");
                SendImoji();
            }
        });

        imoji11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji11.png?alt=media&token=1920a583-c70e-4f32-99aa-77acb759e30e");
                SendImoji();
            }
        });

        imoji12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji12.png?alt=media&token=b185deb0-8bb8-4b5b-9065-bdda0bd5614e");
                SendImoji();
            }
        });

        imoji13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji13.png?alt=media&token=b908feb5-eefb-4c1a-89e6-614ecf8a90da");
                SendImoji();
            }
        });

        imoji14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji14.png?alt=media&token=b1986899-ef9f-49bf-b361-85e63c04aab9");
                SendImoji();
            }
        });

        imoji15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji15.png?alt=media&token=b6ecf407-2a98-4ea2-ae68-dc053b729f46");
                SendImoji();
            }
        });

        imoji16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/utalk-ca307.appspot.com/o/UTALK%20imoji%2Femoji16.png?alt=media&token=075970bf-1071-4877-a0dc-0c5b08d103fe");
                SendImoji();
            }
        });




    }

    private void checkInputs()
    {
        if (!TextUtils.isEmpty(userMessageInputs.getText().toString()))
        {
            UserReff.child(messageSenderID).child("VisitId").setValue(messageReciverID);
            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.GONE);
            linearLayout3.setVisibility(View.GONE);
            ImojiListLayout.setVisibility(View.GONE);
            sendMessagebtn.setVisibility(View.VISIBLE);
            relativeLayout.setBackgroundResource(R.drawable.message_text_box_bacgrount_white);
            linearLayout4.setVisibility(View.VISIBLE);
            linearLayout5.setVisibility(View.GONE);


        }else
        {
            UserReff.child(messageSenderID).child("VisitId").removeValue();



            //relativeLayout2.setBackground(Drawable.createFromPath(String.valueOf(R.drawable.post_btn_background)));
            relativeLayout.setBackgroundResource(R.drawable.message_text_box_background);

            sendMessagebtn.setVisibility(View.GONE);
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            linearLayout3.setVisibility(View.VISIBLE);
            linearLayout4.setVisibility(View.GONE);
            linearLayout5.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == Gallery_pick && resultCode == RESULT_OK)
        {
            final Uri imageUri = data.getData();
            if (imageUri!=null) {

                loadingBar.setTitle("Image Load");
                loadingBar.setMessage("Load your selected image..");
                loadingBar.show();
                relativeLayout.setBackgroundResource(R.drawable.border);
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.GONE);
                linearLayout3.setVisibility(View.GONE);
                linearLayout4.setVisibility(View.GONE);

                final File file1 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,imageUri),new File(this.getCacheDir(),"temp1")));
                final Uri uri11 = Uri.fromFile(file1);



                //StorageReference filePath = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(imageUri.getLastPathSegment()+ ".jpg");                         //loadingBar.setTitle("Profile Image");
                StorageReference filePath1 = postImagesReff.child("User Images").child(messageSenderID).child("MessageSendImage").child(imageUri.getLastPathSegment()+ ".jpg");
                filePath1.putFile(uri11).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {


                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (task.isSuccessful())
                                    {
                                        Image1DownloadUrl = uri.toString();
                                        uri1 = imageUri;
                                        sendMessagebtn.setVisibility(View.VISIBLE);
                                        DownloadUrl.setText(Image1DownloadUrl);
                                        constraintLayout.setVisibility(View.VISIBLE);
                                        Picasso.get().load(Image1DownloadUrl).placeholder(R.drawable.blank_cover_image).into(ImageSendView);

                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                    }


                                }
                            });

                        }
                        file1.delete();
                    }
                });

            }



        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}