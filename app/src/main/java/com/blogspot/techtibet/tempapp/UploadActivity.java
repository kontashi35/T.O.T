package com.blogspot.techtibet.tempapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {
    private static final int FILE_CODE = 1;
    private static final int PICK_IMAGE = 0;
    private FirebaseFirestore mStore;
    private StorageReference mStorage;
    private Button mUploadBtn;
    private Button mPausePlayBtn;
    private Button mCancelBtn;
    private TextView mFilename;
    private TextView mSpaceMb;
    private TextView mPerc;
    private ProgressBar mProgress;
    private StorageTask mStorageTask;
    private LinearLayout mProgressLayout;
    private RelativeLayout uploadLayout;
    private FirebaseAuth mAuth;
    String displayName=null;
    Uri thumbUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mStore=FirebaseFirestore.getInstance();
        mUploadBtn=findViewById(R.id.upload_btn);
        mPausePlayBtn=findViewById(R.id.pause_upload);
        mCancelBtn=findViewById(R.id.cancel_btn);
        mFilename=findViewById(R.id.upload_name_text);
        mSpaceMb=findViewById(R.id.upload_mb);
        mPerc=findViewById(R.id.upload_perc);
        mProgress=findViewById(R.id.upload_progress_bar);
        mStorage= FirebaseStorage.getInstance().getReference();
        mProgressLayout=findViewById(R.id.progress_layout);
        uploadLayout=findViewById(R.id.uploadlayout);
        mAuth=FirebaseAuth.getInstance();



        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status=mUploadBtn.getText().toString();
                if(status.equals("Upload Thumbnail")){
                    uploadThumbnail();

                }else if(status.equals("Select file to upload")){
                    mUploadBtn.setEnabled(false);
                    uploadFile();
                }

            }
        });
        mPausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPausePlayBtn.getText().toString().equals("Pause Upload")){
                    mStorageTask.pause();
                    mPausePlayBtn.setText("Resume Upload");
                }else if(mPausePlayBtn.getText().toString().equals("Resume Upload")){
                    mStorageTask.resume();
                    mPausePlayBtn.setText("Pause Upload");
                }

            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStorageTask.cancel();
                sendToMain();
                mProgressLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void sendToMain() {
        Intent intent=new Intent(UploadActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void uploadFile() {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try{
            startActivityForResult(
                    Intent.createChooser(intent,"Select a file to upload"),
            FILE_CODE);
        }catch(Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadThumbnail() {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select thumbnail"),PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK){
            thumbUri=data.getData();
            Snackbar.make(uploadLayout,"Thumbnail, Uploaded successfully",Snackbar.LENGTH_SHORT).show();
            mUploadBtn.setText("Select file to upload");
        }
        if(requestCode==FILE_CODE && resultCode==RESULT_OK){
            mProgressLayout.setVisibility(View.VISIBLE);
            Uri fileUri=data.getData();
            String uriString=fileUri.toString();
            File myFile=new File(uriString);

            if(uriString.startsWith("content://")){
                Cursor cursor=null;
                try{
                    cursor=UploadActivity.this.getContentResolver().query(fileUri,null,null,null,null);
                    if((cursor!=null) && cursor.moveToFirst()){
                        displayName=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                    }
                }finally {
                    cursor.close();
                }

            }else if(uriString.startsWith("file://")){
                displayName=myFile.getName();
            }
            mFilename.setText(displayName);
            StorageReference mFileStorage=mStorage.child("files/"+displayName);
            mStorageTask=mFileStorage.putFile(fileUri).addOnSuccessListener(this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final String downloadurl=taskSnapshot.getDownloadUrl().toString();

                StorageReference mThumbStorage=mStorage.child("thumbnail/"+displayName);
                mThumbStorage.putFile(thumbUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String finalDisplayName=displayName;
                        if(finalDisplayName.indexOf(".")>0){
                            finalDisplayName=finalDisplayName.substring(0,finalDisplayName.lastIndexOf("."));

                        }

                        String thumbDownloadurl=taskSnapshot.getDownloadUrl().toString();
                        String uploader="";
                        Date date=new Date();
                        DateFormat formatdate=new SimpleDateFormat("dd-MM-yyyy");
                        String strDate=formatdate.format(date);

                        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                        if(!user.getEmail().isEmpty()){
                            uploader=user.getEmail();
                        }else{
                            uploader=user.getPhoneNumber();
                        }

                        Map<String,Object> map=new HashMap<>();
                        map.put("video_url",downloadurl);
                        map.put("video_name", finalDisplayName);
                        map.put("time",strDate);
                        map.put("user",uploader);
                        map.put("thumb_url",thumbDownloadurl);
                        map.put("view_count",0);
                        map.put("real_time", FieldValue.serverTimestamp());
                        mStore.collection("Files").add(map).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UploadActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        sendToMain();
                        Toast.makeText(UploadActivity.this, "uploaded succesful", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(UploadActivity.this,new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(this,new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    mProgress.setProgress((int) progress);
                    String mBprogress=taskSnapshot.getBytesTransferred()/(1024*1024)+"/"+taskSnapshot.getTotalByteCount()/(1024*1024)+"mb";
                    mSpaceMb.setText(mBprogress);
                    mPerc.setText((int) progress+"%");
                }
            });
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
