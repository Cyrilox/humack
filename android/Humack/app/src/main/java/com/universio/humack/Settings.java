package com.universio.humack;

import android.content.SharedPreferences;
import android.content.res.Resources;

/**
 * Created by Cyril Humbertclaude on 16/06/2015.
 */
public class Settings {

    private static SharedPreferences sharedPreferences;
    private static long animationSpeedMin, animationSpeedMax, animationSpeed, animationDrawerSpeed;
    private static boolean synergologyGuide;

    public Settings(SharedPreferences sharedPreferences, Resources resources){
        Settings.sharedPreferences = sharedPreferences;
        init(resources);
    }

    private void init(Resources resources){
        animationSpeedMin = (long)resources.getInteger(R.integer.animation_speed_min);
        animationSpeedMax = (long)resources.getInteger(R.integer.animation_speed_max);
        long animationSpeedDefault = (long)resources.getInteger(R.integer.animation_speed_default);
        animationSpeed = sharedPreferences.getLong("animation_speed", animationSpeedDefault);
        animationDrawerSpeed = (long)resources.getInteger(R.integer.animation_drawer_speed);
        synergologyGuide = sharedPreferences.getBoolean("synergology_guide", false);
        save();
    }

    public static long getAnimationSpeedMin() {
        return animationSpeedMin;
    }

    public static long getAnimationSpeedMax() {
        return animationSpeedMax;
    }

    public static long getAnimationSpeed() {
        return animationSpeed;
    }

    public static long getAnimationDrawerSpeed() {
        return animationDrawerSpeed;
    }

    public static void setAnimationSpeed(long animationSpeed) {
        Settings.animationSpeed = animationSpeed;
        save();
    }

    public static boolean isSynergologyGuide() {
        return synergologyGuide;
    }

    public static void setSynergologyGuide(boolean synergologyGuide) {
        Settings.synergologyGuide = synergologyGuide;
        save();
    }

    private static void save(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("animation_speed", animationSpeed);
        editor.putBoolean("synergology_guide", synergologyGuide);


        editor.apply();
    }
}
