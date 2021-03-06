package com.example.woolky.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.woolky.R;
import com.example.woolky.domain.ShareLocationType;
import com.example.woolky.domain.Statistics;
import com.example.woolky.domain.user.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_google);
        
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton google = findViewById(R.id.sign_in_button);

        google.setOnClickListener(v -> signIn());

        TextView textView = (TextView) google.getChildAt(0);
        textView.setText("Sign In");

        VideoView video = findViewById(R.id.login_video);
        video.setVideoPath("android.resource://" + getPackageName() + "/" +R.raw.login);
        video.setOnPreparedListener(mp -> mp.setLooping(true));
        video.start();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        Button b = findViewById(R.id.login_UI_BT);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) b.getLayoutParams();

        params.height = (int) (height * 0.45);

        b.setLayoutParams(new ConstraintLayout.LayoutParams(params));
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoView video = findViewById(R.id.login_video);
        video.setVideoPath("android.resource://" + getPackageName() + "/" +R.raw.login);
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        video.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseAuth.getInstance().signOut();
        //mGoogleSignInClient.signOut();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }

    private void updateUI(FirebaseUser account) {
        if (account != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("userId", account.getUid());
            startActivity(intent);
            finish();
        }


    }

    private void signIn() {
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        loginActivityResultLauncher.launch(signInIntent);
    }

    ActivityResultLauncher<Intent> loginActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ProgressBar bar = findViewById(R.id.progressBar);
                    bar.setVisibility(View.VISIBLE);
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Log.d("success", "firebaseAuthWithGoogle:" + account.getId());
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        Log.w("failed", "Google sign in failed", e);
                    }
                }
            });

    private void firebaseAuthWithGoogle(String idToken) {
        Log.d("TOKEN", idToken);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("success", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://woolky-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                            DatabaseReference userRef = databaseRef.child("users").child(user.getUid());
                            userRef.get().addOnSuccessListener(dataSnapshot -> {
                                boolean newAccount = !dataSnapshot.exists();
                                if (newAccount) {
                                    User newUser = new User(user.getUid(), user.getDisplayName(),
                                            0, R.color.user_default_color, ShareLocationType.ALL, user.getPhotoUrl().toString());
                                    Statistics statistics = new Statistics(0);
                                    newUser.setStats(statistics);
                                    userRef.setValue(newUser).addOnSuccessListener((unused -> updateUI(user)));
                                } else {
                                    updateUI(user);
                                }
                            });
                        } else {
                            Log.w("failed", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
}