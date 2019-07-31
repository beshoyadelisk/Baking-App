package com.example.bakingapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bakingapp.GsonResponse.RecipeGsonResponse;
import com.example.bakingapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.viewHolder> {

    private List<RecipeGsonResponse> recipeResponse;
    private List<Integer> recipePic;
    private final RecipeAdapterOnClickHandler recipeAdapterOnClickHandler;
    private final String TAG = RecipeGsonResponse.class.getSimpleName();

    public interface RecipeAdapterOnClickHandler {
        void onClick(RecipeGsonResponse recipe);
    }

    public RecipeAdapter(RecipeAdapterOnClickHandler clickHandler, List<Integer> recipePic) {
        recipeAdapterOnClickHandler = clickHandler;
        this.recipePic = recipePic;
    }


    public void setRecipeData(ArrayList<RecipeGsonResponse> recipeData) {
        this.recipeResponse = recipeData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recipe_list_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (recipeResponse == null)
            return 0;
        else
            return recipeResponse.size();
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.bindRecipe(recipeResponse.get(holder.getAdapterPosition()));
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView recipeName, servingNumber;
        private final ImageView recipePoster;
        private final Context mContext;

        private viewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipe_tv);
            recipePoster = itemView.findViewById(R.id.recipe_iv);
            servingNumber = itemView.findViewById(R.id.serving_tv);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        private void bindRecipe(RecipeGsonResponse recipeResponse) {
            recipeName.setText(recipeResponse.getName());
            String servingText = itemView.getResources().getString(R.string.serving) + recipeResponse.getServings();
            servingNumber.setText(servingText);
            recipePoster.setImageResource(recipePic.get(getAdapterPosition()));
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            recipeAdapterOnClickHandler.onClick(recipeResponse.get(getAdapterPosition()));
        }
    }


}