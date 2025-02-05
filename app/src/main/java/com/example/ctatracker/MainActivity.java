package com.example.ctatracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
//import android.window.SplashScreen;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import androidx.core.splashscreen.SplashScreen;
import android.Manifest;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private trackerAdapter trackerAdapter;

    private AdView adView;

    private static final String API_KEY = "CYE7SDQGvNbsRTgz3MjGbyegC";
    private static final String adUnitId = "ca-app-pub-3940256099942544/6300978111";
    private static final String TAG = "MainActivity";

    private boolean keepOn = true;
    private static final long minSplashTime = 2000;
    private long startTime;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);


        startTime = System.currentTimeMillis();
        splashScreen.setKeepOnScreenCondition(() -> {
            long elapsedTime = System.currentTimeMillis() - startTime;
            Log.d(TAG, "Splash screen elapsed time: " + elapsedTime);
            return keepOn || (elapsedTime <= minSplashTime);
        });


        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        requestLocationPermission();
        checkNetworkConnectivity();


        trackerAdapter = new trackerAdapter(this, new ArrayList<>());
        activityMainBinding.recylerView.setLayoutManager(new LinearLayoutManager(this));
        activityMainBinding.recylerView.setAdapter(trackerAdapter);


        setupSearchFunctionality();


        setupAds();
        fetchRoutes();


        setupInfoButton();
    }

    private void setupSearchFunctionality() {
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
    }

    private void setupInfoButton() {
        final String url = "https://www.transitchicago.com/developers/bustracker/";

        activityMainBinding.info.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setIcon(R.drawable.splash_logo)
                    .setTitle("Bus Tracker - CTA")
                    .setMessage("CTA Bus Tracker data provided by\n" +
                            "Chicago Transit Authority\n\n" +
                            url)
                    .setPositiveButton("OK", (dialogInterface, which) -> dialogInterface.dismiss())
                    .setNeutralButton("Open Link", (dialogInterface, which) -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    })
                    .create();

            dialog.show();

            TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
            if (messageView != null) {
                messageView.setTextColor(Color.GRAY);
            }
        });
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {

            onLocationPermissionGranted();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                onLocationPermissionGranted();
            } else {

                showRationaleDialog();
            }
        }
    }

    private void showRationaleDialog() {
        View rootView = activityMainBinding.getRoot();
        rootView.setBackgroundColor(getResources().getColor(android.R.color.black, getTheme()));


        activityMainBinding.toolbar.setVisibility(View.GONE);
        activityMainBinding.searchInputLayout.setVisibility(View.GONE);
        activityMainBinding.recylerView.setVisibility(View.GONE);
        activityMainBinding.adViewContainer.setVisibility(View.GONE);
        activityMainBinding.info.setVisibility(View.GONE);
        activityMainBinding.busIcon.setVisibility(View.GONE);

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.splash_logo)
                .setTitle("Fine Accuracy Needed")
                .setMessage("This application needs fine accuracy\npermission in order to determine the\nclosest bus stops to your location. It will\nnot function properly without it. Will you\nallow it?")
                .setPositiveButton("Yes", (dialog, which) -> requestLocationPermission()) // Re-request permission
                .setNegativeButton("No Thanks", (dialog, which) -> showFinalRationaleDialog()) // Show final rationale
                .setCancelable(false)
                .show();
    }

    private void showFinalRationaleDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.splash_logo)
                .setTitle("Fine Accuracy Needed")
                .setMessage("This application needs fine accuracy\npermission in order to determine the\nclosest bus stops to your location. It will\nnot function properly without it. Will you\nallow it?")
                .setPositiveButton("OK", (dialog, which) -> closeApp())
                .setCancelable(false)
                .show();
    }

    private void closeApp() {
        finishAffinity();
    }

    private void onLocationPermissionGranted() {
        View rootView = activityMainBinding.getRoot();
        rootView.setBackgroundColor(getResources().getColor(R.color.darkgreen, getTheme()));


        activityMainBinding.toolbar.setVisibility(View.VISIBLE);
        activityMainBinding.searchInputLayout.setVisibility(View.VISIBLE);
        activityMainBinding.recylerView.setVisibility(View.VISIBLE);
        activityMainBinding.adViewContainer.setVisibility(View.VISIBLE);
        activityMainBinding.info.setVisibility(View.VISIBLE);
        activityMainBinding.busIcon.setVisibility(View.VISIBLE);
        setupAds();
        fetchRoutes();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkLocationAvailability();
        checkNetworkConnectivity();
    }

        private void checkLocationAvailability() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isLocationEnabled) {
            showLocationRationaleAndClose();

            }
        }

        private void showLocationRationaleAndClose() {


        new AlertDialog.Builder(this)
                .setTitle("Bus Tracker - CTA")
                .setMessage("Unable to determine device location. If this\n is an emulator. please set the location")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    finish();
                })
                .show();
        }

    private void checkNetworkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnected()) {
            showNetworkRationaleAndClose();
        }
    }

    private void showNetworkRationaleAndClose() {

        new AlertDialog.Builder(this)
                .setTitle("Bus Tracker - CTA")
                .setMessage("Unable to contact Bus Tracker API due\n to network problem. Please check\n your network connection")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    finish();
                })
                .show();
    }



    private void loadAdaptiveBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdListener(new BannerAdListener());
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float adWidthPixels = adView.getWidth();

        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels;
        }

        float density = getResources().getDisplayMetrics().density;
        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);

    }

    private void dismissSplashScreen() {
        keepOn = false;
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
                dismissSplashScreen();
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
        adView.setAdSize(getAdSize());

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
