package com.spapp.shadipedia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spapp.shadipedia.FireBase.ChatMessage;
import com.bumptech.glide.Glide;
import com.spapp.shadipedia.Provider.SinglePostChatsList;
import com.example.shadipedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProviderPostChatListAdapter extends RecyclerView.Adapter<ProviderPostChatListAdapter.ViewHolder> {

    String imageReference;
    ArrayList<ChatMessage> list;
    Context context;
    long count;
    Uri imageUri;

    FirebaseDatabase db;

    public ProviderPostChatListAdapter(ArrayList<ChatMessage> list, Context context) {
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

        db = FirebaseDatabase.getInstance();
        db.getReference("Messages").child(chatMessage.getNum()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = snapshot.getChildrenCount();
                if (count > 1) {
                    holder.lastMessage.setText("" + count + " Chats");
                } else {
                    holder.lastMessage.setText("" + count + " Chat");
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
                if (count == 0) {
                    Toast.makeText(context, "There is not chat ", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(v.getContext(), SinglePostChatsList.class);
                    // Toast.makeText(context, "" + chatMessage.getNum(), Toast.LENGTH_SHORT).show();
                    intent.putExtra("postid", "" + chatMessage.getNum());
                    context.startActivity(intent);
                }
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
