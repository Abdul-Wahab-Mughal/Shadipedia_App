package com.spapp.shadipedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.shadipedia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spapp.shadipedia.FireBase.Rating;

import java.util.ArrayList;


public class Rate_Display extends AppCompatActivity {

    RatingBar ratingBar;
    ArrayList<Rating> list = new ArrayList<>();
    RecyclerView recyclerView;
    UserRatingViewAdapter adapter;
    TextView avgtext;

    // fire Database
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference rateref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_display);

        ratingBar = findViewById(R.id.ratingBarposttotal);
        recyclerView = findViewById(R.id.ratedisplayuser);
        avgtext=findViewById(R.id.avgratetext);

        rateref = db.getReference().child("RatingTotal");

        String ProviderPostID = getIntent().getStringExtra("postid");


        rateref.child(ProviderPostID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String star = snapshot.child("star").getValue().toString();
                    String num = snapshot.child("num").getValue().toString();

                    float s = Float.parseFloat(star);
                    float n = Float.parseFloat(num);
                    float avg = s / n;
                    ratingBar.setRating(avg);
                    avgtext.setText(avg+" outof 5 Rating");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //create Recycler
        adapter = new UserRatingViewAdapter(list, Rate_Display.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Rate_Display.this));
        recyclerView.setAdapter(adapter);

        db.getReference("Rating").child(ProviderPostID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Toast.makeText(getApplicationContext(), "" + snapshot.child(dataSnapshot.getKey()).child("comment").getValue().toString() ,Toast.LENGTH_SHORT).show();
                    String id = dataSnapshot.getKey();
                    String name = snapshot.child(dataSnapshot.getKey()).child("name").getValue().toString();
                    String comment = snapshot.child(dataSnapshot.getKey()).child("comment").getValue().toString();
                    String rate = snapshot.child(dataSnapshot.getKey()).child("rating").getValue().toString();
                    list.add(new Rating(id, name, comment, rate));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}