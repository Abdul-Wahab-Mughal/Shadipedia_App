package com.spapp.shadipedia.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spapp.shadipedia.FireBase.Post_Item;
import com.spapp.shadipedia.FavoriteAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shadipedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class UserFavorite extends Fragment {

    //Database
    DatabaseReference ref;
    FirebaseDatabase db;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    View view;

    //Post display
    FavoriteAdapter adapter;
    ArrayList<Post_Item> list = new java.util.ArrayList<>();
    RecyclerView showPost;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.user_favorite, container, false);

        db = FirebaseDatabase.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Posts");

        // create RecycleView
        showPost = view.findViewById(R.id.showFavorites);
        adapter = new FavoriteAdapter(list, getContext());
        showPost.setAdapter(adapter);
        showPost.setLayoutManager(new LinearLayoutManager(getContext()));

        db.getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    if (!Objects.equals(dataSnapshot.getKey(), "")) {
                        db.getReference().child("favorites").child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                                    if (dataSnapshot1.getKey().equals(user.getUid())) {

                                        Post_Item post_item = dataSnapshot.getValue(Post_Item.class);
                                        post_item.getProviderId(dataSnapshot.getKey());
                                        list.add(post_item);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }
}