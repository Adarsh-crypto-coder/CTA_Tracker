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
        try {
            int backgroundColor = Color.parseColor(route.getRtclr());
            busListEntryBinding.textView.setBackgroundColor(backgroundColor);

            double red = Color.red(backgroundColor) / 255.0;
            double green = Color.green(backgroundColor) / 255.0;
            double blue = Color.blue(backgroundColor) / 255.0;

            red = (red <= 0.03928) ? red / 12.92 : Math.pow((red + 0.055) / 1.055, 2.4);
            green = (green <= 0.03928) ? green / 12.92 : Math.pow((green + 0.055) / 1.055, 2.4);
            blue = (blue <= 0.03928) ? blue / 12.92 : Math.pow((blue + 0.055) / 1.055, 2.4);

            double luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue;

            int textColor = (luminance < 0.25) ? Color.WHITE : Color.BLACK;
            busListEntryBinding.tvNumber.setTextColor(textColor);
            busListEntryBinding.routeName.setTextColor(textColor);

        } catch (Exception e) {
            busListEntryBinding.textView.setBackgroundColor(Color.GRAY);

            busListEntryBinding.tvNumber.setTextColor(Color.WHITE);
            busListEntryBinding.routeName.setTextColor(Color.WHITE);
        }

        itemView.setOnClickListener(v -> {
            ((MainActivity) v.getContext()).fetchDirections(route.getRt(), v);
        });
    }
}
