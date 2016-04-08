package com.vinkrish.fling;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

/**
 * Created by vinkrish on 10/03/16.
 */
public class AnimationUtil {

    public static class TrajectoryAnimation extends Animation {
        final int targetX;
        final int targetY;
        final float targetRotation;
        View view;
        int startX;
        int startY;
        float startRotation;

        public TrajectoryAnimation(View view, int targetX, int targetY, float targetRotation, int startX, int startY, float startRotation) {
            this.view = view;
            this.targetX = targetX;
            this.targetY = targetY;
            this.targetRotation = targetRotation;
            this.startX = startX;
            this.startY = startY;
            this.startRotation = startRotation;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newX = (int) (startX + ((targetX - startX) * interpolatedTime));
            int newY = (int) (startY + ((targetY - startY) * interpolatedTime));
            ((RelativeLayout.LayoutParams) view.getLayoutParams()).leftMargin = newX;
            ((RelativeLayout.LayoutParams) view.getLayoutParams()).topMargin = newY;
            ((RelativeLayout.LayoutParams) view.getLayoutParams()).rightMargin = -2000;
            ((RelativeLayout.LayoutParams) view.getLayoutParams()).bottomMargin = -2000;

            float newRotation = startRotation - ((startRotation - targetRotation) * interpolatedTime);
            view.setRotation(newRotation);
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public static void alphaTranslate(View view, Context context) {
        view.setVisibility(View.VISIBLE);
        AnimationSet animationSet = new AnimationSet(context , null);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, view.getHeight(), 0);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(700);
        view.startAnimation(animationSet);
    }

    public static void alphaScaleInOut (final View view, Context context) {
        AnimationSet animationSet = new AnimationSet(context , null);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f,1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(500);
        view.startAnimation(animationSet);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AlphaAnimation fade = new AlphaAnimation(1f, 0f);
                fade.setDuration(500);
                view.startAnimation(fade);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public static void alphaInOut(View view, Context context) {
        AnimationSet animationSet = new AnimationSet(context , null);
        AlphaAnimation fadeIn = new AlphaAnimation(0.5f, 1f);
        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(fadeOut);
        animationSet.setDuration(1000);
        view.startAnimation(animationSet);
    }



}
