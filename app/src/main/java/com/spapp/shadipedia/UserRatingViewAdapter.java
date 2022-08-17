package com.spapp.shadipedia;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shadipedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.spapp.shadipedia.FireBase.Rating;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRatingViewAdapter extends RecyclerView.Adapter<UserRatingViewAdapter.ViewHolder> {

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String user = firebaseAuth.getCurrentUser().getUid();
    ArrayList<Rating> list;
    Context context;
    String imageReference;
    Uri imageUri;

    public UserRatingViewAdapter(ArrayList<Rating> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_rate_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRatingViewAdapter.ViewHolder holder, int position) {
        Rating rating = list.get(position);
        holder.name.setText(rating.getName());
        holder.comment.setText(rating.getComment());
        float rate = Float.parseFloat(rating.getRating());
        holder.ratingBar.setRating(rate);
        //Toast.makeText(context,""+rating.getId(),Toast.LENGTH_SHORT).show();

        imageReference = "images/profile/user/" + rating.getId();
        StorageReference refs = FirebaseStorage.getInstance().getReference().child(imageReference);
        refs.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    imageUri = task.getResult();
                    Glide.with(holder.image.getContext()).load(imageUri).into(holder.image);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name, comment;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.userimagerating);
            name = itemView.findViewById(R.id.userName);
            ratingBar = itemView.findViewById(R.id.ratingBarpost);
            comment = itemView.findViewById(R.id.usercomment);
        }
    }
}
