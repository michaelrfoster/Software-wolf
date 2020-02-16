package com.mrfoster.software_wolf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class DayActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
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
}
