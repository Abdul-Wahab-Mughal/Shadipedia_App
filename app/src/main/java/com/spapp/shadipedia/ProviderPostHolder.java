package com.spapp.shadipedia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spapp.shadipedia.FireBase.Post_Item;
import com.spapp.shadipedia.Provider.ProviderPostDisplay;
import com.bumptech.glide.Glide;
import com.example.shadipedia.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProviderPostHolder extends FirebaseRecyclerAdapter<Post_Item, ProviderPostHolder.myviewHolder> {

    DatabaseReference databaseReference, rateref;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String user = firebaseAuth.getCurrentUser().getUid();

    Uri imageUri;
    Context c;

    public ProviderPostHolder(@NonNull FirebaseRecyclerOptions<Post_Item> options, Context c) {
        super(options);
        this.c = c;
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewHolder holder, int position, @NonNull Post_Item model) {

        String x = firebaseAuth.getCurrentUser().getUid();
        holder.name.setText(model.getName());
        holder.title.setText(model.getTitle());
        holder.mobile.setText(model.getMobile());
        holder.city.setText(model.getCity());
        holder.category.setText(model.getCategory());
        holder.description.setText(model.getDescription());
        holder.price.setText(model.getPrice() + " .RS");
        holder.favorite.setEnabled(false);

        String imageReference = "images/post/" + model.getNum() + "/postImage" ;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(imageReference);
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
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
                Intent intent = new Intent(v.getContext(), ProviderPostDisplay.class);
                intent.putExtra("postid", "" + num);
                c.startActivity(intent);
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
        Button providersinglepost;
        Button favorite;
        RatingBar ratingBar;
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
            category = itemView.findViewById(R.id.postCategory);
            description = itemView.findViewById(R.id.postDescription);
            price = itemView.findViewById(R.id.postPrice);
            favorite = itemView.findViewById(R.id.favorite);
            ratingBar=itemView.findViewById(R.id.ratingBar);
        }
    }
}
