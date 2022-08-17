package com.spapp.shadipedia.User;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.shadipedia.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.spapp.shadipedia.User_home_page;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserLoginFragment extends Fragment {
    private static final int RC_SIGN_IN = 123;
    TextView signin, froget;
    TextInputEditText email, password;
    TextView Reg_form;
    View view;

    SignInButton google;
    ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String emailPattern = "[a-zA-Z0-9._-]+(@gmail||@hotmail)+\\.+com+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_login_fragment, container, false);
        Reg_form = view.findViewById(R.id.Register);
        signin = view.findViewById(R.id.signin);
        froget = view.findViewById(R.id.ForgotuPassword);
        email = view.findViewById(R.id.editEmail);
        password = view.findViewById(R.id.editPassword);
        firebaseAuth = FirebaseAuth.getInstance();
        google = view.findViewById(R.id.gsign_in_button);
        createRequest();

        google.setOnClickListener(v -> {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Logging you in..."); // Setting Message
            progressDialog.setTitle("Login"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);
            Log.d("On click google", "login before google signin");
            googleSignIn();

        });

        signin.setOnClickListener(v -> {
            String e = email.getText().toString();
            String p = password.getText().toString();
            if (e.isEmpty()) {
                email.setError("Email is required");
                email.requestFocus();
                return;
            }
            if (p.isEmpty()) {
                password.setError("Password is required");
                password.requestFocus();
                return;
            }
            if (p.length() < 6) {
                password.setError("Password Should be greater than 6");
                password.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                email.setError("Enter Valid Email");
                email.requestFocus();
                return;
            }
            if (e.matches(emailPattern)) {
                Message("Working on it ...");
                Thread thread = new Thread(() -> firebaseAuth.signInWithEmailAndPassword(e, p).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    String pemail = (String) documentSnapshot.get("email");
                                    if (Objects.equals(pemail, e)) {
                                        Intent intent = new Intent(getActivity(), User_home_page.class);
                                        startActivity(intent);
                                        Message("Logged in Successful");

                                    } else {
                                        FirebaseAuth.getInstance().signOut();
                                        Message("Login failed! Please try again later or Create Account");
                                    }
                                }).addOnFailureListener(e1 -> {
                            Log.d("read user data", "on failure listener" + e1.toString());
                            Message("Failed to get user data");
                        });
                    } else {
                        Message("Login failed! Please try again later");
                    }
                }));
                thread.start();
            } else {
                Message("Invalid email address");
            }
        });

        Reg_form.setOnClickListener(v -> LoadFragment(new UserRegisterFragment()));

        froget.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), User_Forget_Password.class);
            startActivity(intent);
        });
        return view;
    }

    private void Message(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
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

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("889034131197-knekvblvqta3bn2lk3nplmftvro7fcv3.apps.googleusercontent.com")
                .requestEmail()
                .build();
        Log.d("After create request", "before mgoogle");
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.d("On activity result", "if rc sign in");
                // Google Sign In was successful, authenticate with Firebase
                // progressDialog.setMessage("Google signed in");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Log.d("On activity result", "exception");
                progressDialog.setMessage("Google Authentication failed.");
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog);
                builder.setTitle("Login Error");
                Log.d("Error", "" + e);
                builder.setMessage("Error : " + e.getMessage());
                builder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                builder.create().show();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("firebaseauthwithgoogle", "start");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), task -> {
            Log.d("firebaseauthwithgoogle", "on complete");
            if (task.isSuccessful()) {
                Log.d("firebaseauthwithgoogle", "if success");
                progressDialog.setMessage("Getting Your data...");
                //Write user data to Cloud firestore and shared pref
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String gname = acct.getDisplayName();
                String gemail = acct.getEmail();
                String gphoto = acct.getPhotoUrl().toString();
                String gid = firebaseUser.getUid();
                String  gage = "Null";

                //Make map
                Map<String, Object> map = new HashMap<>();
                map.put("name", gname);
                map.put("email", gemail);
                map.put("image", gphoto);
                map.put("userId", gid);
                map.put("age", gage);
                map.put("mobile",gage);

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();


                //Sign in success, update UI with the signed-in user's information
                firestore.collection("provider").document(firebaseUser.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String pemail = (String) documentSnapshot.get("email");
                            if (Objects.equals(pemail, gemail)) {
                                FirebaseAuth.getInstance().signOut();
                                Message("Login failed! Please try again later or Create Account");
                                progressDialog.dismiss();
                            } else {
                                firestore.collection("users").document(firebaseUser.getUid()).set(map);
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                Intent intent = new Intent(getActivity(), User_home_page.class);
                                startActivity(intent);
                                Message("Logged in Successful");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e1) {
                        Log.d("read user data", "on failure listener" + e1.toString());
                        Message("Failed to get provider data");
                    }
                });
                //Cancel progress dialog
            } else {
                //cancel progress dialog
                Log.d("auth with google", "else");
                Log.d("firebaseauthwithgoogle", task.getException().toString());
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog);
                builder.setTitle("Login Error");
                builder.setMessage("Google Sign in Error : " + task.getException().getMessage());
                builder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        });
    }
}