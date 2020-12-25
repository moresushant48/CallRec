package io.moresushant48.callrec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    public  static final File rootFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "CallRec");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForPermissions();

        createAppDirIfNotExists();

        // Call Service
        ContextCompat.startForegroundService(this, new Intent(this, RecService.class));
    }


    /*
    *   CREATE STORAGE DIRECTORY, IF DOSEN'T EXIST ALREADY.
    * */

    private void createAppDirIfNotExists() {

        // Create dir if dosen't exist.
        if(!rootFolder.exists())
            if(rootFolder.mkdirs())
                Log.e("DIR", "Created");
            else
                Log.e("DIR", "Cant create");

    }

    /*
    *   GET USER PERMISSIONS.
    *   START
    * */

    private void checkForPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            // WHEN PERMISSION IS NOT GRANTED.

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PROCESS_OUTGOING_CALLS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show Alertbox in response.
                new AlertDialog.Builder(this)
                        .setTitle("Alert..!")
                        .setMessage("Grant permissions to Record Phone Calls.")
                        .setPositiveButton("Ok", (dialog, which) ->
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.READ_PHONE_STATE,
                                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                }, 1))
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            Toast.makeText(MainActivity.this, "Application won't work without Granting Permissions.", Toast.LENGTH_LONG).show();
                            MainActivity.this.finish();
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CODE);
            }

        } else {
            Toast.makeText(this, "Welcome.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && (grantResults[0] + grantResults[1] + grantResults[2] + grantResults[3] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Permissions Granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions Denied. Application cannot work.", Toast.LENGTH_SHORT).show();
                MainActivity.this.finish();
            }
        }

    }

    /*
     *   GET USER PERMISSIONS.
     *   END
     * */
}