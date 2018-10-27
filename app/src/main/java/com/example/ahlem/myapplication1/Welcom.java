package com.example.ahlem.myapplication1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class Welcom extends AppCompatActivity {
    String id,Email;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("simpleusers");
static   UserInfos user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vide);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
if(getValue("userID")==-1){

    Intent intent = new Intent(Welcom.this,Login.class);

    startActivity(intent);
    finish();
}
else {

    id=getvalue("userID",getValue("userID"));
    Email=getvalue("Email",getValue("Email"));

    final Intent intent = new Intent(Welcom.this,FirstActivity.class);
    intent.putExtra("userID", id);
    intent.putExtra("Email", Email);

    if(isNetworkAvailable()) {
        myRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = new UserInfos(id, dataSnapshot.child("_name").getValue().toString(), dataSnapshot.child("_lastname").getValue().toString(), dataSnapshot.child("_filephoto").getValue().toString(), (dataSnapshot.child("Mobile").getValue().toString()), dataSnapshot.child("localisation").getValue().toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    else {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }


}


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
    public int getValue(String name) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getInt(name, -1);
    }


public String getvalue(String file,int len){
    try{
        FileInputStream fIn = openFileInput(file);
        InputStreamReader isr = new InputStreamReader(fIn);
        char[] inputBuffer = new char[len];
        //len is the length of that saved string in the file

        isr.read(inputBuffer);

        String readString = new String(inputBuffer);
        return readString;
    }catch(IOException e){

    }
return null;
}
    }
