package com.example.chaos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;

import com.example.chaos.Data.WeatherData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String api_key = Config.API_KEY.getValue();
    double lat = 23.748997;
    double lon = -84.387985;
    boolean permissionGrantedApprox = false;
    boolean permissionGrantedFine = false;

    Retrofit retrofit;
    WeatherAPI weatherAPI;
    WeatherData weatherData;

    AppCompatTextView location, temperature, condition;
    AppCompatImageView weatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        getUserLocation();

        location = findViewById(R.id.locationText);
        temperature = findViewById(R.id.tempText);
        condition = findViewById(R.id.condText);
        weatherIcon = findViewById(R.id.weatherIcon);

        if (permissionGrantedApprox || permissionGrantedFine) {

            retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create()).build();
            weatherAPI = retrofit.create(WeatherAPI.class);

            Call<WeatherData> call = weatherAPI.getWeather(lat, lon, api_key);
            call.enqueue(new Callback<WeatherData>() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                    if (response.isSuccessful()) {
                        weatherData = response.body();
                        if (weatherData != null) {
                            String temp = weatherData.getName() + ", " + weatherData.getSys().getCountry();
                            location.setText(temp);
                            temp = String.format("%.2f", weatherData.getMain().getTemp() - 274.15) + "° C";
                            temperature.setText(temp);
                            temp = weatherData.getWeather().get(0).getDescription();
                            condition.setText(temp);

                            temp = "w"+weatherData.getWeather().get(0).getIcon();
                            weatherIcon.setBackgroundResource(getResources().getIdentifier(temp,"drawable",getPackageName()));
                        }
                    } else {
                        Log.d("Error: ", response.message());
                    }
                }

                @Override
                public void onFailure(Call<WeatherData> call, Throwable t) {
                    Log.d("Failure: ", t.getMessage());
                }
            });
        } else {
            Toast.makeText(this, "Location Disabled", Toast.LENGTH_SHORT).show();
        }
    }

    public void getUserLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show();
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            Log.d("Lat and Long", "Lat "+lastKnownLocation.getLatitude() +"Long "+lastKnownLocation.getLongitude());
            lat = lastKnownLocation.getLatitude();
            lon = lastKnownLocation.getLongitude();
        }

//        LocationListener locationListener = location -> {
//            Log.d("Lat and Long", "Lat "+location.getLatitude() +"Long "+location.getLongitude());
//            lat = location.getLatitude();
//            lon = location.getLongitude();
//        };
//
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            permissionGrantedApprox = true;
        }

        if (requestCode == 2) {
            permissionGrantedFine = true;
        }

    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            permissionGrantedFine = true;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        } else {
            permissionGrantedApprox = true;
        }
    }
}