package com.universio.humack;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Cyril Humbertclaude on 25/08/2015.
 */
public class SplashscreenFragment extends Fragment {


    public SplashscreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splashscreen, container, false);
    }
}
