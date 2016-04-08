package com.vinkrish.fling;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class FlingActivity extends AppCompatActivity {
    private FrameLayout podium;
    private TextView queueView;
    private RelativeLayout parentView;
    private int score, wrongScore, queueTopMargin, queueLeftMargin;
    private TextView yourScore, yourWrongScore, oddPlusOne, evenPlusOne;
    private ImageView oddTick, oddWrong, evenTick, evenWrong;
    private GestureDetectorCompat mDetector;
    //private GestureDetector mDetector;
    private static final int SWIPE_MIN_DISTANCE = 90;
    private static final int SWIPE_THRESHOLD_VELOCITY = 60;
    float initialX;
    float initialY;
    private boolean oddTrue, evenTrue, fallBack, oddFalse, evenFalse;
    private final float SPRITE_SCALE = 0.2f;
    private int SPRITE_SIZE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fling);

        initView();
        SPRITE_SIZE = (int) (CommonUtils.getScreenWidth(FlingActivity.this) * SPRITE_SCALE);

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        queueLeftMargin = (displayMetrics.widthPixels / 2) - 80;
        //mDetector = new GestureDetector(new MyGestureListener());

        podium.post(new Runnable() {
            @Override
            public void run() {
                int[] viewLocation = new int[2];
                podium.getLocationOnScreen(viewLocation);
                queueTopMargin = viewLocation[1] - podium.getHeight() - 130;
                generateAndAnimate();
            }
        });

        //findViewById(R.id.bottomLayout).setOnDragListener(new DragListener());
    }

    private void initView() {
        parentView = (RelativeLayout) findViewById(R.id.parent_view);
        podium = (FrameLayout) findViewById(R.id.podium);
        yourScore = (TextView) findViewById(R.id.your_score);
        yourWrongScore = (TextView) findViewById(R.id.your_wrong_score);
        oddPlusOne = (TextView) findViewById(R.id.odd_plus_one);
        evenPlusOne = (TextView) findViewById(R.id.even_plus_one);
        oddTick = (ImageView) findViewById(R.id.odd_tick);
        oddWrong = (ImageView) findViewById(R.id.odd_wrong);
        evenTick = (ImageView) findViewById(R.id.even_tick);
        evenWrong = (ImageView) findViewById(R.id.even_wrong);
    }

    private void generateAndAnimate() {

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = queueLeftMargin;
        lp.topMargin = queueTopMargin;
        //lp.addRule(RelativeLayout.ABOVE, podium.getId());
        //lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //lp.setMargins(0, 0, 0, queueBottomMargin);

        queueView = new TextView(getApplicationContext());
        queueView.setTag("dnd");
        queueView.setLayoutParams(lp);
        queueView.setText(generateNumber() + "");
        queueView.setTextSize(50);
        queueView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.lightBlack));
        parentView.addView(queueView);
        queueView.setVisibility(View.INVISIBLE);

        queueView.post(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.alphaTranslate(queueView, getApplicationContext());
            }
        });

        initSpriteTouchListener(queueView);

    }

    private int generateNumber() {
        Random r = new Random();
        int Low = 1;
        int High = 100;
        return r.nextInt(High - Low) + Low;
    }

    private boolean isItOdd() {
        try {
            int number = Integer.parseInt(queueView.getText().toString());
            if (number % 2 == 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean isItEven() {
        try {
            int number = Integer.parseInt(queueView.getText().toString());
            if (number % 2 == 0) {
                return true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateYourScore() {
        score++;
        yourScore.setText(score + "");
    }

    private void updateYourWrongScore() {
        wrongScore++;
        yourWrongScore.setText(wrongScore + "");
    }

    private void initSpriteTouchListener(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            float dX;
            float dY;
            float pivotX;
            float pivotY;

            @Override
            public boolean onTouch(final View view, MotionEvent event) {

                mDetector.onTouchEvent(event);
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = view.getX();
                        initialY = view.getY();
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.leftMargin = (int) (event.getRawX() + dX);
                        layoutParams.topMargin = (int) (event.getRawY() + dY);
                        layoutParams.rightMargin = (int) (0 - SPRITE_SIZE);
                        view.setLayoutParams(layoutParams);

                        view.setPivotX(pivotX);
                        view.setPivotY(pivotY);

                        break;

                    case MotionEvent.ACTION_UP:
                        if (fallBack) {
                            wrongGuess(view);
                        } else if (oddTrue) {
                            oddTrue(view);
                        } else if (evenTrue) {
                            evenTrue(view);
                        } else if (oddFalse) {
                            oddFalse(view);
                        } else if (evenFalse) {
                            evenFalse(view);
                        }
                        break;

                    default:
                        return false;
                }
                return true;
            }
        });
    }

    private void oddTrue(View view) {
        rightGuess(view);
        AnimationUtil.alphaScaleInOut(oddTick, getApplicationContext());
        AnimationUtil.alphaInOut(oddPlusOne, getApplicationContext());
        updateYourScore();
    }

    private void evenTrue(View view) {
        rightGuess(view);
        AnimationUtil.alphaScaleInOut(evenTick, getApplicationContext());
        AnimationUtil.alphaInOut(evenPlusOne, getApplicationContext());
        updateYourScore();
    }

    private void oddFalse(View view) {
        rightGuess(view);
        AnimationUtil.alphaScaleInOut(oddWrong, getApplicationContext());
        updateYourWrongScore();
    }

    private void evenFalse(View view) {
        rightGuess(view);
        AnimationUtil.alphaScaleInOut(evenWrong, getApplicationContext());
        updateYourWrongScore();
    }

    private void rightGuess(final View view) {
        int[] viewLocation = new int[2];
        if (oddTrue || oddFalse) {
            findViewById(R.id.is_it_odd).getLocationOnScreen(viewLocation);
        } else if (evenTrue || evenFalse) {
            findViewById(R.id.is_it_even).getLocationOnScreen(viewLocation);
        }

        AnimationUtil.TrajectoryAnimation resetPositionAnimation = new AnimationUtil.TrajectoryAnimation(view, (int) viewLocation[0], (int) viewLocation[1], 0, (int) view.getX(), (int) view.getY(), view.getRotation());
        resetPositionAnimation.setDuration(500);
        resetPositionAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
                parentView.removeView(queueView);
                generateAndAnimate();
            }
        });
        resetPositionAnimation.setFillAfter(false);
        view.startAnimation(resetPositionAnimation);
    }

    private void wrongGuess(View view) {
        AnimationUtil.TrajectoryAnimation resetPositionAnimation = new AnimationUtil.TrajectoryAnimation(view, (int) initialX, (int) initialY, 0, (int) view.getX(), (int) view.getY(), view.getRotation());
        resetPositionAnimation.setDuration(300);
        resetPositionAnimation.setFillAfter(true);
        view.startAnimation(resetPositionAnimation);
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

            float event1X = event1.getRawX();
            float event1Y = event1.getRawY();
            float event2X = event2.getRawX();
            float event2Y = event2.getRawY();

            if (event1Y - event2Y > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY &&
                    event2X > event1X) {
                Log.d("even", "right");
                fallBack = false;
                oddTrue = false;
                oddFalse = false;

                if (isItEven()) {
                    evenTrue = true;
                    evenFalse = false;
                } else {
                    evenTrue = false;
                    evenFalse = true;
                }

                return false; // Bottom to top and right fling
            } else if (event1Y - event2Y > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY &&
                    event1X > event2X) {
                Log.d("odd", "right");
                fallBack = false;
                evenTrue = false;
                evenFalse = false;

                if (isItOdd()) {
                    oddTrue = true;
                    oddFalse = false;
                } else {
                    oddTrue = false;
                    oddFalse = true;
                }

                return false; // Bottom to top and left fling
            } else {
                Log.d("fallback", "right");
                fallBack = true;
                evenTrue = false;
                evenFalse = false;
                oddTrue = false;
                oddFalse = false;
            }

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
        */

    }

}
