package com.example.bakingapp.DataBase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;


@Database(version = RecipeDatabase.VERSION)
public final class RecipeDatabase {

    public RecipeDatabase() {
    }

    public static final int VERSION = 1;

    @Table(RecipeListColumns.class)
    public static final String RECIPE_LIST = "list";

    @Table(RecipeIngredientsColumns.class)
    public static final String RECIPE_INGREDIENTS = "recipe_ingredients";

}