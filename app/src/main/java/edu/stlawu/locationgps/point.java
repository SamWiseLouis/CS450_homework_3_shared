package edu.stlawu.locationgps;

public class point {
    private Double latitude;
    private Double longitude;
    private long time;

    public point(Double lat, Double lon, long t){
        this.latitude = lat;
        this.longitude = lon;
        this.time = t;  // transfer to seconds
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude(){
        return longitude;
    }
    public long getTime(){
        return time;
    }
}
