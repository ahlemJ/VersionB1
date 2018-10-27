package com.example.ahlem.myapplication1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class Calendrierr  extends AppCompatActivity {
  ArrayList<liststate> list;
  ListView listview ;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    DatabaseReference myRef1 = database.getReference("simpleusers");
    ChildEventListener value , value1;
    String date,Email;
    ImageView back,refresh;
    String userID, position;
    CalendrierAdapter adapter;
   String idStade , photoUrl;
    Calendar myCalendar=null;
    String[] values;
    protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.calendrierr);
   setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

       Intent intent = getIntent();
         //userID = getIntent().getStringExtra("userID");
         userID=FirstActivity.user.getId();
         photoUrl = FirstActivity.user.get_filephoto();
       // photoUrl = getIntent().getStringExtra("photoUser");
         position = getIntent().getStringExtra("pos");
         idStade=position;
        ImageView profile_pic= findViewById(R.id.profile_pic);

        MultiTransformation multi = new MultiTransformation(
                new RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.BOTTOM));
        Glide.with(this).load(photoUrl)
                .apply(bitmapTransform(multi))
                .into(profile_pic);
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calendrierr.this, FirstActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("photoUser", photoUrl);
                // i.putExtra("pos", userID);
                startActivity(intent);
                finish();
            }
        });
        back=(ImageView)findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setColorFilter(R.color.colorPrimaryDark);
                Intent intent = new Intent(Calendrierr.this,StadeProfile.class);
                intent.putExtra("pos", position);
                intent.putExtra("userID", userID);
                intent.putExtra("photoUser", photoUrl);
                startActivity(intent);
                finish();
            }
        });
        refresh=(ImageView)findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setColorFilter(R.color.colorPrimaryDark);
                Intent intent = new Intent(Calendrierr.this,Calendrierr.class);
                intent.putExtra("pos", position);
                intent.putExtra("userID", userID);
                intent.putExtra("photoUser", photoUrl);
                startActivity(intent);
                finish();
            }
        });
      listview = (ListView) findViewById(R.id.timelist);
      values = new String[] { "00:00-01:30", "01:30-03:00", "03:00-04:30",
                "04:30-06:00", "06:00-07:30", "07:30-09:00", "09:00-10:30", "10:30-12:00",
                "12:00-13:30", "13:30-15:00", "15:00-16:30", "16:30-18:00", "18:00-19:30", "19:30-21:00",
                "21:00-22:30", "22:30-00:00" };
        list = new ArrayList<>();

        for (int i = 0; i < values.length; ++i) {
            list.add(new liststate(0,values[i]));
        }
        adapter = new CalendrierAdapter(this, list);
        listview.setAdapter(adapter);

        mDisplayDate = (TextView) findViewById(R.id.Date);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Calendrierr.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("Calendrier", "onDateSet: mm-dd-yyy: " + day + "-" + month + "-" + year);
                date = year+"-"+month+"-"+day;
                mDisplayDate.setText(date);
                restart();
                if(isNetworkAvailable())
                {
                    restart();
                    adapterlist();
                }
                else {Toast.makeText(Calendrierr.this, "Pas de connexion internet", Toast.LENGTH_LONG).show();
                }


            }
        };

        init_list();

    }
    public  void  adapterlist(){
        Query query=myRef.child(position).child("reservation").orderByChild("date").equalTo(date);


        value = new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                list.get(Integer.parseInt(dataSnapshot.child("etat").getValue().toString())).setEtat(1);
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
               list.get(Integer.parseInt(dataSnapshot.child("etat").getValue().toString())).setEtat(0);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        query.addChildEventListener(value);

        Query query1=myRef.child(position).child("Newreservation").orderByChild("date").equalTo(date);


        value1 = new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                list.get(Integer.parseInt(dataSnapshot.child("etat").getValue().toString())).setEtat(1);
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                list.get(Integer.parseInt(dataSnapshot.child("etat").getValue().toString())).setEtat(0);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        query1.addChildEventListener(value1);

        myRef.child(position).child("tempBloquer").child(date).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        list.get(Integer.parseInt(data.getKey())).setEtat(2);

                   }adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    public  void restart(){
        for(int i=0;i<list.size();i++){
list.get(i).setEtat(0);


        }

        adapter.notifyDataSetChanged();

    }
    public void init_list()
    {
        final ImageView[] im = new ImageView[1];
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id1) {
               if(date!=null){
                if(list.get(position).getEtat()==0)
                {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Calendrierr.this);
                    builder1.setMessage("Voulez-vous effectuer une réservation pour cette heure?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Oui",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                     if(isNetworkAvailable()) {
                                         myCalendar = Calendar.getInstance();
                                         DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                                         String dateExact = df.format(myCalendar.getTime());
                                         list.get(position).setEtat(1);
                                         im[0] =(ImageView)view.findViewById(R.id.check_bloc);
                                         im[0].setImageResource(R.drawable.check);
                                         im[0].setBackgroundResource(R.color.colorPrimary);
                                         writeNewReservation(myRef, myRef1, userID, date, values[position], position, dateExact);
                                         Toast.makeText(Calendrierr.this, "Bien enregistrée", Toast.LENGTH_LONG).show();
                                         dialog.cancel();
                                     } else {
                                         Toast.makeText(Calendrierr.this, "Pas de connexion internet. Impossible d'effectuer une réservation", Toast.LENGTH_LONG).show();

                                     }
                                    //Intent i = new Intent(getBaseContext(), StadeProfile.class);
                                    //i.putExtra("pos", pos);
                                    //startActivity(i);
                                    //finish();



                                }
                            });

                    builder1.setNegativeButton(
                            "Non",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }else if (list.get(position).getEtat()==2){

                    Toast.makeText(getApplicationContext(), "Non disponible, désactivé par l'administrateur", Toast.LENGTH_SHORT).show(); }


               }else {           Toast.makeText(getApplicationContext(), "choisissez une date d'abord", Toast.LENGTH_SHORT).show();
               }
            }
        });


    }
    public  void writeNewReservation(final  DatabaseReference databaseReference,DatabaseReference myRef1, String userID, String date, String heure, int etat, String DateDeDemande) {

        NewReservation newReservation = new NewReservation( date, heure, Integer.toString(etat),DateDeDemande, photoUrl,userID);
      // NewReservation newReservation1 = new NewReservation( date, heure, Integer.toString(etat),DateDeDemande, idStade);
        //Toast.makeText(Calendrierr.this,photoUrl , Toast.LENGTH_LONG).show();


        String key = databaseReference.child(idStade).child("Newreservation").push().getKey().toString();

        databaseReference.child(idStade).child("Newreservation").child(key).setValue(newReservation);
       // myRef1.child(userID).child("Myreservation").child(key).setValue(newReservation1);



        databaseReference.child(idStade).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ii=dataSnapshot.child("numberres").getValue().toString();
                int i = Integer.parseInt( ii);
                if(ii!=null) { databaseReference.child(idStade).child("numberres").setValue(i+1);}
                else{databaseReference.child(idStade).child("numberres").setValue("1");}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
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
