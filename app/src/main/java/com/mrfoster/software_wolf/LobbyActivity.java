package com.mrfoster.software_wolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LobbyActivity extends AppCompatActivity {

    public static int SIGN_IN_REQUEST_CODE = 10;
    private static final String TAG = "LobbyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_lobby);
        StaticVars.gameStateReference = FirebaseDatabase.getInstance().getReference().child("game_state");

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(), SIGN_IN_REQUEST_CODE);
        } else {
            // User is already signed in, therefore, display a welcome Toast
            Toast.makeText(this, "Welcome " +
                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                    Toast.LENGTH_LONG).show();
        }


        StaticVars.player = new Player(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        DatabaseReference temp = FirebaseDatabase.getInstance().getReference().child("players").push();
        temp.setValue(StaticVars.player);
        StaticVars.playerId = temp.getKey();
        Log.v(TAG, "The playerId is " + StaticVars.playerId);
        StaticVars.game_state = "lobby_state";


        ValueEventListener gameStateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO Move to the next activity
                // Log.w(TAG, "Data has been changed to " +  dataSnapshot.getValue() + ".equals(1) which equals " +  dataSnapshot.getValue().toString().equals("1") + " and the gameStateReference is " + StaticVars.gameStateReference.child("game_state") + " \n(StaticVars.game_state == 0) =" + (StaticVars.game_state == 0));
                if ((StaticVars.game_state.equals("lobby_state")) && dataSnapshot.getValue().toString().equals("role_state")) {
                    openRoleActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        StaticVars.gameStateReference.addValueEventListener(gameStateListener);
    }

    public void openRoleActivity() {
        StaticVars.game_state = "role_state";
        Intent intent = new Intent(this, RoleActivity.class);
        startActivity(intent);
        finish();
    }
}
