package com.example.ctatracker;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ctatracker.databinding.StopListEntryBinding;

import java.util.List;

public class StopsAdapter extends RecyclerView.Adapter<StopsViewHolder> {
    private List<Stops> stops;
    private final OnStopClickListener listener;
    private final String routeNumber;
    private final String routeName;
    private final String direction;


    public interface OnStopClickListener {
        void onStopClick(Stops stop);
    }

    public StopsAdapter(List<Stops> stops, OnStopClickListener listener, String routeNumber, String routeName, String direction) {
        this.stops = stops;
        this.listener = listener;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.direction = direction;

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
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PredictionsActivity.class);
            intent.putExtra("route_number", routeNumber);
            intent.putExtra("route_name", routeName);
            intent.putExtra("stop_id", stop.getStpid());
            intent.putExtra("stop_name", stop.getStpnm());
            intent.putExtra("direction", direction);
            v.getContext().startActivity(intent);
        });
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
