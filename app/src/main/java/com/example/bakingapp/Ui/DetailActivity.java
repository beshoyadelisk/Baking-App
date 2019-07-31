package com.example.bakingapp.Ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bakingapp.GsonResponse.RecipeGsonResponse;
import com.example.bakingapp.R;
import com.example.bakingapp.Ui.Fragments.DetailFragment;
import com.example.bakingapp.Ui.Fragments.ExoPlayerFragment;
import com.example.bakingapp.Ui.Fragments.RecipeFragment;
import com.example.bakingapp.Utilities.RecipesIdlingResource;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    public static final String ACTION_DATA_UPDATED = "com.example.bakingapp.ACTION_DATA_UPDATE";
    private boolean mTwoPane;
    private ArrayList<RecipeGsonResponse> recipeLists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString(RecipeFragment.NAME_KEY);
        setTitle(name);
        if (findViewById(R.id.steps_linear_layout) != null) {
            mTwoPane = true;
            Button next = (Button) findViewById(R.id.button_next);
            Button prev = (Button) findViewById(R.id.button_previous);
            next.setVisibility(View.GONE);
            prev.setVisibility(View.GONE);
            if (savedInstanceState == null) {
                DetailFragment detailFragment = new DetailFragment();
                ExoPlayerFragment exoPlayerFragment = new ExoPlayerFragment();
                detailFragment.setArguments(bundle);
                exoPlayerFragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.ingredient_container, detailFragment).commit();
                fragmentManager.beginTransaction().add(R.id.steps_container, exoPlayerFragment).commit();
            }
        } else {
            if (savedInstanceState == null) {
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.detail_container, detailFragment).commit();

            }

        }
    }


}
