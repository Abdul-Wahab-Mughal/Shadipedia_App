package com.spapp.shadipedia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.spapp.shadipedia.Provider.ProviderLoginFragment;
import com.example.shadipedia.R;

public class MainProviderFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_provider_fragment);
        LoadFragment(new ProviderLoginFragment());
    }

    private void LoadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fT = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fT.replace(R.id.fragment_provider, fragment);
        fT.commit();
        // save the changes
    }
}