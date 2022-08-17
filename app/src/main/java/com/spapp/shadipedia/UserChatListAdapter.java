package com.spapp.shadipedia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spapp.shadipedia.FireBase.ChatMessage;
import com.bumptech.glide.Glide;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatListAdapter extends RecyclerView.Adapter<UserChatListAdapter.ViewHolder> {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    DatabaseReference reference;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    String imageReference;
    ArrayList<ChatMessage> list;
    Context context;
    Uri imageUri;

    public UserChatListAdapter(ArrayList<ChatMessage> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_chat_list_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = list.get(position);
        holder.userName.setText(chatMessage.getName());
        //Toast.makeText(context, "" + chatMessage.getNum(), Toast.LENGTH_SHORT).show();
        reference = db.getReference("LastMessages").child(user.getUid()).child(chatMessage.getNum());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String lastmessage = snapshot.child("lastmessage").getValue().toString();
                    holder.lastMessage.setText(lastmessage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        imageReference = "images/post/" + chatMessage.getNum() + "/postImage";
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

        holder.openchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UserChatActivity.class);
                intent.putExtra("postid", "" + chatMessage.getNum());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout openchat;
        CircleImageView image;
        TextView userName, lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.chatImage);
            userName = itemView.findViewById(R.id.chatname);
            lastMessage = itemView.findViewById(R.id.chatmessagelast);
            openchat = itemView.findViewById(R.id.openchat);
        }
    }
}
