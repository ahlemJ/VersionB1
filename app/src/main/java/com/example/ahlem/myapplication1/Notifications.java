package com.example.ahlem.myapplication1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public  class Notifications extends AppCompatActivity {
    static ValueEventListener value1, valuee;
ListView notif;
ImageView back, profile_pic;
String userID;
    DatabaseReference myRef, myRef1;
    View footer=null;
    Button button;
    int nombre=10;
    NotificationAdapter adapter;
    ArrayList<NewReservation> reservations;
    FirebaseDatabase database;
    Integer number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        back =findViewById(R.id.back);
        notif=findViewById(R.id.notif);
        profile_pic= findViewById(R.id.profile_pic);
        Intent intent = getIntent();
     //number = intent.getIntExtra("userID");
        userID= FirstActivity.user.getId();
        reservations= new ArrayList<>();
        reservations.clear();
        adapter= new NotificationAdapter(this, reservations);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("simpleusers");
        myRef1=database.getReference("users");

       // final String  photoUrl = intent.getStringExtra("photoUser");
       final String photoUrl = FirstActivity.user.get_filephoto();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Notifications.this, FirstActivity.class);
                //i.putExtra("userID", userID);
               // i.putExtra("photoUser", photoUrl);
                startActivity(i);
                finish();
            }
        });
        MultiTransformation multi = new MultiTransformation(
                new RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.BOTTOM));
        Glide.with(this).load(photoUrl)
                .apply(bitmapTransform(multi))
                .into(profile_pic);


        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Notifications.this, FirstActivity.class);
                //intent.putExtra("userID", userID);
                //intent.putExtra("photoUser", photoUrl);
                // i.putExtra("pos", userID);
                startActivity(intent);
                finish();
            }
        });

        if(reservations.size()>=10&& footer==null){
            //  footer = (ViewGroup)inflate(R.layout.footer,list_reser1,false);
            footer = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
            notif.addFooterView(footer,null,true);
            button=(Button)findViewById(R.id.ajouter);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nombre=nombre+10;
                    reservation(nombre);
                    //  myRef.child(id).child("Newreservation").limitToFirst(10)
                   // adapter.notifyDataSetChanged();
                }
            });}
        if(reservations.size()<10 && footer!=null) {notif.removeFooterView(footer);}
        if(reservations.isEmpty())  { reservation(10);}
        else
            {notif.setAdapter(adapter);
               /*if(FirstActivity.number>0) {
                   for (int i = 0; i < FirstActivity.number; i++) {
                       notif.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.colorPrimary1));

                   }
               }*/
            }

    }



    private void reservation(final int i) {
        reservations.clear();

        Query query = myRef.child(userID).child("notifreservation").orderByKey()
                .limitToLast(i);

        value1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //reservations.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    NewReservation reservation = data.getValue(NewReservation.class);
                    reservations.add(reservation);
                    }
                Collections.reverse(reservations);
                adapter.notifyDataSetChanged();
                if(reservations.size()>=i && footer==null){
                    // footer = (View) inflater.inflate(R.layout.footer,list_reser1,false);
                    footer = ((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
                    //footer = (ViewGroup)LayoutInflater.inflate(R.layout.footer,list_reser1,false);

                    notif.addFooterView(footer,null,true);
                    button=(Button)findViewById(R.id.ajouter);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nombre=nombre+10;
                            reservation(nombre);
                            //  myRef.child(id).child("Newreservation").limitToFirst(10)
                           // adapter.notifyDataSetChanged();
                        }
                    });}
                if(reservations.size()<i && footer!=null)
                {notif.removeFooterView(footer);
                footer=null;
                }
               notif.setAdapter(adapter);
//                notif.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.colorPrimary1));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addListenerForSingleValueEvent(value1);
    }

}
