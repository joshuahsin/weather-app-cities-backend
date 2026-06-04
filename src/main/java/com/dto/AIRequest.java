package com.dto;

public class AIRequest {
    private AIContext context;

    public AIContext getContext() { return context; }
    public void setContext(AIContext context) { this.context = context; }

    public static class AIContext {
        private LocationContext location;
        private WeatherContext weather;

        public LocationContext getLocation() { return location; }
        public void setLocation(LocationContext location) { this.location = location; }
        public WeatherContext getWeather() { return weather; }
        public void setWeather(WeatherContext weather) { this.weather = weather; }
    }

    public static class LocationContext {
        private double lat;
        private double lon;

        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }
        public double getLon() { return lon; }
        public void setLon(double lon) { this.lon = lon; }
    }

    public static class WeatherContext {
        private double temperature;
        private double feels_like;
        private String description;
        private double humidity;
        private double cloud_coverage;
        private double rain;
        private double snow;
        private double wind_speed;
        private double visibility;
        private String sunrise;
        private String sunset;
        private String current_day;
        private String current_time;

        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public double getFeels_like() { return feels_like; }
        public void setFeels_like(double feels_like) { this.feels_like = feels_like; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public double getHumidity() { return humidity; }
        public void setHumidity(double humidity) { this.humidity = humidity; }
        public double getCloud_coverage() { return cloud_coverage; }
        public void setCloud_coverage(double cloud_coverage) { this.cloud_coverage = cloud_coverage; }
        public double getRain() { return rain; }
        public void setRain(double rain) { this.rain = rain; }
        public double getSnow() { return snow; }
        public void setSnow(double snow) { this.snow = snow; }
        public double getWind_speed() { return wind_speed; }
        public void setWind_speed(double wind_speed) { this.wind_speed = wind_speed; }
        public double getVisibility() { return visibility; }
        public void setVisibility(double visibility) { this.visibility = visibility; }
        public String getSunrise() { return sunrise; }
        public void setSunrise(String sunrise) { this.sunrise = sunrise; }
        public String getSunset() { return sunset; }
        public void setSunset(String sunset) { this.sunset = sunset; }
        public String getCurrent_day() { return current_day; }
        public void setCurrent_day(String current_day) { this.current_day = current_day; }
        public String getCurrent_time() { return current_time; }
        public void setCurrent_time(String current_time) { this.current_time = current_time; }
    }
}
