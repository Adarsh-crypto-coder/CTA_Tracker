package com.example.ctatracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ctatracker.databinding.BusListEntryBinding;
import com.example.ctatracker.databinding.StopListEntryBinding;

import java.util.List;

public class StopsAdapter extends RecyclerView.Adapter<StopsViewHolder> {
    private List<Stops> stops;
    private final OnStopClickListener listener;

    public interface OnStopClickListener {
        void onStopClick(Stops stop);
    }

    public StopsAdapter(List<Stops> stops, OnStopClickListener listener) {
        this.stops = stops;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StopListEntryBinding binding = StopListEntryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new StopsViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull StopsViewHolder holder, int position) {
        Stops stop = stops.get(position);
        holder.bind(stop);
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    public void updateStops(List<Stops> newStops) {
        this.stops = newStops;
        notifyDataSetChanged();
    }
}
