package com.example.tripmanager2;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

public class Utils {
    public static float DP_TO_PX(float dp, Context context){
        return  dp * GetDensity(context);
    }
    public static float PX_TO_DP(float px, Context context){
        return px / GetDensity(context);
    }

    public static float GetDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

    public static int GetHeightOfView(View contentView) {
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return contentView.getMeasuredHeight();
    }
}
