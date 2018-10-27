package com.example.ahlem.myapplication1;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoGenerale extends AppCompatActivity {
ImageView back;
Button confirmer;
    TextInputEditText localisation;
    TextInputEditText numero;
    TextInputEditText name;
    Boolean changement= false;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("simpleusers");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parametre1);
        back=(ImageView)findViewById(R.id.back);
        confirmer = findViewById(R.id.confirmer);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        Intent intent= getIntent();
        final String  id = intent.getStringExtra("userID");
        final String  photoUser = intent.getStringExtra("photoUser");
        name=(TextInputEditText)findViewById(R.id.name);
        localisation=(TextInputEditText)findViewById(R.id.localisation);
        numero=(TextInputEditText)findViewById(R.id.numéro);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setColorFilter(R.color.colorPrimary);
                Intent intent = new Intent(InfoGenerale.this,Parametre.class);
                intent.putExtra("userID", id);
                intent.putExtra("photoUser", photoUser);
                startActivity(intent);
                finish();
            }
        });

        localisation.setText(FirstActivity.user.getLocalisation());
        name.setText(FirstActivity.user.get_name());
        numero.setText("+216 "+ FirstActivity.user.getMobile());

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


                if(changement){
                    Intent intent = new Intent(InfoGenerale.this,FirstActivity.class);
                    intent.putExtra("id", id);

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

        //activityManager.moveTaskToFront(getTaskId(), 0);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return false;
    }


}
