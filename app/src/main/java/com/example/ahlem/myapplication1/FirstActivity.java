package com.example.ahlem.myapplication1;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import android.os.Handler;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class FirstActivity extends AppCompatActivity {
    String userID;
Button cherchez, reservation ;
Spinner spinner;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ImageView profile_pic;
TextView nameUser;
String photoUser;
static  UserInfos user;

static  String name;
    StorageReference storagereference;
    StorageReference ref;
    ImageView notif, refresh ;
    private Uri filePath=null;
    private final int PICK_iMAGE_REQUEST = 71 ;
    static ValueEventListener  valuee;
    static  Integer number;
    MediaPlayer mp;
    Vibrator v;
    FirebaseStorage storage;
    String Img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("simpleusers");
        storage = FirebaseStorage.getInstance();
        storagereference = storage.getReference();
        cherchez = findViewById(R.id.chercher);
        reservation = findViewById(R.id.reservation);
        profile_pic = findViewById(R.id.profile_pic);
        spinner = findViewById(R.id.spinner);
        nameUser = findViewById(R.id.name);
        notif = findViewById(R.id.notif);
        refresh=(ImageView)findViewById(R.id.refresh);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.googleevent);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setColorFilter(R.color.even);
                Intent intent = new Intent(FirstActivity.this,FirstActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (!isNetworkAvailable()) {
            RelativeLayout no = findViewById(R.id.no_connexion);
            no.setVisibility(View.VISIBLE);
        }
        if (isNetworkAvailable()) {

            if (Welcom.user != null && Login.user== null && user==null) {
                user = new UserInfos(Welcom.user.getId(), Welcom.user.get_name(), Welcom.user.get_lastname(), Welcom.user.get_filephoto(), Welcom.user.getMobile(), Welcom.user.getLocalisation());
                uploadInfo();
            }
            else if (Login.user != null && Welcom.user == null) {
                user = new UserInfos(Login.user.getId(), Login.user.get_name(), Login.user.get_lastname(), Login.user.get_filephoto(), Login.user.getMobile(), Login.user.getLocalisation());
                uploadInfo();
            }
            else {

                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final ProgressDialog progressDialog = new ProgressDialog(FirstActivity.this);
                progressDialog.setTitle("Chargement...");
                progressDialog.show();

                myRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(UserInfos.class);
                        user.setId(userID);
                        uploadInfo();
                        progressDialog.cancel();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            String[] items = new String[]{"", "Paramètres", "Deconnexion"};

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items) {
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View v = null;

                    // If this is the initial dummy entry, make it hidden
                    if (position == 0) {
                        TextView tv = new TextView(getContext());
                        tv.setHeight(0);
                        tv.setVisibility(View.GONE);
                        v = tv;
                    } else {
                        // Pass convertView as null to prevent reuse of special case views
                        v = super.getDropDownView(position, null, parent);
                    }

                    // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
                    parent.setVerticalScrollBarEnabled(false);
                    return v;
                }
            };

            spinner.setAdapter(dataAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isNetworkAvailable()) {
                    switch (position) {
                        case 0:

                            break;
                        case 1:
                            Intent i = new Intent(FirstActivity.this, Parametre.class);
                            startActivity(i);
                            finish();
                            break;
                        case 2:
                            // Whatever you want to happen when the second item gets selected
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            prefs.edit().remove("userID").commit();
                            prefs.edit().remove("Email").commit();
                            removefile("userID");
                            removefile("Email");
                            FirebaseAuth.getInstance().signOut();
                            i = new Intent(FirstActivity.this, Login.class);
                            startActivity(i);
                            finish();
                            break;

                    }
                    }else {Toast.makeText(getApplicationContext(), "Vérifiez votre connexion internet puis refrechir", Toast.LENGTH_SHORT).show();}
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            valuee = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    number = Integer.parseInt(dataSnapshot.getValue().toString());
                    if (number > 0) {
                        //Toast.makeText(getApplicationContext(), number + "  aaa", Toast.LENGTH_SHORT).show();
                        v.vibrate(900);
                        mp.start();
                        addnotifcation(number);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            myRef.child(userID).child("numbernotif").addValueEventListener(valuee);



            profile_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isNetworkAvailable()) {
                        Intent intent = new Intent();
                        intent.setType("image/+");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent.createChooser(intent, "Choisissez une image"), PICK_iMAGE_REQUEST);
                    }
                    else {Toast.makeText(getApplicationContext(), "Vérifiez votre connexion internet puis rafraîchir", Toast.LENGTH_SHORT).show();}
                }
            });

            notif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(FirstActivity.this, Notifications.class);
                    // i.putExtra("numberNotif", number);
                    myRef.child(userID).child("numbernotif").removeEventListener(valuee);
                    myRef.child(userID).child("numbernotif").setValue(0);
                    //i.putExtra("photoUser", photoUser);
                    startActivity(i);
                    finish();
                }
            });
            //f=FirstActivity.this;

            reservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isNetworkAvailable()) {
                    Intent i = new Intent(FirstActivity.this, MesReservation.class);

                    // i.putExtra("userID", userID);
                    //i.putExtra("photoUser", photoUser);
                    startActivity(i);
                    finish();}
                    else{Toast.makeText(getApplicationContext(), "Vérifiez votre connexion internet puis rafraîchir", Toast.LENGTH_SHORT).show();}
                }
            });
            cherchez.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isNetworkAvailable()) {
                        Intent i = new Intent(FirstActivity.this, MainActivity.class);
                        //i.putExtra("userID", userID);
                        //i.putExtra("photoUser", photoUser);
                        startActivity(i);
                        finish();
                    }else {Toast.makeText(getApplicationContext(), "Vérifiez votre connexion internet puis rafraîchir", Toast.LENGTH_SHORT).show();}
                }
            });
        }
    }
    public void removefile(String name){
        String dir = getFilesDir().getAbsolutePath();
        File f0 = new File(dir, name);
        f0.delete();
    }

    private void addnotifcation(Integer number) {
        TextView b = findViewById(R.id.badge);

        View v = findViewById(R.id.badgeCotainer);
        if(v != null) {
            v.setVisibility(View.VISIBLE);
        }
        b.setText(number.toString());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PICK_iMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ){
            filePath = data.getData();
            super.onActivityResult(requestCode,resultCode,data);
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                profile_pic.setImageBitmap(bitmap);
                uploadImag();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onPause() {

        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
      //  activityManager.moveTaskToFront(getTaskId(), 0);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return false;
    }
    /**
     * Prepare some dummy data for gridview
     */
    private void uploadImag(){
        if(filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Téléchargement...");
            progressDialog.show();
            ref = storagereference.child("image/"+ UUID.randomUUID().toString());
            ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    Toast.makeText(FirstActivity.this, "Téléchargée", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(FirstActivity.this, "Echec de téléchargement"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0+taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Upload .. ");
                }
            });
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Img=taskSnapshot.getDownloadUrl().toString();
                    if(Welcom.user!=null)
                    {
                        Welcom.user.set_filephoto(Img);
                    }else {
                        Login.user.set_filephoto(Img);
                    }
                    user.set_filephoto(Img);
                    myRef.child(userID).child("_filephoto").setValue(Img);
                }
            });
        }
        else {
            Toast.makeText(FirstActivity.this, "Insérez une photo", Toast.LENGTH_SHORT).show();
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
public  void uploadInfo()
{   userID = user.getId();
    Toast.makeText(FirstActivity.this, photoUser , Toast.LENGTH_SHORT).show();

    photoUser = user.get_filephoto();
    if (photoUser != null) {
        MultiTransformation multi = new MultiTransformation(
                new RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.BOTTOM));
       // RoundedCornersTransformation round = new RoundedCornersTransformation(128,0,RoundedCornersTransformation.CornerType.BOTTOM);
        Glide.with(getApplicationContext()).load(photoUser)
                .apply(bitmapTransform(multi))
                .into(profile_pic);
    }
    nameUser.setText(user.get_name() + " " + user.get_lastname());}

}