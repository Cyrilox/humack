package com.universio.humack;

import android.os.Bundle;
import android.widget.SeekBar;

/**
 * Created by Cyril Humbertclaude on 08/06/2015.
 */
public class SettingsActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener{

    private SeekBar animationSpeedBar;
    private long animationSpeedRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupActivity(R.layout.activity_settings, R.drawable.icon_settings);
        super.onCreate(savedInstanceState);
    }

    protected void init(){
        //Animations speed
        animationSpeedRange = Settings.getAnimationSpeedMax() - Settings.getAnimationSpeedMin();
        animationSpeedBar = (SeekBar)findViewById(R.id.activity_settings_animation_speed);
        int animationSpeedProgress = 100 - Math.round((Settings.getAnimationSpeed() - Settings.getAnimationSpeedMin()) * 100 / animationSpeedRange);
        animationSpeedBar.setProgress(animationSpeedProgress);
        animationSpeedBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if(seekBar == animationSpeedBar){
            long animationSpeed = Settings.getAnimationSpeedMin() + Math.round((100 - progress) * animationSpeedRange / 100);
            Settings.setAnimationSpeed(animationSpeed);
        }
    }
}
