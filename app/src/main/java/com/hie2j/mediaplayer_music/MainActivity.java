package com.hie2j.mediaplayer_music;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int MSG_PROGRESS_CHANGE = 1001;
    private Button btnStart;
    private Button btnPause;
    private Button btnStop;
    private Button btnSingleCycle;
    private Button btnPrevious;
    private Button btnNext;
    private SeekBar seekBar;
    private TextView txtCurrent;
    private TextView txtTotal;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPause = false;
    private Handler handler;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.start);
        btnPause = findViewById(R.id.pause);
        btnStop = findViewById(R.id.stop);
        btnSingleCycle = findViewById(R.id.single_cycle);
        btnPrevious = findViewById(R.id.previous);
        btnNext = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        txtCurrent = findViewById(R.id.current);
        txtTotal = findViewById(R.id.total);

        final String[] paths = {"/mnt/shared/Other/shangfeng.aac","/mnt/shared/Other/xiaoqingwa.mp3"};

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                int duration = mediaPlayer.getDuration();
                int position = duration * progress / 100;
                mediaPlayer.seekTo(position);
            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == MSG_PROGRESS_CHANGE) {
                    int position = msg.arg1;
                    int duration = msg.arg2;
                    txtCurrent.setText(getTimeStr(position));
                    txtTotal.setText(getTimeStr(duration));
                }
                return false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (mediaPlayer.isPlaying()) {
                        int position = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();
                        int progress = position * 100 / duration;
                        seekBar.setProgress(progress);

                        Message message = new Message();
                        message.what = MSG_PROGRESS_CHANGE;
                        message.arg1 = position;
                        message.arg2 = duration;
                        handler.sendMessage(message);
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        btnSingleCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isLooping()) {
                    mediaPlayer.setLooping(true);
                    Toast.makeText(MainActivity.this, "进入单曲循环", Toast.LENGTH_SHORT).show();
                } else {
                    mediaPlayer.setLooping(false);
                    Toast.makeText(MainActivity.this, "退出单曲循环", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 0){
                    i = 1;
                }else{
                    i = 0;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(paths[i]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.prepareAsync();
                }else {
                    mediaPlayer.reset();
                    mediaPlayer.prepareAsync();
                    try {
                        mediaPlayer.setDataSource(paths[i]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isPause = true;
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i != 0){
                    i = 0;
                }else{
                    i = 1;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(paths[i]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });

                }else {
                    mediaPlayer.reset();
                    mediaPlayer.prepareAsync();
                    try {
                        mediaPlayer.setDataSource(paths[i]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isPause = true;
                }
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPause) {
                    mediaPlayer.start();
                    isPause = false;
                } else {
                    // 第一次或者停止后启动
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource("/mnt/shared/Other/shangfeng.aac");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.prepareAsync();
                }
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPause = true;
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            }
        });


    }

    private String getTimeStr(int position) {
        StringBuffer buffer = new StringBuffer();

        int sencod = position / 1000;
        int min = sencod / 60;
        sencod = sencod % 60;

        if (min < 10) {
            buffer.append("0");
        }
        buffer.append(min);
        buffer.append(":");
        if (sencod < 10) {
            buffer.append("0");
        }
        buffer.append(sencod);
        return buffer.toString();
    }

}