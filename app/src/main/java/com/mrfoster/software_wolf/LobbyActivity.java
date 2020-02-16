package com.mrfoster.software_wolf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

        //if (StaticVars.game_state != null && !StaticVars.game_state.equals("lobby_state")) {
        //    refusePlayer();
        //}

        setContentView(R.layout.activity_lobby);
        StaticVars.gameStateReference = FirebaseDatabase.getInstance().getReference().child("game_state");

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            Log.d(TAG, "its null");
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Log.d(TAG, "not null");
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
        }
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.d(TAG, "still null");
        }

        //EditText et = findViewById(R.id.changeNameExitText);
        //et.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        //StaticVars.player = new Player(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        StaticVars.player = new Player("Jeffery");
        DatabaseReference temp = FirebaseDatabase.getInstance().getReference().child("players").push();
        temp.setValue(StaticVars.player);
        StaticVars.playerId = temp.getKey();
        Log.v(TAG, "The playerId is " + StaticVars.playerId);
        StaticVars.game_state = "lobby_state";

        FirebaseDatabase.getInstance().getReference().child("players").child(StaticVars.playerId).child("dead").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getValue().equals("true")) {
                    FirebaseDatabase.getInstance().getReference().child("players").child(StaticVars.playerId).removeValue();
                    openDeadActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        final TextView playerCountTextView = findViewById(R.id.playerCountTextView);
        FirebaseDatabase.getInstance().getReference().child("players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playerCountTextView.setText(Long.toString(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openDeadActivity() {
        StaticVars.game_state = "dead_state";
        Intent intent = new Intent(this, DeadActivity.class);
        startActivity(intent);
        finish();
    }

    private void refusePlayer() {
        Toast.makeText(this, "Sorry, game already in session! Try again later", Toast.LENGTH_LONG).show();
        finish();
    }

    public void openRoleActivity() {
        StaticVars.game_state = "role_state";
        Intent intent = new Intent(this, RoleActivity.class);
        startActivity(intent);
        finish();
    }

    public void setName(View v) {
        EditText changeNameEditText = findViewById(R.id.changeNameExitText);
        StaticVars.player.setName(changeNameEditText.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("players").child(StaticVars.playerId).child("name").setValue(StaticVars.player.getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG).show();
                // Close the app
                finish();
            }
        }
    }

}
