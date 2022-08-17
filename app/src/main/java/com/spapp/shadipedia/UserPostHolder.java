package com.spapp.shadipedia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spapp.shadipedia.FireBase.Post_Item;
import com.spapp.shadipedia.FireBase.Rating;
import com.spapp.shadipedia.User.User_Post_Display;
import com.bumptech.glide.Glide;
import com.example.shadipedia.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserPostHolder extends FirebaseRecyclerAdapter<Post_Item, UserPostHolder.myviewHolder> {

    DatabaseReference databaseReference, rateref;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String user = firebaseAuth.getCurrentUser().getUid();

    String imageReference;
    Uri imageUri;
    boolean textclick = false;
    Context c;
    String z;

    public UserPostHolder(@NonNull FirebaseRecyclerOptions<Post_Item> options, Context c, String z) {
        super(options);
        this.c = c;
        this.z = z;
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewHolder holder, int position, @NonNull Post_Item model) {
        holder.name.setText(model.getName());
        holder.title.setText(model.getTitle());
        holder.mobile.setText(model.getMobile());
        holder.city.setText(model.getCity());
        holder.description.setText(model.getDescription());
        holder.category.setText(model.getCategory());
        holder.price.setText(model.getPrice() + " .Rs");

        imageReference = "images/post/" + model.getNum() + "/postImage";
        StorageReference refs = FirebaseStorage.getInstance().getReference().child(imageReference);
        refs.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    imageUri = task.getResult();
                    Glide.with(holder.PostImage.getContext()).load(imageUri).into(holder.PostImage);
                }
            }
        });

        int num = Integer.parseInt(model.getNum());
        String nums = model.getNum();

        holder.providersinglepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), User_Post_Display.class);
                intent.putExtra("postid", "" + num);
                intent.putExtra("CN", z);
                c.startActivity(intent);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("favorites");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(nums).hasChild(user)) {
                    holder.like.setBackgroundResource(R.drawable.favorite);
                } else {
                    holder.like.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (z.equals("1")) {
                    Toast.makeText(c, "Only User uses it", Toast.LENGTH_SHORT).show();
                } else {
                    textclick = true;
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (textclick == true) {
                                if (snapshot.child(nums).hasChild(user)) {
                                    databaseReference.child(nums).child(user).removeValue();
                                } else {
                                    databaseReference.child(nums).child(user).setValue(true);
                                }
                                textclick = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

        rateref = db.getReference().child("RatingTotal");
        rateref.child(nums).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String star = snapshot.child("star").getValue().toString();
                    String num = snapshot.child("num").getValue().toString();

                    float s = Float.parseFloat(star);
                    float n = Float.parseFloat(num);
                    float avg = s / n;
                    holder.ratingBar.setRating(avg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public myviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card, parent, false);
        return new myviewHolder(v);
    }

    static class myviewHolder extends RecyclerView.ViewHolder {
        ImageButton like;
        RatingBar ratingBar;
        Button providersinglepost, favorite;
        ImageView PostImage;
        TextView name, title, mobile, city, description, price, category;

        public myviewHolder(@NonNull View itemView) {
            super(itemView);
            providersinglepost = itemView.findViewById(R.id.OpensinglePost);
            PostImage = itemView.findViewById(R.id.postImagecard);
            name = itemView.findViewById(R.id.postName);
            title = itemView.findViewById(R.id.postTitle);
            mobile = itemView.findViewById(R.id.postMobile);
            city = itemView.findViewById(R.id.postCity);
            description = itemView.findViewById(R.id.postDescription);
            category = itemView.findViewById(R.id.postCategory);
            price = itemView.findViewById(R.id.postPrice);
            favorite = itemView.findViewById(R.id.favorite);
            like = itemView.findViewById(R.id.like);
            ratingBar=itemView.findViewById(R.id.ratingBar);
        }
    }
}
