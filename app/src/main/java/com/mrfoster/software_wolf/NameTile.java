package com.mrfoster.software_wolf;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class NameTile {

    private String name;

    public NameTile(String name) {
        this.name = name;
    }

    public NameTile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
