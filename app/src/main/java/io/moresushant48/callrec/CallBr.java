package io.moresushant48.callrec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import io.moresushant48.callrec.Helpers.GetCurrentDateTime;

public class CallBr extends BroadcastReceiver {

    final private static String AUDIO_EXTENSION = ".amr";

    MediaRecorder recorder;
    private boolean recordStarted = false;

    Bundle bundle;
    String state;
    String inCall, outCall;
    public boolean wasRinging = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        recordAndSave(context, intent);

    }

    /*
    *   SETUP THE MEDIARECORDER (@MediaRecorder) FOR FURTHER RECORDINGS.
    * */

    private void recorderSetup() {
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setAudioSamplingRate(8000);
        recorder.setAudioEncodingBitRate(12200);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }

    /*
    *   UNDERSTAND AND DECIDE THE BROADCAST ACTIONS
    *   @ACTION_IN - INCOMING CALL, EXECUTING @inComingCallAction
    *   @ACTION_OUT - OUTGOING CALL, EXECUTING @outGoingCallAction
    * */

    private void recordAndSave(Context context, Intent intent) {

        // INCOMING
        if(intent.getAction().equals(RecService.ACTION_IN)) {

            inComingCallAction(context, intent);
        }

        // OUTGOING
        else if(intent.getAction().equals(RecService.ACTION_OUT)) {

            outGoingCallAction(context, intent);
        }
    }

    /*
    *   PERFORM ACTIONS RELATED TO @ACTION_IN.
    * */

    private void inComingCallAction(Context context, Intent intent) {
        if((bundle = intent.getExtras()) != null) {

            state = bundle.getString(TelephonyManager.EXTRA_STATE);
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                inCall = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                wasRinging = true;

            }
            else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                if (wasRinging) {

                    Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show();
                    recorderSetup();

                    recorder.setOutputFile(MainActivity.rootFolder.getPath() + File.separator + "IN-" + GetCurrentDateTime.getCurrentDateTime() + AUDIO_EXTENSION);
                    Log.e("Filename",MainActivity.rootFolder.getPath());

                    try {
                        recorder.prepare();
                        Thread.sleep(2000);
                        recorder.start();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    recordStarted = true;
                }
            }
            else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                wasRinging = false;
                Toast.makeText(context, "REJECT", Toast.LENGTH_LONG).show();
                if (recordStarted) {
                    Toast.makeText(context, "Stopping recorder.", Toast.LENGTH_LONG).show();
                    recorder.stop();
                    recordStarted = false;
                }
            }
        }
    }

    /*
     *   PERFORM ACTIONS RELATED TO @ACTION_OUT.
     * */

    private void outGoingCallAction(Context context, Intent intent) {
        if((bundle = intent.getExtras()) != null) {

            outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Toast.makeText(context, "OUT : " + outCall, Toast.LENGTH_LONG).show();

            recorderSetup();

            recorder.setOutputFile(MainActivity.rootFolder.getPath() + File.separator + "OUT-" + GetCurrentDateTime.getCurrentDateTime() + AUDIO_EXTENSION);
            Log.e("Filename", MainActivity.rootFolder.getPath());

            try {
                recorder.prepare();
                Thread.sleep(2000);
                recorder.start();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            recordStarted = true;

            if ((bundle = intent.getExtras()) != null) {
                state = bundle.getString(TelephonyManager.EXTRA_STATE);
                if (state != null) {
                    if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        wasRinging = false;
                        if (recordStarted) {
                            Toast.makeText(context, "Stopping recorder.", Toast.LENGTH_LONG).show();
                            recorder.stop();
                            recordStarted = false;
                        }
                    }
                }
            }
        }
    }
}
