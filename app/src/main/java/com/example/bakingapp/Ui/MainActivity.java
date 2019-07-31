package com.example.bakingapp.Ui;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.bakingapp.R;
import com.example.bakingapp.Ui.Fragments.RecipeFragment;
import com.example.bakingapp.Utilities.RecipesIdlingResource;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        RecipeFragment recipeFragment = new RecipeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.recipe_container, recipeFragment);
        recipeFragment.refresh();
    }


}
