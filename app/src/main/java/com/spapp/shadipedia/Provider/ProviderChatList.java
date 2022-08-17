package com.spapp.shadipedia.Provider;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.spapp.shadipedia.ProviderPostChatListAdapter;
import com.spapp.shadipedia.FireBase.ChatMessage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spapp.shadipedia.FireBase.Post_Item;
import com.example.shadipedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProviderChatList extends Fragment {
    RecyclerView usersList;
    ProviderPostChatListAdapter adapter;
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
        view = inflater.inflate(R.layout.provider_chat_list, container, false);

        usersList = view.findViewById(R.id.PChatRecyclarView);
        chatempty = view.findViewById(R.id.chatempty);
        db = FirebaseDatabase.getInstance();
        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.show();

        adapter = new ProviderPostChatListAdapter(list, getContext());
        usersList.setAdapter(adapter);
        usersList.setLayoutManager(new LinearLayoutManager(getContext()));

        db.getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post_Item post_item = dataSnapshot.getValue(Post_Item.class);
                    String prid = post_item.getProviderId(dataSnapshot.getKey());

                    if (prid.equals(user.getUid())) {
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

        pd.dismiss();
        return view;
    }
}