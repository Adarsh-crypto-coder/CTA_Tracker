package com.example.ctatracker;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ctatracker.databinding.ActivityPredictionsBinding;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PredictionsActivity extends AppCompatActivity {
    private ActivityPredictionsBinding binding;
    private PredictionsAdapter predictionsAdapter;
    private static final String API_KEY = "CYE7SDQGvNbsRTgz3MjGbyegC";
    private static final String TAG = "PredictionsActivity";
    private Handler refreshHandler = new Handler();
    private static final int REFRESH_INTERVAL = 60000;

    private AdView adView1;
    private static final String adUnitId1 = "ca-app-pub-3940256099942544/6300978111";

    private int ctr = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPredictionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupAds();



        String routeNumber = getIntent().getStringExtra("route_number");
        String routeName = getIntent().getStringExtra("route_name");
        String stopId = getIntent().getStringExtra("stop_id");
        String stopName = getIntent().getStringExtra("stop_name");
        String direction = getIntent().getStringExtra("direction");

        setSupportActionBar(binding.predictiontoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" Route " + routeNumber + " - " + routeName);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a",Locale.US);
        String currentTime = simpleDateFormat.format(new Date());
        binding.predictionStops.setText(stopName + "(" + direction + ")\n" + currentTime);


        predictionsAdapter = new PredictionsAdapter(stopName);
        binding.predRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.predRecyclerView.setAdapter(predictionsAdapter);

        binding.main.setOnRefreshListener(() -> {
            fetchPredictions(routeNumber,stopId);
            binding.main.setRefreshing(false);
        });

        setupRefreshTimer(routeNumber, stopId);
        fetchPredictions(routeNumber, stopId);
    }

    private void loadAdaptiveBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView1.setAdListener(new BannerAdListener());
        adView1.loadAd(adRequest);
    }
    private AdSize getAdSize() {

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float adWidthPixels = adView1.getWidth();

        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels;// * 0.75f;
        }

        float density = getResources().getDisplayMetrics().density;
        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);

    }

    class BannerAdListener extends AdListener {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
            Log.d(TAG, "onAdClosed: ");
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            super.onAdFailedToLoad(loadAdError);
            Log.d(TAG, "onAdFailedToLoad: " + loadAdError);
            Toast.makeText(PredictionsActivity.this,
                    loadAdError.getMessage() + " (Code: " + loadAdError.getCode() + ")",
                    Toast.LENGTH_LONG).show();

        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            Log.d(TAG, "onAdOpened: ");
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            Log.d(TAG, "onAdLoaded: ");

        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
            Log.d(TAG, "onAdClicked: ");
        }

        @Override
        public void onAdImpression() {
            super.onAdImpression();
            Log.d(TAG, "onAdImpression: ");
        }
    }

    private void setupAds() {
        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("33BE2250B43518CCDA7DE426D04EE231"))
                .build();
        MobileAds.setRequestConfiguration(configuration);

        MobileAds.initialize(this, initializationStatus -> Log.d(TAG, "onInitializationComplete"));
        adView1 = new AdView(this);
        adView1.setAdUnitId(adUnitId1);
        // Set size before adding to container
        adView1.setAdSize(getAdSize());

        binding.adViewContainer.addView(adView1);
        loadAdaptiveBanner();
    }


    private void setupRefreshTimer(String routeNumber, String stopId) {
        Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                fetchPredictions(routeNumber, stopId);
                refreshHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    private void fetchPredictions(String routeNumber, String stopId) {
        new Thread(() -> {
            HttpURLConnection predConn = null;
            HttpURLConnection vehicleConn = null;
            try {
                // First, fetch predictions
                URL predUrl = new URL("https://www.ctabustracker.com/bustime/api/v2/getpredictions"
                        + "?key=" + API_KEY
                        + "&rt=" + routeNumber
                        + "&stpid=" + stopId
                        + "&format=json");

                predConn = (HttpURLConnection) predUrl.openConnection();
                predConn.setConnectTimeout(5000);

                BufferedReader predReader = new BufferedReader(new InputStreamReader(predConn.getInputStream()));
                StringBuilder predResult = new StringBuilder();
                String line;
                while ((line = predReader.readLine()) != null) {
                    predResult.append(line);
                }
                predReader.close();


                URL vehicleUrl = new URL("https://www.ctabustracker.com/bustime/api/v2/getvehicles"
                        + "?key=" + API_KEY
                        + "&rt=" + routeNumber
                        + "&format=json");

                vehicleConn = (HttpURLConnection) vehicleUrl.openConnection();
                vehicleConn.setConnectTimeout(5000);

                BufferedReader vehicleReader = new BufferedReader(new InputStreamReader(vehicleConn.getInputStream()));
                StringBuilder vehicleResult = new StringBuilder();
                while ((line = vehicleReader.readLine()) != null) {
                    vehicleResult.append(line);
                }
                vehicleReader.close();


                parsePredictionsAndUpdateUI(predResult.toString(), vehicleResult.toString());

            } catch (Exception e) {
                e.printStackTrace();
                showError("Error fetching predictions: " + e.getMessage());
            } finally {
                if (predConn != null) predConn.disconnect();
                if (vehicleConn != null) vehicleConn.disconnect();
            }
        }).start();
    }

    private void parsePredictionsAndUpdateUI(String predResult, String vehicleResult) {
        try {
            JSONObject predJsonObject = new JSONObject(predResult);
            JSONObject vehicleJsonObject = new JSONObject(vehicleResult);

            JSONObject predBustimeResponse = predJsonObject.getJSONObject("bustime-response");
            JSONObject vehicleBustimeResponse = vehicleJsonObject.getJSONObject("bustime-response");

            JSONArray predictions = predBustimeResponse.getJSONArray("prd");
            JSONArray vehicles = vehicleBustimeResponse.getJSONArray("vehicle");

            Map<String, VehicleLocation> vehicleLocations = new HashMap<>();
            for (int j = 0; j < vehicles.length(); j++) {
                JSONObject vehicle = vehicles.getJSONObject(j);
                vehicleLocations.put(
                        vehicle.getString("vid"),
                        new VehicleLocation(
                                vehicle.getString("lat"),
                                vehicle.getString("lon")
                        )
                );
            }

            List<Predictions> predictionList = new ArrayList<>();
            for (int i = 0; i < predictions.length(); i++) {
                JSONObject pred = predictions.getJSONObject(i);
                String vehicleId = pred.getString("vid");

                VehicleLocation location = vehicleLocations.get(vehicleId);
                String latitude = location != null ? location.latitude : "0";
                String longitude = location != null ? location.longitude : "0";

                Predictions prediction = new Predictions(
                        pred.getString("prdctdn"),
                        pred.getString("prdtm"),
                        vehicleId,
                        pred.getString("dstp"),
                        pred.getBoolean("dly"),
                        pred.getString("des"),
                        pred.getString("rtdir"),
                        latitude,
                        longitude
                );
                predictionList.add(prediction);
            }

            runOnUiThread(() -> {
                predictionsAdapter.setPredictions(predictionList);
                if (predictionList.isEmpty()) {
                    Toast.makeText(this, "No upcoming arrivals", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            showError("Error parsing predictions: " + e.getMessage());
        }
    }


    private void showError(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refreshHandler.removeCallbacksAndMessages(null);
        binding = null;
    }
}