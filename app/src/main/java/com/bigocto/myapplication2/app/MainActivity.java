package com.bigocto.myapplication2.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;


public class MainActivity extends ActionBarActivity implements View.OnTouchListener ,View.OnClickListener{

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ViewFlipper mViewFlipper;
    private float touchDownRawY;  // 手指按下的Y坐标
    private float touchDownY;
    private float touchUpY;  //手指松开的Y坐标
    private float touchMoveY;
    private View view1;
    private View view2;
    private int flipperViewCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        setContentView(view);

        mViewFlipper = (ViewFlipper) findViewById(R.id.body_flipper);
        view.setOnTouchListener(this);
        view.setOnClickListener(this);
        view1 = findViewById(R.id.layout01);
//        view1.setOnTouchListener(this);
        view2 = findViewById(R.id.layout02);
//        view2.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // screen width px
        int height = metric.heightPixels;  // screen height px
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownRawY = event.getRawY();
                touchDownY = event.getY();
                flipperViewCount = mViewFlipper.getChildCount();
                //If have previous view
                if (mViewFlipper.getDisplayedChild() > 0) {
                    View leftChild = mViewFlipper.getChildAt(mViewFlipper.getDisplayedChild() - 1);
                    //You must set the top view to invisible or visible
                    //or it will not move to the position you tell it
                    leftChild.setVisibility(View.INVISIBLE);
                    leftChild.layout(leftChild.getLeft(),
                            -height, leftChild.getRight(),
                            0);
                }
                //If have the next view
                if (mViewFlipper.getDisplayedChild() < flipperViewCount - 1) {
                    View rightChild = mViewFlipper.getChildAt(mViewFlipper.getDisplayedChild() + 1);
                    rightChild.setVisibility(View.INVISIBLE);
                    rightChild.layout(rightChild.getLeft(),
                            height, rightChild.getRight(),
                            height * 2);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                View currentView = mViewFlipper.getCurrentView();
                if (0 == mViewFlipper.getDisplayedChild() && 1 == flipperViewCount) {
                    return false;
                }
                //If display view don't have previous, can pull up ,can't drop down
                if (0 == mViewFlipper.getDisplayedChild()) {
                    if ((event.getRawY() - touchDownRawY) < 0) {
                        currentView.layout(currentView.getLeft(),
                                (int) (event.getRawY() - touchDownRawY), currentView.getRight(),
                                (int) (event.getRawY() - touchDownRawY + height));
                    }
                }
                if (flipperViewCount - 1 == mViewFlipper.getDisplayedChild()) {
                    if ((event.getRawY() - touchDownRawY) > 0) {
                        currentView.layout(currentView.getLeft(),
                                (int) (event.getRawY() - touchDownRawY), currentView.getRight(),
                                (int) (event.getRawY() - touchDownRawY + height));
                    }
                }

                //The next view move when current view move
                if (mViewFlipper.getDisplayedChild() < flipperViewCount - 1) {
                    View nextView = mViewFlipper.getChildAt(mViewFlipper.getDisplayedChild() + 1);
                    nextView.layout(nextView.getLeft(),
                            (int) (event.getY() - touchDownY + height), currentView.getRight(),
                            (int) (event.getY() - touchDownY + height * 2));
                    if (nextView.getVisibility() == View.INVISIBLE) {
                        nextView.setVisibility(View.VISIBLE);
                    }
                }

                //The previous view move when current view move
                if (mViewFlipper.getDisplayedChild() > 0) {
                    View previous = mViewFlipper.getChildAt(mViewFlipper.getDisplayedChild() - 1);
                    previous.layout(previous.getLeft(),
                            (int) (event.getY() - touchDownY - height), currentView.getRight(),
                            (int) (event.getY() - touchDownY));
                    if (previous.getVisibility() == View.INVISIBLE) {
                        previous.setVisibility(View.VISIBLE);
                    }
                }

                return true;


            case MotionEvent.ACTION_UP:
                float releasePoint = event.getRawY();

                if (Math.abs(touchDownRawY - releasePoint) > 0) {
                    if (touchDownRawY > releasePoint && mViewFlipper.getDisplayedChild() < flipperViewCount -1) {
                        myNextAnimation(height);
                        mViewFlipper.showNext();
                    }
                    if (touchDownRawY < releasePoint && mViewFlipper.getDisplayedChild() > 0){
                        myPreviousAnimation(height);
                        mViewFlipper.showPrevious();
                    }
                }
                return true;
        }
        return false;
    }

    private void myNextAnimation(int height) {
        TranslateAnimation in = new TranslateAnimation(0, 0, mViewFlipper.getChildAt(mViewFlipper.getDisplayedChild() + 1).getTop(), 0);
        in.setDuration(400);
        TranslateAnimation out = new TranslateAnimation(0,0, 0, 0);
        out.setDuration(400);
        mViewFlipper.setInAnimation(in);
        mViewFlipper.setOutAnimation(out);
    }

    private void myPreviousAnimation(int height) {
        TranslateAnimation in = new TranslateAnimation(0, 0, 0, 0);
        in.setDuration(400);
        TranslateAnimation out = new TranslateAnimation(0,0, mViewFlipper.getCurrentView().getTop(), height);
        out.setDuration(400);
        mViewFlipper.setInAnimation(in);
        mViewFlipper.setOutAnimation(out);
    }

    @Override
    public void onClick(View view) {

    }
}
