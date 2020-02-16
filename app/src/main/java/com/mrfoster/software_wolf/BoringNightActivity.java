package com.mrfoster.software_wolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class BoringNightActivity extends AppCompatActivity {

    private static final String TAG = "BoringNightActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boring_night);

        ValueEventListener gameStateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "The state is " + StaticVars.game_state.toString());
                if ((StaticVars.game_state.equals("night_state")) && dataSnapshot.getValue().toString().equals("day_state")) {
                    openDayActivity();
                } else if ((StaticVars.game_state.equals("night_state")) && dataSnapshot.getValue().toString().equals("result_state")) {
                    openResultsActivity();
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

    private void openDayActivity() {
        StaticVars.game_state = "day_state";
        Intent intent = new Intent(this, DayActivity.class);
        startActivity(intent);
        finish();
    }

    private void openResultsActivity() {
        StaticVars.game_state = "result_state";
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
        finish();
    }
}
