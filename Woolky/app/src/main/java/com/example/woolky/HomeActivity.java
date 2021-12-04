package com.example.woolky;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.woolky.ui.home.HomeFragment;
import com.example.woolky.ui.map.VicinityMapFragment;
import com.example.woolky.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends AppCompatActivity {

    private FirebaseUser currentUser;

    private BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selected = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selected = new HomeFragment();

                            Bundle user = new Bundle();
                            user.putParcelable("current_user", currentUser);

                            selected.setArguments(user);
                            break;

                        case R.id.nav_map:
                            selected = new VicinityMapFragment();
                            break;

                        case R.id.nav_profile:
                            selected = new ProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, selected).commit();

                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent login = getIntent();
        currentUser = login.getParcelableExtra("current_user");

        //testar
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//ijoijj

        BottomNavigationView bottomNav = findViewById(R.id.navigation_bottom);
        bottomNav.setOnItemSelectedListener(navListener);

        Bundle user = new Bundle();
        user.putParcelable("current_user", currentUser);

        HomeFragment home = new HomeFragment();
        home.setArguments(user);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment, home ).commit();



    }

    private void updateUiWithCurrentUser() {


    }

    public void logout(){
        startActivity(new Intent(HomeActivity.this, SplashScreenActivity.class));
        finish();
    }




}