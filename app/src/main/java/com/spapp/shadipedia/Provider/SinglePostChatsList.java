package com.spapp.shadipedia.Provider;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spapp.shadipedia.FireBase.ChatMessage;
import com.spapp.shadipedia.SinglePostChatListAdapter;
import com.example.shadipedia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SinglePostChatsList extends AppCompatActivity {

    RecyclerView usersList;
    ArrayList<ChatMessage> list = new ArrayList<>();
    ProgressDialog pd;
    SinglePostChatListAdapter adapter;
    String d;

    //Database
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_post_chats_list);

        usersList = findViewById(R.id.PostChatuserRecyclarView);
        db = FirebaseDatabase.getInstance();
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        String ProviderPostID = getIntent().getStringExtra("postid");

        // Create Recycler
        adapter = new SinglePostChatListAdapter(list, SinglePostChatsList.this);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(SinglePostChatsList.this));
        usersList.setAdapter(adapter);

        db.getReference("Messages").child(ProviderPostID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                list.clear();
                //Toast.makeText(getApplicationContext(), "" + snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    d = dataSnapshot.getKey();

                    firestore.collection("users").document(d).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                String pname = (String) documentSnapshot.get("name");
                                String puserid = (String) documentSnapshot.get("userId");

                                list.add(new ChatMessage(pname, puserid, ProviderPostID));
                                adapter.notifyDataSetChanged();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        pd.dismiss();
    }

}