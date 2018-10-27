package com.example.ahlem.myapplication1;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.example.ahlem.myapplication1._sliders.FragmentSlider;
import com.example.ahlem.myapplication1._sliders.SliderIndicator;
import com.example.ahlem.myapplication1._sliders.SliderPagerAdapter;
import com.example.ahlem.myapplication1._sliders.SliderView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static java.lang.Integer.parseInt;

public class StadeProfile extends AppCompatActivity {
    TextView nameprofile, localisation, phoneNumber;
    FirebaseDatabase database;
    DatabaseReference myRef;

    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteAdapterAct adapter = null;
    ImageView photo;
  static   String profile ;
    Button  list;
    LinearLayout reserve, evenement ;
    Button indiv;
    private SliderPagerAdapter mAdapter;
    private SliderIndicator mIndicator;
    private SliderView sliderView;
    private LinearLayout mLinearLayout;
    ImageButton back, feedback;
    TextView ballon,douche,boteille;
    ImageButton search1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile2);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("pos");

        ballon=findViewById(R.id.ballon);
        douche=findViewById(R.id.douche);
        boteille=findViewById(R.id.bouteille);
       // final String  userID = intent.getStringExtra("userID");
        //final String  photoUrl = intent.getStringExtra("photoUser");
        nameprofile = findViewById(R.id.namestade);
        localisation= findViewById(R.id.localisation);
        reserve = findViewById(R.id.reserve);
        search1=findViewById(R.id.search1);
       // indiv= findViewById(R.id.indiv);
        back = findViewById(R.id.back);
        feedback = findViewById(R.id.feedback);
       // list = findViewById(R.id.list);
        photo= findViewById(R.id.profile_pic);
        evenement = findViewById(R.id.evenement);
        phoneNumber=findViewById(R.id.phone);


        if(!isNetworkAvailable())
        { RelativeLayout no = findViewById(R.id.no_connexion);
            no.setVisibility(View.VISIBLE);
        }
        if(profile!=null){nameprofile.setText(profile);}


      //  Toast.makeText(StadeProfile.this, userID , Toast.LENGTH_SHORT).show();
       MultiTransformation multi = new MultiTransformation(
                new RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.BOTTOM));
        Glide.with(this).load(FirstActivity.user.get_filephoto())
                .apply(bitmapTransform(multi))
                .into(photo);
       photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StadeProfile.this, FirstActivity.class);
               // intent.putExtra("userID", userID);
                //intent.putExtra("photoUser", photoUrl);
                // i.putExtra("pos", userID);
                startActivity(intent);
                finish();
            }
        });


       //final  String id1 =MainActivity.customers.get(position).getId();

        database = FirebaseDatabase.getInstance();
        myRef=database.getReference("users");

        final List<Fragment> fragments = new ArrayList<>();

        sliderView = (SliderView) findViewById(R.id.sliderView);
        mLinearLayout = (LinearLayout) findViewById(R.id.pagesContainer);
        sliderView.setDurationScroll(800);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra("pos", id);
                //i.putExtra("userID", userID);
                //i.putExtra("photoUser", photoUrl);
                startActivity(i);
                finish();
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
      if (isNetworkAvailable()) {
          Intent i = new Intent(getBaseContext(), Feedback.class);
          i.putExtra("pos", id);
          //i.putExtra("userID", userID);
          //i.putExtra("photoUser", photoUrl);
          startActivity(i);
          finish();
      }
      else{Toast.makeText(StadeProfile.this, "Pas de connexion internet", Toast.LENGTH_LONG).show();}
            }
        });
        search1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    i.putExtra("pos", id);
                    // i.putExtra("userID", userID);
                    //i.putExtra("photoUser", photoUrl);
                    startActivity(i);
                    finish();
                }
                else{Toast.makeText(StadeProfile.this, "Pas de connexion internet", Toast.LENGTH_LONG).show();}
            }
        });

        myRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if(dataSnapsho)
                if(dataSnapshot.exists()) {
                    if(profile==null){
                    profile = dataSnapshot.child("firstName").getValue().toString();}
                    nameprofile.setText(profile);
                    ballon.setText(dataSnapshot.child("ballnumb").getValue().toString());
                    boteille.setText(dataSnapshot.child("bottelnumb").getValue().toString());
                    douche.setText(dataSnapshot.child("douchenumb").getValue().toString());
                    localisation.setText("Situé à "+dataSnapshot.child("localisation").getValue().toString());
                    phoneNumber.setText("Tél : "+dataSnapshot.child("phoneNumber").getValue().toString());
                    for (DataSnapshot snapshot : dataSnapshot.child("photoScroll").getChildren()) {

                        fragments.add(FragmentSlider.newInstance(snapshot.getValue().toString()));


                    }
                    mAdapter = new SliderPagerAdapter(getSupportFragmentManager(), fragments);
                    sliderView.setAdapter(mAdapter);
                    mIndicator = new SliderIndicator(getApplicationContext(), mLinearLayout, sliderView, R.drawable.indicator);
                    mIndicator.setPageCount(fragments.size());
                    mIndicator.show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    Intent intent = new Intent(StadeProfile.this, Calendrierr.class);
                    intent.putExtra("pos", id);
                    //intent.putExtra("userID", userID);
                    //intent.putExtra("photoUser", photoUrl);

                    startActivity(intent);
                    finish();
                }
                else{Toast.makeText(StadeProfile.this, "Pas de connexion internet", Toast.LENGTH_LONG).show();}
            }
        });



        evenement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    Intent intent = new Intent(StadeProfile.this, Evenement.class);
                    intent.putExtra("pos", id);
                    //intent.putExtra("userID", userID);
                    //intent.putExtra("photoUser", photoUrl);
                    startActivity(intent);
                    finish();
                }
                else{Toast.makeText(StadeProfile.this, "Pas de connexion internet", Toast.LENGTH_LONG).show();}
            }
        });


    }
    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        //activityManager.moveTaskToFront(getTaskId(), 0);
    }
   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return false;
    }*/
    private boolean isNetworkAvailable() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
