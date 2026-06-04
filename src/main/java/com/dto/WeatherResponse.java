package com.dto;

public class WeatherResponse {
    private LocationData location;
    private WeatherData weather;
    private WindData wind;
    private TimeData time;

    public LocationData getLocation() { return location; }
    public void setLocation(LocationData location) { this.location = location; }
    public WeatherData getWeather() { return weather; }
    public void setWeather(WeatherData weather) { this.weather = weather; }
    public WindData getWind() { return wind; }
    public void setWind(WindData wind) { this.wind = wind; }
    public TimeData getTime() { return time; }
    public void setTime(TimeData time) { this.time = time; }

    public static class LocationData {
        private double lat;
        private double lon;
        private String name;
        private String country;

        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }
        public double getLon() { return lon; }
        public void setLon(double lon) { this.lon = lon; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }

    public static class WeatherData {
        private int id;
        private int temp;
        private int feels_like;
        private int humidity;
        private int pressure;
        private String description;
        private String main;
        private double visibility;
        private int cloud_coverage;
        private double rain;
        private double snow;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getTemp() { return temp; }
        public void setTemp(int temp) { this.temp = temp; }
        public int getFeels_like() { return feels_like; }
        public void setFeels_like(int feels_like) { this.feels_like = feels_like; }
        public int getHumidity() { return humidity; }
        public void setHumidity(int humidity) { this.humidity = humidity; }
        public int getPressure() { return pressure; }
        public void setPressure(int pressure) { this.pressure = pressure; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getMain() { return main; }
        public void setMain(String main) { this.main = main; }
        public double getVisibility() { return visibility; }
        public void setVisibility(double visibility) { this.visibility = visibility; }
        public int getCloud_coverage() { return cloud_coverage; }
        public void setCloud_coverage(int cloud_coverage) { this.cloud_coverage = cloud_coverage; }
        public double getRain() { return rain; }
        public void setRain(double rain) { this.rain = rain; }
        public double getSnow() { return snow; }
        public void setSnow(double snow) { this.snow = snow; }
    }

    public static class WindData {
        private double speed;
        private double deg;
        private String gust;

        public double getSpeed() { return speed; }
        public void setSpeed(double speed) { this.speed = speed; }
        public double getDeg() { return deg; }
        public void setDeg(double deg) { this.deg = deg; }
        public String getGust() { return gust; }
        public void setGust(String gust) { this.gust = gust; }
    }

    public static class TimeData {
        private String sunrise;
        private String sunset;
        private String timestamp;

        public String getSunrise() { return sunrise; }
        public void setSunrise(String sunrise) { this.sunrise = sunrise; }
        public String getSunset() { return sunset; }
        public void setSunset(String sunset) { this.sunset = sunset; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}
