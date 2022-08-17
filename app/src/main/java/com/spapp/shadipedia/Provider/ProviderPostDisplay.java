package com.spapp.shadipedia.Provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.spapp.shadipedia.Full_Screen_Image;
import com.spapp.shadipedia.Provider_home_page;
import com.example.shadipedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class ProviderPostDisplay extends AppCompatActivity {

    DatabaseReference ref, reffavorites,refMessage;
    StorageReference Storef;
    FirebaseStorage storage;

    String imageReference, idReference, id, Name, ProviderPostID;
    ImageView PostImage;
    TextView name, title, mobile, city, description, price, category, address;
    Uri imageUri;

    ProgressBar progressBar;
    Button edit, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_post_display);
        PostImage = findViewById(R.id.postpImagecard);
        name = findViewById(R.id.postpName);
        title = findViewById(R.id.postpTitle);
        mobile = findViewById(R.id.postpMobile);
        city = findViewById(R.id.postpCity);
        description = findViewById(R.id.postpDescription);
        price = findViewById(R.id.postpPrice);
        category = findViewById(R.id.postpCategory);
        address = findViewById(R.id.postpAddress);
        progressBar = findViewById(R.id.progressshowpost);
        edit = findViewById(R.id.EditPost);
        delete = findViewById(R.id.DeletePost);

        storage = FirebaseStorage.getInstance();
        reffavorites = FirebaseDatabase.getInstance().getReference().child("favorites");
        refMessage = FirebaseDatabase.getInstance().getReference().child("Messages");
        ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        ProviderPostID = getIntent().getStringExtra("postid");

        ref.child(ProviderPostID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    id = snapshot.child("num").getValue().toString();
                    Name = snapshot.child("name").getValue().toString();
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

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Provider_Edit_Post.class);
                intent.putExtra("postid", id);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProviderPostDisplay.this);
                builder.setTitle("Delete");
                builder.setMessage("Are you want to delete this post");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when user clicked the Yes button
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ref.child(id).removeValue();
                                reffavorites.child(id).removeValue();
                                refMessage.child(id).removeValue();
                                //refMessage.child().child(id).removeValue();
                                imageReference = "images/post/" + id + "/postImage";
                                idReference = "images/post/" + id + "/idcardImage";
                                Storef = storage.getReference();
                                Storef.child(imageReference).delete();
                                Storef.child(imageReference).delete();
                                Intent intent = new Intent(getApplicationContext(), Provider_home_page.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}