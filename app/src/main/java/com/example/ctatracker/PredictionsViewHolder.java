package com.example.ctatracker;

import android.content.Intent;
import android.gesture.Prediction;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ctatracker.databinding.PredictionListEntryBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PredictionsViewHolder extends RecyclerView.ViewHolder {
    private final PredictionListEntryBinding binding;
    private final String stopName;

    public PredictionsViewHolder(PredictionListEntryBinding binding, String stopName) {
        super(binding.getRoot());
        this.binding = binding;
        this.stopName=stopName;
    }

    public void bind(Predictions prediction) {
        binding.busNumber.setText("Bus #" + prediction.getVehicleId());
        binding.preddirection.setText(prediction.getDirection() + " to " + prediction.getDestination());
        binding.due.setText("Due in " + prediction.getMinutes() + " mins at");

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd HH:mm", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            Date date = inputFormat.parse(prediction.getArrivalTime());
            binding.time.setText(outputFormat.format(date));
        } catch (ParseException e) {
            binding.time.setText(prediction.getArrivalTime());
        }

        itemView.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setIcon(R.drawable.splash_logo)
                    .setTitle("Bus #" + prediction.getVehicleId())
                    .setMessage(String.format("Bus #%s is %.1f meters (%s min) away from the %s",
                            prediction.getVehicleId(),
                            Double.parseDouble(prediction.getDistance()),  // Convert to double for decimal formatting
                            prediction.getMinutes(),
                            stopName))
                    .setPositiveButton("OK", null)
                    .setNeutralButton("SHOW ON MAP",(dialog, which) -> {
                // Open Google Maps with bus location
                openGoogleMaps(prediction.getLatitude(), prediction.getLongitude());
            })
            .show();
        });
    }

    private void openGoogleMaps(String latitude, String longitude) {

        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(itemView.getContext().getPackageManager()) != null) {
            itemView.getContext().startActivity(mapIntent);
        } else {
            Uri browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
            itemView.getContext().startActivity(browserIntent);
        }
    }
}