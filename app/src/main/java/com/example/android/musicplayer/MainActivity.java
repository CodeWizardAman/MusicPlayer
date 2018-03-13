package com.example.android.musicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import 	java.util.Date;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private ImageView artistImage;
    private TextView leftTime;
    private TextView rightTime;
    private SeekBar seekBar;
    private Button prevButton;
    private Button playButton;
    private Button nextButton;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");

                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                leftTime.setText(String.valueOf(dateFormat.format(new Date(currentPosition))));
                rightTime.setText(String.valueOf(dateFormat.format(new Date(duration - currentPosition))));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setUpUI() {

        mediaPlayer = new MediaPlayer();

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mundiyan);

        artistImage = (ImageView) findViewById(R.id.img_view);
        leftTime = (TextView) findViewById(R.id.left_txt);
        rightTime = (TextView) findViewById(R.id.right_txt);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        prevButton = (Button) findViewById(R.id.prev_btn);
        playButton = (Button) findViewById(R.id.play_btn);
        nextButton = (Button) findViewById(R.id.next_btn);

        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.prev_btn:
                backMusic();
                break;

            case R.id.next_btn:
                nextMusic();
                break;

            case R.id.play_btn:
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        pauseMusic();
                    } else {
                        startMusic();
                    }
                }
                break;
        }

    }

    private void startMusic() {


        mediaPlayer.start();
        updateThread();
        playButton.setBackgroundResource(R.drawable.pause);


    }

    private void pauseMusic() {


        mediaPlayer.pause();
        playButton.setBackgroundResource(R.drawable.play);


    }

    public void updateThread(){

        thread = new Thread(){
            @Override
            public void run() {
                super.run();

                try{
                    while(mediaPlayer != null && mediaPlayer.isPlaying()) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);

                                leftTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss").format(
                                        new Date(mediaPlayer.getCurrentPosition())
                                )));

                                rightTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss").format(
                                        new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition())
                                        )
                                ));
                            }
                        });
                    }
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }

            }
        };
        thread.start();
    }

    public void backMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
        }
    }

    public void nextMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }

    @Override
    protected void onDestroy() {

        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}
