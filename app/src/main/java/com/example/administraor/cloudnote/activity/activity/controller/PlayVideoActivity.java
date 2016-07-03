package com.example.administraor.cloudnote.activity.activity.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

import com.example.administraor.cloudnote.R;

import java.io.File;

public class PlayVideoActivity extends AppCompatActivity {

    private String path;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        videoView = (VideoView)findViewById(R.id.video_view);
        path = getIntent().getExtras().get("path").toString();
        if (path!=null){
            File file = new File(path);
            if (file.exists()){
                videoView.setVideoPath(file.getAbsolutePath());
                videoView.requestFocus();
                videoView.start();
            }
        }
    }
}
