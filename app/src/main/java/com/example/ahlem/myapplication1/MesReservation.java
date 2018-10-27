package com.example.ahlem.myapplication1;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MesReservation extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef, myRef1;
    private FirebaseAuth mAuth;
    ImageButton back;
    ImageView profile_pic;
ListView list_reser1;
    NewReservation reservation;
ReservationAdapter adapter ;
    View footer=null;
    Button button;
    int nombre=10;
    String name, photo;
    SwipeRefreshLayout swipeRefreshLayout;
  static   ArrayList<String> reservationId;
String id ;
  static   String  userID;
    String  photoUrl1;

 ArrayList<NewReservation> reservations;
    static ValueEventListener value1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mesreservation);
        profile_pic= findViewById(R.id.profile_pic);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        list_reser1=findViewById(R.id.list_reser);
        back=findViewById(R.id.back);
      //swipeRefreshLayout=findViewById(R.id.swipe);
        mAuth = FirebaseAuth.getInstance();

        reservations = new ArrayList<>();
        reservationId= new ArrayList<>();
        reservations.clear();
        reservationId.clear();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("simpleusers");
        myRef1=database.getReference("users");
        adapter= new ReservationAdapter(this , reservations);
        Intent intent = getIntent();
         userID = intent.getStringExtra("userID");
         // photoUrl1 = intent.getStringExtra("photoUser");
        MultiTransformation multi = new MultiTransformation(
                new RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.BOTTOM));
        Glide.with(this).load(FirstActivity.user.get_filephoto())
                .apply(bitmapTransform(multi))
                .into(profile_pic);


        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MesReservation.this, FirstActivity.class);
                //intent.putExtra("userID", userID);
                //intent.putExtra("photoUser", photoUrl1);
                // i.putExtra("pos", userID);
                startActivity(intent);
                finish();
            }
        });
        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MesReservation.this,MesReservation.class);
                       // intent.putExtra("pos", position);
                        intent.putExtra("userID", userID);
                        intent.putExtra("photoUser", photoUrl1);


                        // intent.putExtra("Email",Email);
                        startActivity(intent);
                        finish();
                    }
                }, 1500);
            }

        });*/
        if(reservations.size()>=10&& footer==null){
          //  footer = (ViewGroup)inflate(R.layout.footer,list_reser1,false);
            footer = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
            list_reser1.addFooterView(footer,null,true);
            button=(Button)findViewById(R.id.ajouter);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nombre=nombre+10;
                    reservation(nombre);
                    //  myRef.child(id).child("Newreservation").limitToFirst(10)

                }
            });}
        if(reservations.size()<10 && footer!=null) {list_reser1.removeFooterView(footer);}
        if(reservations.isEmpty())  {
            reservation(10);
             }
        else {list_reser1.setAdapter(adapter);}

         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), FirstActivity.class);
              //  i.putExtra("userID", userID);
                //i.putExtra("userID", userID);
                //i.putExtra("photoUser", photoUrl1);
                startActivity(i);
                finish();
            }
        });


        list_reser1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
               // Toast.makeText(MesReservation.this,  "  id stade ", Toast.LENGTH_SHORT).show();
              //  Toast.makeText(getApplicationContext(), reservationId.get(position) + "  id stade ", Toast.LENGTH_SHORT).show();
                Object item = parent.getItemAtPosition(position);
                if (item instanceof StadeModel){
                    NewReservation reservation=(NewReservation) item;
               // String it = ((NewReservation) item).getId();
                    }
                //String it = ((NewReservation) item).getIdTerrain();
                String it = reservations.get(position).getId();
              Toast.makeText(getApplicationContext(), it + "  id stade ", Toast.LENGTH_SHORT).show();

                myRef1.child(it).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                   name= dataSnapshot.child("firstName").getValue().toString();
                   String phone = dataSnapshot.child("phoneNumber").getValue().toString();
                           String date = reservations.get(position).getDateDeDemande();
                        afficherInfo(name, phone, date);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

    }

   private void afficherInfo(String name, String phone, String date) {
       final Dialog dialog = new Dialog(this);
       dialog.setContentView(R.layout.dialoguelayout);
       //ImageView ph = dialog.findViewById(R.id.image);
    //   RequestOptions myOptions = new RequestOptions().override(1000, 1000);
      // Glide.with(this).load(photo).apply(myOptions).into(ph);

       TextView terrain = dialog.findViewById(R.id.text);
       TextView phoneview = dialog.findViewById(R.id.tel);
       TextView dateview = dialog.findViewById(R.id.date);
       terrain.setText(name);
       phoneview.setText("Tél : " +phone);
       dateview.setText("Réservation effectuée le " +date);

       Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
       // if button is clicked, close the custom dialog
       dialogButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog.dismiss();

           }
       });
       dialog.show();
       terrain.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MesReservation.this, StadeProfile.class);
               intent.putExtra("pos", id);
              // intent.putExtra("userID", userID);
               //intent.putExtra("photoUser", photoUrl1);
               startActivity(intent);
           }
       });
   }


    private void reservation(final int i) {

        Query query = myRef.child(FirstActivity.user.getId()).child("Myreservation").orderByKey()
                .limitToLast(i);

        value1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reservations.clear();
                reservationId.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    
                    reservationId.add(data.getKey().toString());
                    reservations.add(new NewReservation(data.child("date").getValue().toString(),data.child("heure").getValue().toString(),data.child("etat").getValue().toString(),data.child("dateDeDemande").getValue().toString(),data.child("profilephoto").getValue().toString(),data.child("id").getValue().toString()));

                }
                Collections.reverse(reservations);
                Collections.reverse(reservationId);
                adapter.notifyDataSetChanged();
                if(reservations.size()>=i&& footer==null){
                    footer = ((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);

                    list_reser1.addFooterView(footer,null,true);
                    button=(Button)findViewById(R.id.ajouter);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nombre=nombre+10;
                            reservation(nombre);

                        }
                    });}
                if(reservations.size()<i && footer!=null) {list_reser1.removeFooterView(footer); footer=null;}
                list_reser1.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addListenerForSingleValueEvent(value1);
    }

    private void addReservation(final NewReservation reservation1) {
        String id = reservation.getId();

        myRef1.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

              String name= dataSnapshot.child("firstName").getValue().toString();

               reservation.setId(name);
               reservations.add(reservation);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


}
