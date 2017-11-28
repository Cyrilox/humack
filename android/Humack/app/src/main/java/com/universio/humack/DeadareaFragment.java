package com.universio.humack;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeadareaFragment extends Fragment {

    /**
     * Use this factory method to create a new instance of this fragment
     * @return A new instance of fragment AttitudeFragment.
     */
    public static DeadareaFragment newInstance() {
        return new DeadareaFragment();
    }

    public DeadareaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deadarea, container, false);
    }
}
