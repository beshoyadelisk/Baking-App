package com.example.bakingapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bakingapp.GsonResponse.RecipeGsonResponse;
import com.example.bakingapp.R;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.viewHolder> {
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.steps_list_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private TextView stepDescription;
        private SimpleExoPlayerView mPlayerView;
        private viewHolder(View itemView) {
            super(itemView);
            stepDescription = itemView.findViewById(R.id.description_tv);
            mPlayerView.findViewById(R.id.playerView);
        }

        private void bindSteps(RecipeGsonResponse.StepsBean recipeResponse) {
            String id = String.valueOf(recipeResponse.getId());
            String shortDescription = id + ". " + recipeResponse.getShortDescription();
            stepDescription.setText(shortDescription);
        }


    }
}
