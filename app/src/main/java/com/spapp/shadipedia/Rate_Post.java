package com.spapp.shadipedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shadipedia.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spapp.shadipedia.FireBase.Rating;

public class Rate_Post extends AppCompatActivity {

    RatingBar ratingBarset;
    TextView ratingtext;
    TextInputEditText commenttext;
    Button submit;
    int rateValue;
    String setr, rnum;

    // Fire Database
    FirebaseDatabase db;
    DatabaseReference databaseReference, databaseReferencetotal;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_post);

        ratingBarset = findViewById(R.id.ratingBarpost);
        ratingtext = findViewById(R.id.ratingq);
        commenttext = findViewById(R.id.editcomment);
        submit = findViewById(R.id.Submitrate);

        db = FirebaseDatabase.getInstance();

        String ProviderPostID = getIntent().getStringExtra("postid");
        databaseReference = db.getReference().child("Rating").child(ProviderPostID);
        databaseReferencetotal = db.getReference().child("RatingTotal").child(ProviderPostID);

        ratingBarset.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            rateValue = (int) ratingBar.getRating();

            if (rateValue == 1) {
                ratingtext.setText("Bad " + rateValue + "/5");
            } else if (rateValue == 2) {
                ratingtext.setText("Ok " + rateValue + "/5");
            } else if (rateValue == 3) {
                ratingtext.setText("Good " + rateValue + "/5");
            } else if (rateValue == 4) {
                ratingtext.setText("Very Good " + rateValue + "/5");
            } else if (rateValue == 5) {
                ratingtext.setText("Best " + rateValue + "/5");
            }
        });


        submit.setOnClickListener(v -> {
            String com = commenttext.getText().toString();
            if (rateValue > 0 && !com.isEmpty()) {
                databaseReferencetotal.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String r = snapshot.child("star").getValue().toString();
                        String rn = snapshot.child("num").getValue().toString();

                        int rint = Integer.parseInt(r);
                        rint = rint + rateValue;
                        setr = String.valueOf(rint);

                        int rns= Integer.parseInt(rn);
                        rns += 1;
                        rnum = String.valueOf(rns);
                        //Message("" + setr);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                firestore.collection("users").document(user.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String pname = (String) documentSnapshot.get("name");
                            String ratestar = String.valueOf(rateValue);
                            if (com.isEmpty()) {
                                commenttext.setError("Write a Comments");
                                commenttext.requestFocus();
                                return;
                            }
                            Rating rating = new Rating(pname, com, ratestar);
                            databaseReference.child(user.getUid()).setValue(rating).addOnCompleteListener(task -> {

                                Rating rating1 = new Rating(setr, rnum);
                                databaseReferencetotal.setValue(rating1).addOnCompleteListener(task1 -> {

                                    Intent intent = new Intent(getApplicationContext(), User_home_page.class);
                                    startActivity(intent);
                                });
                            });
                        });

            } else {
                if (rateValue == 0) {
                    Message("set Rating");
                }
                if (com.isEmpty()) {
                    Message("write Comment");
                }
            }
        });
    }

    private void Message(String string) {
        Toast.makeText(getApplication().getBaseContext(), string, Toast.LENGTH_SHORT).show();
    }
}