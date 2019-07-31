package com.example.bakingapp.Ui;

import android.content.Context;
import android.content.Intent;


import android.content.res.AssetManager;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;


import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.example.bakingapp.GsonResponse.RecipeGsonResponse;
import com.example.bakingapp.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {
    private ArrayList<RecipeGsonResponse.StepsBean> sList;
    private ArrayList<RecipeGsonResponse.IngredientsBean> iList;
    private List<RecipeGsonResponse> rList;
    private int position = 1;

    @Before
    public void setUp() {

        getTargetContext().deleteDatabase("database.db");
        ActivityScenario.launch(MainActivity.class);
    }

    @Rule
    public ActivityTestRule<DetailActivity> mActivityTextRule =
            new ActivityTestRule<DetailActivity>(DetailActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();

                    getData(targetContext);

                    Intent results = new Intent(targetContext, DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("recipeName", rList.get(position).getName());
                    bundle.putParcelableArrayList("ingredientsList", (ArrayList<? extends Parcelable>) rList.get(position).getIngredients());
                    bundle.putParcelableArrayList("stepsList", (ArrayList<? extends Parcelable>) rList.get(position).getSteps());
                    results.putExtras(bundle);
                    return results;
                }
            };

    @Test
    public void checkIfCorrectData() {
        String recipeName = rList.get(position).getName();
        onView((withId(R.id.detail_container))).check(matches(isDisplayed()));
        onView(withId(R.id.steps_detail_rv))
                .perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.step_tv)).check(matches(isDisplayed()));
    }


    public void getData(Context context) {

        Gson gson = new GsonBuilder().create();
        Type recipeListType = new TypeToken<ArrayList<RecipeGsonResponse>>() {
        }.getType();

        AssetManager assetManager = context.getAssets();

        try {
            InputStream inputStream = assetManager.open("baking.json");
            Reader reader;
            reader = new InputStreamReader(inputStream, "UTF-8");
            rList = gson.fromJson(reader, recipeListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}