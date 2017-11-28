package com.universio.humack;

import android.content.SharedPreferences;
import android.content.res.Resources;

/**
 * Created by Cyril Humbertclaude on 16/06/2015.
 */
public class Settings {

    private static SharedPreferences sharedPreferences;
    private static long animationSpeedMin, animationSpeedMax, animationSpeed;

    public Settings(SharedPreferences sharedPreferences, Resources resources){
        Settings.sharedPreferences = sharedPreferences;
        init(resources);
    }

    private void init(Resources resources){
        animationSpeedMin = (long)resources.getInteger(R.integer.animation_speed_min);
        animationSpeedMax = (long)resources.getInteger(R.integer.animation_speed_max);
        long animationSpeedDefault = (long)resources.getInteger(R.integer.animation_speed_default);
        animationSpeed = sharedPreferences.getLong("animation_speed", animationSpeedDefault);
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

    public static void setAnimationSpeed(long animationSpeed) {
        Settings.animationSpeed = animationSpeed;
        save();
    }

    private static void save(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("animation_speed", animationSpeed);

        editor.apply();
    }
}
