package com.blogspot.techtibet.tempapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.util.HashMap;
import java.util.Map;

public class PlayActivity extends AppCompatActivity{
//    private static final String TAG ="" ;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private TextView vName;
    private TextView vCount;
    private  TextView vDate;
    private TextView vUploader;
    private ProgressBar mBuffer;
//    int duration=0;
//    int current=0;
//uV
    RelativeLayout mBottomLayout;
    FrameLayout mVideoLayout;
    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mAuth=FirebaseAuth.getInstance();
        mStore=FirebaseFirestore.getInstance();
        mBuffer=findViewById(R.id.video_buffer);


        Intent intent=getIntent();
        String videourl=intent.getStringExtra("videourl");
        String vname=intent.getStringExtra("vname");
        long vcount= intent.getLongExtra("vcount",0);
        String vdate=intent.getStringExtra("vdate");
        String vuploader=intent.getStringExtra("vuploader");
        final int[] height = new int[1];


        final String videoId=intent.getStringExtra("videoId");

        mVideoLayout=findViewById(R.id.video_layout);
        mBottomLayout=findViewById(R.id.rellayout);
        vName=findViewById(R.id.play_video_name);
        vCount=findViewById(R.id.play_view_count);
        vDate=findViewById(R.id.play_video_date);
        vUploader=findViewById(R.id.play_video_uploader);
        setVideoDetail(vname,vcount,vdate,vuploader);



        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
        Uri uri=Uri.parse(videourl);
        mVideoView.setVideoURI(uri);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
        mVideoView.setMediaController(mMediaController);
        mVideoView.start();
        countView(videoId,vcount);
        mVideoView.post(new Runnable() {
            @Override
            public void run() {
                 height[0] =mVideoLayout.getHeight();
            }
        });





        mVideoView.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {



            @Override
            public void onScaleChange(boolean isFullscreen) {


                if (isFullscreen) {
                    ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    mVideoLayout.setLayoutParams(layoutParams);
                    //GONE the unconcerned views to leave room for video and controller
                    mBottomLayout.setVisibility(View.GONE);
                } else {

                    ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int cachedHeight=300;
                    layoutParams.height = height[0];
                    Toast.makeText(PlayActivity.this, "inside:"+ height[0], Toast.LENGTH_SHORT).show();
                    mVideoLayout.setLayoutParams(layoutParams);
                    mBottomLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPause(MediaPlayer mediaPlayer) {
            }

            @Override
            public void onStart(MediaPlayer mediaPlayer) {

            }

            @Override
            public void onBufferingStart(MediaPlayer mediaPlayer) {
                mBuffer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onBufferingEnd(MediaPlayer mediaPlayer) {
                mBuffer.setVisibility(View.INVISIBLE);

            }
        });


    }

    private void countView(String videoId,long viewVount) {
        viewVount++;
        Map<String,Object> map=new HashMap<>();
        map.put("view_count",viewVount);
        mStore.collection("Files").document(videoId).update(map);
    }

    private void setVideoDetail(String vname, long vcount, String vdate, String vuploader) {
        vName.setText(vname);
        vCount.setText(vcount+" views");
        vDate.setText(vdate);
        vUploader.setText(vuploader);
    }

}
