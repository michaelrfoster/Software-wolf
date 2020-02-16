package com.mrfoster.software_wolf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.List;

public class DayActivity extends AppCompatActivity {

    private static final String TAG = "DayActivity";

    private FirebaseListAdapter<NameTile> adapter = new FirebaseListAdapter<NameTile>(this, NameTile.class,
            R.layout.name_tile, FirebaseDatabase.getInstance().getReference().child("players")) {
        @Override
        protected void populateView(View v, NameTile model, int position) {

            try {

                // Get references to the views of message.xml
                TextView name = (TextView) v.findViewById(R.id.name);
                // Set their text
                name.setText(model.getName());
            } catch (Exception e) {
                return;
            }
        }
    };

    private String prevName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        ListView lv = findViewById(R.id.nameListView);
        final TextView tv = findViewById(R.id.whoKillTextView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                final String selectedItem = ((NameTile) parent.getItemAtPosition(position)).getName();
                // Display the selected item text on TextView
                tv.setText(selectedItem);
                final DatabaseReference temp = FirebaseDatabase.getInstance().getReference().child("players").child(StaticVars.playerId).child("vote");
                temp.setValue(selectedItem);
            }
        });

        ValueEventListener gameStateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((StaticVars.game_state.equals("day_state")) && dataSnapshot.getValue().toString().equals("lynch_state")) {
                    openLynchActivity();
                } else if ((StaticVars.game_state.equals("day_state")) && dataSnapshot.getValue().toString().equals("night_state")) {
                    openNightActivity();
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
        displayNameTiles();
    }

    private void displayNameTiles() {
        ListView listOfNames = (ListView)findViewById(R.id.nameListView);

        listOfNames.setAdapter(adapter);
    }

    private void openNightActivity() {
        StaticVars.game_state = "night_state";
        Intent intent = new Intent(this, NightActivity.class);
        startActivity(intent);
        finish();
    }

    private void openLynchActivity() {
        StaticVars.game_state = "lynch_state";
        Intent intent = new Intent(this, LynchActivity.class);
        startActivity(intent);
        finish();
    }
}
