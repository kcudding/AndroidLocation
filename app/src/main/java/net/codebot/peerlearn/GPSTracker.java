package net.codebot.peerlearn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import static android.content.Context.LOCATION_SERVICE;

public class GPSTracker implements LocationListener {

    private static Context context;
    private static GPSTracker instance;
    protected LocationManager locationManager;

    // tag for debugging
    public static final String TAG = "GPS_TRACKER";

    // flags
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    // last data fetched
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public static GPSTracker Builder(Context _context) {
        context = _context;
        return Builder();
    }

    public static GPSTracker Builder() {
        if (instance == null) {
            instance = new GPSTracker();
        }
        return instance;
    }

    private GPSTracker() {
        Log.d(TAG, "Constructor invoked");
    }

    public Context getContext() { return context; }

    public Location getLocation() {
        try {
            // clear last location
            location = null;

            // no network provider is enabled
            if (!canGetLocation()) {
                Log.d(TAG, "No data provider available");
            } else {
                // try network first
                if (isNetworkEnabled) {
                    try {
                        // success will set location
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            Log.d(TAG, "Network request succeeded");

                            if (location != null) {
                                Log.d(TAG, "Network request succeeded not null");
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    } catch (SecurityException exception) {
                        Log.d(TAG, "Network security exception");
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    // network must have failed so try GPS here
                    if (location == null) {
                        try {
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    Log.d(TAG, "GPS request succeeded");
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        } catch (SecurityException ex) {
                            Log.d(TAG, "GPS security exception");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Carry application context
     * @param _context
     */
    public void setContext(Context _context) {
        context = _context;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */

    public boolean canGetLocation() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGPSEnabled || isNetworkEnabled;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     * */

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}