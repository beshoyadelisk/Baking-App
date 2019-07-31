package com.example.bakingapp.Utilities;

import com.example.bakingapp.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAssets {
    private static final List<Integer> recipePic = new ArrayList<Integer>() {{
        add(R.drawable.nutellapie);
        add(R.drawable.brownies);
        add(R.drawable.yellowcake);
        add(R.drawable.cheesecake);

    }};

    public static List<Integer> getRecipePic() {
        return recipePic;
    }
}
