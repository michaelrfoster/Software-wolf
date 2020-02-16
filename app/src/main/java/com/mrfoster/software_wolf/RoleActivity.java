package com.mrfoster.software_wolf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RoleActivity extends AppCompatActivity {

    private static final String TAG = "RoleActivity";
    private static String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        ValueEventListener gameStateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((StaticVars.game_state.equals("role_state")) && dataSnapshot.getValue().toString().equals("day_state")) {
                    openDayActivity();
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

    @Override
    protected void onStart() {
        super.onStart();
        getRole();
    }

    private void getRole() {

        //final String[] role = new String[1];
        final TextView roleTextView = findViewById(R.id.roleTextView);
        Log.d(TAG, "I should have this role:" + String.valueOf(FirebaseDatabase.getInstance().getReference().child("players").child(StaticVars.playerId).child("role")));
        FirebaseDatabase.getInstance().getReference().child("players").child(StaticVars.playerId).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "YOU SHOULD BE HERE");
                if (StaticVars.game_state.equals("role_state")) {
                    Log.d(TAG, "dataSnapshot = " + dataSnapshot.toString() + "\n dataSnapshot Key = " + dataSnapshot.getKey().toString() + "\n dataSnapshot Value = " + dataSnapshot.getValue().toString());
                    role = dataSnapshot.getValue().toString();
                    roleTextView.setText(role);

                    switch (role != null ? role : "Villager") {
                        case "Villager":
                            StaticVars.player.setRole(Player.Role.Villager);
                            break;
                        case "Werewolf":
                            StaticVars.player.setRole(Player.Role.Werewolf);
                            break;
                        default:
                            StaticVars.player.setRole(Player.Role.Villager);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                role = null;
            }
        });

        Log.d(TAG, "The role is " + role);


        Log.d(TAG, "The text will say " + role);
    }

    /*public void ready(View view) {
        if (!StaticVars.player.isReady()) {
            StaticVars.player.setReady(true);
            FirebaseDatabase.getInstance().getReference().child("ready_players").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    FirebaseDatabase.getInstance().getReference().child("ready_players").setValue((long)dataSnapshot.getValue()+1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }*/

    public void openDayActivity() {
        StaticVars.game_state = "day_state";
        Intent intent = new Intent(this, DayActivity.class);
        startActivity(intent);
        finish();
    }

}
