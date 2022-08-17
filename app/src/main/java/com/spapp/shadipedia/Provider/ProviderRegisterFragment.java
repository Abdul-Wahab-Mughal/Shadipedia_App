package com.spapp.shadipedia.Provider;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.spapp.shadipedia.FireBase.Providers;
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
import com.spapp.shadipedia.MainProviderFragment;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProviderRegisterFragment extends Fragment {
    TextInputEditText name, age, email, mobile, password;
    ImageView profilepic;
    Uri providerImageUri;
    TextView submit;
    TextView login_form;
    MaterialCheckBox CheckBox;
    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    FirebaseStorage storage;
    View view;
    String imageUrlString, emailPattern = "[a-zA-Z0-9._-]+(@gmail||@hotmail)+\\.+com+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.provider_register_fragment, container, false);
        name = view.findViewById(R.id.editPName);
        age = view.findViewById(R.id.editPAge);
        email = view.findViewById(R.id.editPEmail);
        mobile = view.findViewById(R.id.editPMobile);
        password = view.findViewById(R.id.editPPassword);
        login_form = view.findViewById(R.id.Login);
        profilepic = view.findViewById(R.id.Pprofilepicture);
        submit = view.findViewById(R.id.Signupprovider);
        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        CheckBox = view.findViewById(R.id.ptc);
        storage = FirebaseStorage.getInstance();
        profilepic = view.findViewById(R.id.Pprofilepicture);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat gday = new SimpleDateFormat("EEEE");
        SimpleDateFormat gdate = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat gtime = new SimpleDateFormat("hh:mm:ss a");
        String day = gday.format(calendar.getTime());
        String date = gdate.format(calendar.getTime());
        String Time = gtime.format(calendar.getTime());

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 100);
            }
        });

        login_form.setOnClickListener(v -> LoadFragment(new ProviderLoginFragment()));

        submit.setOnClickListener(v -> {
            final String e = email.getText().toString().trim();
            final String n = name.getText().toString().trim();
            final String m = mobile.getText().toString().trim();
            final String p = password.getText().toString().trim();
            final String a = age.getText().toString().trim();
            if (profilepic.getDrawable()==null){
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
            if (e.isEmpty()) {
                email.setError("Email is Required.");
                email.requestFocus();
                return;
            }
            if (!a.equals("")) {
                if (Integer.parseInt(a) <= 0) {
                    age.setError("Please enter a valid age.");
                    age.requestFocus();
                    return;
                }
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
            if (p.length() < 6) {
                password.setError("password Should be greater than 6");
                password.requestFocus();
                return;
            }
            if (!CheckBox.isChecked()) {
                CheckBox.setError("Agree To continue.");
                CheckBox.requestFocus();
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
                                String imageReference = "images/profile/provider/" + x;
                                StorageReference ref = storage.getReference().child(imageReference);
                                Message("Registration successful!");
                                DocumentReference df = fireStore.collection("provider")
                                        .document(Objects.requireNonNull(fireAuth.getCurrentUser()).getUid());
                                Map<String, Object> map = new HashMap<>();
                                Providers provider = new Providers(x,n, e, a, m, p, imageUrlString);
                                map.put(Providers.getProviderKey(),provider.getProviderId());
                                map.put(Providers.getNameKey(), provider.getName());
                                map.put(Providers.getAgeKey(), provider.getAge());
                                map.put(Providers.getEmailKey(), provider.getEmail());
                                map.put(Providers.getMobileKey(), provider.getMobile());
                                map.put(Providers.getPasswordKey(), provider.getPassword());
                                Bitmap bitmap = ((BitmapDrawable) profilepic.getDrawable()).getBitmap();
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
                                            providerImageUri = task1.getResult();
                                            Message("Image Upload");

                                        } else {
                                            // Handle failures
                                            // ...
                                        }
                                    }
                                });

                                imageUrlString = providerImageUri.toString();
                                Log.d("Image URL String",imageUrlString);
                                map.put(Providers.getImageKey(), imageUrlString);

                                df.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent=new Intent(getActivity(), MainProviderFragment.class);
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
            providerImageUri = data.getData();
            profilepic.setImageURI(providerImageUri);
        }
    }

    private void LoadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fT = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fT.replace(R.id.fragment_provider, fragment);
        fT.commit();
        // save the changes
    }
}