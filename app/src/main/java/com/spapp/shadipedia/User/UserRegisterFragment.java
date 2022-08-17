package com.spapp.shadipedia.User;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.spapp.shadipedia.FireBase.Users;
import com.example.shadipedia.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.spapp.shadipedia.MainUserFragment;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class UserRegisterFragment extends Fragment {
    TextInputEditText name, email, password, age, mobile;
    ImageView profilePicture;
    Uri userImageUri;
    TextView submit;
    TextView login_form;
    FirebaseAuth fireAuth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    MaterialCheckBox termsAndCond;
    View view;
    String imageUrlString, emailPattern = "[a-zA-Z0-9._-]+(@gmail||@hotmail)+\\.+com+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_register_fragment, container, false);
        fireAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        login_form = view.findViewById(R.id.Login);
        name = view.findViewById(R.id.editUName);
        email = view.findViewById(R.id.editUEmail);
        password = view.findViewById(R.id.editUPassword);
        age = view.findViewById(R.id.editUAge);
        mobile = view.findViewById(R.id.editUMobile);
        submit = view.findViewById(R.id.Signupuser);
        termsAndCond = view.findViewById(R.id.tc);
        profilePicture = view.findViewById(R.id.profilepicture);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat gday = new SimpleDateFormat("EEEE");
        SimpleDateFormat gdate = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat gtime = new SimpleDateFormat("hh:mm:ss a");
        String day = gday.format(calendar.getTime());
        String date = gdate.format(calendar.getTime());
        String Time = gtime.format(calendar.getTime());

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 100);
            }
        });

        login_form.setOnClickListener(v -> LoadFragment(new UserLoginFragment()));

        submit.setOnClickListener(v -> {
            final String e = email.getText().toString().trim();
            final String n = name.getText().toString().trim();
            final String p = password.getText().toString().trim();
            final String a = age.getText().toString().trim();
            final String m = mobile.getText().toString().trim();
            if (profilePicture.getDrawable() == null) {
                Message("set Image post");
                return;
            }
            if (n.isEmpty()) {
                name.setError("Name is Required.");
                name.requestFocus();
                return;
            }
            if (e.isEmpty()) {
                email.setError("Email is Required.");
                email.requestFocus();
                return;
            }
            if (a.isEmpty()) {
                age.setError("Age is Required.");
                age.requestFocus();
                return;
            }
            if (Integer.parseInt(a) <= 0) {
                age.setError("Please enter a valid age.");
                age.requestFocus();
                return;
            }
            if (Integer.parseInt(a) >= 100) {
                age.setError("Please enter a valid age.");
                age.requestFocus();
                return;
            }
            if (m.isEmpty()) {
                mobile.setError("Age is Required.");
                mobile.requestFocus();
                return;
            }
            if (m.length() != 11) {
                mobile.setError("Please enter a valid number.");
                mobile.requestFocus();
                return;
            }
            if (p.length() < 6) {
                password.setError("password Should be greater than 6");
                password.requestFocus();
                return;
            }
            if (!termsAndCond.isChecked()) {
                termsAndCond.setError("Agree To continue.");
                termsAndCond.requestFocus();
                return;
            }
            if (e.matches(emailPattern)) {
                Message("Working on it, please wait.");
                Thread thread = new Thread(() -> fireAuth.fetchSignInMethodsForEmail(e).addOnCompleteListener(task -> {
                    boolean check = !Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).isEmpty();
                    if (!check) {
                        fireAuth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                String x = fireAuth.getUid();
                                String imageReference = "images/profile/user/" + x;
                                StorageReference ref = storage.getReference().child(imageReference);
                                Message("Registration successful!");
                                DocumentReference df = firestore.collection("users")
                                        .document(Objects.requireNonNull(fireAuth.getCurrentUser()).getUid());
                                Map<String, Object> map = new HashMap<>();
                                Users user = new Users(x,n, e, a, m, p, userImageUri);
                                map.put(Users.getUserIdKey(), user.getUserid());
                                map.put(Users.getNameKey(), user.getName());
                                map.put(Users.getAgeKey(), user.getAge());
                                map.put(Users.getMobileKey(), user.getMobile());
                                map.put(Users.getEmailKey(), user.getEmail());
                                map.put(Users.getPasswordKey(), user.getPassword());
                                Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 60, baos);
                                byte[] data = baos.toByteArray();

                                UploadTask uploadTask = ref.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc
                                    }
                                });

                                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task1) throws Exception {
                                        if (!task1.isSuccessful()) {
                                            throw Objects.requireNonNull(task1.getException());
                                        }

                                        // Continue with the task to get the download URL
                                        return ref.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task1) {
                                        if (task1.isSuccessful()) {
                                            userImageUri = task1.getResult();
                                            Toast.makeText(getActivity().getBaseContext(), "Image Uploaded.", Toast.LENGTH_LONG).show();

                                        } else {
                                            // Handle failures
                                            // ...
                                        }
                                    }
                                });
                                imageUrlString = userImageUri.toString();
                                map.put("image", imageUrlString);

                                df.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(getActivity(), MainUserFragment.class);
                                        startActivity(intent);
                                        Message("User Data Added");
                                    }
                                });
                            } else {
                                Message("Registration failed! Please try again later");
                            }
                        });
                    } else {
                        Message("Email already present");
                    }
                }));
                thread.start();
            } else {
                Message("Invalid email address");
            }
        });
        return view;
    }

    private void Message(String string) {
        Toast.makeText(getActivity().getBaseContext(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            userImageUri = data.getData();
            profilePicture.setImageURI(userImageUri);
        }
    }

    private void LoadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fT = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fT.replace(R.id.fragment_user, fragment);
        fT.commit();
        // save the changes
    }
}