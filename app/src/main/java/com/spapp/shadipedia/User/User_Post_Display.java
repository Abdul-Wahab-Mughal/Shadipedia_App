package com.spapp.shadipedia.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.spapp.shadipedia.Full_Screen_Image;
import com.spapp.shadipedia.Rate_Display;
import com.spapp.shadipedia.Rate_Post;
import com.spapp.shadipedia.UserChatActivity;
import com.example.shadipedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class User_Post_Display extends AppCompatActivity {

    //Fire Database
    DatabaseReference ref, rateref, databaseReference;
    FirebaseDatabase db;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();

    ImageView PostImage;
    RatingBar ratingview;
    TextView name, title, mobile, city, description, price, category, address;
    String imageReference;
    Uri imageUri;

    ProgressBar progressBar;
    Button chat, rating, viewratingbtn;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_post_display);
        PostImage = findViewById(R.id.postdImagecard);
        name = findViewById(R.id.postdName);
        title = findViewById(R.id.postdTitle);
        mobile = findViewById(R.id.postdMobile);
        city = findViewById(R.id.postdCity);
        description = findViewById(R.id.postdDescription);
        price = findViewById(R.id.postdPrice);
        category = findViewById(R.id.postdCategory);
        address = findViewById(R.id.postdAddress);
        progressBar = findViewById(R.id.progressshowUserpost);
        chat = findViewById(R.id.PostChat);
        rating = findViewById(R.id.ratebutton);
        viewratingbtn = findViewById(R.id.ratebuttonshow);
        ratingview = findViewById(R.id.ratingBaruserpost);

        ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        rateref = FirebaseDatabase.getInstance().getReference().child("RatingTotal");
        String ProviderPostID = getIntent().getStringExtra("postid");
        String CN = getIntent().getStringExtra("CN");

        ref.child(ProviderPostID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    id = snapshot.child("num").getValue().toString();
                    String Name = snapshot.child("name").getValue().toString();
                    String Title = snapshot.child("title").getValue().toString();
                    String Mobile = snapshot.child("mobile").getValue().toString();
                    String City = snapshot.child("city").getValue().toString();
                    String Description = snapshot.child("description").getValue().toString();
                    String Price = snapshot.child("price").getValue().toString();
                    String Category = snapshot.child("category").getValue().toString();
                    String Address = snapshot.child("address").getValue().toString();

                    imageReference = "images/post/" + id + "/postImage";
                    name.setText(Name);
                    title.setText(Title);
                    mobile.setText(Mobile);
                    city.setText(City);
                    description.setText(Description);
                    price.setText(Price);
                    category.setText(Category);
                    address.setText(Address);
                    StorageReference refs = FirebaseStorage.getInstance().getReference().child(imageReference);
                    refs.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                imageUri = task.getResult();
                                Glide.with(getApplicationContext()).load(imageUri).into(PostImage);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rateref.child(ProviderPostID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String star = snapshot.child("star").getValue().toString();
                    String num = snapshot.child("num").getValue().toString();

                    float s= Float.parseFloat(star);
                    float n= Float.parseFloat(num);
                    float avg = s / n;
                    ratingview.setRating(avg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        PostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Full_Screen_Image.class);
                Bitmap _bitmap = ((BitmapDrawable) PostImage.getDrawable()).getBitmap();; // your bitmap
                ByteArrayOutputStream _bs = new ByteArrayOutputStream();
                _bitmap.compress(Bitmap.CompressFormat.PNG, 60, _bs);
                intent.putExtra("image", _bs.toByteArray());
                startActivity(intent);
            }
        });

        viewratingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Rate_Display.class);
                intent.putExtra("postid", "" + id);
                startActivity(intent);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CN.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Only User uses it", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), UserChatActivity.class);
                    intent.putExtra("postid", "" + id);
                    startActivity(intent);

                }
            }
        });

        db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference().child("Rating").child(ProviderPostID);

        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CN.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Only User uses it", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(user.getUid())) {
                                Toast.makeText(getApplicationContext(), "You Rate it's ones", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), Rate_Post.class);
                                intent.putExtra("postid", "" + id);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }
}