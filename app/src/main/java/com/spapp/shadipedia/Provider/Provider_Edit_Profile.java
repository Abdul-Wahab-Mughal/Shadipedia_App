package com.spapp.shadipedia.Provider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shadipedia.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class Provider_Edit_Profile extends AppCompatActivity {
    TextInputEditText name, age, mobile;
    Button submit;
    FirebaseAuth fireAuth;
    FirebaseUser User;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_edit_profile);
        name = findViewById(R.id.editPchangename);
        age = findViewById(R.id.editPchangeage);
        mobile = findViewById(R.id.editPchangemobile);
        submit = findViewById(R.id.Submiteproviderpro);

        fireAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        User = fireAuth.getCurrentUser();

        firestore.collection("provider").document(fireAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String pname = (String) documentSnapshot.get("name");
                    String pmobile = (String) documentSnapshot.get("mobile");
                    String page = (String) documentSnapshot.get("age");
                    name.setText(pname);
                    age.setText(page);
                    mobile.setText(pmobile);
                });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = name.getText().toString().trim();
                String a = age.getText().toString().trim();
                String m = mobile.getText().toString().trim();
                if (n.isEmpty()) {
                    name.setError("Name is Required.");
                    name.requestFocus();
                    return;
                }
                if (!n.matches("^[a-z A-Z]*$")) {
                    name.setError("Name must only contain letters");
                    name.requestFocus();
                    return;
                }
                if (a.isEmpty()) {
                    age.setError("Age is Required.");
                    age.requestFocus();
                    return;
                }
                if (Integer.parseInt(a) <= 0) {
                    age.setError("Please enter a valid age.");
                    age.requestFocus();
                    return;
                }
                if (Integer.parseInt(a) >= 100) {
                    age.setError("Please enter a valid age.");
                    age.requestFocus();
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
                Message("working on it, please wait.");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            DocumentReference updates = firestore.collection("provider").document(fireAuth.getCurrentUser().getUid());
                            updates.update("name", n, "age", a, "mobile", m).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Message("Profile Update Successfully");
                                    Provider_Edit_Profile.super.onBackPressed();
                                }
                            });
                        }  catch (Exception ignored) {
                    }


                    }
                });
                thread.start();
            }
        });
    }

    private void Message(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }
}