package com.example.ctatracker;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ctatracker.databinding.StopListEntryBinding;

import java.util.Locale;

public class StopsViewHolder extends RecyclerView.ViewHolder {
    private final StopListEntryBinding binding;
    private final StopsAdapter.OnStopClickListener listener;

    public StopsViewHolder(StopListEntryBinding binding, StopsAdapter.OnStopClickListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.listener = listener;
    }

    public void bind(Stops stop) {
        binding.stopName.setText(stop.getStpnm());
        int distanceInMeters = (int) stop.getDistance();

        String direction = Stops.getCardinalDirection(stop.getBearing());

        String locationInfo = String.format(Locale.US, "%dm %s of your location",
                distanceInMeters, direction);
        binding.direction.setText(locationInfo);
    }
}
