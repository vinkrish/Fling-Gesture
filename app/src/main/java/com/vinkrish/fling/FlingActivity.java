package com.vinkrish.fling;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class FlingActivity extends AppCompatActivity {
    private FrameLayout podium;
    private TextView queueView;
    private RelativeLayout bottomLayout;
    private int score, wrongScore;
    private TextView yourScore, yourWrongScore, oddPlusOne, evenPlusOne;
    private ImageView oddTick, oddWrong, evenTick, evenWrong;
    private GestureDetectorCompat mDetector;
    //private GestureDetector mDetector;
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_THRESHOLD_VELOCITY = 60;
    private MotionEvent motionEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fling);

        initView();

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        //mDetector = new GestureDetector(new MyGestureListener());

        podium.post(new Runnable() {
            @Override
            public void run() {
                generateAndAnimate();
            }
        });

        //findViewById(R.id.bottomLayout).setOnDragListener(new DragListener());
    }

    private void initView(){
        podium = (FrameLayout) findViewById(R.id.podium);
        bottomLayout = (RelativeLayout)findViewById(R.id.bottomLayout);
        yourScore = (TextView) findViewById(R.id.your_score);
        yourWrongScore = (TextView) findViewById(R.id.your_wrong_score);
        oddPlusOne = (TextView) findViewById(R.id.odd_plus_one);
        evenPlusOne = (TextView) findViewById(R.id.even_plus_one);
        oddTick = (ImageView) findViewById(R.id.odd_tick);
        oddWrong = (ImageView) findViewById(R.id.odd_wrong);
        evenTick = (ImageView) findViewById(R.id.even_tick);
        evenWrong = (ImageView) findViewById(R.id.even_wrong);
    }

    private void generateAndAnimate(){

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ABOVE, podium.getId());
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);

        queueView = new TextView(getApplicationContext());
        queueView.setTag("dnd");
        queueView.setLayoutParams(lp);
        queueView.setText(generateNumber() + "");
        queueView.setTextSize(50);
        queueView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.lightBlack));
        bottomLayout.addView(queueView);
        queueView.setVisibility(View.INVISIBLE);

        queueView.post(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.alphaTranslate(queueView, getApplicationContext());
            }
        });

        queueView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                /*ClipData.Item item = new ClipData.Item((CharSequence)view.getTag());

                String[] mimeTypes = { ClipDescription.MIMETYPE_TEXT_PLAIN };
                ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                view.startDrag(data, shadowBuilder, view, 0);

                view.setVisibility(View.INVISIBLE);*/

                motionEvent = event;
                mDetector.onTouchEvent(event);

                return true;
            }
        });

    }

    private int generateNumber(){
        Random r = new Random();
        int Low = 1;
        int High = 100;
        return r.nextInt(High-Low) + Low;
    }

    private boolean isItOdd(){
        try{
            int number = Integer.parseInt(queueView.getText().toString());
            if(number % 2 == 0) {
                return false;
            }
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean isItEven(){
        try{
            int number = Integer.parseInt(queueView.getText().toString());
            if(number % 2 == 0) {
                return true;
            }
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateYourScore() {
        yourScore.setText(score+"");
    }

    private void updateYourWrongScore() {
        yourWrongScore.setText(wrongScore+"");
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if(event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY &&
                    event2.getX()>event1.getX()) {

                if (isItEven()) {
                    AnimationUtil.alphaScaleInOut(evenTick, getApplicationContext());
                    AnimationUtil.alphaInOut(evenPlusOne, getApplicationContext());
                    score++;
                    updateYourScore();
                } else {
                    AnimationUtil.alphaScaleInOut(evenWrong, getApplicationContext());
                    wrongScore++;
                    updateYourWrongScore();
                }
                bottomLayout.removeView(queueView);
                generateAndAnimate();

                Log.d("fling from - ", "Bottom to Top & towards Right");
                return false; // Bottom to top and right fling
            } else if (event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY &&
                    event1.getX()>event2.getX()){

                if (isItOdd()) {
                    AnimationUtil.alphaScaleInOut(oddTick, getApplicationContext());
                    AnimationUtil.alphaInOut(oddPlusOne, getApplicationContext());
                    score++;
                    updateYourScore();
                } else {
                    AnimationUtil.alphaScaleInOut(oddWrong, getApplicationContext());
                    wrongScore++;
                    updateYourWrongScore();
                }
                bottomLayout.removeView(queueView);
                generateAndAnimate();

                Log.d("fling from - ", "Bottom to Top & towards Left");
                return false; // Bottom to top and left fling
            }

            Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());

            //queueView.setTranslationY(event2.getY());

            return true;
        }

        /*
        if(event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.d("fling from - ", "Right to Left");
                return false; // Right to left
            }  else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.d("fling from - ", "Left to Right");
                return false; // Left to right
            }

            if(event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                Log.d("fling from - ", "Bottom to Top");
                return false; // Bottom to top
            }  else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                Log.d("fling from - ", "Top to Bottom");
                return false; // Top to bottom
            }
        * */

    }

}
