package com.example.woolky.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.woolky.FriendsInvitesListener;
import com.example.woolky.R;
import com.example.woolky.domain.FriendsInvite;
import com.example.woolky.domain.InviteState;
import com.example.woolky.domain.User;
import com.example.woolky.domain.games.EscapeRoomGameInvite;
import com.example.woolky.domain.games.GameInvite;
import com.example.woolky.domain.games.GameInvitesListener;
import com.example.woolky.domain.games.escaperooms.EscapeRoom;
import com.example.woolky.domain.games.escaperooms.EscapeRoomGame;
import com.example.woolky.domain.games.tictactoe.TicTacToeGame;
import com.example.woolky.ui.games.escaperooms.PlayEscapeRoomFragment;
import com.example.woolky.ui.games.tictactoe.PlayTicTacToeFragment;
import com.example.woolky.ui.home.HomeFragment;
import com.example.woolky.ui.map.VicinityMapFragment;
import com.example.woolky.ui.profile.ProfileFragment;
import com.example.woolky.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    private static final int FINE_LOCATION_CODE = 114;

    private Handler handler;
    private DatabaseReference databaseRef;
    private Context cx = this;
    private GameInvitesListener gameListener;
    private FriendsInvitesListener friendListener;
    private User signedInUser;
    public boolean isPlaying;
    private BottomNavigationView bottomNav;

    private List<User> users;
    private boolean permissionsGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        permissionsGranted = Utils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);

        handler = new Handler();
        users = new ArrayList<>();

        bottomNav = findViewById(R.id.navigation_bottom);
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
            item -> {
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
            };

    public void changeToMap() {
        bottomNav.setSelectedItemId(R.id.nav_map);
    }

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
                    Toast.makeText(getBaseContext(), "The invite was " + inviteState.toString(), Toast.LENGTH_SHORT).show();
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

    public void setListenerToGameInvite(String gameId, DatabaseReference inviteStateRef, GameInvite invite) {
        inviteStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                InviteState inviteState = snapshot.getValue(InviteState.class);
                if (inviteState != InviteState.SENT) {
                    if (inviteState == InviteState.DECLINED)
                        Toast.makeText(getBaseContext(), "The invite was " + inviteState.toString(), Toast.LENGTH_SHORT).show();

                    if (inviteState == InviteState.ACCEPTED && !isPlaying) {
                        isPlaying = true;
                        switch (invite.getGameMode()) {
                            case TIC_TAC_TOE: {
                                setupTicTacToeGame(gameId, false);
                                break;
                            }
                            case ESCAPE_ROOM: {
                                setupEscapeRoomGame(gameId, ((EscapeRoomGameInvite) invite).getEscapeRoomId(),
                                        invite.getFromId(), ((EscapeRoomGameInvite) invite).getPlayersIds(), false);
                                break;
                            }
                        }
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

    public void setupEscapeRoomGame(String gameId, String escapeRoomId, String escapeRoomOwnerId, List<String> playersIds, boolean isReceiver) {
        DatabaseReference gameRef = databaseRef.child("games").child(gameId);

        databaseRef.child("escapeRooms").child(escapeRoomOwnerId).child(escapeRoomId)
                .get().addOnSuccessListener(
                        dataSnapshot -> {
                            EscapeRoom escapeRoom = dataSnapshot.getValue(EscapeRoom.class);
                            EscapeRoomGame escapeRoomGame = new EscapeRoomGame(escapeRoom, playersIds);

                            if (!isReceiver)
                                gameRef.setValue(escapeRoomGame);

                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                                new PlayEscapeRoomFragment(gameRef, escapeRoomGame)).commit();
            });
    }

    public void setupTicTacToeGame(String gameID, boolean isReceiver) {
        TicTacToeGame.Piece piece = isReceiver ? TicTacToeGame.Piece.X : TicTacToeGame.Piece.O;
        TicTacToeGame ticTacToeGame = new TicTacToeGame(2, signedInUser.getCurrentPosition().getLatLng(), piece);

        DatabaseReference gameRef = databaseRef.child("games").child(gameID);

        PlayTicTacToeFragment playTicTacToeFragment = new PlayTicTacToeFragment(gameRef, ticTacToeGame);

        Bundle bundle = new Bundle();
        bundle.putBoolean("isReceiver", isReceiver);
        playTicTacToeFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, playTicTacToeFragment).commitNow();
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

    public DatabaseReference getDatabaseReference() {
        return databaseRef;
    }

    public boolean isPermissionsGranted() {
        return permissionsGranted;
    }

    public void setPermissionsGranted(boolean permissionsGranted) {
        this.permissionsGranted = permissionsGranted;
    }
}