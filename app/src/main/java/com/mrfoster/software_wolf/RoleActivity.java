package com.mrfoster.software_wolf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class RoleActivity extends AppCompatActivity {

    private static final String TAG = "RoleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        ValueEventListener gameStateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO Move to the next activity
                // Log.w(TAG, "Data has been changed to " +  dataSnapshot.getValue() + ".equals(1) which equals " +  dataSnapshot.getValue().toString().equals("1") + " and the gameStateReference is " + StaticVars.gameStateReference.child("game_state") + " \n(StaticVars.game_state == 0) =" + (StaticVars.game_state == 0));
                if ((StaticVars.game_state == 1) && dataSnapshot.getValue().toString().equals("2")) {
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

        final String[] role = new String[1];
        final TextView roleTextView = findViewById(R.id.roleTextView);
        FirebaseDatabase.getInstance().getReference().child(StaticVars.playerId).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "YOU SHOULD BE HERE");
                if (StaticVars.game_state == 1) {
                    role[0] = dataSnapshot.getValue().toString();
                    roleTextView.setText(role[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                role[0] = null;
            }
        });

        switch (role[0] != null ? role[0] : "Villager") {
            case "Villager":
                StaticVars.player.setRole(Player.Role.Villiager);
                break;
            case "Werewolf":
                StaticVars.player.setRole(Player.Role.Werewolf);
                break;
            default:
                StaticVars.player.setRole(Player.Role.Villiager);
                break;
        }

        Log.d(TAG, "The text will say " + role[0]);
    }

    public void ready(View view) {
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
    }

    public void openDayActivity() {
        StaticVars.game_state = 2;
        Intent intent = new Intent(this, DayActivity.class);
        startActivity(intent);
        finish();
    }

}
