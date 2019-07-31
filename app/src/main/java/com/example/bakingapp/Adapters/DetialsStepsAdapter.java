package com.example.bakingapp.Adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bakingapp.GsonResponse.RecipeGsonResponse;
import com.example.bakingapp.R;

import java.util.List;

public class DetialsStepsAdapter extends RecyclerView.Adapter<DetialsStepsAdapter.viewHolder> {
    private List<RecipeGsonResponse.StepsBean> recipeStepsList;
    private final StepAdapterOnClickHandler stepAdapterOnClickHandler;

    public interface StepAdapterOnClickHandler {
        void onClick(RecipeGsonResponse.StepsBean stepsBean);
    }

    public DetialsStepsAdapter(StepAdapterOnClickHandler stepAdapterOnClickHandler, List<RecipeGsonResponse.StepsBean> stepsBeans) {
        this.stepAdapterOnClickHandler = stepAdapterOnClickHandler;
        recipeStepsList = stepsBeans;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.steps_detail_list_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        viewHolder.bindSteps(recipeStepsList.get(viewHolder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        if (recipeStepsList == null)
            return 0;
        else
            return recipeStepsList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView stepDescription;

        private viewHolder(View itemView) {
            super(itemView);
            stepDescription = itemView.findViewById(R.id.step_tv);
            itemView.setOnClickListener(this);
        }

        private void bindSteps(RecipeGsonResponse.StepsBean recipeResponse) {
            String id = String.valueOf(recipeResponse.getId());
            String shortDescription = id + ". " + recipeResponse.getShortDescription();
            stepDescription.setText(shortDescription);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            RecipeGsonResponse.StepsBean stepsBean = recipeStepsList.get(adapterPosition);
            stepAdapterOnClickHandler.onClick(stepsBean);
        }
    }
}
