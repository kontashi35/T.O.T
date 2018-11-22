package com.blogspot.techtibet.tempapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {
    private static final String TAG = "";
    private Toolbar mToolbar;
    private LinearLayout mVerifyLayot;
    private EditText mPhoneNo;
    private Button mSendBtn;
    private ProgressBar mProgres;
    private ProgressBar mProgressVerify;

    private FirebaseAuth mAuth;
    private EditText mVerificationText;
    int btnType=0;
    String mS;
    PhoneAuthProvider.ForceResendingToken mForceResendingToken;
     PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        mPhoneNo=findViewById(R.id.phoneno);
        mSendBtn= findViewById(R.id.sendbtn);
        mProgres=findViewById(R.id.progress);
        mProgressVerify=findViewById(R.id.progress_verify);

        mVerificationText=findViewById(R.id.verificationcode);
        mAuth=FirebaseAuth.getInstance();
        mToolbar=findViewById(R.id.phone_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.phone);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVerifyLayot=(LinearLayout)findViewById(R.id.verificationlayot);
       mSendBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String phno = mPhoneNo.getText().toString();
               String code=mVerificationText.getText().toString();
               if(!TextUtils.isEmpty(phno) || !TextUtils.isEmpty(code)){
                   phno="+91"+phno;
                   mPhoneNo.setEnabled(false);
                   if(btnType==0){

                       if (!TextUtils.isEmpty(phno)) {
                           mProgres.setVisibility(View.VISIBLE);
                           PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                   phno,
                                   60,
                                   TimeUnit.SECONDS,
                                   PhoneActivity.this,
                                   mCallback
                           );

                       }
                   }else{

                       Toast.makeText(PhoneActivity.this, R.string.verify_text, Toast.LENGTH_SHORT).show();

                       if(!TextUtils.isEmpty(code)){
                           mProgressVerify.setVisibility(View.VISIBLE);
                           PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(mS,code);
                           signInWithPhoneAuthCredential(phoneAuthCredential);
                       }else{
                           Toast.makeText(PhoneActivity.this, R.string.enter_verify_code, Toast.LENGTH_SHORT).show();
                       }

                   }
               }else{
                   Toast.makeText(PhoneActivity.this, R.string.dont_leave_empty, Toast.LENGTH_SHORT).show();
               }



           }

       });
        mCallback=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                mProgres.setVisibility(View.INVISIBLE);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneActivity.this, ":"+e.getMessage(), Toast.LENGTH_SHORT).show();
                mProgres.setVisibility(View.INVISIBLE);
                mPhoneNo.setEnabled(true);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mProgres.setVisibility(View.INVISIBLE);
                mSendBtn.setText(R.string.verify_code);
                mVerifyLayot.setVisibility(View.VISIBLE);
                mPhoneNo.setEnabled(true);
                Toast.makeText(PhoneActivity.this, R.string.code_sent, Toast.LENGTH_SHORT).show();
                btnType=1;
                mS=s;
                mForceResendingToken=forceResendingToken;
                Log.d(TAG, "onCodeSent mS: "+mS);
                Log.d(TAG, "onCodeSent: mForce"+mForceResendingToken);

            }
        };
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressVerify.setVisibility(View.INVISIBLE);
                            Intent intent=new Intent(PhoneActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                            // ...
                        } else {
                            mProgressVerify.setVisibility(View.INVISIBLE);
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(PhoneActivity.this, "signInWithCredential:failure:"+task.getException(), Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Intent intent=new Intent(PhoneActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}



