package com.example.ctatracker;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ctatracker.databinding.BusListEntryBinding;

import java.util.ArrayList;
import java.util.List;

public class trackerAdapter extends RecyclerView.Adapter<trackerViewHolder> {
    private final MainActivity mainActivity;
    private final List<Route> displayRouteList;
    private final List<Route> fullRouteList;

    public trackerAdapter(MainActivity mainActivity, List<Route> routeList) {
        this.mainActivity = mainActivity;
        this.fullRouteList = new ArrayList<>(routeList);
        this.displayRouteList = new ArrayList<>(routeList);
    }

    @NonNull
    @Override
    public trackerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BusListEntryBinding busListEntryBinding = BusListEntryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new trackerViewHolder(busListEntryBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull trackerViewHolder holder, int position) {
        Route route = displayRouteList.get(position);
        holder.bind(route);

        holder.itemView.setOnClickListener(v -> {

            mainActivity.fetchDirections(route.getRt(), v);
        });
    }

    @Override
    public int getItemCount() {
        return displayRouteList.size();
    }

    public List<Route> getFullRouteList() {
        return fullRouteList;
    }

    public void setRoutes(List<Route> routes) {
        fullRouteList.clear();
        fullRouteList.addAll(routes);
        displayRouteList.clear();
        displayRouteList.addAll(routes);
        notifyDataSetChanged();
    }

    public void filter(String searchText) {
        ArrayList<Route> temp = new ArrayList<>();
        for (Route route : fullRouteList) {
            if (route.getRt().toLowerCase().contains(searchText.toLowerCase()) ||
                    route.getRtnm().toLowerCase().contains(searchText.toLowerCase())) {
                temp.add(route);
            }
        }

        int size = displayRouteList.size();
        displayRouteList.clear();
        notifyItemRangeRemoved(0, size);

        displayRouteList.addAll(temp);
        notifyItemRangeChanged(0, displayRouteList.size());
    }
}
