package com.example.ahlem.myapplication1;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;



public class Feedback extends AppCompatActivity {
    TextView validd;
    ImageView addph;
    String Img;
    ImageView back,img,refrech;
    EditText publication,title;
    boolean rempli=false;

    private final int PICK_iMAGE_REQUEST = 71;
    FirebaseStorage storage;
    private Uri filePath = null;
    StorageReference storagereference;
    StorageReference ref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
Publication publicat=new Publication();
String id, userID,photoUrl;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpublication);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        back = (ImageView) findViewById(R.id.back);
        addph =  findViewById(R.id.addph);
        validd=findViewById(R.id.validd);
        publication = (EditText) findViewById(R.id.publicationn);
        title = (EditText) findViewById(R.id.titlee);

        img=(ImageView)findViewById(R.id.img);
        storage = FirebaseStorage.getInstance();
        storagereference = storage.getReference();
       // Toast.makeText(Feedback.this,FirstActivity.name + " " + FirstActivity.lastname, Toast.LENGTH_LONG).show();

        Intent intent = getIntent();
          id = intent.getStringExtra("pos");
         // userID = intent.getStringExtra("userID");
          //photoUrl = intent.getStringExtra("photoUser");
        userID= FirstActivity.user.getId();
        photoUrl=FirstActivity.user.get_filephoto();
        refrech=(ImageView)findViewById(R.id.refresh);
        refrech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Feedback.this,Feedback.class);
                intent.putExtra("pos", id);
              //  intent.putExtra("userID", userID);
                //intent.putExtra("photoUser", photoUrl);
               // intent.putExtra("Email", Email);
              //  p.clear();
                startActivity(intent);
                finish();



            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setColorFilter(R.color.colorPrimaryDark);
                Intent intent = new Intent(Feedback.this, StadeProfile.class);
                intent.putExtra("pos", id);
                //intent.putExtra("userID", userID);
                //intent.putExtra("photoUser", photoUrl);
                startActivity(intent);
                finish();

            }
        });
        addph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/+");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent, "Select Picture"), PICK_iMAGE_REQUEST);


            }
        });
       validd.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String titre=title.getText().toString();
              String text= publication.getText().toString();
              if(titre.length()!=0){
                  publicat.setTitre(titre);
              if(text.length()==0 && !rempli){
                  Toast.makeText(Feedback.this, "ajouter quelque chose à publier", Toast.LENGTH_SHORT).show();

              }
              else {
                  if(rempli){
                  publicat.setText(text);
                  uploadImag();


              }else {


                  publicat.setText(text);
                  publicat.setLastname(FirstActivity.user.get_lastname());
                  publicat.setName(FirstActivity.user.get_name());
                  publicat.setPhotoProfile(photoUrl);
                 // publicat.setKey(FirstActivity.user.getId());
                  myRef.child(id).child("feedback").push().setValue(publicat);
                      Toast.makeText(Feedback.this,publicat.toString() , Toast.LENGTH_LONG).show();

                      myRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(DataSnapshot dataSnapshot) {
                              String ii=dataSnapshot.child("numberfeedback").getValue().toString();
                              int i = Integer.parseInt( ii);
                              if(ii!=null) { myRef.child(id).child("numberfeedback").setValue(i+1);}
                              else{myRef.child(id).child("numberfeedback").setValue("1");}

                          }

                          @Override
                          public void onCancelled(DatabaseError databaseError) {

                          }
                      });
                      Toast.makeText(Feedback.this, "Envoyé", Toast.LENGTH_SHORT).show();
                  Intent intent = new Intent(Feedback.this, Feedback.class);
                      intent.putExtra("pos", id);
                      ///intent.putExtra("userID", userID);
                      //intent.putExtra("photoUser", photoUrl);
                 // intent.putExtra("Email", Email);
                  startActivity(intent);
                  finish();
              }

              }}else {
                  Toast.makeText(Feedback.this, "ajouter un titre", Toast.LENGTH_SHORT).show();
              }


           }
       });

    }
        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            if (requestCode == PICK_iMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();
                super.onActivityResult(requestCode, resultCode, data);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                   img.setImageBitmap(bitmap);
                   addph.setVisibility(View.GONE);
                   img.setVisibility(View.VISIBLE);
                   img.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           Intent intent = new Intent();
                           intent.setType("image/+");
                           intent.setAction(Intent.ACTION_GET_CONTENT);
                           startActivityForResult(intent.createChooser(intent, "Select Picture"), PICK_iMAGE_REQUEST);

                       }
                   });
rempli=true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private void uploadImag () {
            if (filePath != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                ref = storagereference.child("image/" + UUID.randomUUID().toString());
                ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        progressDialog.dismiss();
                        Toast.makeText(Feedback.this, "Telechargé", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Feedback.this, "Problème de telechargement" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 + taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Upload" + (int) progress + "4");
                    }
                });
                ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                      Img=taskSnapshot.getDownloadUrl().toString();
                        publicat.setImg(Img);

                        myRef.child(id).child("feedback").push().setValue(publicat);
                        Intent intent = new Intent(Feedback.this, Feedback.class);
                        intent.putExtra("pos", id);
                        //intent.putExtra("userID", userID);
                        //intent.putExtra("photoUser", photoUrl);
                        startActivity(intent);
                        finish();


                    }
                });
            } else {
                Toast.makeText(Feedback.this, "Insérez une photo", Toast.LENGTH_SHORT).show();
            }

        }
        @Override
        protected void onPause () {
            super.onPause();

            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);

           // activityManager.moveTaskToFront(getTaskId(), 0);
        }
        @Override
        public boolean onKeyDown ( int keyCode, KeyEvent event){
            // Do nothing or catch the keys you want to block
            return false;
        }
    }
