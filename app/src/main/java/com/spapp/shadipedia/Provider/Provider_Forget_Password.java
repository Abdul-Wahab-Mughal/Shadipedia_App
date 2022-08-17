package com.spapp.shadipedia.Provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.shadipedia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.spapp.shadipedia.MainUserFragment;

import java.util.Objects;

public class Provider_Forget_Password extends AppCompatActivity {

    TextInputEditText email, mobile, pass;
    Button submit, submitpass;
    FirebaseAuth fireAuth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    LinearLayout Lpass;
    String Fpass, Fid, e, emailPattern = "[a-zA-Z0-9._-]+@(gmail||hotmail)+\\.+com+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_forget_password);
        email = findViewById(R.id.forgetPemail);
        mobile = findViewById(R.id.forgetPmobilenumber);
        pass = findViewById(R.id.forgetPpass);
        submit = findViewById(R.id.Submitforgetprovider);
        submitpass = findViewById(R.id.Submitforgetproivderpass);
        Lpass = findViewById(R.id.Ppass);
        Lpass.setVisibility(View.INVISIBLE);
        submitpass.setVisibility(View.INVISIBLE);

        fireAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        submit.setOnClickListener(v -> {
            e = email.getText().toString().trim();
            String m = mobile.getText().toString().trim();
            if (e.isEmpty()) {
                email.setError("Name is Required.");
                email.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                email.setError("Enter Valid Email");
                email.requestFocus();
                return;
            }
            if (m.isEmpty()) {
                mobile.setError("Age is Required.");
                mobile.requestFocus();
                return;
            }
            if (m.length() != 11) {
                mobile.setError("Please enter a valid number.");
                mobile.requestFocus();
                return;
            }
            if (e.matches(emailPattern)) {

                Message("working on it, please wait.");
                Thread thread = new Thread(() -> firestore.collection("provider").whereEqualTo("email", e).get()
                        .addOnCompleteListener(task -> {
                            int z = 1;
                            for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                                z = 2;
                                String Fmobile = doc.getString("mobile");
                                Fpass = doc.getString("password");
                                Fid = doc.getString("providerId");
                                if (Objects.equals(Fmobile, m)) {
                                    Lpass.setVisibility(View.VISIBLE);
                                    submitpass.setVisibility(View.VISIBLE);
                                    email.setEnabled(false);
                                    mobile.setEnabled(false);
                                    submit.setEnabled(false);
                                } else {
                                    Message("email or mobile is incorrect");
                                }
                            }
                            if (z == 1) {
                                Toast.makeText(getApplicationContext(), "write correct email ", Toast.LENGTH_SHORT).show();
                            }
                        }));
                thread.start();
            } else {
                Message("Invalid email address");
            }
        });

        submitpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = pass.getText().toString().trim();
                if (p.isEmpty()) {
                    pass.setError("Password is required");
                    pass.requestFocus();
                    return;
                }
                if (p.length() < 6) {
                    pass.setError("Password Should be greater than 6");
                    pass.requestFocus();
                    return;
                }
                Message("working on it, please wait.");

                Thread thread = new Thread(() -> {

                    fireAuth.signInWithEmailAndPassword(e, Fpass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user = fireAuth.getCurrentUser();
                            firestore.collection("provider").document(user.getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        String pemail = (String) documentSnapshot.get("email");
                                        if (Objects.equals(pemail, e)) {

                                            AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), Fpass);
                                            user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Update Password
                                                    user.updatePassword(p).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            DocumentReference updates = firestore.collection("provider").document(user.getUid());
                                                            updates.update("password", p).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Message("Password Reset Successfully");
                                                                    FirebaseAuth.getInstance().signOut();
                                                                    Intent intent = new Intent(getApplicationContext(), MainUserFragment.class);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Message("Password Reset Failed");
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Message("authenticated failed");
                                                }
                                            });


                                        } else {
                                            FirebaseAuth.getInstance().signOut();
                                            Message("Login failed! Please try again later or Create Account");
                                        }
                                    }).addOnFailureListener(e1 -> {
                                Log.d("read user data", "on failure listener" + e1.toString());
                                Message("Failed to get user data");
                            });
                        }
                    });
                });
                thread.start();
            }
        });
    }

    private void Message(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }
}