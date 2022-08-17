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
import android.widget.Toast;

import com.spapp.shadipedia.Provider.ProviderChatList;
import com.spapp.shadipedia.Provider.Provider_Home_Fragment;
import com.spapp.shadipedia.Provider.Provider_Profile_Fragment;
import com.spapp.shadipedia.Provider.ProviderMyPostFragment;
import com.example.shadipedia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Provider_home_page extends AppCompatActivity {
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
        setContentView(R.layout.provider_home_page);
        bottomNavigationView = findViewById(R.id.providerbar);
        bottomNavigationView.setSelectedItemId(R.id.explorenav);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.pro_fragment, new Provider_Home_Fragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.explorenav:
                    loadFragment(new Provider_Home_Fragment());
                    Log.d("HomeFragment", "Selected");
                    return true;
                case R.id.chatnav:
                    loadFragment(new ProviderChatList());
                    Log.d("ChatFragment", "Selected");
                    return true;
                case R.id.tripsnav:
                    loadFragment(new ProviderMyPostFragment());
                    Log.d("HomeFragment", "Selected");
                    return true;
                case R.id.profilenav:
                    loadFragment(new Provider_Profile_Fragment());
                    Log.d("profileFragment", "Selected");
                    return true;
            }
            return false;
        }
    };

    private void Message(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        Log.d("mainActivity", "loadFragment() before");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.pro_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        Log.d("mainActivity", "loadFragment() after");
    }

}