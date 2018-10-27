package com.example.ahlem.myapplication1;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileOutputStream;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
public class Login extends AppCompatActivity {
    private EditText log;
    private EditText passwd ;
    private Button connexion ;
    private FirebaseAuth auth;
    private String id = null;
    String value;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("simpleusers");
    static UserInfos user;
    Boolean existcn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginn);
        log = (EditText) findViewById(R.id.username);
        passwd = (EditText) findViewById(R.id.password);
        connexion = (Button) findViewById(R.id.signin);
        TextView signup;
        ImageView wifi = findViewById(R.id.wifi1);
        if (isNetworkAvailable()) {
            wifi.setVisibility(View.GONE);
            existcn = true;


        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        auth = FirebaseAuth.getInstance();


        if (existcn) {
            connexion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String pseudo = log.getText().toString();
                    final String password = passwd.getText().toString();

                    if (TextUtils.isEmpty(pseudo)) {
                        Toast.makeText(getApplicationContext(), "Entrer votre addresse mail!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {

                        Toast.makeText(getApplicationContext(), "Entrer votre mot de passe!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final ProgressDialog progressDialog = new ProgressDialog(Login.this);
                    progressDialog.setTitle("Authentification...");
                    progressDialog.show();
                    auth.signInWithEmailAndPassword(pseudo, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        if (password.length() < 6) {
                                            passwd.setError(getString(R.string.minimum_password));
                                        } else {
                                            Toast.makeText(Login.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                        }
                                    } else {

                                        id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        String Email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//
                                        setValue(id.length(), "userID");
                                        setValue(Email.length(), "Email");

                                        setvalue(id, "userID");
                                        setvalue(Email, "Email");
                                        final Intent intent = new Intent(Login.this, FirstActivity.class);

                                        myRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                user= new UserInfos(id,dataSnapshot.child("_name").getValue().toString(),dataSnapshot.child("_lastname").getValue().toString(),dataSnapshot.child("_filephoto").getValue().toString(),(dataSnapshot.child("Mobile").getValue().toString()),dataSnapshot.child("localisation").getValue().toString());
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    }
                                }
                            });


                }

            });
            signup = (TextView) findViewById(R.id.textsignup);
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Login.this, SignUpActivity.class));
                    finish();
                }
            });

        }
    }
    public void setValue(int newValue,String name) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(name, newValue);
        editor.commit();
    }
    public void setvalue(String data,String file){
        try {
            FileOutputStream fOut = openFileOutput(file,MODE_PRIVATE);
            fOut.write(data.getBytes());
            fOut.close();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

       // activityManager.moveTaskToFront(getTaskId(), 0);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return false;
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
