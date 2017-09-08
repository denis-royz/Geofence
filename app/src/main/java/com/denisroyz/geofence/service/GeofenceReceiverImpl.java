package com.denisroyz.geofence.service;

import android.location.Location;

import com.denisroyz.geofence.model.GPSRule;
import com.denisroyz.geofence.model.UserLocation;
import com.denisroyz.geofence.model.WifiRule;
import com.denisroyz.geofence.repository.CurrentLocationRepository;
import com.denisroyz.geofence.repository.GeofenceRuleRepository;
import com.denisroyz.geofence.repository.RuleRepositoryUpdateListener;
import com.denisroyz.geofence.repository.UserLocationRepositoryUpdateListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Heralt on 06.09.2017.
 */

public class GeofenceReceiverImpl implements GeofenceReceiver, UserLocationRepositoryUpdateListener, RuleRepositoryUpdateListener{

    private boolean mInside = false;

    private Set<GeofenceReceiverListener> listeners = new HashSet<>();
    private Set<GeofenceReceiverListener> newListeners = new HashSet<>();

    private CurrentLocationRepository mCurrentLocationRepository;
    private GeofenceRuleRepository mGeofenceRuleRepository;

    public GeofenceReceiverImpl(CurrentLocationRepository currentLocationRepository, GeofenceRuleRepository geofenceRuleRepository){
        this.mCurrentLocationRepository = currentLocationRepository;
        this.mGeofenceRuleRepository = geofenceRuleRepository;
        mGPSRule = geofenceRuleRepository.getGpsRule();
        mWifiRule = geofenceRuleRepository.getWifiRule();
    }

    private void subscribe(){
        mCurrentLocationRepository.addOnChangeListener(this);
        mGeofenceRuleRepository.addOnChangeListener(this);
    }

    private void unSubscribe(){
        mCurrentLocationRepository.removeOnChangeListener(this);
        mGeofenceRuleRepository.removeOnChangeListener(this);

    }
    private GPSRule mGPSRule;
    private WifiRule mWifiRule;
    private UserLocation mUserLocation;

    @Override
    public void onGpsRuleUpdated(GPSRule gpsRule) {
        this.mGPSRule = gpsRule;
        reCalculate();
    }
    @Override
    public void onWifiRuleUpdated(WifiRule wifiRule) {
        this.mWifiRule = wifiRule;
        reCalculate();
    }

    @Override
    public void onUserLocationUpdated(UserLocation userLocation) {
        mUserLocation = userLocation;
        reCalculate();
    }

    private void reCalculate(){
        boolean inside = false;
        if (mUserLocation == null) return;
        if (checkIsInsideWiFiNetwork()) inside = true;
        if (checkIsInsideGPSRadius()) inside = true;
        if (inside!=mInside){
            mInside = inside;
            notifyListeners();
        } else {
            notifyNewListeners();
        }
    }

    private boolean checkIsInsideGPSRadius(){
        return(
                mUserLocation.getLatitude()!=null
                &&mUserLocation.getLongitude()!=null
                &&mGPSRule!=null
                &&mGPSRule.getLat()!=null
                &&mGPSRule.getLon()!=null
                &&mGPSRule.getRadius()!=null
                &&ensureGPSPointIsInRadius(mUserLocation.getLatitude(), mUserLocation.getLongitude(), mGPSRule.getLat(), mGPSRule.getLon(), mGPSRule.getRadius())
        );
    }
    private boolean checkIsInsideWiFiNetwork(){
        return(
                mUserLocation.getWifiNetworkName()!=null
                &&mWifiRule!=null
                &&mWifiRule.getWifiNetworkName()!=null
                &&mWifiRule.getWifiNetworkName().equals(mUserLocation.getWifiNetworkName())
        );
    }

    //TODO implement
    private boolean ensureGPSPointIsInRadius(double lat, double lon, double latCenter, double lonCenter, double radius){

        double result = distance(lat, latCenter, lon, lonCenter);
        return result<radius;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @return Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance;
    }
    @Override
    public void addGeofenceReceiverListener(GeofenceReceiverListener geofenceReceiverListener) {
        listeners.add(geofenceReceiverListener);
        newListeners.add(geofenceReceiverListener);
        subscribe();
    }

    @Override
    public void removeGeofenceReceiverListener(GeofenceReceiverListener geofenceReceiverListener) {
        listeners.remove(geofenceReceiverListener);
        if (listeners.size()==0){
            unSubscribe();
        }
    }

    @Override
    public void notifyListeners() {
        for (GeofenceReceiverListener listener: listeners){
            listener.onGeofenceStatusUpdated(mInside);
        }
        newListeners.clear();
    }

    private void notifyNewListeners() {
        for (GeofenceReceiverListener listener: newListeners){
            listener.onGeofenceStatusUpdated(mInside);
        }
        newListeners.clear();
    }

    @Override
    public boolean getGeoFenceStatus() {
        return mInside;
    }


}
