package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    Toolbar mToolBar;

    ConstraintLayout nameChangeLayout, passwordChangeLayout, signOutLayout;
    RelativeLayout nameChangeRelativeLayout, passwordChangeRelativeLayout, settingsLayout, termsConditionLayout, aboutLayout, requestLayout;
    EditText firstNameText, lastNameText, oldPasswordText, newPasswordText, newConfirmPasswordText;
    FirebaseAuth mAuth;
    String currentUserId, password, Type;
    Button nameChangeBtn, passwordChangeBtn;
    DatabaseReference userReference,UserReff;
    TextView termsConditionText, aboutText;
    ImageView AppBarIcon;
    TextView AppBarTitle;
    RecyclerView allMessageRequestFriends;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Type = getIntent().getExtras().get("type").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");

        mToolBar = findViewById(R.id.settings_app_bar);

        AppBarIcon = findViewById(R.id.settings_app_bar_icon);
        AppBarTitle = findViewById(R.id.settings_app_bar_title);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");
        AppBarTitle.setText(Type);




        settingsLayout = findViewById(R.id.settings_layout);
        termsConditionLayout = findViewById(R.id.terms_condition_layout);
        termsConditionText = findViewById(R.id.terms_condition_text);



        nameChangeLayout = findViewById(R.id.personal_constraint_layout);
        passwordChangeLayout = findViewById(R.id.security_constraint_layout);
        signOutLayout = findViewById(R.id.sign_out_constraint_layout);

        nameChangeRelativeLayout = findViewById(R.id.name_change_relative_layout);
        passwordChangeRelativeLayout = findViewById(R.id.password_change_relative_layout);


        aboutLayout = findViewById(R.id.about_layout);
        aboutText = findViewById(R.id.about_text);

        requestLayout = findViewById(R.id.find_message_request_layout);
        allMessageRequestFriends = findViewById(R.id.all_request_friends);

        firstNameText = findViewById(R.id.change_first_name);
        lastNameText = findViewById(R.id.change_last_name);
        oldPasswordText = findViewById(R.id.old_password);
        newPasswordText = findViewById(R.id.new_password);
        newConfirmPasswordText = findViewById(R.id.confirm_new_password);
        nameChangeBtn = findViewById(R.id.name_change_btn);
        passwordChangeBtn = findViewById(R.id.password_change_btn);

        switch (Type) {
            case "Settings":
                settingsLayout.setVisibility(View.VISIBLE);

                userReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String firstname = Objects.requireNonNull(dataSnapshot.child("firstName").getValue()).toString();
                            String lastname = Objects.requireNonNull(dataSnapshot.child("lastName").getValue()).toString();
                            password = Objects.requireNonNull(dataSnapshot.child("Password").getValue()).toString();

                            firstNameText.setText(firstname);
                            lastNameText.setText(lastname);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                AppBarIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                nameChangeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nameChangeRelativeLayout.setVisibility(View.VISIBLE);
                        AppBarTitle.setText("Name Changes");

                        AppBarIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nameChangeRelativeLayout.setVisibility(View.GONE);
                                AppBarTitle.setText("Settings");
                            }
                        });

                    }
                });

                nameChangeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (firstNameText.getText().toString().isEmpty() && lastNameText.getText().toString().isEmpty()) {

                        } else {
                            HashMap userMap = new HashMap();
                            userMap.put("firstName", firstNameText.getText().toString());
                            userMap.put("lastName", lastNameText.getText().toString());
                            userMap.put("fullName", firstNameText.getText().toString() + " " + lastNameText.getText().toString());

                            userReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        nameChangeRelativeLayout.setVisibility(View.GONE);
                                        AppBarTitle.setText("Settings");
                                    }
                                }
                            });
                        }
                    }
                });


                passwordChangeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passwordChangeRelativeLayout.setVisibility(View.VISIBLE);
                        AppBarTitle.setText("Password Changes");
                        AppBarIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                passwordChangeRelativeLayout.setVisibility(View.GONE);
                                AppBarTitle.setText("Settings");
                            }
                        });
                    }
                });

                passwordChangeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (oldPasswordText.getText().toString().isEmpty() && newPasswordText.getText().toString().isEmpty() && newConfirmPasswordText.getText().toString().isEmpty()) {

                        } else {
                            if (oldPasswordText.getText().toString().equals(password)) {
                                if (newPasswordText.getText().toString().length() >= 8 && newConfirmPasswordText.getText().toString().matches(newPasswordText.getText().toString())) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.updatePassword(newPasswordText.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        userReference.child("Password").setValue(newPasswordText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    oldPasswordText.setText("");
                                                                    newPasswordText.setText("");
                                                                    newConfirmPasswordText.setText("");

                                                                    CharSequence[] options = new CharSequence[]
                                                                            {
                                                                                    "Sign out",
                                                                                    "Stay sign in"
                                                                            };

                                                                    final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                                                                    builder.setTitle("Select Option");
                                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            if (which == 0) {
                                                                                mAuth.signOut();
                                                                                Intent profileIntent = new Intent(SettingsActivity.this, RegistrationSplashActivity.class);
                                                                                startActivity(profileIntent);
                                                                                finish();
                                                                            }
                                                                            if (which == 1) {

                                                                                passwordChangeRelativeLayout.setVisibility(View.GONE);
                                                                                getSupportActionBar().setTitle("Settings");
                                                                            }
                                                                        }
                                                                    });
                                                                    builder.show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });

                signOutLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAuth.signOut();
                        Intent intent = new Intent(SettingsActivity.this, RegistrationSplashActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });


                break;
            case "Terms and condition":
                termsConditionLayout.setVisibility(View.VISIBLE);
                setTermsConditionText();

                break;
            case "About":
                aboutLayout.setVisibility(View.VISIBLE);
                aboutText.setText("Utalk messenger is africa cloud base instant messaging app that provide secured environments for easy communication. Utalk Messenger is secured, fast and reliable.\n" +
                        "\n" +
                        "With Utalk Messenger you can navigate to different function, share photo, video, files also do audio call.Utalk Messenger do not store your data on our system you can easily access your data anywhere anytime.Utalk is free to use any one can use it.\n" +
                        "\n" +
                        "Any one can use Utalk Messenger, Business users, Big and Small organization.\n" +
                        "Ultak messenger calls are unlimited, no monthly subscription free to use any time.\n" +
                        "\n" +
                        "Utalk Messenger issecured all messeges are encrypted making sure that only recipient of a message is able to descrypte same.\n" +
                        "We do not store or retain any contacts data, we save temporary your offline messages, and once delievered to your contac it will delete same from our system..Utalk messenger allows you to set a password and username for your account when you join.\n" +
                        "\n" +
                        "usernames helps other users to find you on utalk and connect with you.\n" +
                        "\n" +
                        "For support Please email help@utalk.info\n");
                break;
            case "Message Request":

                requestLayout.setVisibility(View.VISIBLE);
                allMessageRequestFriends.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SettingsActivity.this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                allMessageRequestFriends.setLayoutManager(linearLayoutManager);

                DisplayAllUser();
                break;
        }

        AppBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

    }

    private void DisplayAllUser() {

        Query query = userReference.child("Message Request").orderByChild("type");

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User, SettingsActivity.AllUserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SettingsActivity.AllUserViewHolder allUserViewHolder, final int position, @NonNull final User user) {

                final String userId = getRef(position).getKey();



                UserReff.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String name = dataSnapshot.child("fullName").getValue().toString();
                            String image = dataSnapshot.child("profileImage").getValue().toString();

                            allUserViewHolder.userName.setText(name);

                            Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(allUserViewHolder.userImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                allUserViewHolder.AcceptMessageRequestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {



                        UserReff.child(currentUserId).child("Message Friends").child(userId).child("permission").setValue("true");

                        UserReff.child(userId).child("Message Friends").child(currentUserId).child("permission").setValue("true");

                        UserReff.child(currentUserId).child("Message Request").child(userId).removeValue();

                        UserReff.child(userId).child("Message Request").child(currentUserId).removeValue();

                        allUserViewHolder.AcceptMessageRequestBtn.setVisibility(View.GONE);
                        allUserViewHolder.DeleteMessageRequestBtn.setVisibility(View.GONE);



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
                                if (!task.isSuccessful()) {
                                    return;
                                }
                                UserReff.child(userId).child("Message Request").child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {

                                            allUserViewHolder.AcceptMessageRequestBtn.setVisibility(View.GONE);
                                            allUserViewHolder.DeleteMessageRequestBtn.setVisibility(View.GONE);

                                        }

                                    }
                                });
                            }
                        });



                    }
                });



            }

            public AllUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_request_user, parent ,false);
                return new AllUserViewHolder(view);
            }
        };
        adapter.startListening();
        allMessageRequestFriends.setAdapter(adapter);
    }

    public static class AllUserViewHolder extends RecyclerView.ViewHolder {

        View mView;

        CircleImageView userImage;
        TextView userName;
        TextView  AcceptMessageRequestBtn, DeleteMessageRequestBtn;

        AllUserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            userImage = itemView.findViewById(R.id.request_user_profile_image);
            userName = itemView.findViewById(R.id.request_user_name);
            AcceptMessageRequestBtn = itemView.findViewById(R.id.request_accept_chat_request);
            DeleteMessageRequestBtn = itemView.findViewById(R.id.request_decline_chat_request);


        }


    }

    @SuppressLint("SetTextI18n")
    private void setTermsConditionText()
    {
        termsConditionText.setText("1. Introduction\n" +
                "Welcome to utalk Messenger (“Company”, “we”, “our”, “us”)!\n" +
                "These Terms of Service (“Terms”, “Terms of Service”) govern your use of our website located at utalk.info (together or individually “Service”) operated by utalk.\n" +
                "Our Privacy Policy also governs your use of our Service and explains how we collect, safeguard and disclose information that results from your use of our web pages.\n" +
                "Your agreement with us includes these Terms and our Privacy Policy (“Agreements”). You acknowledge that you have read and understood Agreements, and agree to be bound of them.\n" +
                "If you do not agree with (or cannot comply with) Agreements, then you may not use the Service, but please let us know by emailing at help@utalk.info so we can try to find a solution. These Terms apply to all visitors, users and others who wish to access or use Service.\n" +
                "2. Communications\n" +
                "By using our Service, you agree to subscribe to newsletters, marketing or promotional materials and other information we may send. However, you may opt out of receiving any, or all, of these communications from us by following the unsubscribe link or by emailing at help@utalk.info.\n" +
                "3. Contests, Sweepstakes and Promotions\n" +
                "Any contests, sweepstakes or other promotions (collectively, “Promotions”) made available through Service may be governed by rules that are separate from these Terms of Service. If you participate in any Promotions, please review the applicable rules as well as our Privacy Policy. If the rules for a Promotion conflict with these Terms of Service, Promotion rules will apply.\n" +
                "4. Content\n" +
                "Our Service allows you to post, link, store, share and otherwise make available certain information, text, graphics, videos, or other material (“Content”). You are responsible for Content that you post on or through Service, including its legality, reliability, and appropriateness.\n" +
                "By posting Content on or through Service, You represent and warrant that: (i) Content is yours (you own it) and/or you have the right to use it and the right to grant us the rights and license as provided in these Terms, and (ii) that the posting of your Content on or through Service does not violate the privacy rights, publicity rights, copyrights, contract rights or any other rights of any person or entity. We reserve the right to terminate the account of anyone found to be infringing on a copyright.\n" +
                "You retain any and all of your rights to any Content you submit, post or display on or through Service and you are responsible for protecting those rights. We take no responsibility and assume no liability for Content you or any third party posts on or through Service. However, by posting Content using Service you grant us the right and license to use, modify, publicly perform, publicly display, reproduce, and distribute such Content on and through Service. You agree that this license includes the right for us to make your Content available to other users of Service, who may also use your Content subject to these Terms.\n" +
                "utalk has the right but not the obligation to monitor and edit all Content provided by users.\n" +
                "In addition, Content found on or through this Service are the property of utalk or used with permission. You may not distribute, modify, transmit, reuse, download, repost, copy, or use said Content, whether in whole or in part, for commercial purposes or for personal gain, without express advance written permission from us.\n" +
                "5. Prohibited Uses\n" +
                "You may use Service only for lawful purposes and in accordance with Terms. You agree not to use Service:\n" +
                "0.1. In any way that violates any applicable national or international law or regulation.\n" +
                "0.2. For the purpose of exploiting, harming, or attempting to exploit or harm minors in any way by exposing them to inappropriate content or otherwise.\n" +
                "0.3. To transmit, or procure the sending of, any advertising or promotional material, including any “junk mail”, “chain letter,” “spam,” or any other similar solicitation.\n" +
                "0.4. To impersonate or attempt to impersonate Company, a Company employee, another user, or any other person or entity.\n" +
                "0.5. In any way that infringes upon the rights of others, or in any way is illegal, threatening, fraudulent, or harmful, or in connection with any unlawful, illegal, fraudulent, or harmful purpose or activity.\n" +
                "0.6. To engage in any other conduct that restricts Aor inhibits anyone’s use or enjoyment of Service, or which, as determined by us, may harm or offend Company or users of Service or expose them to liability.\n" +
                "Additionally, you agree not to:\n" +
                "0.1. Use Service in any manner that could disable, overburden, damage, or impair Service or interfere with any other party’s use of Service, including their ability to engage in real time activities through Service.\n" +
                "0.2. Use any robot, spider, or other automatic device, process, or means to access Service for any purpose, including monitoring or copying any of the material on Service.\n" +
                "0.3. Use any manual process to monitor or copy any of the material on Service or for any other unauthorized purpose without our prior written consent.\n" +
                "0.4. Use any device, software, or routine that interferes with the proper working of Service.\n" +
                "0.5. Introduce any viruses, trojan horses, worms, logic bombs, or other material which is malicious or technologically harmful.\n" +
                "0.6. Attempt to gain unauthorized access to, interfere with, damage, or disrupt any parts of Service, the server on which Service is stored, or any server, computer, or database connected to Service.\n" +
                "0.7. Attack Service via a denial-of-service attack or a distributed denial-of-service attack.\n" +
                "0.8. Take any action that may damage or falsify Company rating.\n" +
                "0.9. Otherwise attempt to interfere with the proper working of Service.\n" +
                "6. Analytics\n" +
                "We may use third-party Service Providers to monitor and analyze the use of our Service.\n" +
                "7. No Use By Minors\n" +
                "Service is intended only for access and use by individuals at least eighteen (18) years old. By accessing or using Service, you warrant and represent that you are at least eighteen (18) years of age and with the full authority, right, and capacity to enter into this agreement and abide by all of the terms and conditions of Terms. If you are not at least eighteen (18) years old, you are prohibited from both the access and usage of Service.\n" +
                "8. Accounts\n" +
                "When you create an account with us, you guarantee that you are above the age of 18, and that the information you provide us is accurate, complete, and current at all times. Inaccurate, incomplete, or obsolete information may result in the immediate termination of your account on Service.\n" +
                "You are responsible for maintaining the confidentiality of your account and password, including but not limited to the restriction of access to your computer and/or account. You agree to accept responsibility for any and all activities or actions that occur under your account and/or password, whether your password is with our Service or a third-party service. You must notify us immediately upon becoming aware of any breach of security or unauthorized use of your account.\n" +
                "You may not use as a username the name of another person or entity or that is not lawfully available for use, a name or trademark that is subject to any rights of another person or entity other than you, without appropriate authorization. You may not use as a username any name that is offensive, vulgar or obscene.\n" +
                "We reserve the right to refuse service, terminate accounts, remove or edit content, or cancel orders in our sole discretion.\n" +
                "9. Intellectual Property\n" +
                "Service and its original content (excluding Content provided by users), features and functionality are and will remain the exclusive property of utalk and its licensors. Service is protected by copyright, trademark, and other laws of and foreign countries. Our trademarks may not be used in connection with any product or service without the prior written consent of utalk.\n" +
                "10. Copyright Policy\n" +
                "We respect the intellectual property rights of others. It is our policy to respond to any claim that Content posted on Service infringes on the copyright or other intellectual property rights (“Infringement”) of any person or entity.\n" +
                "If you are a copyright owner, or authorized on behalf of one, and you believe that the copyrighted work has been copied in a way that constitutes copyright infringement, please submit your claim via email to help@utalk.info, with the subject line: “Copyright Infringement” and include in your claim a detailed description of the alleged Infringement as detailed below, under “DMCA Notice and Procedure for Copyright Infringement Claims”\n" +
                "You may be held accountable for damages (including costs and attorneys’ fees) for misrepresentation or bad-faith claims on the infringement of any Content found on and/or through Service on your copyright.\n" +
                "11. DMCA Notice and Procedure for Copyright Infringement Claims\n" +
                "You may submit a notification pursuant to the Digital Millennium Copyright Act (DMCA) by providing our Copyright Agent with the following information in writing (see 17 U.S.C 512(c)(3) for further detail):\n" +
                "0.1. an electronic or physical signature of the person authorized to act on behalf of the owner of the copyright’s interest;\n" +
                "0.2. a description of the copyrighted work that you claim has been infringed, including the URL (i.e., web page address) of the location where the copyrighted work exists or a copy of the copyrighted work;\n" +
                "0.3. identification of the URL or other specific location on Service where the material that you claim is infringing is located;\n" +
                "0.4. your address, telephone number, and email address;\n" +
                "0.5. a statement by you that you have a good faith belief that the disputed use is not authorized by the copyright owner, its agent, or the law;\n" +
                "0.6. a statement by you, made under penalty of perjury, that the above information in your notice is accurate and that you are the copyright owner or authorized to act on the copyright owner’s behalf.\n" +
                "You can contact our Copyright Agent via email at help@utalk.info.\n" +
                "12. Error Reporting and Feedback\n" +
                "You may provide us either directly at help@utalk.info or via third party sites and tools with information and feedback concerning errors, suggestions for improvements, ideas, problems, complaints, and other matters related to our Service (“Feedback”). You acknowledge and agree that: (i) you shall not retain, acquire or assert any intellectual property right or other right, title or interest in or to the Feedback; (ii) Company may have development ideas similar to the Feedback; (iii) Feedback does not contain confidential information or proprietary information from you or any third party; and (iv) Company is not under any obligation of confidentiality with respect to the Feedback. In the event the transfer of the ownership to the Feedback is not possible due to applicable mandatory laws, you grant Company and its affiliates an exclusive, transferable, irrevocable, free-of-charge, sub-licensable, unlimited and perpetual right to use (including copy, modify, create derivative works, publish, distribute and commercialize) Feedback in any manner and for any purpose.\n" +
                "13. Links To Other Web Sites\n" +
                "Our Service may contain links to third party web sites or services that are not owned or controlled by utalk.\n" +
                "utalk has no control over, and assumes no responsibility for the content, privacy policies, or practices of any third party web sites or services. We do not warrant the offerings of any of these entities/individuals or their websites.\n" +
                "For example, the outlined. \n" +
                "YOU ACKNOWLEDGE AND AGREE THAT COMPANY SHALL NOT BE RESPONSIBLE OR LIABLE, DIRECTLY OR INDIRECTLY, FOR ANY DAMAGE OR LOSS CAUSED OR ALLEGED TO BE CAUSED BY OR IN CONNECTION WITH USE OF OR RELIANCE ON ANY SUCH CONTENT, GOODS OR SERVICES AVAILABLE ON OR THROUGH ANY SUCH THIRD PARTY WEB SITES OR SERVICES.\n" +
                "WE STRONGLY ADVISE YOU TO READ THE TERMS OF SERVICE AND PRIVACY POLICIES OF ANY THIRD PARTY WEB SITES OR SERVICES THAT YOU VISIT.\n" +
                "14. Disclaimer Of Warranty\n" +
                "THESE SERVICES ARE PROVIDED BY COMPANY ON AN “AS IS” AND “AS AVAILABLE” BASIS. COMPANY MAKES NO REPRESENTATIONS OR WARRANTIES OF ANY KIND, EXPRESS OR IMPLIED, AS TO THE OPERATION OF THEIR SERVICES, OR THE INFORMATION, CONTENT OR MATERIALS INCLUDED THEREIN. YOU EXPRESSLY AGREE THAT YOUR USE OF THESE SERVICES, THEIR CONTENT, AND ANY SERVICES OR ITEMS OBTAINED FROM US IS AT YOUR SOLE RISK.\n" +
                "NEITHER COMPANY NOR ANY PERSON ASSOCIATED WITH COMPANY MAKES ANY WARRANTY OR REPRESENTATION WITH RESPECT TO THE COMPLETENESS, SECURITY, RELIABILITY, QUALITY, ACCURACY, OR AVAILABILITY OF THE SERVICES. WITHOUT LIMITING THE FOREGOING, NEITHER COMPANY NOR ANYONE ASSOCIATED WITH COMPANY REPRESENTS OR WARRANTS THAT THE SERVICES, THEIR CONTENT, OR ANY SERVICES OR ITEMS OBTAINED THROUGH THE SERVICES WILL BE ACCURATE, RELIABLE, ERROR-FREE, OR UNINTERRUPTED, THAT DEFECTS WILL BE CORRECTED, THAT THE SERVICES OR THE SERVER THAT MAKES IT AVAILABLE ARE FREE OF VIRUSES OR OTHER HARMFUL COMPONENTS OR THAT THE SERVICES OR ANY SERVICES OR ITEMS OBTAINED THROUGH THE SERVICES WILL OTHERWISE MEET YOUR NEEDS OR EXPECTATIONS.\n" +
                "COMPANY HEREBY DISCLAIMS ALL WARRANTIES OF ANY KIND, WHETHER EXPRESS OR IMPLIED, STATUTORY, OR OTHERWISE, INCLUDING BUT NOT LIMITED TO ANY WARRANTIES OF MERCHANTABILITY, NON-INFRINGEMENT, AND FITNESS FOR PARTICULAR PURPOSE.\n" +
                "THE FOREGOING DOES NOT AFFECT ANY WARRANTIES WHICH CANNOT BE EXCLUDED OR LIMITED UNDER APPLICABLE LAW.\n" +
                "15. Limitation Of Liability\n" +
                "EXCEPT AS PROHIBITED BY LAW, YOU WILL HOLD US AND OUR OFFICERS, DIRECTORS, EMPLOYEES, AND AGENTS HARMLESS FOR ANY INDIRECT, PUNITIVE, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGE, HOWEVER IT ARISES (INCLUDING ATTORNEYS’ FEES AND ALL RELATED COSTS AND EXPENSES OF LITIGATION AND ARBITRATION, OR AT TRIAL OR ON APPEAL, IF ANY, WHETHER OR NOT LITIGATION OR ARBITRATION IS INSTITUTED), WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE, OR OTHER TORTIOUS ACTION, OR ARISING OUT OF OR IN CONNECTION WITH THIS AGREEMENT, INCLUDING WITHOUT LIMITATION ANY CLAIM FOR PERSONAL INJURY OR PROPERTY DAMAGE, ARISING FROM THIS AGREEMENT AND ANY VIOLATION BY YOU OF ANY FEDERAL, STATE, OR LOCAL LAWS, STATUTES, RULES, OR REGULATIONS, EVEN IF COMPANY HAS BEEN PREVIOUSLY ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. EXCEPT AS PROHIBITED BY LAW, IF THERE IS LIABILITY FOUND ON THE PART OF COMPANY, IT WILL BE LIMITED TO THE AMOUNT PAID FOR THE PRODUCTS AND/OR SERVICES, AND UNDER NO CIRCUMSTANCES WILL THERE BE CONSEQUENTIAL OR PUNITIVE DAMAGES. SOME STATES DO NOT ALLOW THE EXCLUSION OR LIMITATION OF PUNITIVE, INCIDENTAL OR CONSEQUENTIAL DAMAGES, SO THE PRIOR LIMITATION OR EXCLUSION MAY NOT APPLY TO YOU.\n" +
                "16. Termination\n" +
                "We may terminate or suspend your account and bar access to Service immediately, without prior notice or liability, under our sole discretion, for any reason whatsoever and without limitation, including but not limited to a breach of Terms.\n" +
                "If you wish to terminate your account, you may simply discontinue using Service.\n" +
                "All provisions of Terms which by their nature should survive termination shall survive termination, including, without limitation, ownership provisions, warranty disclaimers, indemnity and limitations of liability.\n" +
                "17. Governing Law\n" +
                "These Terms shall be governed and construed in accordance with the laws of South Africa, which governing law applies to agreement without regard to its conflict of law provisions.\n" +
                "Our failure to enforce any right or provision of these Terms will not be considered a waiver of those rights. If any provision of these Terms is held to be invalid or unenforceable by a court, the remaining provisions of these Terms will remain in effect. These Terms constitute the entire agreement between us regarding our Service and supersede and replace any prior agreements we might have had between us regarding Service.\n" +
                "18. Changes To Service\n" +
                "We reserve the right to withdraw or amend our Service, and any service or material we provide via Service, in our sole discretion without notice. We will not be liable if for any reason all or any part of Service is unavailable at any time or for any period. From time to time, we may restrict access to some parts of Service, or the entire Service, to users, including registered users.\n" +
                "19. Amendments To Terms\n" +
                "We may amend Terms at any time by posting the amended terms on this site. It is your responsibility to review these Terms periodically.\n" +
                "Your continued use of the Platform following the posting of revised Terms means that you accept and agree to the changes. You are expected to check this page frequently so you are aware of any changes, as they are binding on you.\n" +
                "By continuing to access or use our Service after any revisions become effective, you agree to be bound by the revised terms. If you do not agree to the new terms, you are no longer authorized to use Service.\n" +
                "20. Waiver and Severability\n" +
                "No waiver by Company of any term or condition set forth in Terms shall be deemed a further or continuing waiver of such term or condition or a waiver of any other term or condition, and any failure of Company to assert a right or provision under Terms shall not constitute a waiver of such right or provision.\n" +
                "If any provision of Terms is held by a court or other tribunal of competent jurisdiction to be invalid, illegal or unenforceable for any reason, such provision shall be eliminated or limited to the minimum extent such that the remaining provisions of Terms will continue in full force and effect.\n" +
                "21. Acknowledgement\n" +
                "BY USING SERVICE OR OTHER SERVICES PROVIDED BY US, YOU ACKNOWLEDGE THAT YOU HAVE READ THESE TERMS OF SERVICE AND AGREE TO BE BOUND BY THEM.\n" +
                "22. Contact Us\n" +
                "Please send your feedback, comments, and requests for technical support by email: help@utalk.info.\n" +
                "These Terms and Conditions were created for utalk.info by on 2020-05-30.\n" +
                "\n");
    }

}