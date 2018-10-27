package com.example.ahlem.myapplication1;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;



public class Parametrezz extends AppCompatActivity {
    ArrayList<String> parametre=new ArrayList<String>();
    ListView l;
    String id,date,Email;
    ImageView back,refresh;
TextInputEditText name;
    TextInputEditText localisation;
    TextInputEditText pass1;
    TextInputEditText pass2;
    TextInputEditText numero;
    TextInputEditText oldpasss;
boolean changement =false;
    Button confirmer;
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("simpleusers");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parametre1);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        Intent intent = getIntent();
        id = intent.getStringExtra("userID");
        Email=intent.getStringExtra("Email");
        back=(ImageView)findViewById(R.id.back);
        Toast.makeText(getApplicationContext(), "Cliquez deux fois sur chaque champs pour avoir vos coordonnées par défault", Toast.LENGTH_SHORT).show();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setColorFilter(R.color.colorPrimary);
                Intent intent = new Intent(Parametrezz.this,FirstActivity.class);
                intent.putExtra("userID", id);
                intent.putExtra("photoUser", FirstActivity.user.get_filephoto());
                startActivity(intent);
                finish();
            }
        });
name=(TextInputEditText)findViewById(R.id.name);
        localisation=(TextInputEditText)findViewById(R.id.localisation);
        pass1=(TextInputEditText)findViewById(R.id.mdp1);
        pass2=(TextInputEditText)findViewById(R.id.mdp2);
        numero=(TextInputEditText)findViewById(R.id.numéro);
        oldpasss=(TextInputEditText)findViewById(R.id.oldmdp);

        localisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localisation.setText(FirstActivity.user.getLocalisation());
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText(FirstActivity.user.get_name());
            }
        });

        numero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numero.setText("+216 "+ FirstActivity.user.getMobile());
            }
        });
        confirmer=(Button)findViewById(R.id.confirmer);
        confirmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name.getText().toString().length()>0){

                    if(!name.getText().toString().equals(FirstActivity.user.get_name())){
                   myRef.child(id).child("firstName").setValue(name.getText().toString());
                    Toast.makeText(getApplicationContext(), "Le nom du stade est changé ", Toast.LENGTH_SHORT).show();
                   changement=true;
                    }
                }if(localisation.getText().toString().length()>0 && !localisation.getText().toString().equals(FirstActivity.user.getLocalisation())){
                    myRef.child(id).child("localisation").setValue(localisation.getText().toString());

                    Toast.makeText(getApplicationContext(), "la localisation du stade est changÃ©", Toast.LENGTH_SHORT).show();
                    changement=true;

                }
                if(numero.getText().toString().length()>0&& !numero.getText().toString().equals(FirstActivity.user.getMobile())){
                    myRef.child(id).child("phoneNumber").setValue(numero.getText().toString());

                    Toast.makeText(getApplicationContext(), "le numéro du stade est changé", Toast.LENGTH_SHORT).show();
                    changement=true;

                }
                if (pass1.getText().toString().length()>0 && pass2.getText().toString().length()>0){
                    if(!pass1.getText().toString().equals(pass2.getText().toString())){
                        Toast.makeText(getApplicationContext(), "erreur dans l'ecriture de mode passe", Toast.LENGTH_SHORT).show();

                    }
                   else if ((pass1.getText().toString().length()<6)){
                        Toast.makeText(getApplicationContext(), R.string.minimum_password, Toast.LENGTH_SHORT).show();
                        }
                        else {
                        user=FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(user.getEmail(), oldpasss.getText().toString());

// Prompt the user to re-provide their sign-in credentials
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.updatePassword(pass1.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "Password updated", Toast.LENGTH_SHORT).show();


                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Error password not updated", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Error auth failed", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                        changement=true;

                    }
                }
if(changement){
    Intent intent = new Intent(Parametrezz.this,FirstActivity.class);
    intent.putExtra("id", id);
    intent.putExtra("Email",Email);
    startActivity(intent);
    finish();

}
            }
        });

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
}
