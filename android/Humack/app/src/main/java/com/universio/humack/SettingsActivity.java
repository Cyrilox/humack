package com.universio.humack;

import android.view.View;
import android.widget.SeekBar;

/**
 * Created by Cyril Humbertclaude on 08/06/2015.
 */
public class SettingsActivity extends ActivityFragment implements SeekBar.OnSeekBarChangeListener{

    private SeekBar animationSpeedBar;
    private long animationSpeedRange;

    public static SettingsActivity newInstance(){
        return new SettingsActivity();
    }

    /**
     * Called in order to create the view
     * @return The layout resource id
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    @Override
    public void init(){
        View rootView = getView();
        //Animations speed
        animationSpeedRange = Settings.getAnimationSpeedMax() - Settings.getAnimationSpeedMin();
        if(rootView != null)
            animationSpeedBar = (SeekBar)rootView.findViewById(R.id.activity_settings_animation_speed);
        int animationSpeedProgress = 100 - Math.round((Settings.getAnimationSpeed() - Settings.getAnimationSpeedMin()) * 100 / animationSpeedRange);
        animationSpeedBar.setProgress(animationSpeedProgress);
        animationSpeedBar.setOnSeekBarChangeListener(this);

        //Fermeture de l'écran de chargement
        mainActivity.closeSplashscreen();
    }

    @Override
    public boolean onBackPressed() {
        return false;
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
