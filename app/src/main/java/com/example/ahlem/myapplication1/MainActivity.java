package com.example.ahlem.myapplication1;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MainActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener{
    FirebaseDatabase database;
    DatabaseReference myRef;
    SearchView search ;
ImageView profile_pic;
public static   ArrayList<StadeModel> customers ;
    public  ArrayList<StadeModel> customers1 ;
    public  ListView list;
    ImageView s;
    TextView t;

    AutoCompleteAdapter adapter1=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_test);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
      Intent intent = getIntent();
       final String  userID = intent.getStringExtra("userID");
       final String  photoUrl = intent.getStringExtra("photoUser");

        list= findViewById(R.id.listview);

        customers = new ArrayList<>();
        customers1 = new ArrayList<>();
        search = findViewById(R.id.search);
        s= findViewById(R.id.s);
        t= findViewById(R.id.text);
        profile_pic=findViewById(R.id.profile_pic);
        MultiTransformation multi = new MultiTransformation(
                new RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.BOTTOM));
        Glide.with(this).load(FirstActivity.user.get_filephoto())
                .apply(bitmapTransform(multi))
                .into(profile_pic);
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FirstActivity.class);
               i.putExtra("userID", userID);
                i.putExtra("photoUser", photoUrl);
                startActivity(i);
                finish();
            }
        });

        int closeButtonId = getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButtonImage = (ImageView) search.findViewById(closeButtonId);
        closeButtonImage.setImageResource(R.drawable.close_icone);

        int ButtonId = getResources().getIdentifier("android:id/search_go_btn", null, null);
        ImageView ButtonImage = (ImageView) search.findViewById(ButtonId);
        ButtonImage.setImageResource(R.drawable.search_icone);

        adapter1 = new AutoCompleteAdapter(MainActivity.this,customers1);
       // list.setAdapter(adapter1);
        list.setVisibility(View.INVISIBLE);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // customers.clear();
                customers1.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // StadeModel stadeModel = data.getValue(StadeModel.class);
                    String id = data.getKey();
                    String value = data.child("firstName").getValue(String.class);
                    String localisation = data.child("localisation").getValue(String.class);
                    String photo = data.child("profilePic").getValue(String.class);

                    if(customers1.size()==0)
                    customers1.add(new StadeModel(customers1.size(), value, localisation, id, photo));
                    else {customers1.add(new StadeModel(customers1.size()-1, value, localisation, id, photo));}




                }

                adapter1 = new AutoCompleteAdapter(MainActivity.this,customers1);
                list.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
                search.setOnQueryTextListener(MainActivity.this);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, StadeProfile.class);

                Object item = parent.getItemAtPosition(position);
                if (item instanceof StadeModel){
                    StadeModel student=(StadeModel) item;}
                String it = ((StadeModel) item).getId();

                intent.putExtra("pos", it);
                intent.putExtra("userID", userID);
                intent.putExtra("photoUser", photoUrl);
                startActivity(intent);
                finish();

            }


        });
    }




    @Override
    public boolean onQueryTextSubmit(String query) {
        list.setVisibility(View.VISIBLE);
        s.setVisibility(View.INVISIBLE);
        t.setVisibility(View.INVISIBLE);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        list.setVisibility(View.VISIBLE);
        s.setVisibility(View.INVISIBLE);
        t.setVisibility(View.INVISIBLE);
        String text = newText;
        if(adapter1!=null)
        {adapter1.filter(text);}
        return false;

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
