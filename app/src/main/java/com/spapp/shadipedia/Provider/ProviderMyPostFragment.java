package com.spapp.shadipedia.Provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spapp.shadipedia.FireBase.Post_Item;
import com.spapp.shadipedia.ProviderPostHolder;
import com.example.shadipedia.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProviderMyPostFragment extends Fragment {
    String userPost;
    FloatingActionButton fab;
    ProgressBar progressBar;
    TextView postempty;

    // Database
    DatabaseReference ref;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    View view;

    //Post display
    ProviderPostHolder providerPostHolder;
    FirebaseRecyclerOptions<Post_Item> options;
    RecyclerView showPost;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.provider_my_post_fragment, container, false);

        fab = view.findViewById(R.id.floating_action_button);
        progressBar = view.findViewById(R.id.mypostbar);
        postempty = view.findViewById(R.id.postempty);

        //create Database
        userPost = user.getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Posts");

        // create RecycleView
        showPost = view.findViewById(R.id.showPostHome);
        showPost.setHasFixedSize(true);
        showPost.setLayoutManager(new LinearLayoutManager(getContext()));

        fab.setOnClickListener(v -> {
            if (user.isEmailVerified()) {
                Intent intent = new Intent(getActivity(), Post_Form.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(),"Verify Email First",Toast.LENGTH_SHORT).show();
            }
        });

        Query query = ref.orderByChild("providerId").startAt(userPost).endAt(userPost + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Post_Item>().setQuery(query, Post_Item.class).build();
        providerPostHolder = new ProviderPostHolder(options, getContext());
        showPost.setAdapter(providerPostHolder);
        progressBar.setVisibility(View.GONE);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    providerPostHolder.notifyDataSetChanged();
                } else {
                    postempty.setText("Post is empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override
    public void onStart() {
        super.onStart();
        providerPostHolder.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override
    public void onStop() {
        super.onStop();
        //providerPostHolder.stopListening();
    }
}