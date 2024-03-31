package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.widget.EditText;

import com.example.weatherapp.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 8a69bcf3e3111a4d6486607f2faabfff

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchWeatherData("Ratlam");
        SearchCity();

    }

    private void SearchCity() {
        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null) {
                    fetchWeatherData(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

    }

    private void fetchWeatherData(String cityName) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<WeatherApp> call = apiInterface.getWeatherData(cityName, "8a69bcf3e3111a4d6486607f2faabfff", "metric");

        call.enqueue(new Callback<WeatherApp>() {
            @Override
            public void onResponse(@NonNull Call<WeatherApp> call, @NonNull Response<WeatherApp> response) {

                WeatherApp responseBody = response.body();
                if (response.isSuccessful() && responseBody != null) {
                    String temperature = String.valueOf(responseBody.getMain().getTemp());
                    String humidity = String.valueOf(responseBody.getMain().getHumidity());
                    String windSpeed = String.valueOf(responseBody.getWind().getSpeed());
                    String sunRise = String.valueOf(responseBody.getSys().getSunrise());
                    String sunSet = String.valueOf(responseBody.getSys().getSunset());
                    String seaLevel = String.valueOf(responseBody.getMain().getSeaLevel());
                    String condition = String.valueOf(responseBody.getWeather().isEmpty() ? "unknown" : responseBody.getWeather().get(0).getMain());
                    String maxTemp = String.valueOf(responseBody.getMain().getTempMax());
                    String minTemp = String.valueOf(responseBody.getMain().getTempMin());

                    binding.temper.setText(temperature);
                    binding.weather.setText(condition);
                    binding.maxTemp.setText(maxTemp + " °C");
                    binding.minTemp.setText(minTemp + " °C");
                    binding.humidity.setText(humidity + " %");
                    binding.windSpeed.setText(windSpeed + " m/s");
                    binding.sunRise.setText(time(Long.parseLong(sunRise)));
                    binding.sunSet.setText(time(Long.parseLong(sunSet)));
                    binding.sea.setText(seaLevel + " hPa");
                    binding.condition.setText(condition);
                    binding.day.setText(dayName(System.currentTimeMillis()) + " / ");
                    binding.date.setText(date(System.currentTimeMillis()));
                    binding.cityName.setText(cityName);

                    //Log.d("TAG", "onResponse: " + temperature);

                    changeImagesAccordingToWeatherConditions(condition);
                }

            }

            @Override
            public void onFailure(Call<WeatherApp> call, Throwable t) {
                // Handle failure here
                t.printStackTrace();
            }
        });

    }

    private void changeImagesAccordingToWeatherConditions(String conditions) {

        switch (conditions) {

            case "Clear Sky":
            case "Sunny":
            case "Clear":
                binding.getRoot().setBackgroundResource(R.drawable.clear_bg);
                binding.lottieAnimationView.setAnimation(R.raw.sunny);
                break;

            case "Party Clouds":
            case "Clouds":
            case "Overcast":
                binding.getRoot().setBackgroundResource(R.drawable.cloud_bg);
                binding.lottieAnimationView.setAnimation(R.raw.cloudy);
                break;

            case "Light Rain":
            case "Drizzle":
            case "Moderate Rain":
            case "Showers":
            case "Heavy Rain":
                binding.getRoot().setBackgroundResource(R.drawable.rain_bg);
                binding.lottieAnimationView.setAnimation(R.raw.rain);
                break;

            case "Light Snow":
            case "Moderate Snow":
            case "Heavy Snow":
            case "Blizzard":
                binding.getRoot().setBackgroundResource(R.drawable.snow_bg);
                binding.lottieAnimationView.setAnimation(R.raw.snow);
                break;

            case "Mist":
            case "Foggy":
                binding.getRoot().setBackgroundResource(R.drawable.mist_fog_bg);
                binding.lottieAnimationView.setAnimation(R.raw.fog_mist);
                break;

            case "Haze":
                binding.getRoot().setBackgroundResource(R.drawable.haze_bg);
                binding.lottieAnimationView.setAnimation(R.raw.haze);
                break;

            case "Storm":
                binding.getRoot().setBackgroundResource(R.drawable.storm_bg);
                binding.lottieAnimationView.setAnimation(R.raw.storm);
                break;

            case "Smoke":
                binding.getRoot().setBackgroundResource(R.drawable.smoke_bg);
                binding.lottieAnimationView.setAnimation(R.raw.smoky);
                break;

            default:
                binding.getRoot().setBackgroundResource(R.drawable.neutral_bg);
                binding.lottieAnimationView.setAnimation(R.raw.sun_with_cloud);
                break;
        }

        binding.lottieAnimationView.playAnimation();
    }

    private static String dayName(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String date(long l) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private static String time(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timeStamp*1000));
    }

}