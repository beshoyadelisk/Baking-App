package com.example.bakingapp.Ui.Fragments;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bakingapp.Adapters.DetialsStepsAdapter;
import com.example.bakingapp.DataBase.RecipeIngredientsColumns;
import com.example.bakingapp.DataBase.RecipeListColumns;
import com.example.bakingapp.DataBase.RecipeProvider;
import com.example.bakingapp.GsonResponse.RecipeGsonResponse;
import com.example.bakingapp.R;
import com.example.bakingapp.Ui.DetailActivity;
import com.example.bakingapp.Utilities.Utility;
import com.example.bakingapp.Widget.RecipeWidget;
import com.example.bakingapp.databinding.FragmentDetailBinding;

import java.util.ArrayList;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;


public class DetailFragment extends Fragment implements DetialsStepsAdapter.StepAdapterOnClickHandler {
    protected static final String STEPS_SIZE = "StepsSize";
    private static final String FAVOURITE_KEY = "IsFavourite";
    private FragmentDetailBinding fragmentDetailBinding;
    private DetialsStepsAdapter detialsStepsAdapter;
    private ArrayList<RecipeGsonResponse.IngredientsBean> ingredientsList;
    private ArrayList<RecipeGsonResponse.StepsBean> stepsList;
    private String name;
    private Menu menu;
    private int recipeId,serving;
    private boolean isFavourite = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        recipeId = getArguments().getInt(RecipeFragment.ID_KEY);
        if(savedInstanceState!=null){
            isFavourite=savedInstanceState.getBoolean(FAVOURITE_KEY);
        }
        if (Utility.recipeExist(getContext(), recipeId)) {
            isFavourite = true;
        } else {
            isFavourite = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
        if (savedInstanceState != null) {
            ingredientsList = savedInstanceState.getParcelableArrayList(RecipeFragment.INGREDIENTS_KEY);
            stepsList = savedInstanceState.getParcelableArrayList(RecipeFragment.STEPS_KEY);
            detialsStepsAdapter = new DetialsStepsAdapter(this, stepsList);
            setIngredient();
        } else {
            setDetailData();
        }
        fragmentDetailBinding.stepsDetailRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        fragmentDetailBinding.stepsDetailRv.setHasFixedSize(true);
        fragmentDetailBinding.stepsDetailRv.setAdapter(detialsStepsAdapter);
        name = getArguments().getString(RecipeFragment.NAME_KEY);
        serving = getArguments().getInt(RecipeFragment.SERVING_KEY);
        return fragmentDetailBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(RecipeFragment.INGREDIENTS_KEY, ingredientsList);
        outState.putParcelableArrayList(RecipeFragment.STEPS_KEY, stepsList);
        outState.putBoolean(FAVOURITE_KEY,isFavourite);
    }

    @Override
    public void onClick(RecipeGsonResponse.StepsBean stepsBean) {
        Bundle stepsDetailBundle = new Bundle();
        stepsDetailBundle.putParcelableArrayList(RecipeFragment.STEPS_KEY, stepsList);
        stepsDetailBundle.putString(RecipeFragment.NAME_KEY, name);
        stepsDetailBundle.putInt(RecipeFragment.ID_KEY, stepsBean.getId());
        ExoPlayerFragment exoPlayerFragment = new ExoPlayerFragment();
        exoPlayerFragment.setArguments(stepsDetailBundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, exoPlayerFragment, "StepsFragment")
                .addToBackStack("DetailFragment")
                .commit();
    }

    private void setDetailData() {
        try {
            ingredientsList = getArguments().getParcelableArrayList(RecipeFragment.INGREDIENTS_KEY);
            stepsList = getArguments().getParcelableArrayList(RecipeFragment.STEPS_KEY);
            detialsStepsAdapter = new DetialsStepsAdapter(this, stepsList);
            setIngredient();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void setIngredient() {
        String ingredientLine = "";
        for (int i = 0; i < ingredientsList.size(); i++) {
            RecipeGsonResponse.IngredientsBean ingredientsBean = ingredientsList.get(i);
            ingredientLine = ingredientLine + ". " + ingredientsBean.getIngredient() +
                    "( " + ingredientsBean.getQuantity() + ingredientsBean.getMeasure() + " )";
            if (i < ingredientsList.size() - 1) {
                ingredientLine = ingredientLine + '\n';
            }
        }
        fragmentDetailBinding.expandableText.append(ingredientLine);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favourite_menu, menu);
        this.menu = menu;
        favouriteMenuItem();
    }

    private void favouriteMenuItem() {
        if (isFavourite) {
            menu.getItem(1).setVisible(true);
            menu.getItem(0).setVisible(false);

        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        int listId = getArguments().getInt(RecipeFragment.ID_KEY);
        Log.d("MenuClicked", "recipe id = " + listId + ", recipe name = " + name);

        if (id == R.id.remove_fav_menu_item) {
            clearDatabase();
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);

        } else if (id == R.id.save_fav_menu_item) {
            saveToDatabase(stepsList.get(listId));
            menu.getItem(1).setVisible(true);
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    private void clearDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Delete recipe and ingredient in database
                Utility.setSavedIngredientName(getContext(), "");
                getContext().getContentResolver().delete(RecipeProvider.RecipeIngredients.CONTENT_URI, null, null);
                getContext().getContentResolver().delete(RecipeProvider.RecipeList.CONTENT_URI, null, null);
                RecipeWidget.sendRefreshBroadcast(getContext());
                isFavourite = false;
            }
        }).start();
    }

    private void saveToDatabase(final RecipeGsonResponse.StepsBean stepsBean) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                /* Save recipe and ingredients to database. */
                ContentValues recipeValues = new ContentValues();
                recipeValues.put(RecipeListColumns.RECIPE_NAME, name);
                recipeValues.put(RecipeListColumns.RECIPE_ID, stepsBean.getId());
                recipeValues.put(RecipeListColumns.SERVING_SIZE, serving);

                ArrayList<ContentProviderOperation> batchOperations;
                batchOperations = new ArrayList<>(stepsList.size());
                for (RecipeGsonResponse.IngredientsBean ingredients : ingredientsList) {
                    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                            RecipeProvider.RecipeIngredients.CONTENT_URI);
                    builder.withValue(RecipeIngredientsColumns.RECIPE_LIST_ID, stepsBean.getId());
                    builder.withValue(RecipeIngredientsColumns.INGREDIENT, ingredients.getIngredient());
                    builder.withValue(RecipeIngredientsColumns.MEASUREMENT, ingredients.getMeasure());
                    builder.withValue(RecipeIngredientsColumns.QUANTITY, ingredients.getQuantity());
                    batchOperations.add(builder.build());
                }

                try {
                    getContext().getContentResolver().delete(RecipeProvider.RecipeIngredients.CONTENT_URI, null, null);
                    getContext().getContentResolver().delete(RecipeProvider.RecipeList.CONTENT_URI, null, null);
                    Utility.setSavedIngredientName(getContext(), name);
                    getContext().getContentResolver().insert(RecipeProvider.RecipeList.CONTENT_URI, recipeValues);
                    getContext().getContentResolver().applyBatch(RecipeProvider.AUTHORITY, batchOperations);
                    RecipeWidget.sendRefreshBroadcast(getContext());
                    isFavourite = true;
                } catch (RemoteException | OperationApplicationException e) {
                    Log.e(LOG_TAG, "Error applying batch insert", e);
                }
            }
        }).start();


    }

}
