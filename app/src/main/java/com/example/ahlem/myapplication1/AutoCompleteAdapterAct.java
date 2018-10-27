package com.example.ahlem.myapplication1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class AutoCompleteAdapterAct extends ArrayAdapter<StadeModel> {
    ArrayList<StadeModel> customers5, tempCustomer, suggestions, arraylist;
    LayoutInflater inflater;
    Context mContext;

    public AutoCompleteAdapterAct(Context context, ArrayList<StadeModel> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        mContext = context;
        this.customers5 = objects;
        // this.tempCustomer = new ArrayList<StadeModel>(objects);
        // this.suggestions = new ArrayList<StadeModel>(objects);
        this.arraylist = new ArrayList<StadeModel>();
        this.arraylist.addAll(customers5);
        inflater = LayoutInflater.from(mContext);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StadeModel customer = getItem(position);
        Context context = getContext();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.autocompete_item, parent, false);
        }
        TextView txtCustomer = (TextView) convertView.findViewById(R.id.tvCustomer);
        TextView localisation = (TextView) convertView.findViewById(R.id.loc1);
        ImageView ivCustomerImage = (ImageView) convertView.findViewById(R.id.ivCustomerImage);
        if (txtCustomer != null)
            txtCustomer.setText(customer.getFirstName());
        localisation.setText(customer.getLocalisation());
       // Picasso.with(context).load(customer.getProfilePic()).into(ivCustomerImage);

        // if (ivCustomerImage != null && customer.getProfilePic() != null)
        //   Picasso.with(context).load(customer.getProfilePic()).into(ivCustomerImage);
        /*// Now assign alternate color for rows
        if (position % 2 == 0)
            Toast.makeText(getContext(), "jklj", Toast.LENGTH_LONG).show();
            //convertView.setBackgroundColor(getContext().getColor(R.color.odd));
        else
            //convertView.setBackgroundColor(getContext().getColor(R.color.even));
            Toast.makeText(getContext(), "jklj", Toast.LENGTH_LONG).show();*/
        return convertView;
    }
}