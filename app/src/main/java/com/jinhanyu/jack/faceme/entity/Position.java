package com.jinhanyu.jack.faceme.entity;

/**
 * Created by anzhuo on 2016/11/2.
 */

public class Position {
    private String country;
    private String province;
    private String city;
    private double latitude;
    private double longitude;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {

        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
