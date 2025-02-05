package com.example.ctatracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ctatracker.databinding.ActivityStopsBinding;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StopsActivity extends AppCompatActivity {
    private ActivityStopsBinding binding;
    private StopsAdapter stopsAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;

    private AdView adView2;
    private static final String adUnitId2 = "ca-app-pub-3940256099942544/6300978111";

    private static final String API_KEY = "CYE7SDQGvNbsRTgz3MjGbyegC";
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final float MAX_DISTANCE = 1000;
    private static final String TAG = "StopsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStopsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupAds();

        setSupportActionBar(binding.stopstoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String routeNumber = getIntent().getStringExtra("route_number");
        String routeName = getIntent().getStringExtra("route_name");
        String direction = getIntent().getStringExtra("direction");

        getSupportActionBar().setTitle(" Route " + routeNumber + " - " + routeName);
        binding.textView2.setText(direction + " Stops ");

        stopsAdapter = new StopsAdapter(new ArrayList<>(), stop -> {},
                getIntent().getStringExtra("route_number"),
                getIntent().getStringExtra("route_name"),
                getIntent().getStringExtra("direction")
        );

        binding.stopsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.stopsRecyclerView.setAdapter(stopsAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        } else {

            checkLocationPermissionAndFetchStops(routeNumber, direction);
        }
    }

    private void loadAdaptiveBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView2.setAdListener(new BannerAdListener());
        adView2.loadAd(adRequest);
    }

    private AdSize getAdSize() {

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float adWidthPixels = adView2.getWidth();

        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels;
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
            Toast.makeText(StopsActivity.this,
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
        adView2 = new AdView(this);
        adView2.setAdUnitId(adUnitId2);
        // Set size before adding to container
        adView2.setAdSize(getAdSize());

        binding.adViewContainer.addView(adView2);
        loadAdaptiveBanner();
    }

    private void checkLocationPermissionAndFetchStops(String routeNumber, String direction) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getUserLocationAndFetchStops(routeNumber, direction);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                String routeNumber = getIntent().getStringExtra("route_number");
                String direction = getIntent().getStringExtra("direction");
                checkLocationPermissionAndFetchStops(routeNumber, direction);
            } else {

                Toast.makeText(this,
                        "Location permission is required to show nearby stops",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void getUserLocationAndFetchStops(String routeNumber, String direction) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted");
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setInterval(0)
                    .setFastestInterval(0)
                    .setNumUpdates(1);

            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        Log.e(TAG, "Location result is null");
                        return;
                    }

                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        userLocation = location;
                        Log.d(TAG, String.format("Got user location - Latitude: %.6f, Longitude: %.6f",
                                location.getLatitude(), location.getLongitude()));

                        fetchStops(routeNumber, direction);

                        fusedLocationClient.removeLocationUpdates(this);
                    } else {
                        Log.e(TAG, "Location is null!");
                        Toast.makeText(StopsActivity.this,
                                "Unable to get current location",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };

            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );

        } catch (SecurityException e) {
            Log.e(TAG, "Security exception when accessing location", e);
            Toast.makeText(this, "Location permission error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void fetchStops(String routeNumber, String direction) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://www.ctabustracker.com/bustime/api/v2/getstops"
                        + "?key=" + API_KEY
                        + "&rt=" + routeNumber
                        + "&dir=" + direction
                        + "&format=json");
                Log.d(TAG, "Fetching stops from URL: " + url);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                Log.d(TAG, "API Response: " + result.toString());
                parseStopsAndUpdateUI(result.toString());

            } catch (Exception e) {
                Log.e(TAG, "Error fetching stops", e);
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(StopsActivity.this,
                            "Error fetching stops: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    private void parseStopsAndUpdateUI(String result) {
        try {
            Log.d(TAG, "Parsing stops data");
            JSONObject jsonObject = new JSONObject(result);
            JSONObject bustimeResponse = jsonObject.getJSONObject("bustime-response");
            JSONArray stopsArray = bustimeResponse.getJSONArray("stops");

            Log.d(TAG, "Number of stops from API: " + stopsArray.length());

            List<Stops> stopsList = new ArrayList<>();
            for (int i = 0; i < stopsArray.length(); i++) {
                JSONObject stopObj = stopsArray.getJSONObject(i);
                Stops stop = new Stops();
                stop.setStpid(stopObj.getString("stpid"));
                stop.setStpnm(stopObj.getString("stpnm"));
                stop.setLat(stopObj.getDouble("lat"));
                stop.setLon(stopObj.getDouble("lon"));

                // Calculate distance from user
                float[] results = new float[2];
                Location.distanceBetween(
                        userLocation.getLatitude(), userLocation.getLongitude(),
                        stop.getLat(), stop.getLon(),
                        results
                );
                stop.setDistance(results[0]);
                stop.setBearing(results[1]);

                if (results[0] <= MAX_DISTANCE) {
                    stopsList.add(stop);
                }
            }

            Log.d(TAG, "Number of nearby stops: " + stopsList.size());
            runOnUiThread(() -> {
                if (stopsList.isEmpty()) {
                    Toast.makeText(this, "No stops found within 1000m", Toast.LENGTH_SHORT).show();
                }
                if (stopsAdapter != null) {
                    Log.d(TAG, "Updating adapter with stops");
                    stopsAdapter.updateStops(stopsList);
                } else {
                    Log.e(TAG, "StopsAdapter is null!");
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing stops data", e);
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(StopsActivity.this,
                        "Error parsing stops data: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            });
        }
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
        binding = null;
    }
}
