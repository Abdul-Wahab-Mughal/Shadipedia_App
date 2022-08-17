package com.spapp.shadipedia.Provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.spapp.shadipedia.FireBase.DateTime;
import com.spapp.shadipedia.FireBase.Post_Item;
import com.spapp.shadipedia.FireBase.Rating;
import com.spapp.shadipedia.Provider_home_page;
import com.example.shadipedia.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.sql.DataTruncation;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class Post_Form extends AppCompatActivity {
    TextInputEditText name, title, description, mobile, address, Price;
    String PostCount = "0";
    ImageView image;
    Uri providerImageUri;
    Button post;

    //Database
    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;

    FirebaseDatabase db;
    DatabaseReference reference, countreference, timereference, ratereference;
    StorageReference storef;
    FirebaseStorage storage;

    private AutoCompleteTextView citys, categorys;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_form);

        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        db = FirebaseDatabase.getInstance();

        image = findViewById(R.id.postimage);
        name = findViewById(R.id.editpostname);
        title = findViewById(R.id.editposttitle);
        description = findViewById(R.id.editpostDescription);
        mobile = findViewById(R.id.editpostMobile);
        address = findViewById(R.id.editpostAddress);
        citys = findViewById(R.id.editcardcity);
        citys.setEnabled(false);
        categorys = findViewById(R.id.editpostcategory);
        categorys.setEnabled(false);
        Price = findViewById(R.id.editpostPrice);
        post = findViewById(R.id.postsubmit);

        // Current Time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat gday = new SimpleDateFormat("EEEE");
        SimpleDateFormat gdate = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat gtime = new SimpleDateFormat("hh:mm:ss a");
        String day = gday.format(calendar.getTime());
        String date = gdate.format(calendar.getTime());
        String time = gtime.format(calendar.getTime());

        String[] city = new String[]{
                "Rawalpindi", "Islamabad",
        };
        String[] category = new String[]{
                "Hall", "Rent Car", "Food", "Decoration",
        };
        ArrayAdapter<String> adaptercity = new ArrayAdapter<>(Post_Form.this, R.layout.dropdown, city);
        ArrayAdapter<String> adaptercategory = new ArrayAdapter<>(Post_Form.this, R.layout.dropdown, category);
        citys.setAdapter(adaptercity);
        categorys.setAdapter(adaptercategory);

        image.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, 100);
        });

        countreference = db.getReference("PostCount");
        countreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostCount = snapshot.child("num").getValue().toString();
                int add = Integer.parseInt(PostCount);
                add += 1;
                PostCount = String.valueOf(add);
                // Message("" + PostCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        post.setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String t = title.getText().toString().trim();
            String des = description.getText().toString().trim();
            String m = mobile.getText().toString().trim();
            String a = address.getText().toString().trim();
            String c = citys.getText().toString().trim();
            String P = Price.getText().toString().trim();
            String cat = categorys.getText().toString().trim();
            if (image.getDrawable() == null) {
                Message("set Image post");
                return;
            }
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
            if (t.isEmpty()) {
                title.setError("title is Required.");
                title.requestFocus();
                return;
            }
            if (des.isEmpty()) {
                description.setError("Description is Required.");
                description.requestFocus();
                return;
            }
            if (m.isEmpty()) {
                mobile.setError("mobile is Required.");
                mobile.requestFocus();
                return;
            }
            if (m.length() != 11) {
                mobile.setError("Please enter a valid number.");
                mobile.requestFocus();
                return;
            }
            if (a.isEmpty()) {
                address.setError("Address is Required");
                address.requestFocus();
                return;
            }
            if (c.isEmpty()) {
                citys.setError("City is Required");
                citys.requestFocus();
                return;
            }
            if (cat.isEmpty()) {
                categorys.setError("Category is Required");
                categorys.requestFocus();
                return;
            }
            if (!P.isEmpty()) {
                if (Integer.parseInt(P) <= 0) {
                    Price.setError("Please enter a valid price");
                    Price.requestFocus();
                    return;
                }
            }
            Message("Working on it, please wait...");
            String userid = fireAuth.getUid();

            // Post image
            String imageReferences = "images/post/" + PostCount + "/postImage";
            storef = storage.getReference().child(imageReferences);
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTasks = storef.putBytes(data);

            Task<Uri> urlTasks = uploadTasks.continueWithTask(task1 -> {
                if (!task1.isSuccessful()) {
                    throw Objects.requireNonNull(task1.getException());
                }
                // Continue with the task to get the download URL
                return storef.getDownloadUrl();
            }).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    providerImageUri = task1.getResult();
                    Message("Post Image Uploaded");
                }
            });
            Message("Data Add ...");

            //Add Post
            // Message("" + PostCount);
            Post_Item post_items = new Post_Item(PostCount, n, t, des, m, a, c, cat, P, userid);
            reference = db.getReference().child("Posts");
            reference.child(PostCount).setValue(post_items).addOnCompleteListener(task -> {

                // add Date
                DateTime dateTime = new DateTime(day, date, time);
                timereference = db.getReference().child("Posts");
                timereference.child(PostCount).child("Time").setValue(dateTime).addOnCompleteListener(task12 -> {

                    //add rating
                    ratereference = db.getReference().child("RatingTotal").child(PostCount);
                    Rating rating = new Rating("0", "0");
                    ratereference.setValue(rating).addOnCompleteListener(task14 -> {

                        //add psotcount
                        countreference = db.getReference().child("PostCount");
                        countreference.child("num").setValue(PostCount).addOnCompleteListener(task13 -> {

                            Intent intent = new Intent(getApplicationContext(), Provider_home_page.class);
                            startActivity(intent);
                        });
                    });
                });
            });
        });
    }

    private void Message(String string) {
        Toast.makeText(getApplication().getBaseContext(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 100) {
            providerImageUri = data.getData();
            image.setImageURI(providerImageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}