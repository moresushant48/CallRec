package io.moresushant48.callrec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.moresushant48.callrec.Adapters.RecordingsAdapter;
import io.moresushant48.callrec.Helpers.GetRecordings;
import io.moresushant48.callrec.Helpers.PlaySelectedMedia;

public class MainActivity extends AppCompatActivity implements RecordingsAdapter.OnItemClickListener {

    private static final int REQUEST_CODE = 1;
    public static final File rootFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "CallRec");

    private RecordingsAdapter recordingsAdapter;
    private ArrayList<HashMap<String, String>> listRecordings;

    private SwipeRefreshLayout refreshRecordings;
    private RecyclerView rvRecordings;
    private LinearLayout noDataFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForPermissions();

        createAppDirIfNotExists();

        // Call Service.
        ContextCompat.startForegroundService(this, new Intent(this, RecService.class));

        // Setup Refresh listener.
        refreshRecordings = findViewById(R.id.refreshRecordings);
        refreshRecordings.setOnRefreshListener(this::feedRecordings);

        // No data found.
        noDataFound = findViewById(R.id.noDataFound);

        // Setup Recordings ListView.
        rvRecordings = findViewById(R.id.rvRecordings);

        // Finally Feed the Recordings to listView.
        feedRecordings();
    }

    /*
     *   Setup @arrayAdapter.
     *   Set Adapter for @lvRecordings
     * */

    private void feedRecordings() {
        // Populate the ListView with Recordings.
        listRecordings = GetRecordings.getRecordings();

        // Display Views According to Recording availability.
        if (listRecordings.isEmpty()) {
            rvRecordings.setVisibility(View.GONE);
            noDataFound.setVisibility(View.VISIBLE);
        } else {
            rvRecordings.setVisibility(View.VISIBLE);
            noDataFound.setVisibility(View.GONE);
        }

        recordingsAdapter = new RecordingsAdapter(this, listRecordings, this);
        rvRecordings.setAdapter(recordingsAdapter);
        rvRecordings.setLayoutManager(new LinearLayoutManager(this));
        refreshRecordings.setRefreshing(false);
    }

    @Override
    public void onItemClickListener(int position) {

        new PlaySelectedMedia(listRecordings.get(position).get("song_name"), listRecordings.get(position).get("song_path")).show(getSupportFragmentManager(), "PlaySelectedMedia");

    }

    /*
     *   CREATE STORAGE DIRECTORY, IF DOSEN'T EXIST ALREADY.
     * */

    private void createAppDirIfNotExists() {

        // Create dir if dosen't exist.
        if (!rootFolder.exists())
            if (rootFolder.mkdirs())
                Log.e("DIR", "Created");
            else
                Log.e("DIR", "Cant create");

    }

    /*
    *   OPTIONS MENU
    * */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_settings :
                Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_about :
                Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     *   GET USER PERMISSIONS.
     *   START
     * */

    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // WHEN PERMISSION IS NOT GRANTED.

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) ||
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

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && (grantResults[0] + grantResults[1] + grantResults[2] + grantResults[3] == PackageManager.PERMISSION_GRANTED)) {
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