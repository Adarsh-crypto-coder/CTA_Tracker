package com.example.ctatracker;

import android.gesture.Prediction;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ctatracker.databinding.PredictionListEntryBinding;

import java.util.ArrayList;
import java.util.List;

public class PredictionsAdapter extends RecyclerView.Adapter<PredictionsViewHolder> {
    private List<Predictions> predictions = new ArrayList<>();
    private final String stopName;

    public PredictionsAdapter(String stopName) {
        this.stopName = stopName;
    }

    @NonNull
    @Override
    public PredictionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PredictionListEntryBinding binding = PredictionListEntryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PredictionsViewHolder(binding,stopName);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionsViewHolder holder, int position) {
        holder.bind(predictions.get(position));
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    public void setPredictions(List<Predictions> newPredictions) {
        this.predictions = newPredictions;
        notifyDataSetChanged();
    }
}
