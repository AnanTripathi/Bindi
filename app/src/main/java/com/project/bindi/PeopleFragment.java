package com.project.bindi;

import android.os.Bundle;

import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class PeopleFragment extends Fragment {

    // This is the gesture detector compat instance.
    private GestureDetectorCompat gestureDetectorCompat = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_people, container, false);


        DetectSwipeGestureListener listener = new DetectSwipeGestureListener(getActivity());
        // Create the gesture detector with the gesture listener.
        gestureDetectorCompat = new GestureDetectorCompat(getActivity(), listener);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Pass activity on touch event to the gesture detector.
                gestureDetectorCompat.onTouchEvent(event);
                // Return true to tell android OS that event has been consumed,
                // do not pass it to other event listeners.
                return true;
            }
        });
        
        return view;
    }
}