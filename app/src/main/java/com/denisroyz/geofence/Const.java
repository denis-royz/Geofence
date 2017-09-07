package com.denisroyz.geofence;

/**
 * Created by Heralt on 21.11.2016.
 */

public class Const {


    public static long UPDATE_FREQ = 1000;

    public interface PREFS {
        String PREFS_GEOFENCE_RULE = "com.denisroyz.geofence.rule.prefs";
        
    }
    public interface ACTION {
        String MAIN_ACTION = "com.denisroyz.geofence.action.main";
        String START_FOREGROUND_ACTION = "com.denisroyz.geofence.action.startforeground";
        String STOP_FOREGROUND_ACTION = "com.denisroyz.geofence.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }
    public interface NOTIFICATION_CHANNEL_ID{
        String FOREGROUND_SERVICE = "com.denisroyz.geofence.ui.geofence.service_fg";
    }
}
