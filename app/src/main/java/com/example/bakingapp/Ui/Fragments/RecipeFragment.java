package com.example.bakingapp.Ui.Fragments;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bakingapp.Adapters.RecipeAdapter;
import com.example.bakingapp.DataBase.RecipeIngredientsColumns;
import com.example.bakingapp.DataBase.RecipeListColumns;
import com.example.bakingapp.DataBase.RecipeProvider;
import com.example.bakingapp.GsonResponse.RecipeGsonResponse;
import com.example.bakingapp.R;
import com.example.bakingapp.Utilities.CalculateColumnsNumber;
import com.example.bakingapp.Ui.DetailActivity;
import com.example.bakingapp.Utilities.ImageAssets;
import com.example.bakingapp.Utilities.NetworkUtils;
import com.example.bakingapp.Utilities.Utility;
import com.example.bakingapp.databinding.FragmentRecipeListBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

public class RecipeFragment extends Fragment implements RecipeAdapter.RecipeAdapterOnClickHandler {
    public static final String SERVING_KEY = "SERVING_KEY";
    public static final String NAME_KEY = "NAME_KEY";
    public static final String IMAGE_KEY = "IMAGE_KEY";
    public static final String INGREDIENTS_KEY = "INGREDIENTS_KEY";
    public static final String STEPS_KEY = "STEPS_KEY";
    public static final String ID_KEY = "ID_KEY";
    public static final String RECIPE_LIST = "RECIPE_KEY";

    private ArrayList<RecipeGsonResponse> recipeGsonResponseList;
    private static RecipeAdapter recipeAdapter;
    private FragmentRecipeListBinding fragmentRecipeListBinding;


    public RecipeFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRecipeListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container, false);
        recipeAdapter = new RecipeAdapter(this, ImageAssets.getRecipePic());
        int mNoOfColumns = CalculateColumnsNumber.calculateNoOfColumns(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mNoOfColumns);
        fragmentRecipeListBinding.recipeRv.setLayoutManager(gridLayoutManager);
        fragmentRecipeListBinding.recipeRv.setHasFixedSize(true);


        if (savedInstanceState != null) {
            recipeGsonResponseList = savedInstanceState.getParcelableArrayList(RECIPE_LIST);
            setRecipeData(recipeGsonResponseList);
        } else {
            loadRecipeData();

        }
        fragmentRecipeListBinding.recipeRv.setAdapter(recipeAdapter);
        getActivity().setTitle("Recipe List");
        return fragmentRecipeListBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(RECIPE_LIST, recipeGsonResponseList);
        super.onSaveInstanceState(outState);
    }


    private void loadRecipeData() {
        showRecipeData();
        fetchData();
    }

    private void fetchData() {
        URL recipeRequestUrl = NetworkUtils.buildUrl();
        final String recipeRequestUrlString = recipeRequestUrl.toString();
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(recipeRequestUrlString, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                fragmentRecipeListBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonRecipeResponse = new String(responseBody);
                Gson gson = new Gson();
                Type recipeListType = new TypeToken<List<RecipeGsonResponse>>() {
                }.getType();
                recipeGsonResponseList = gson.fromJson(jsonRecipeResponse, recipeListType);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
                Log.d("Error with client", throwable.getMessage());
                showErrorMessage();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                fragmentRecipeListBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
                setRecipeData(recipeGsonResponseList);
            }
        });
    }

    private void setRecipeData(ArrayList<RecipeGsonResponse> recipeDataList) {
        if (recipeDataList != null) {
            recipeAdapter.setRecipeData(recipeDataList);
        } else
            showErrorMessage();
    }

    private void showRecipeData() {
        fragmentRecipeListBinding.recipeRv.setVisibility(View.VISIBLE);
        fragmentRecipeListBinding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        fragmentRecipeListBinding.recipeRv.setVisibility(View.INVISIBLE);
        fragmentRecipeListBinding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(RecipeGsonResponse recipe) {
        Bundle bundleExtras = new Bundle();
        bundleExtras.putInt(ID_KEY, recipe.getId());
        bundleExtras.putInt(SERVING_KEY, recipe.getServings());
        bundleExtras.putString(NAME_KEY, recipe.getName());
        bundleExtras.putString(IMAGE_KEY, recipe.getImage());
        bundleExtras.putParcelableArrayList(INGREDIENTS_KEY, (ArrayList<? extends Parcelable>) recipe.getIngredients());
        bundleExtras.putParcelableArrayList(STEPS_KEY, (ArrayList<? extends Parcelable>) recipe.getSteps());



//        Bundle recipeListBundle = new Bundle();
//        recipeListBundle.putParcelableArrayList(RECIPE_LIST, recipeGsonResponseList);
//        onSaveInstanceState(recipeListBundle);
//        getActivity().getSupportFragmentManager().beginTransaction()
////                .replace(R.id.recipe_container, detailFragment, "DetailFragment")
////                .addToBackStack("RecipeFragment")
////                .commit();
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtras(bundleExtras);
        getActivity().startActivity(intent);
    }


    public void refresh() {
        if (recipeAdapter != null)
            recipeAdapter.notifyDataSetChanged();
    }
}
