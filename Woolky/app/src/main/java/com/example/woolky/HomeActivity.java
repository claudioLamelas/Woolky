package com.example.woolky;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.woolky.domain.FriendsInvite;
import com.example.woolky.domain.GameMode;
import com.example.woolky.domain.InviteState;
import com.example.woolky.domain.TicTacToe;
import com.example.woolky.domain.User;
import com.example.woolky.ui.home.HomeFragment;
import com.example.woolky.ui.map.GameModeFragment;
import com.example.woolky.ui.map.VicinityMapFragment;
import com.example.woolky.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeActivity extends AppCompatActivity {
    private Handler handler;
    private DatabaseReference databaseRef;
    private Context cx = this;
    private GameInvitesListener gameListener;
    private FriendsInvitesListener friendListener;
    private User signedInUser;

    private List<User> users;    //Talvez não seja a classe mais indicada para ter isto, mas por agora fica aqui


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        handler = new Handler();
        users = new ArrayList<>();

        BottomNavigationView bottomNav = findViewById(R.id.navigation_bottom);
        bottomNav.setOnItemSelectedListener(navListener);

        String userId = getIntent().getStringExtra("userId");

        databaseRef = FirebaseDatabase.getInstance("https://woolky-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference usersRef = databaseRef.child("users");
        usersRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    User user = d.getValue(User.class);
                    users.add(user);
                }
            }
        });
        usersRef.child(userId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                signedInUser = dataSnapshot.getValue(User.class);
                getSupportFragmentManager().beginTransaction().add(R.id.fragment, new HomeFragment()).commit();
            }
        });

        DatabaseReference gameInvitesRef = databaseRef.child("gameInvites").child(userId);
        DatabaseReference friendInviteRef = databaseRef.child("friendInvite").child(userId);

        gameListener = new GameInvitesListener(cx, getSupportFragmentManager());
        gameInvitesRef.addChildEventListener(gameListener);
        friendListener = new FriendsInvitesListener(cx, getSupportFragmentManager());
        friendInviteRef.addChildEventListener(friendListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        signedInUser.setCurrentPosition(null);
        DatabaseReference userRef = databaseRef.child("users").child(signedInUser.getUserId());
        userRef.setValue(signedInUser);
        DatabaseReference gameInvitesRef = databaseRef.child("gameInvites").child(signedInUser.getUserId());
        gameInvitesRef.removeEventListener(gameListener);
        handler.removeCallbacksAndMessages(null);
    }


    private BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selected = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selected = new HomeFragment();
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

    public void logout() {
        startActivity(new Intent(HomeActivity.this, SplashScreenActivity.class));
        finish();
    }
    public void setListenerFriendsInvite(String inviteId, DatabaseReference inviteStateRef, String toUserId) {
        inviteStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                InviteState inviteState = snapshot.getValue(InviteState.class);
                if (inviteState != InviteState.SENT) {
                    if (inviteState == InviteState.DECLINED) {
                        Toast.makeText(getBaseContext(), "The invite was " + inviteState.toString(), Toast.LENGTH_SHORT).show();
                    }
                    if (inviteState == InviteState.ACCEPTED) {
                        setupFriend(inviteId,false, toUserId);
                    }
                    inviteStateRef.removeEventListener(this);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        });
    }

    public void setListenerToGameInvite(String inviteId, DatabaseReference inviteStateRef) {
        inviteStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                InviteState inviteState = snapshot.getValue(InviteState.class);
                if (inviteState != InviteState.SENT) {
                    if (inviteState == InviteState.DECLINED) {
                        Toast.makeText(getBaseContext(), "The invite was " + inviteState.toString(), Toast.LENGTH_SHORT).show();
                    }

                    if (inviteState == InviteState.ACCEPTED) {
                        setupGame(inviteId, GameMode.TIC_TAC_TOE, false);
                    }
                    inviteStateRef.removeEventListener(this);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        });
    }
    public void setupFriend(String friendInviteID, boolean isReceiver, String toUserId) {

        List<String> friends = signedInUser.getFriends();

        if (friends == null){
            friends = new ArrayList<>();
        }

        if (isReceiver) {
            DatabaseReference friendRef = databaseRef.child("friendInvite").child(signedInUser.getUserId()).child(friendInviteID);
            List<String> finalFriends = friends;
            friendRef.get().addOnSuccessListener(dataSnapshot -> {
                FriendsInvite friendsInvite = dataSnapshot.getValue(FriendsInvite.class);
                finalFriends.add(friendsInvite.getFrom_id());
                signedInUser.setFriends(finalFriends);
                databaseRef.child("users").child(signedInUser.getUserId()).setValue(signedInUser);
            });
        }else{
            DatabaseReference friendRef = databaseRef.child("friendInvite").child(toUserId).child(friendInviteID);
            List<String> finalFriends = friends;
            friendRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    FriendsInvite friendsInvite = dataSnapshot.getValue(FriendsInvite.class);
                    finalFriends.add(toUserId);
                    signedInUser.setFriends(finalFriends);
                    databaseRef.child("users").child(signedInUser.getUserId()).setValue(signedInUser);
                }
            });
        }
    }

    public void setupGame(String gameInviteID, GameMode gameMode, boolean isReceiver) {
        if (gameMode == GameMode.TIC_TAC_TOE) {
            TicTacToe.Piece piece = isReceiver ? TicTacToe.Piece.X : TicTacToe.Piece.O;
            TicTacToe ticTacToe = new TicTacToe(2, signedInUser.getCurrentPosition().getLatLng(), piece);

            DatabaseReference gameRef = databaseRef.child("games").child(gameInviteID);

            GameModeFragment gameModeFragment = new GameModeFragment(gameRef, ticTacToe);
            /*gameModeFragment.setGameRef(gameRef);*/

            Bundle bundle = new Bundle();
            bundle.putBoolean("isReceiver", isReceiver);
            gameModeFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, gameModeFragment).commitNow();
        }
    }

    public DatabaseReference getDatabaseRef() {
        return databaseRef;
    }

    public User getSignedInUser() {
        return signedInUser;
    }

    public List<User> getUsers() { return users; }

    public void setUsers(List<User> users) {
        this.users = users;
        int secondsDelayed = 15;
        handler.postDelayed(() -> {
            this.users.clear();
        }, secondsDelayed * 1000);
    }

}