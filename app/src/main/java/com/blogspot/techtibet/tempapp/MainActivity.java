package com.blogspot.techtibet.tempapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private Toolbar mToolbar;
     RecyclerView mainRecycler;
     List<Videos> videoList;
     VideosRecyclerAdapter videosRecyclerAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        mStore=FirebaseFirestore.getInstance();

        mainRecycler=findViewById(R.id.main_recycler);
        mToolbar=findViewById(R.id.maintoolbar);
        mToolbar.setLogo(R.mipmap.ic_mainlogo);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");
        videoList=new ArrayList<>();
        videosRecyclerAdapter=new VideosRecyclerAdapter(getApplicationContext(),videoList);
        mainRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mainRecycler.setHasFixedSize(true);
        mainRecycler.setAdapter(videosRecyclerAdapter);


        mStore.collection("Files").orderBy("real_time", Query.Direction.DESCENDING).addSnapshotListener(MainActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    for(DocumentChange doc:documentSnapshots.getDocumentChanges()){
                        if(doc.getType()==DocumentChange.Type.ADDED){
                            String video_id=doc.getDocument().getId();
                            Videos videos=doc.getDocument().toObject(Videos.class).withId(video_id);
                            videoList.add(videos);
                            videosRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }
        });




    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser=mAuth.getCurrentUser();
        if(currentuser==null){
            Intent newintent=new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(newintent);
            finish();
        }
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logoutbtn){
            mAuth.signOut();
            Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;

        }else if(item.getItemId()==R.id.settingbtn){
            Toast.makeText(this, "under construction", Toast.LENGTH_SHORT).show();
            return  true;

        }else if(item.getItemId()==R.id.addbtn){
            Intent newintent=new Intent(MainActivity.this,UploadActivity.class);
            startActivity(newintent);
            return  true;

        }else if(item.getItemId()==R.id.searchbtn){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}

