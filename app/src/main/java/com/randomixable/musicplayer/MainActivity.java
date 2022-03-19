package com.randomixable.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private SeekBar seek_bar;
    private ImageButton button_play, button_stop;
    private TextView txtTimeDuration;

    private MediaPlayer mediaPlayer;
    private double startTime = 0 ;
    private double finalTime = 0;

    private boolean isPlaying = false;

    private Handler syncHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seek_bar = findViewById(R.id.seek_bar);
        button_play = findViewById(R.id.button_play) ;
        button_stop = findViewById(R.id.button_stop);
        txtTimeDuration = findViewById(R.id.txtTimeDuration);

        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afdObj = getAssets().openFd("sinister.mp3");
            mediaPlayer.setDataSource(afdObj.getFileDescriptor());
            mediaPlayer.prepare();
            Toast.makeText(this, "Tap to play", Toast.LENGTH_SHORT).show();

        }
        catch (Exception ex)  {
            ex.printStackTrace();
        }

        seek_bar.setProgress(0);

        button_play.setOnClickListener(view -> {
            if(!isPlaying) {
                isPlaying = true;

                button_play.setImageResource(R.drawable.btn_pause);

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                txtTimeDuration.setText(String.format("%d min %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MILLISECONDS.toMinutes((long) startTime)
                ));

                seek_bar.setMax((int) finalTime);
                seek_bar.setProgress((int) startTime);

                mediaPlayer.start();

                syncHandler.postDelayed(updateDuration, 100);
            }
            else {
                isPlaying = false;

                button_play.setImageResource(R.drawable.btn_play);
                mediaPlayer.pause();
            }
        });

        button_stop.setOnClickListener(view -> {
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
            seek_bar.setProgress(0);
            button_play.setImageResource(R.drawable.btn_play);
            isPlaying = false;
        });

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
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

    private Runnable updateDuration = new Runnable() {

        @Override
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            txtTimeDuration.setText(String.format("%d min %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime)
            ));
            seek_bar.setProgress((int) startTime);
            syncHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}