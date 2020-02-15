package com.mrfoster.software_wolf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RoleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        getRole();
    }

    private void getRole() {
        String role = FirebaseDatabase.getInstance().getReference().child(StaticVars.playerId).child("role").getKey();
        switch (role != null ? role : "Villager") {
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

        TextView roleTextView = findViewById(R.id.roleTextView);
        roleTextView.setText(role);
    }


}
