package com.cs501.project.Model;

public class Weather {

    private double currentTemp;
    private String weatherType;
    private String weatherDes;
    private double windSpeed;
    private double windGust;
    private double clouds;
    private double humidity;

    public Weather(double currentTemp, String weatherType, String weatherDes, double windSpeed, double windGust, double clouds, double humidity){
        this.currentTemp = currentTemp;
        this.weatherType = weatherType;
        this.weatherDes = weatherDes;
        this.windSpeed = windSpeed;
        this.windGust = windGust;
        this.clouds = clouds;
        this.humidity = humidity;
    }

    public double getCurrentTemp() {
        return currentTemp;
    }

    public String getWeatherType() {
        return weatherType;
    }

    public String getWeatherDes() {
        return weatherDes;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindGust() {
        return windGust;
    }

    public double getClouds() {
        return clouds;
    }

    public double getHumidity() {
        return humidity;
    }
}
