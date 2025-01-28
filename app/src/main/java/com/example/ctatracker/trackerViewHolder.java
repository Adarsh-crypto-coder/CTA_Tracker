package com.example.ctatracker;

import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ctatracker.databinding.BusListEntryBinding;

public class trackerViewHolder extends RecyclerView.ViewHolder {

    private BusListEntryBinding busListEntryBinding;
    public trackerViewHolder(BusListEntryBinding busListEntryBinding) {
        super(busListEntryBinding.getRoot());
        this.busListEntryBinding = busListEntryBinding;
    }

    public void bind(Route route){
        busListEntryBinding.tvNumber.setText(route.getRt());
        busListEntryBinding.routeName.setText(route.getRtnm());
        try{
            busListEntryBinding.textView.setBackgroundColor(Color.parseColor(route.getRtclr()));
        } catch (Exception e) {
            busListEntryBinding.textView.setBackgroundColor(Color.GRAY);
        }
    }
}
