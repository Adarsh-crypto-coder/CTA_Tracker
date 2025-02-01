package com.example.ctatracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ctatracker.databinding.ActivityMainBinding;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private trackerAdapter trackerAdapter;

    private AdView adView;

    private static final String API_KEY = "CYE7SDQGvNbsRTgz3MjGbyegC";
    private static final String adUnitId = "ca-app-pub-3940256099942544/6300978111";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize binding first
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        // Setup RecyclerView and adapter
        trackerAdapter = new trackerAdapter(this, new ArrayList<>());
        activityMainBinding.recylerView.setLayoutManager(new LinearLayoutManager(this));
        activityMainBinding.recylerView.setAdapter(trackerAdapter);

        // Set up search functionality
        activityMainBinding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (trackerAdapter != null) {
                    trackerAdapter.filter(editable.toString());
                }
            }
        });

        // Setup ads
        setupAds();

        // Fetch route data
        fetchRoutes();
    }

    private void loadAdaptiveBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.setAdListener(new BannerAdListener());
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float adWidthPixels = adView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
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
            Toast.makeText(MainActivity.this,
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

    private void fetchRoutes() {
        new Thread(() -> {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("https://www.ctabustracker.com/bustime/api/v2/getroutes?key=" + API_KEY + "&format=json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                conn.disconnect();

                parseAndUpdateUI(result.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void parseAndUpdateUI(String result) {
        runOnUiThread(() -> {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject bustimeResponse = jsonObject.getJSONObject("bustime-response");
                JSONArray routes = bustimeResponse.getJSONArray("routes");

                List<Route> routeList = new ArrayList<>();
                for (int i = 0; i < routes.length(); i++) {
                    JSONObject routeObj = routes.getJSONObject(i);
                    Route route = new Route();
                    route.setRt(routeObj.getString("rt"));
                    route.setRtnm(routeObj.getString("rtnm"));
                    route.setRtclr(routeObj.getString("rtclr"));
                    route.setRtdd(routeObj.getString("rtdd"));
                    routeList.add(route);
                }

                trackerAdapter.setRoutes(routeList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void setupAds() {
        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("33BE2250B43518CCDA7DE426D04EE231"))
                .build();
        MobileAds.setRequestConfiguration(configuration);

        MobileAds.initialize(this, initializationStatus -> Log.d(TAG, "onInitializationComplete"));
        adView = new AdView(this);
        adView.setAdUnitId(adUnitId);
        activityMainBinding.adViewContainer.addView(adView);
        activityMainBinding.adViewContainer.post(this::loadAdaptiveBanner);
    }

    public void fetchDirections(String routeId, View anchorView) {
        new Thread(() -> {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("https://www.ctabustracker.com/bustime/api/v2/getdirections?key=" + API_KEY + "&rt=" + routeId + "&format=json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                conn.disconnect();

                showDirectionsPopup(result.toString(), routeId, anchorView);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "Failed to fetch directions", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void showDirectionsPopup(String result, String routeId, View anchorView) {
        runOnUiThread(() -> {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject bustimeResponse = jsonObject.getJSONObject("bustime-response");
                JSONArray directions = bustimeResponse.getJSONArray("directions");

                PopupMenu popup = new PopupMenu(this, anchorView);
                for (int i = 0; i < directions.length(); i++) {
                    JSONObject dirObj = directions.getJSONObject(i);
                    String dir = dirObj.getString("dir");
                    popup.getMenu().add(dir);
                }

                popup.setOnMenuItemClickListener(item -> {
                    // Find the route in the adapter's list
                    Route selectedRoute = null;
                    for (Route route : trackerAdapter.getFullRouteList()) {
                        if (route.getRt().equals(routeId)) {
                            selectedRoute = route;
                            break;
                        }
                    }

                    if (selectedRoute != null) {
                        Intent intent = new Intent(MainActivity.this, StopsActivity.class);
                        intent.putExtra("route_number", selectedRoute.getRt());
                        intent.putExtra("route_name", selectedRoute.getRtnm());
                        intent.putExtra("direction", item.getTitle().toString());
                        startActivity(intent);
                    }
                    return true;
                });

                popup.show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to parse directions",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
