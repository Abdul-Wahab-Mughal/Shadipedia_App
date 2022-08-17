package com.spapp.shadipedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.shadipedia.R;
import com.google.firebase.auth.FirebaseAuth;

public class getStarted extends AppCompatActivity {
    Button user, provider;
    FirebaseAuth fAuth;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        user = findViewById(R.id.User);
        provider = findViewById(R.id.Provider);
        fAuth = FirebaseAuth.getInstance();

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainUserFragment.class);
                startActivity(intent);
            }
        });

        provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainProviderFragment.class);
                startActivity(intent);
            }
        });
    }
}