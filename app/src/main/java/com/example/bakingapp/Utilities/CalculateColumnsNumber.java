package com.example.bakingapp.Utilities;

import android.content.Context;
import android.util.DisplayMetrics;

public class CalculateColumnsNumber {
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
