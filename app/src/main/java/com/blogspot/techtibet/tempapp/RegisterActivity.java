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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConPassword;

    private Button mRegBtn;
    private Toolbar mToolbar;
    private ProgressDialog mProgress;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide notification bar
        setContentView(R.layout.activity_register);


        mAuth=FirebaseAuth.getInstance();
        mStore=FirebaseFirestore.getInstance();
        mEmail=findViewById(R.id.regemail);
        mPassword=findViewById(R.id.regpassword);
        mConPassword=findViewById(R.id.regconpassword);

        mRegBtn=findViewById(R.id.regbtn);
        mProgress=new ProgressDialog(this);
        mToolbar=(Toolbar)findViewById(R.id.regtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.register_new_account_register_text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String creating=getString(R.string.creating);
        String message=getString(R.string.creating_message);
        mProgress.setTitle(creating);
        mProgress.setMessage(message);
        mProgress.setIcon(R.drawable.playstore_icon);
        mProgress.setCanceledOnTouchOutside(false);



        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String conpassword=mConPassword.getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) &&!TextUtils.isEmpty(conpassword))
                {
                    if(password.equals(conpassword)){
                        mProgress.show();
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                mProgress.dismiss();
                                                Toast.makeText(RegisterActivity.this, R.string.code_sent, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    mAuth.signOut();
//                                Toast.makeText(RegisterActivity.this, "Create Successful", Toast.LENGTH_LONG).show();
//                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                                mProgress.dismiss();

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, R.string.email_badly_format, Toast.LENGTH_LONG).show();
                                mProgress.dismiss();
                            }
                        });
                    }else{
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, R.string.password_should_match, Toast.LENGTH_SHORT).show();
                    }


            }
            else{
                    Toast.makeText(RegisterActivity.this, R.string.dont_leave_empty, Toast.LENGTH_LONG).show();

                }
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            sentTomain();
        }
    }

    private void sentTomain() {
        mProgress.dismiss();
        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


}
