package com.project.bindi;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    Context context;

    public DetectSwipeGestureListener(Context context) {
        this.context = context;
    }

    // Minimal x and y axis swipe distance.
    private static int MIN_SWIPE_DISTANCE_X = 100;
    private static int MIN_SWIPE_DISTANCE_Y = 100;

    // Maximal x and y axis swipe distance.
    private static int MAX_SWIPE_DISTANCE_X = 1000;
    private static int MAX_SWIPE_DISTANCE_Y = 1000;

    // Source activity that display message in text view.
//    private DetectSwipeDirectionActivity activity = null;


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
        if((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X))
        {
            if(deltaX > 0)
            {
               // this.context.displayMessage("Swipe to left");
                Toast.makeText(context, "swipe to left", Toast.LENGTH_SHORT).show();
            }else
            {
                //this.activity.displayMessage("Swipe to right");
                Toast.makeText(context, "swipe to right", Toast.LENGTH_SHORT).show();
            }
        }

        if((deltaYAbs >= MIN_SWIPE_DISTANCE_Y) && (deltaYAbs <= MAX_SWIPE_DISTANCE_Y))
        {
            if(deltaY > 0)
            {
                //this.activity.displayMessage("Swipe to up");
                Toast.makeText(context, "swipe to up", Toast.LENGTH_SHORT).show();
            }else
            {
               // this.activity.displayMessage("Swipe to down");
                Toast.makeText(context, "swope to down", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    // Invoked when single tap screen.
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Toast.makeText(context, "Singlw tap confirmed", Toast.LENGTH_SHORT).show();
        return true;
    }

    // Invoked when double tap screen.
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Toast.makeText(context, "double tap confirmed", Toast.LENGTH_SHORT).show();
        return true;
    }
}
