package com.blogspot.techtibet.tempapp;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConPassword;

    private Button mRegBtn;
    private Button LogBtn;
    private Toolbar mToolbar;
    private ProgressDialog mProgress;
    private TextView mPhoneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        mStore=FirebaseFirestore.getInstance();
        mEmail=(EditText)findViewById(R.id.regemail);
        mPassword=(EditText)findViewById(R.id.regpassword);
        mConPassword=(EditText)findViewById(R.id.regconpassword);

        mPhoneBtn=(TextView)findViewById(R.id.phonebtn);
        mRegBtn=(Button)findViewById(R.id.regbtn);
        LogBtn=(Button)findViewById(R.id.logbtn);
        mProgress=new ProgressDialog(this);
        mToolbar=(Toolbar)findViewById(R.id.regtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Register");
        mProgress.setTitle("Creating");
        mProgress.setMessage("wait dude its loading");

        mPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,PhoneActivity.class);
                startActivity(intent);
            }
        });

        LogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
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
                                                Toast.makeText(RegisterActivity.this, "sent", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(RegisterActivity.this, "You got some error" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                mProgress.dismiss();
                            }
                        });
                    }else{
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, "Password doest match,try again", Toast.LENGTH_SHORT).show();
                    }


            }
            else{
                    Toast.makeText(RegisterActivity.this, "fill empty field", Toast.LENGTH_LONG).show();

                }
            }
        });
    }


}
