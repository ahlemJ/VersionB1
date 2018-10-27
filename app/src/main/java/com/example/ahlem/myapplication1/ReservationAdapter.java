package com.example.ahlem.myapplication1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReservationAdapter extends ArrayAdapter<NewReservation> {
    FirebaseDatabase database;
    DatabaseReference myRef, myRef1;
    String name;
    TextView stade;
    String key , key1;

    ArrayList<NewReservation> users = new ArrayList<>();
    public ReservationAdapter(Context context, ArrayList<NewReservation> users) {
        super(context, 0, users);
        this.users=users;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        myRef1 = database.getReference("simpleusers");
        final NewReservation user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_view_layout, parent, false);
        }
        // Lookup view for data population
        // = (TextView) convertView.findViewById(R.id.textView);
        Button annuler = (Button) convertView.findViewById(R.id.annuler);
        TextView temps = (TextView) convertView.findViewById(R.id.textVieww);
        TextView dateDereservation = (TextView) convertView.findViewById(R.id.textViewww);
        // Populate the data into the template view using the data object
       temps.setText(user.getDate());
       dateDereservation.setText(user.getHeure());
      // key= user.getId();
        key=user.getId();
     //  key1= user.getId();

        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getContext(),  key1 + "  id stade ", Toast.LENGTH_SHORT).show();
               // Toast.makeText(getContext(),  MesReservation.reservationId.get(position) + "  id reser ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Voulez-vous confirmer l'annulation de cette réservation ?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String idUser = FirstActivity.user.getId();
                                if(key!=null && idUser!=null ) {

                                    myRef.child(key).child("reservation").child(user.getProfilephoto()).removeValue();
                                    myRef1.child(idUser).child("Myreservation").child(MesReservation.reservationId.get(position)).removeValue();
                                    users.remove(position);
                                    MesReservation.reservationId.remove(position);
                                    notifyDataSetChanged();
                                    dialog.cancel();
                                    Toast.makeText(getContext(), "Annulation effectuée", Toast.LENGTH_LONG).show();
                                }
                                else { Toast.makeText(getContext(), "Annulation non effectuée", Toast.LENGTH_LONG).show();
                                }
                              //  Intent i = new Intent(ReservationAdapter.this, StadeProfile.class);
                                //i.putExtra("pos", position);
                                //startActivity(i);

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



            }

        });

        return convertView;
    }
    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }

}
