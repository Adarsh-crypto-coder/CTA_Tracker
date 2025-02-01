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

//        binding.getRoot().setOnClickListener(v -> {
//            int position = getAdapterPosition();
//            if (position != RecyclerView.NO_POSITION && listener != null) {
//                listener.onStopClick(stops.get(position));
//            }
//        });
    }

    public void bind(Stops stop) {
        binding.stopName.setText(stop.getStpnm());
        String distance = String.format(Locale.US, "%.1f m", stop.getDistance());
        binding.direction.setText(distance);
    }
}
