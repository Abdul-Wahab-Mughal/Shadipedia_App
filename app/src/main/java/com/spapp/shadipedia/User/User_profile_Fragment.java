package com.spapp.shadipedia.User;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shadipedia.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.spapp.shadipedia.Full_Screen_Image;
import com.spapp.shadipedia.getStarted;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;


public class User_profile_Fragment extends Fragment {
    Button changepassword, editprofile, logout, verifynow;
    TextView name, age, mobile, email, txtemail;
    CircleImageView image;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();

    ProgressBar progressBar;
    View view;
    Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_profile_fragment, container, false);
        changepassword = view.findViewById(R.id.ChangePassword);
        editprofile = view.findViewById(R.id.EditProfile);
        logout = view.findViewById(R.id.Logoutu);
        name = view.findViewById(R.id.pName);
        age = view.findViewById(R.id.pAge);
        mobile = view.findViewById(R.id.pMobile);
        email = view.findViewById(R.id.pEmail);
        txtemail = view.findViewById(R.id.utextEmail);
        image = view.findViewById(R.id.userimageprofile);
        verifynow = view.findViewById(R.id.uverification);
        progressBar = view.findViewById(R.id.userprogressbar);
        verifynow.setVisibility(View.INVISIBLE);
        txtemail.setText("Email: Verfied");

        firestore.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String pname = (String) documentSnapshot.get("name");
                    String pemail = (String) documentSnapshot.get("email");
                    String pmobile = (String) documentSnapshot.get("mobile");
                    String page = (String) documentSnapshot.get("age");
                    String x = user.getUid();
                    String imageReference = "images/profile/user/" + x;
                    name.setText(pname);
                    email.setText(pemail);
                    age.setText(page);
                    mobile.setText(pmobile);
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(imageReference);
                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                imageUri = task.getResult();
                                Glide.with(getContext()).load(imageUri).into(image);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getContext());
                            Glide.with(getContext()).load(account.getPhotoUrl()).into(image);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Full_Screen_Image.class);
                Bitmap _bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                ; // your bitmap
                ByteArrayOutputStream _bs = new ByteArrayOutputStream();
                _bitmap.compress(Bitmap.CompressFormat.PNG, 60, _bs);
                intent.putExtra("image", _bs.toByteArray());
                startActivity(intent);
            }
        });


        if (!user.isEmailVerified()) {
            txtemail.setText("Email: Not Verfied !");
            verifynow.setVisibility(View.VISIBLE);
            verifynow.setText("Verify Email now");
            verifynow.setOnClickListener(v -> {
                user.sendEmailVerification()
                        .addOnSuccessListener(aVoid -> Message("Verification Email Has bess Sent"))
                        .addOnFailureListener(e -> Log.d("OnFailure::", "OnFailure: Email not sent" + e.getMessage()));
            });
        }

        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), User_Change_Password.class);
                startActivity(intent);
            }
        });

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), User_Edit_Profile.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Message("Logout Successful");
                Intent intent = new Intent(getActivity(), getStarted.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void Message(String string) {
        Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
    }

}