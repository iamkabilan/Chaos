package com.example.chaos;

import com.example.chaos.Data.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {
    @GET("weather")
    Call<WeatherData> getWeather(@Query("lat")double latitude, @Query("lon")double longitude, @Query("appid")String apiKey);
}
