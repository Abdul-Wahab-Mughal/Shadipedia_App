package com.spapp.shadipedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.spapp.shadipedia.User.UserChatList;
import com.spapp.shadipedia.User.UserFavorite;
import com.spapp.shadipedia.User.User_profile_Fragment;
import com.spapp.shadipedia.User.UserhomeFragment;
import com.example.shadipedia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class User_home_page extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to exit the app?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when user clicked the Yes button
                finishAffinity();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_page);
        bottomNavigationView = findViewById(R.id.userbar);
        bottomNavigationView.setSelectedItemId(R.id.explorenav);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.m_fragment, new UserhomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener listener = item -> {
        switch (item.getItemId()) {
            case R.id.explorenav:
                loadFragment(new UserhomeFragment());
                Log.d("HomeFragment", "Selected");
                return true;
            case R.id.chatnav:
                loadFragment(new UserChatList());
                Log.d("ChatFragment", "Selected");
                return true;
            case R.id.inboxnav:
                loadFragment(new UserFavorite());
                Log.d("FavoriteFragment", "Selected");
                return true;
            case R.id.profilenav:
                loadFragment(new User_profile_Fragment());
                Log.d("profileFragment", "Selected");
                return true;
        }
        return false;
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        Log.d("mActivity", "loadFragment() before");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.m_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        Log.d("mActivity", "loadFragment() after");
    }
}