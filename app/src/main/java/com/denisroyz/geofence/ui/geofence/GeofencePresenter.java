package com.denisroyz.geofence.ui.geofence;

import com.denisroyz.geofence.dao.GPSRule;
import com.denisroyz.geofence.dao.WifiRule;

/**
 * Created by Heralt on 05.09.2017.
 */

public interface GeofencePresenter {
    void enableSearch(boolean selected);

    void fillView();

    void subscribe();

    void unSubscribe();

    void save(GPSRule gpsRule);
    void save(WifiRule wifiRule);

    void savePermissionState(boolean permissionsGranted);
}
