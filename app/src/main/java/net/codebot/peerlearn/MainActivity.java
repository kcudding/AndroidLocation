package net.codebot.peerlearn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends Activity {
    // used for permission checks
    private static final int REQUEST_CODE_PERMISSION = 2;
    //String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    String[] mPermissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    // tag for debugging
    public static final String TAG = "GPSTRACKER";

    // data to fetch from the location service

    @SuppressLint("HardwareIds")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new SimpleEula(this).show();

        // check permissions
        try {
            boolean perms = true;
            for(String s: mPermissions){
                perms = perms && (ActivityCompat.checkSelfPermission(this, s) == PackageManager.PERMISSION_GRANTED);
            }
            if(!perms){
                ActivityCompat.requestPermissions(this, mPermissions, REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // get data from tracking service
        GPSTracker.Builder().setContext(this.getApplicationContext());

        // make scrollable
        final TextView text = (TextView) findViewById(R.id.editText);
        text.setMovementMethod(new ScrollingMovementMethod());

        // exit program
        final Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")

             @Override
             public void onClick(View v) {
                finishAffinity();
                System.exit(0);
             }

        });

        // start tracking handler
        final Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View arg0) {
                // create a work request to fetch location data
                // first request runs immediately and schedules the next one
                OneTimeWorkRequest locationRequest = new OneTimeWorkRequest.Builder(LocationWorker.class).build();
                WorkManager.getInstance(getApplicationContext()).enqueue(locationRequest);
                text.setText("Peer Learn is tracking\n\n");
                start.setEnabled(false);
            }
        });

        // start tracking handler
        final Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(event -> {
                String locText = "No Location data";
                if(LocationWorker.location != null){
                    String lat = Double.toString(LocationWorker.location.getLatitude());
                    String lg = Double.toString(LocationWorker.location.getLongitude());
                    String alt = Double.toString(LocationWorker.location.getAltitude());
                    locText = lat + "," + lg + "," + alt + ";";
                }
                text.append(
                        LocationWorker.updated + "\n" + locText + "\n"
                );});
        final Button enableButton = (Button) findViewById(R.id.app_settings);
        enableButton.setOnClickListener(event -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }
}