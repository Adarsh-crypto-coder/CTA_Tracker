package com.example.ctatracker;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ctatracker.databinding.BusListEntryBinding;

import java.util.ArrayList;
import java.util.List;

public class trackerAdapter extends RecyclerView.Adapter<trackerViewHolder> {

    public List<Route> routeList;

    public trackerAdapter(List<Route> routeList) {
        this.routeList = routeList;
    }


    @NonNull
    @Override
    public trackerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        com.example.ctatracker.databinding.BusListEntryBinding busListEntryBinding = BusListEntryBinding.inflate
                (LayoutInflater.from(parent.getContext()),parent,false);
        return new trackerViewHolder(busListEntryBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull trackerViewHolder holder, int position) {
        holder.bind(routeList.get(position));
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public void setRoutes(List<Route> routes) {
        routeList = routes;
        notifyDataSetChanged();
    }
}
