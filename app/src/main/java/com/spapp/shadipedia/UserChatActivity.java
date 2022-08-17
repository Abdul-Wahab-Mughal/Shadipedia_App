package com.spapp.shadipedia;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shadipedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserChatActivity extends AppCompatActivity {

    //Database
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    DatabaseReference reference1, referencelast1, reference2, referencelast2, ref;
    FirebaseDatabase db;

    String username, providername, providerId;

    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_chat_activity);

        layout = (LinearLayout) findViewById(R.id.layout1);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference().child("Posts");
        String ProviderPostID = getIntent().getStringExtra("postid");
        ref.child(ProviderPostID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    providerId = snapshot.child("providerId").getValue().toString();

                    reference1 = db.getReference("Messages").child(user.getUid()).child(ProviderPostID);
                    referencelast1 = db.getReference("LastMessages").child(user.getUid()).child(ProviderPostID);
                    reference2 = db.getReference("Messages").child(ProviderPostID).child(user.getUid());
                    referencelast2 = db.getReference("LastMessages").child(ProviderPostID).child(user.getUid());

                    //Show Message
                    firestore.collection("users").document(user.getUid()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                username = (String) documentSnapshot.get("name");

                                firestore.collection("provider").document(providerId).get()
                                        .addOnSuccessListener(documentSnapshots -> {
                                            providername = (String) documentSnapshots.get("name");

                                            reference1.addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {
                                                    String message = dataSnapshot.child("message").getValue().toString();
                                                    String userName = dataSnapshot.child("user").getValue().toString();
                                                    String userTime = dataSnapshot.child("time").getValue().toString();

                                                    if (userName.equals(username)) {
                                                        addMessageBox("You :\n" + message + "\n" + userTime, 1);
                                                    } else {
                                                        addMessageBox(providername + ":\n" + message + "\n" + userTime, 2);
                                                    }
                                                }

                                                @Override
                                                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                }

                                                @Override
                                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                                }

                                                @Override
                                                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                        });
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Current Time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat gday = new SimpleDateFormat("EEEE");
        SimpleDateFormat gdate = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat gtime = new SimpleDateFormat("hh:mm a");
        String day = gday.format(calendar.getTime());
        String date = gdate.format(calendar.getTime());
        String time = gtime.format(calendar.getTime());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();
                firestore.collection("users").document(user.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            username = (String) documentSnapshot.get("name");

                            if (!messageText.equals("")) {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("message", messageText);
                                map.put("user", username);
                                map.put("day", day);
                                map.put("date", date);
                                map.put("time", time);
                                reference1.push().setValue(map);
                                referencelast1.child("lastmessage").setValue(messageText);
                                reference2.push().setValue(map);
                                referencelast2.child("lastmessage").setValue(messageText);
                                messageArea.setText("");
                            }
                        });
            }
        });
    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(UserChatActivity.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
            lp.setMargins(500, 0, 0, 10);
            textView.setPadding(20, 10, 40, 10);
        } else {
            textView.setBackgroundResource(R.drawable.rounded_corner2);
            lp.setMargins(0, 0, 500, 10);
            textView.setPadding(40, 10, 20, 10);
        }
        textView.setLayoutParams(lp);

        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}