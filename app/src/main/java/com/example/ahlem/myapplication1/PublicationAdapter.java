package com.example.ahlem.myapplication1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class PublicationAdapter extends ArrayAdapter<Publication> {
    FirebaseDatabase database;
    DatabaseReference myRef, myRef1;
    String name;
    TextView stade;
    String key , key1;
     Boolean zoomOut = false ;
    ArrayList<Publication> users = new ArrayList<>();
    public PublicationAdapter(Context context, ArrayList<Publication> users) {
        super(context, 0, users);
        this.users=users;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.evenment_item, parent, false);
          // convertView = LayoutInflater.
            //        from(parent.getContext()).
              //      inflate(R.layout.evenment_item, parent, false);
        }
        // Lookup view for data population
        TextView titre= (TextView) convertView.findViewById(R.id.titre);
        TextView text = (TextView) convertView.findViewById(R.id.description);
        ImageView profile = convertView.findViewById(R.id.profile_pic);
       // profile.setImageDrawable();

        String text1 = users.get(position).getText();
        String titre1 = users.get(position).getTitre();
        String image1 = users.get(position).getImg();
        final ImageView image = convertView.findViewById(R.id.image);
             if (image1!=null) {

                Glide.with(getContext()).load(image1)
                         .into(image);

                 }
                 else {image.setVisibility(View.GONE);}
             if (titre1!=null) {
                 //titre1 = String.valueOf(titre1.charAt(0)).toUpperCase() + titre1.subSequence(1, text.length());
                 titre1 = titre1.substring(0,1).toUpperCase() + titre1.substring(1);
                 titre.setText(titre1);}
             if (text1!=null) {  text.setText(text1);}


        return convertView;
    }

}
