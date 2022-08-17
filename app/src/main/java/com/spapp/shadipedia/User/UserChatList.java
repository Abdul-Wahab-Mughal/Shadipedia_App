package com.spapp.shadipedia.User;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spapp.shadipedia.FireBase.ChatMessage;
import com.spapp.shadipedia.UserChatListAdapter;
import com.example.shadipedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserChatList extends Fragment {
    RecyclerView usersList;
    UserChatListAdapter adapter;
    ArrayList<ChatMessage> list = new ArrayList<>();
    ProgressDialog pd;
    TextView chatempty;

    //Database
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    FirebaseDatabase db;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_chat_list, container, false);

        usersList = view.findViewById(R.id.UChatRecyclarView);
        chatempty = view.findViewById(R.id.uchatempty);
        db = FirebaseDatabase.getInstance();
        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.show();
        adapter = new UserChatListAdapter(list, getContext());
        usersList.setAdapter(adapter);
        usersList.setLayoutManager(new LinearLayoutManager(getContext()));


        db.getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    db.getReference().child("Messages").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {

                                if (dataSnapshot1.getKey().equals(dataSnapshot.getKey())) {
                                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                                    chatMessage.getId(dataSnapshot.getKey());
                                    list.add(chatMessage);
                                    chatempty.setVisibility(View.INVISIBLE);
                                }
                                if (list.isEmpty()) {
                                    chatempty.setText("Chat is empty");
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                if (list.isEmpty()) {
                    chatempty.setText("Chat is empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        pd.dismiss();
        return view;
    }
}