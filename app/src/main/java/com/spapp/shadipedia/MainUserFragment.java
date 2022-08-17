package com.spapp.shadipedia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.spapp.shadipedia.User.UserLoginFragment;
import com.example.shadipedia.R;

public class MainUserFragment extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), getStarted.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user_fragment);
        LoadFragment(new UserLoginFragment());

    }

    private void LoadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fT = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fT.replace(R.id.fragment_user, fragment);
        fT.commit();
        // save the changes
    }
}