package com.blogspot.techtibet.tempapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayActivity extends AppCompatActivity{
    private static final String TAG ="" ;
    //    private static final String TAG ="" ;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private TextView vName;
    private TextView vCount;
    private  TextView vDate;
    private TextView vUploader;
    private TextView mCommentBtn;
    private TextView mCommentCount;
    private RecyclerView commentRecyclerView;
    private List<Comments> commentsList;
    private CommentRecyclerAdapter commentAdapter;
    int commentCount=0;
//    int duration=0;
//    int current=0;
//uV
    RelativeLayout mBottomLayout;
    FrameLayout mVideoLayout;
    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mAuth=FirebaseAuth.getInstance();
        mStore=FirebaseFirestore.getInstance();
        mCommentBtn=findViewById(R.id.commentbtn);
        commentRecyclerView=findViewById(R.id.comment_list);
        mCommentCount=findViewById(R.id.comment_count);
        commentsList=new ArrayList<>();
        commentAdapter=new CommentRecyclerAdapter(commentsList,getApplicationContext());
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        commentRecyclerView.setAdapter(commentAdapter);


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
            }

            @Override
            public void onBufferingEnd(MediaPlayer mediaPlayer) {

            }
        });
        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user=mAuth.getCurrentUser();
                if(user.isAnonymous()){
                    Snackbar.make(findViewById(R.id.rmain),R.string.login_request_text,Snackbar.LENGTH_LONG).setAction(R.string.login_text, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAuth.signOut();
                            Intent intent1=new Intent(PlayActivity.this,RegisterActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent1);
                            finish();
                        }
                    }).show();
                }else{
                    final AlertDialog.Builder builder=new AlertDialog.Builder(PlayActivity.this);
                    builder.setTitle(R.string.add_comment_text);
                    LayoutInflater inflater=PlayActivity.this.getLayoutInflater();
                    final View viewInflated= inflater.inflate(R.layout.comment_dialog,null);
                    final EditText mComment=viewInflated.findViewById(R.id.commenttextstart);
                    Button mCommentBtn=viewInflated.findViewById(R.id.commentstartbtn);
                    Button mCommentCancelBtn=viewInflated.findViewById(R.id.commentcancelbtn);
                    builder.setView(viewInflated);
                    final AlertDialog dialog=builder.create();
                    dialog.show();
                    mCommentBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String comment=mComment.getText().toString();
                            if(!TextUtils.isEmpty(comment)){
                                storeComment(videoId,comment);
                                dialog.dismiss();
                            }else{
                                Toast.makeText(PlayActivity.this, R.string.type_some_comment_text, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    mCommentCancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }

            }
        });
        mStore.collection("Comedy").document(videoId).collection("Comment").addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e==null){
                    if(!documentSnapshots.isEmpty()){
                        int count =documentSnapshots.size();
                        mCommentCount.setText(count+" "+getResources().getText(R.string.comment_text));
                    }
                }

            }
        });
        mStore.collection("Comedy").document(videoId).collection("Comment").orderBy("real_time", Query.Direction.ASCENDING).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for(DocumentChange doc:documentSnapshots.getDocumentChanges()){
                    if(doc.getType()==DocumentChange.Type.ADDED){
                        String comment_id=doc.getDocument().getId();
                        Comments comments=doc.getDocument().toObject(Comments.class);
                        commentsList.add(comments);
                        commentAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        mStore.collection("Song").document(videoId).collection("Comment").addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e==null){
                    if(!documentSnapshots.isEmpty()){
                        int count =documentSnapshots.size();
                        mCommentCount.setText(count+" "+getResources().getText(R.string.comment_text));
                        commentRecyclerView.scrollToPosition(documentSnapshots.size()-1);

                    }
                }

            }
        });
        mStore.collection("Song").document(videoId).collection("Comment").orderBy("real_time", Query.Direction.ASCENDING).addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for(DocumentChange doc:documentSnapshots.getDocumentChanges()){
                    if(doc.getType()==DocumentChange.Type.ADDED){
                        String comment_id=doc.getDocument().getId();
                        Comments comments=doc.getDocument().toObject(Comments.class);
                        commentsList.add(comments);
                        commentAdapter.notifyDataSetChanged();
                        commentRecyclerView.scrollToPosition(documentSnapshots.size()-1);

                    }
                }
            }
        });


    }

    private void storeComment(final String videoId,String comment) {
        String username="";
        Date date=new Date();
        DateFormat formatdate=new SimpleDateFormat("dd-MM-yyyy");
        String strDate=formatdate.format(date);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(!user.getEmail().isEmpty()){
            username=user.getEmail().substring(0,user.getEmail().indexOf("@"));
        }else{
            username=user.getPhoneNumber().substring(0,5)+"****"+user.getPhoneNumber().substring(9,user.getPhoneNumber().length());
        }



        final Map<String,Object> map=new HashMap<>();
        map.put("name",username);
        map.put("date",strDate);
        map.put("comment",comment);
        map.put("real_time", FieldValue.serverTimestamp());

        mStore.collection("Song").document(videoId).addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    mStore.collection("Song").document(videoId).collection("Comment").add(map);
                }else{
                    mStore.collection("Comedy").document(videoId).collection("Comment").add(map);

                }
                Snackbar.make(findViewById(R.id.rmain),R.string.comment_added_text,Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void countView(final String videoId, long viewVount) {
        viewVount++;
        final Map<String,Object> map=new HashMap<>();
        map.put("view_count",viewVount);
        mStore.collection("Song").document(videoId).addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
           if(documentSnapshot.exists())  {
               mStore.collection("Song").document(videoId).update(map);
           }
           else{
               mStore.collection("Comedy").document(videoId).update(map);

           }
            }
        });

    }

    private void setVideoDetail(String vname, long vcount, String vdate, String vuploader) {
        vName.setText(vname);
        vCount.setText(vcount+" "+getResources().getText(R.string.view_count_text));
        vDate.setText(vdate);
        vUploader.setText(vuploader);
    }


}
