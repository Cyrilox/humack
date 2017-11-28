package com.universio.humack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.universio.humack.synergology.SynergologyActivity;

public class MainActivity extends Activity {

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = new Settings(getSharedPreferences("user_preferences", 0), getResources());

        startActivity(new Intent(this, SynergologyActivity.class));
    }
}
