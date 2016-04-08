package com.vinkrish.fling;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class CommonUtils {

	// Get dp in pixels
	public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static int pxToDp(int px, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
	
	// Get screen height
	public static int getScreenHeight(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        int height=200;
        if(Build.VERSION.SDK_INT>13){
            display.getSize(size);
            height = size.y;
        }
        else{
            height = display.getHeight();
        }
        return height;
    }

    // Get screen width
    public static int getScreenWidth(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        int width=800;
        if(Build.VERSION.SDK_INT>13){
            display.getSize(size);
            width = size.x;
        }
        else{
            width = display.getWidth();
        }
        return width;
    }
}
