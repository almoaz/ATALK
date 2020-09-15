package com.approxsoft.atalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import Model.Note;


public class SaveNoteActivity extends AppCompatActivity {

    Toolbar mToolBar;
    TextView AddNoteBtn;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    String currentUserId;
    RecyclerView NoteList;

    ImageView AppBarIcon;
    TextView AppBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_note);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("All Note");

        mToolBar = findViewById(R.id.save_note_ap_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");
        AppBarIcon = findViewById(R.id.save_note_app_bar_icon);
        AppBarTitle = findViewById(R.id.save_note_app_bar_title);

        AppBarTitle.setText("Important Note");

        AppBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        AddNoteBtn = findViewById(R.id.add_note_btn);



        NoteList = findViewById(R.id.all_save_note);
        NoteList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        NoteList.setLayoutManager(linearLayoutManager);

        AllDisplayNote();


        AddNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SaveNoteActivity.this,AddNoteActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AllDisplayNote()
    {
        Query query = databaseReference.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Note> options = new FirebaseRecyclerOptions.Builder<Note>().setQuery(query, Note.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Note, NotesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotesViewHolder notesViewHolder, final int position, @NonNull final Note note) {

                final String noteId = getRef(position).getKey();

                notesViewHolder.Note.setText(note.getNote());

                Calendar calForDate = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                final String saveCurrentDate = currentDate.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                final String saveCurrentTime = currentTime.format(calForTime.getTime());

                if (note.getTime().equals(saveCurrentTime))
                {
                    notesViewHolder.date.setText("Just Now");
                }
                else
                    if (note.getTime().equals(saveCurrentDate))
                    {
                        notesViewHolder.date.setText("Today at "+ note.getTime());
                    }
                    else
                    {
                        notesViewHolder.date.setText(note.getDate());
                    }


                    notesViewHolder.EditBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SaveNoteActivity.this);
                            builder.setTitle("Edit Post");

                            final EditText inputFiled = new EditText(SaveNoteActivity.this);
                            inputFiled.setText(notesViewHolder.Note.getText());
                            builder.setView(inputFiled);

                            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    databaseReference.child(noteId).child("note").setValue(inputFiled.getText().toString());
                                    Toast.makeText(SaveNoteActivity.this,"Note Update Successfully",Toast.LENGTH_SHORT).show();

                                }
                            });

                            builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    databaseReference.child(noteId).removeValue();
                                    dialog.cancel();
                                }
                            });

                            Dialog dialog = builder.create();
                            dialog.show();
                            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.background_light);
                        }
                    });


            }

            public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_note_layout, parent ,false);
                return new NotesViewHolder(view);
            }
        };
        adapter.startListening();
        NoteList.setAdapter(adapter);
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {

        View mView;

        TextView Note, date;
        Button EditBtn;

        NotesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            Note = itemView.findViewById(R.id.note_description);
            date = itemView.findViewById(R.id.note_date);
            EditBtn = itemView.findViewById(R.id.edit_note_btn);
        }


    }
}