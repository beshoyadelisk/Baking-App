package com.example.bakingapp.Widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.example.bakingapp.DataBase.RecipeDatabase;
import com.example.bakingapp.DataBase.RecipeIngredientsColumns;
import com.example.bakingapp.DataBase.RecipeListColumns;
import com.example.bakingapp.DataBase.RecipeProvider;
import com.example.bakingapp.R;


public class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor data = null;
    private static final int INDEX_INGREDIENT_ID = 0;
    private static final int INDEX_INGREDIENT_INGREDIENT = 1;
    private static final int INDEX_INGREDIENT_MEASUREMENT = 2;
    private static final int INDEX_INGREDIENT_QUANTITY = 3;
    private static final String[] INGREDIENTS_COLUMNS = {
            RecipeIngredientsColumns.ID,
            RecipeIngredientsColumns.INGREDIENT,
            RecipeIngredientsColumns.MEASUREMENT,
            RecipeIngredientsColumns.QUANTITY,
            RecipeDatabase.RECIPE_INGREDIENTS + "." + RecipeIngredientsColumns.RECIPE_LIST_ID
    };

    public MyWidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;


    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        if (data != null) {
            data.close();
        }
        final long identityToken = Binder.clearCallingIdentity();

        data = mContext.getContentResolver().query(RecipeProvider.RecipeIngredients.CONTENT_URI,
                INGREDIENTS_COLUMNS,
                null,
                null,
                null);

        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
            data = null;
        }
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                data == null || !data.moveToPosition(position)) {
            return null;
        }

        RemoteViews views = new RemoteViews(mContext.getPackageName(),
                R.layout.favorite_recipe_widget_list_item);

        String name = data.getString(INDEX_INGREDIENT_INGREDIENT);
        String measurement = data.getString(INDEX_INGREDIENT_MEASUREMENT);
        double quantity = data.getDouble(INDEX_INGREDIENT_QUANTITY);

        views.setTextViewText(R.id.widget_ingredient_name, name);
        views.setTextViewText(R.id.widget_ingredient_amount, mContext.getString(R.string.ingredient_amount,
                Double.toString(quantity), measurement));

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.favorite_recipe_widget_list_item);

    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (data.moveToPosition(position))
            return data.getLong(INDEX_INGREDIENT_ID);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}