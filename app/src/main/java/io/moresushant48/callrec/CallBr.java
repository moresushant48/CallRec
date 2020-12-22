package io.moresushant48.callrec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class CallBr extends BroadcastReceiver {

    MediaRecorder recorder;
    private boolean recordstarted = false;

    Bundle bundle;
    String state;
    String inCall, outCall;
    public boolean wasRinging = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "CALL", Toast.LENGTH_LONG).show();

        recordAndSave(context, intent);

    }

    private void recordAndSave(Context context, Intent intent) {
        
        if(intent.getAction().equals(RecService.ACTION_IN)) {

            if((bundle = intent.getExtras()) != null) {

                state = bundle.getString(TelephonyManager.EXTRA_STATE);
                if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                    inCall = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    wasRinging = true;
                    Toast.makeText(context, "IN : " + inCall, Toast.LENGTH_LONG).show();

                }
                else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                    if (wasRinging) {

                        Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show();
                        recorder = new MediaRecorder();
                        recorder.reset();
                        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                        recorder.setAudioSamplingRate(8000);
                        recorder.setAudioEncodingBitRate(12200);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                        recorder.setOutputFile(MainActivity.rootFolder.getPath() + File.separator + "AudioRec.amr");
                        Log.e("Filename",MainActivity.rootFolder.getPath());

                        try {
                            recorder.prepare();
                            Thread.sleep(2000);
                            recorder.start();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        recordstarted = true;
                    }

                }
                else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                    wasRinging = false;
                    Toast.makeText(context, "REJECT", Toast.LENGTH_LONG).show();
                    if (recordstarted) {
                        Toast.makeText(context, "Stopping recorder.", Toast.LENGTH_LONG).show();
                        recorder.stop();
                        recordstarted = false;
                    }

                }

            }

        }
        else if(intent.getAction().equals(RecService.ACTION_OUT)) {
            if ((bundle = intent.getExtras()) != null) {
                outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Toast.makeText(context, "OUT : " + outCall, Toast.LENGTH_LONG).show();
            }
        }
    }
}
