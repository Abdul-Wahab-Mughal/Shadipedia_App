package com.spapp.shadipedia.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.spapp.shadipedia.FireBase.Post_Item;
import com.example.shadipedia.R;
import com.spapp.shadipedia.UserPostHolder;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserhomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    String z="2";
    Spinner spinner;
    FloatingActionButton searchpost;
    TextInputEditText searchtext;
    String userPost, spinnertext;
    ProgressBar progressBar;

    //Database
    DatabaseReference ref;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    View view;

    //Post display
    UserPostHolder userPostHolder;
    FirebaseRecyclerOptions<Post_Item> options;
    RecyclerView showPost;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.user_home_fragment, container, false);

        searchpost = view.findViewById(R.id.uSearchButton);
        searchtext = view.findViewById(R.id.uSearch_text);
        spinner = view.findViewById(R.id.spinneruser);

        //create Database
        userPost = user.getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Posts");

        // create RecycleView
        showPost = view.findViewById(R.id.showPostHome);
        showPost.setHasFixedSize(true);
        showPost.setLayoutManager(new LinearLayoutManager(getContext()));

        // create spinner
        ArrayAdapter<CharSequence> adaptercategory = ArrayAdapter.createFromResource(getContext(), R.array.search, R.layout.dropdown);
        spinner.setAdapter(adaptercategory);
        spinner.setOnItemSelectedListener(this);

        options = new FirebaseRecyclerOptions.Builder<Post_Item>().setQuery(ref, Post_Item.class).build();
        userPostHolder = new UserPostHolder(options, getContext(),z);
        showPost.setAdapter(userPostHolder);

        searchtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(searchtext.getText().toString())) {
                    return;
                }
                String st = searchtext.getText().toString();
                processSearch(st, spinnertext);

            }
        });

        searchtext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        searchpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String st = searchtext.getText().toString();
                processSearch(st, spinnertext);
                searchtext.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        return view;
    }

    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override
    public void onStart() {
        super.onStart();
        userPostHolder.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override
    public void onStop() {
        super.onStop();
        userPostHolder.stopListening();
    }

    private void processSearch(String s, String text) {
        if (text.equals("city")) {
            options = new FirebaseRecyclerOptions.Builder<Post_Item>().setQuery(ref.orderByChild("city").startAt(s).endAt(s + "\uf8ff"), Post_Item.class).build();
        }
        if (text.equals("category")) {
            options = new FirebaseRecyclerOptions.Builder<Post_Item>().setQuery(ref.orderByChild("category").startAt(s).endAt(s + "\uf8ff"), Post_Item.class).build();
        }
        if (text.equals("price")) {
            options = new FirebaseRecyclerOptions.Builder<Post_Item>().setQuery(ref.orderByChild("price").startAt(s).endAt(s + "\uf8ff"), Post_Item.class).build();
        }
        if (text.equals("name")) {
            options = new FirebaseRecyclerOptions.Builder<Post_Item>().setQuery(ref.orderByChild("name").startAt(s).endAt(s + "\uf8ff"), Post_Item.class).build();
        }
        userPostHolder = new UserPostHolder(options, getContext(),z);
        userPostHolder.startListening();
        showPost.setAdapter(userPostHolder);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnertext = parent.getItemAtPosition(position).toString().trim();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}