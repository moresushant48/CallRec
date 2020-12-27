package io.moresushant48.callrec.Helpers;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.moresushant48.callrec.R;

public class PlaySelectedMedia extends BottomSheetDialogFragment {

    private MediaPlayer mediaPlayer;
    private TextView txtSelectedMediaName;
    private ImageView btnPlayPause;
    private AppCompatSeekBar seekBar;

    final private String name;
    final private String path;

    public PlaySelectedMedia(String name, String path) {
        this.name = name;
        this.path = path;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_selected_media, container, true);

        txtSelectedMediaName = view.findViewById(R.id.txtSelectedMediaName);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        seekBar = view.findViewById(R.id.seekBar);

        setup();
        play();

        return view;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        stop();
    }

    private void setup() {

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> stop());

        // SET THE MEDIA NAME IN ITS PLACE.
        txtSelectedMediaName.setText(name);

        // Setup Play Pause button.

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    btnPlayPause.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                } else {
                    btnPlayPause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
    }

    /*
     *   GET THE PATH,
     *   PLAY THE RECORDING
     * */

    private void play() {

        // Start Playing Media.

        try {
            if (mediaPlayer != null) {
                mediaPlayer.setDataSource("file://" + path);
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(mp -> {
                    seekBar.setMax(mediaPlayer.getDuration());
                });
                mediaPlayer.start();
            }
        } catch (IOException e) {
            Log.e("TAG", "Could not open file " + path + " for playback.", e);
        }

        // SeekBar Setup

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        }, 0, 1000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /*
     *   STOP MEDIA PLAYER
     * */

    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            dismiss();
        }
    }
}