package com.spapp.shadipedia.Provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.spapp.shadipedia.Full_Screen_Image;
import com.spapp.shadipedia.User_home_page;
import com.bumptech.glide.Glide;
import com.example.shadipedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static java.lang.Thread.sleep;

public class Provider_Edit_Post extends AppCompatActivity {

    TextInputEditText name, title, description, mobile, address, price;
    String imageReference, id, ProviderPostID;
    ImageView image;
    Uri imageUri, providerImageUri;
    ProgressBar progressBar;
    Button editpost;

    //Database
    DatabaseReference ref;

    //dropdown box
    AutoCompleteTextView citys, categorys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_edit_post);

        progressBar = findViewById(R.id.eprogressshowpost);
        image = findViewById(R.id.postEImagecard);
        name = findViewById(R.id.postEName);
        title = findViewById(R.id.postETitle);
        mobile = findViewById(R.id.postEMobile);
        citys = findViewById(R.id.postECity);
        description = findViewById(R.id.postEDescription);
        price = findViewById(R.id.postEPrice);
        categorys = findViewById(R.id.postECategory);
        address = findViewById(R.id.postEAddress);
        citys = findViewById(R.id.postECity);
        citys.setEnabled(false);
        categorys = findViewById(R.id.postECategory);
        categorys.setEnabled(false);
        editpost = findViewById(R.id.postEsubmit);

        ref = FirebaseDatabase.getInstance().getReference("Posts");
        ProviderPostID = getIntent().getStringExtra("postid");

        String[] city = new String[]{
                "Rawalpindi", "Islamabad",
        };
        String[] category = new String[]{
                "Hall", "Rent Car", "Food", "Decoration",
        };
        ArrayAdapter<String> adaptercity = new ArrayAdapter<>(Provider_Edit_Post.this, R.layout.dropdown, city);
        ArrayAdapter<String> adaptercategory = new ArrayAdapter<>(Provider_Edit_Post.this, R.layout.dropdown, category);
        citys.setAdapter(adaptercity);
        categorys.setAdapter(adaptercategory);

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
                    citys.setText(City);
                    description.setText(Description);
                    price.setText(Price);
                    categorys.setText(Category);
                    address.setText(Address);
                    StorageReference refs = FirebaseStorage.getInstance().getReference().child(imageReference);
                    refs.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                imageUri = task.getResult();
                                Glide.with(getApplicationContext()).load(imageUri).into(image);
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

        image.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Full_Screen_Image.class);
            intent.putExtra("postid", "" + id);
            startActivity(intent);
        });

        editpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = name.getText().toString();
                String t = title.getText().toString();
                String des = description.getText().toString();
                String m = mobile.getText().toString();
                String a = address.getText().toString();
                String P = price.getText().toString();

                Message("Working on it, please wait...");
                Thread thread = new Thread(() -> {
                    try {
                        sleep(1000);
                        ref.child(ProviderPostID).child("name").setValue(n);
                        ref.child(ProviderPostID).child("title").setValue(t);
                        ref.child(ProviderPostID).child("description").setValue(des);
                        ref.child(ProviderPostID).child("mobile").setValue(m);
                        ref.child(ProviderPostID).child("address").setValue(a);
                        ref.child(ProviderPostID).child("price").setValue(P);
                        Message("Update is Successful");
                        Intent intent = new Intent(getApplicationContext(), User_home_page.class);
                        startActivity(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            }
        });
    }

    private void Message(String string) {
        Toast.makeText(getApplication().getBaseContext(), string, Toast.LENGTH_SHORT).show();
    }
}