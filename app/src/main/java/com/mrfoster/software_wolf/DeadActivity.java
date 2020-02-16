package com.mrfoster.software_wolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DeadActivity extends AppCompatActivity {

    private static final String TAG = "LobbyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dead);

        ValueEventListener gameStateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO Move to the next activity
                // Log.w(TAG, "Data has been changed to " +  dataSnapshot.getValue() + ".equals(1) which equals " +  dataSnapshot.getValue().toString().equals("1") + " and the gameStateReference is " + StaticVars.gameStateReference.child("game_state") + " \n(StaticVars.game_state == 0) =" + (StaticVars.game_state == 0));
                if ((StaticVars.game_state.equals("dead_state")) && dataSnapshot.getValue().toString().equals("lobby_state")) {
                    openLobbyActivity();
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

    private void openLobbyActivity() {
        StaticVars.game_state = "lobby_state";
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
        finish();
    }
}
