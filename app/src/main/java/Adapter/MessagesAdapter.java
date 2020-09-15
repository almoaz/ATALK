package Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.approxsoft.atalk.R;

import java.util.List;
import java.util.Objects;

import Model.Messages;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    String messageSenderID2 = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String messageSenderID;
    String visitor;

    public MessagesAdapter(List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder

    {
        TextView SenderMessageText, ReceiverMessageText,ReceiverMessageSeen, ReceiverDateAndTime;
        CircleImageView receiverProfileImage, seenIcon;
        ImageView SenderImageView, ReceiverImageView;
        MessageViewHolder(@NonNull View itemView) {

            super(itemView);

            SenderMessageText = itemView.findViewById(R.id.sender_message_text);
            ReceiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.message_profile_image);
            ReceiverMessageSeen = itemView.findViewById(R.id.message_seen_text);
            ReceiverDateAndTime = itemView.findViewById(R.id.receiver_date_and_time);
            SenderImageView = itemView.findViewById(R.id.sender_image_view);
            ReceiverImageView = itemView.findViewById(R.id.receiver_image_view);
            seenIcon = itemView.findViewById(R.id.message_seen_icon);

        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {


        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_of_users, parent,false);
        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position)
    {
        final Messages messages = userMessagesList.get(position);


        final String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();
        String time = messages.getTime();
        String SeenType = messages.getSeenType();
        final String fileUrl = messages.getFileUrl();
        //Boolean fromMessageSeenType = messages.getSeenType();

        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(fromUserID);
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String image = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                    messageSenderID  = dataSnapshot.child("uid").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(holder.receiverProfileImage);
                    Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(holder.seenIcon);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        if (fromMessageType.equals("user"))
        {


            holder.ReceiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImage.setVisibility(View.INVISIBLE);
            holder.SenderMessageText.setVisibility(View.INVISIBLE);
            holder.ReceiverDateAndTime.setVisibility(View.INVISIBLE);
            holder.SenderImageView.setVisibility(View.GONE);
            holder.ReceiverImageView.setVisibility(View.GONE);
            holder.ReceiverMessageSeen.setVisibility(View.GONE);
            //holder.ReciverMessageSeen.setVisibility(View.INVISIBLE);

            if (fromUserID.equals(messageSenderID2))
            {
                if (position == userMessagesList.size()-1)
                {
                    if (SeenType.equals("true"))
                    {
                        //holder.ReceiverMessageSeen.setText("Seen");
                        holder.ReceiverMessageSeen.setVisibility(View.GONE);
                        holder.seenIcon.setVisibility(View.VISIBLE);

                    }else {
                        holder.ReceiverMessageSeen.setText("Delivered");
                        holder.seenIcon.setVisibility(View.GONE);
                    }
                }else {
                    holder.ReceiverMessageSeen.setVisibility(View.INVISIBLE);
                    holder.seenIcon.setVisibility(View.GONE);
                }

                if (messages.getMessage().equals(""))
                {
                    holder.SenderMessageText.setVisibility(View.GONE);

                }else
                {
                    holder.SenderMessageText.setVisibility(View.VISIBLE);
                }

                if (fileUrl.equals(""))
                {
                    holder.SenderImageView.setVisibility(View.GONE);

                }else
                {
                    holder.SenderImageView.setVisibility(View.VISIBLE);
                    holder.ReceiverImageView.setVisibility(View.GONE);
                    Picasso.get().load(fileUrl).placeholder(R.drawable.blank_cover_image).into(holder.SenderImageView);

                }



                holder.SenderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                holder.SenderMessageText.setTextColor(Color.WHITE);
                holder.SenderMessageText.setGravity(Gravity.LEFT);
                holder.SenderMessageText.setText(messages.getMessage());

            }
            else
            {


                if (messages.getMessage().equals(""))
                {
                    holder.ReceiverMessageText.setVisibility(View.GONE);

                }else
                {
                    holder.ReceiverMessageText.setVisibility(View.VISIBLE);
                }

                holder.receiverProfileImage.setVisibility(View.VISIBLE);

                if (position == userMessagesList.size()-4)
                {

                    if (position == userMessagesList.size()-3)
                    {
                        holder.receiverProfileImage.setVisibility(View.INVISIBLE);
                        if (position == userMessagesList.size()-2)
                        {
                            holder.receiverProfileImage.setVisibility(View.INVISIBLE);
                            holder.receiverProfileImage.setVisibility(View.INVISIBLE);
                            if (position == userMessagesList.size()-1)
                            {
                                holder.receiverProfileImage.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }



                if (position == userMessagesList.size()-1)
                {
                    holder.ReceiverMessageText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.ReceiverDateAndTime.setVisibility(View.VISIBLE);
                            holder.ReceiverDateAndTime.setText(messages.getDate() + " " + messages.getTime());

                            holder.ReceiverMessageText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.ReceiverDateAndTime.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });
                }else {
                    holder.ReceiverDateAndTime.setVisibility(View.INVISIBLE);
                }

                if (fileUrl.equals(""))
                {
                    holder.ReceiverImageView.setVisibility(View.GONE);
                }else
                {

                    holder.ReceiverImageView.setVisibility(View.VISIBLE);
                    holder.SenderImageView.setVisibility(View.GONE);
                    Picasso.get().load(fileUrl).placeholder(R.drawable.blank_cover_image).into(holder.ReceiverImageView);
                }




                holder.ReceiverMessageText.setBackgroundResource(R.drawable.reciver_message_text_background);
                holder.ReceiverMessageText.setTextColor(Color.rgb(39,182,182));
                holder.ReceiverMessageText.setGravity(Gravity.LEFT);
                holder.ReceiverMessageText.setText(messages.getMessage());

            }
        }


    }






    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }
}

