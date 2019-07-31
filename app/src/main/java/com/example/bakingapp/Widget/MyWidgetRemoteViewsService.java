package com.example.bakingapp.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.bakingapp.Utilities.Utility;


public class MyWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public void onCreate() {
        super.onCreate();
        RecipeWidget.setWidgetText(this, Utility.getSavedIngredientName(this));
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new MyWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}