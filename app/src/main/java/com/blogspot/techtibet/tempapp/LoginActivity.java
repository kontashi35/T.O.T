package com.blogspot.techtibet.tempapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private EditText mEmail;
    private EditText mPassword;
    private Button mLogBtn;
    private ProgressDialog mProgress;
    private Button mResendBtn;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        mStore=FirebaseFirestore.getInstance();
        mToolbar=findViewById(R.id.logintoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEmail=(EditText)findViewById(R.id.loginemail);
        mPassword=(EditText)findViewById(R.id.loginpass);
        mLogBtn=(Button)findViewById(R.id.loginlog);
        mProgress=new ProgressDialog(this);
        mResendBtn=findViewById(R.id.resendbtn);
        mResendBtn.setVisibility(View.INVISIBLE);
        mResendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mProgress.setTitle("Logging in");
        mProgress.setMessage("Wait while processing");

        mLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mEmail.getText().toString();
                String password=mPassword.getText().toString();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    mProgress.show();
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                if(user.isEmailVerified()){
                                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(LoginActivity.this, "Please verify email to log in", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                }

                                mProgress.dismiss();
                            }else{
                                Toast.makeText(LoginActivity.this,"Login error:"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                mProgress.dismiss();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this,"Login error:"+e.getMessage(),Toast.LENGTH_LONG).show();
                            mProgress.dismiss();
                        }
                    });

                }else{
                    Toast.makeText(LoginActivity.this,"please fill empty field",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
