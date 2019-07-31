package com.example.bakingapp.Utilities;

import android.net.Uri;

import com.example.bakingapp.BuildConfig;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {
    private static final String API_KEY = BuildConfig.MY_RECIPE_API_KEY;

    public static URL buildUrl() {
        URL url;
        Uri buildUri = Uri.parse(API_KEY).buildUpon().build();
        try {
            url = new URL(buildUri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
