package com.blogspot.techtibet.tempapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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

public class MainActivity extends AppCompatActivity{
    private static final String TAG ="" ;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private Toolbar mToolbar;
    private BottomNavigationView mBottonBar;
     private VideoFragment videoFragment;
     private  ComedyFragment comedyFragment;
     private ProgressDialog mDialog;
     private NavigationView mNavigation;
     ImageView mHeaderImage;
     private DrawerLayout mDrawerLayout;
     private ActionBarDrawerToggle mToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar=findViewById(R.id.maintoolbar);
        mToolbar.setLogo(R.mipmap.ic_main_logo);
        mBottonBar=findViewById(R.id.bottom_nav);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.home_menu);
        //drawer,toggle
        mNavigation=findViewById(R.id.navigation__drawer);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open_drawer,R.string.close_drawer);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mAuth=FirebaseAuth.getInstance();
        mStore=FirebaseFirestore.getInstance();
        mDialog=new ProgressDialog(this);
        mDialog.setTitle(R.string.loadind_text);
        String message=getString(R.string.loading_text);
        mDialog.setMessage(message);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        videoFragment=new VideoFragment();
        comedyFragment=new ComedyFragment();
        initializefragment();




        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    if(item.getItemId()==R.id.home_btn){
                        Log.d(TAG, "onNavigationItemSelected: home");
                        changeFragment(videoFragment);
                        mDrawerLayout.closeDrawer(Gravity.START);
                        return true;
                    }
                    else if(item.getItemId()==R.id.profilebtn){
                        Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        mDrawerLayout.closeDrawer(Gravity.START);
                        return true;
                    }else if(item.getItemId()==R.id.more_btn){
                        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.techtibet.blogspot.com"));
                        startActivity(intent);
                        mDrawerLayout.closeDrawer(Gravity.START);

                        return  true;
                    }else if(item.getItemId()==R.id.ppbtn){
                        String url="https://www.freeprivacypolicy.com/privacy/view/fe1ed5d18b3df63ae4b1fef2b5b4f2ea";
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                    else{
                        return false;
                    }


            }

        });



        mBottonBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.songbtn){
                    changeFragment(videoFragment);
                    return true;
                }else if(item.getItemId()==R.id.comedybtn){
                    changeFragment(comedyFragment);

                    return true;
                }
                return false;
            }
        });


    }


    private void initializefragment() {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.maincontainer,videoFragment);
        fragmentTransaction.add(R.id.maincontainer,comedyFragment);
        fragmentTransaction.hide(comedyFragment);
        fragmentTransaction.commit();
        mDialog.dismiss();
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        if(fragment==videoFragment){
            fragmentTransaction.hide(comedyFragment);




        }else if(fragment==comedyFragment){
            fragmentTransaction.hide(videoFragment);



        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser=mAuth.getCurrentUser();
        if(currentuser==null){
            Intent newintent=new Intent(MainActivity.this,HomeActivity.class);
            startActivity(newintent);
            finish();
        }
}

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
    private void updateViews(String languageCode) {
        Context context = LocaleHelper.setLocale(this, languageCode);
        Resources resources = context.getResources();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logoutbtn){
            mAuth.signOut();
            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;

        }else if(item.getItemId()==R.id.settingbtn){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle(R.string.change_language_text);
            builder.setIcon(R.drawable.playstore_icon);
            builder.setPositiveButton(R.string.english_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    updateViews("en");
                    restartActivity();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton(R.string.tibetan_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    updateViews("bo");
                    restartActivity();
                    dialogInterface.dismiss();
                }
            });
            builder.setCancelable(true);
            AlertDialog dialog=builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            return  true;

        }else if(item.getItemId()==R.id.addbtn){
            FirebaseUser user=mAuth.getCurrentUser();
            if(user.isAnonymous()){
                Toast.makeText(this, R.string.login_create_account_to_upload, Toast.LENGTH_SHORT).show();
            }else{
                Intent newintent=new Intent(MainActivity.this,UploadActivity.class);
                startActivity(newintent);
            }

            return  true;

        }else if(item.getItemId()==R.id.searchbtn){
            return true;
        }else if(mToggle.onOptionsItemSelected(item)){
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
    private void restartActivity() {
        Intent intent=getIntent();
        finish();
        startActivity(intent);
        MainActivity.this.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }


}

