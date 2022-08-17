package com.spapp.shadipedia.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.spapp.shadipedia.MainUserFragment;
import com.example.shadipedia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class User_Change_Password extends AppCompatActivity {
    TextInputEditText currentpass, newpass, confirmpass;
    Button Submit;
    FirebaseAuth fireAuth;
    FirebaseUser User;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_change_password);

        currentpass = findViewById(R.id.editucurrentpassword);
        newpass = findViewById(R.id.editunewpassword);
        confirmpass = findViewById(R.id.edituconfirmpassword);
        Submit = findViewById(R.id.Submituserchangpass);

        fireAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        User = fireAuth.getCurrentUser();

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String op = currentpass.getText().toString().trim();
                String np = newpass.getText().toString().trim();
                String cnp = confirmpass.getText().toString().trim();
                if (op.isEmpty()) {
                    currentpass.setError("Current Password is Required.");
                    currentpass.requestFocus();
                    return;
                }
                if (op.length() < 6) {
                    currentpass.setError("password Should be greater than 6");
                    currentpass.requestFocus();
                    return;
                }
                if (np.isEmpty()) {
                    newpass.setError("New Password is Required.");
                    newpass.requestFocus();
                    return;
                }
                if (np.length() < 6) {
                    newpass.setError("password Should be greater than 6");
                    newpass.requestFocus();
                    return;
                }
                if (cnp.isEmpty()) {
                    confirmpass.setError("Confirm Password is Required.");
                    confirmpass.requestFocus();
                    return;
                }
                if (cnp.length() < 6) {
                    confirmpass.setError("password Should be greater than 6");
                    confirmpass.requestFocus();
                    return;
                }
                if (np.equals(cnp)) {
                    Message("Working on it, please wait.");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            firestore.collection("users").document(fireAuth.getCurrentUser().getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        String oldpasscheck = (String) documentSnapshot.get("password");
                                        // OLD Password check
                                        if (oldpasscheck.equals(op)) {
                                            AuthCredential authCredential = EmailAuthProvider.getCredential(User.getEmail(), op);
                                            User.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Update Password
                                                    User.updatePassword(np).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            DocumentReference updates = firestore.collection("users").document(User.getUid());
                                                            updates.update("password", np).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Message("Password Reset Successfully");
                                                                    FirebaseAuth.getInstance().signOut();
                                                                    Intent intent =new Intent(getApplicationContext(), MainUserFragment.class);
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
                                            currentpass.setError("Old password is incorrect");
                                            currentpass.requestFocus();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    currentpass.setError("Old password is incorrect");
                                    currentpass.requestFocus();
                                }
                            });
                        }
                    });
                    thread.start();
                } else {
                    Message("Password missmatching");
                }
            }
        });
    }

    private void Message(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }
}