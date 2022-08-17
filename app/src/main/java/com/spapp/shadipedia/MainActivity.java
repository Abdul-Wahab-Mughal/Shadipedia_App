package com.spapp.shadipedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.example.shadipedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        //Check Internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnected() || !activeNetwork.isAvailable()) {
            //Internet is inactive
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Connection Error");
            builder.setMessage("Unable to connect with internet.Please, Check your internet connection and try again.");
            builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do something when user clicked the Yes button
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }).setNegativeButton("TryAgain", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    recreate();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        } else {
            // Check if user is signed in (non-null) and update UI accordingly.
            new Thread(() -> {
                try {
                    sleep(1000);
                    //Internet Connected
                    //Now Firebase Connecting
                    if (firebaseAuth.getCurrentUser() != null) {
                        firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            Intent intent = new Intent(getApplicationContext(), User_home_page.class);
                                            Toast.makeText(getApplicationContext(), "The user data is exists", Toast.LENGTH_LONG).show();
                                            startActivity(intent);
                                        }
                                    }
                                });
                        firestore.collection("provider").document(firebaseAuth.getCurrentUser().getUid()).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            Intent intent = new Intent(getApplicationContext(), Provider_home_page.class);
                                            startActivity(intent);
                                            Toast.makeText(getApplicationContext(), "The provider data is exists", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    } else {
                        Intent intent = new Intent(getApplicationContext(), getStarted.class);
                        startActivity(intent);
                    }
                } catch (Exception ignored) {
                }
            }).start();
        }
    }
}