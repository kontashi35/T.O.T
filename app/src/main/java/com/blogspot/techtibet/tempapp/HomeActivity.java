package com.blogspot.techtibet.tempapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "";
    private TextView mAnynomousBtn;
    private FirebaseAuth mAuth;
    private Button mRegBtn;
    private Button mLoginBtn;
    private SignInButton mGsignBtm;
    private TextView mPhoneBtn;
    private ProgressDialog mProgress;
    private Switch mLangSwitch;
    private BackgroundPainter backgroundPainter;






    private GoogleSignInClient mGoogleSignClient;
    private static final int RC_SIGN_IN =1 ;




    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
    private void updateViews(String languageCode) {
        Context context = LocaleHelper.setLocale(this, languageCode);
        Resources resources = context.getResources();


    }
    private void restartActivity() {
        Intent intent=getIntent();
        finish();
        startActivity(intent);
        HomeActivity.this.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);

        //add backgrund effect android
        View backgroundImage = findViewById(R.id.homelayout);

        final int[] drawables = new int[3];
        drawables[0] = R.drawable.gradient_1;
        drawables[1] = R.drawable.gradient_2;
        drawables[2] = R.drawable.gradient_3;

        backgroundPainter = new BackgroundPainter(backgroundImage, drawables);
        backgroundPainter.start();


        mLangSwitch=findViewById(R.id.lang_switch);
        String lang=LocaleHelper.getLanguage(this);
        if(lang.equals("bo")){
            mLangSwitch.setChecked(true);
        }
        mLangSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){
                    updateViews("bo");
                    restartActivity();
                }else{
                    updateViews("en");
                    restartActivity();
                }

            }
        });

        mAnynomousBtn=findViewById(R.id.anynomous_login);
        mRegBtn=findViewById(R.id.homeregbtn);
        mLoginBtn=findViewById(R.id.homelog);
        mGsignBtm=findViewById(R.id.google_signin);
        mPhoneBtn=findViewById(R.id.phonebtn);
        mProgress=new ProgressDialog(this);
        String creating=getString(R.string.creating);
        String message=getString(R.string.creating_message);
        mProgress.setTitle(creating);
        mProgress.setMessage(message);
        mProgress.setIcon(R.drawable.playstore_icon);
        mProgress.setCanceledOnTouchOutside(false);



        mAuth=FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignClient= GoogleSignIn.getClient(this,gso);
        mGsignBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.show();
                Intent signInIntent = mGoogleSignClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        mPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,PhoneActivity.class);
                startActivity(intent);
            }
        });


        mAnynomousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.show();
                mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            sentTomain();
                        }
                    }
                });
            }
        });
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

            }
        });
    }

    @Override
    protected void onStart() {
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            sentTomain();
        }
        SharedPreferences pref=getSharedPreferences("check",MODE_PRIVATE);
        Boolean status=pref.getBoolean("status",false);
        if(!status){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle(" ");
            builder.setIcon(R.mipmap.ic_main_logo);
            builder.setMessage("Welcome there,We notice that you just installed our app! Enjoy this platform,Give us a feeback if you are enjoying! Thanks");
            builder.setCancelable(false);
            builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor editor=getSharedPreferences("check",MODE_PRIVATE).edit();
                    editor.putBoolean("status",true);
                    editor.apply();
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog=builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        super.onStart();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Toast.makeText(this, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: "+e.getMessage());
                mProgress.dismiss();
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mProgress.dismiss();
                            sentTomain();
                        } else {
                            // If sign in fails, display a message to the user.
                            mProgress.dismiss();
                            Snackbar.make(findViewById(R.id.relativeregister), "Authentication Failed."+task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
    private void sentTomain() {
        Intent intent=new Intent(HomeActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        mProgress.dismiss();
    }
}
