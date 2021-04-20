package com.example.speedometer;

import android.location.Location;

public class CLocation extends Location {
    private boolean bUserMetricUnits= false;
     private static final float feet = 3.28083989501312f;

    public CLocation(Location location) {
        this(location,true);
    }

    public CLocation(Location location,boolean bUserMetricUnits) {
        super(location);
        this.bUserMetricUnits=bUserMetricUnits;
    }

    public boolean getUserMetricUnits() {
        return this.bUserMetricUnits;
    }

    public void setUserMetricUnits(boolean bUserMetricUnits) {
        this.bUserMetricUnits = bUserMetricUnits;
    }

    @Override
    public float distanceTo(Location dest) {
        float nDistance = super.distanceTo(dest);
        if(!this.getUserMetricUnits()){
            //convert meters to feet
            nDistance = nDistance * this.feet;
        }
        return nDistance;
    }

    @Override
    public double getAltitude() {
        double nAltitude = super.getAltitude();
        if(!this.getUserMetricUnits()){
            //convert meters to feet
            nAltitude = nAltitude * (double) this.feet;
        }
        return nAltitude;
    }


    @Override
    public float getSpeed() {
        float nSpeed = super.getSpeed()* this.feet;
        if(!this.getUserMetricUnits()){
            //convert meters/second to miles/hour
            nSpeed = super.getSpeed() * 2.23693623f;
        }
        return nSpeed;
    }

    @Override
    public float getAccuracy() {
        float nAccuracy = super.getAccuracy();
        if(!this.getUserMetricUnits()){
            //convert meters to feet
            nAccuracy = nAccuracy * this.feet;
        }
        return nAccuracy;
    }

    @Override
    public double getLatitude() {
        return super.getLatitude();
    }

    @Override
    public double getLongitude() {
        return super.getLongitude();
    }
}
