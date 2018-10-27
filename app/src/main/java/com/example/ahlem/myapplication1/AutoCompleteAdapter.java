package com.example.ahlem.myapplication1;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import com.bumptech.glide.module.AppGlideModule;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AutoCompleteAdapter extends ArrayAdapter<StadeModel> {
    ArrayList<StadeModel> customers5, arraylist;
    LayoutInflater inflater;
    Context mContext;

    public AutoCompleteAdapter(Context context, ArrayList<StadeModel> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        mContext=context;
        this.customers5 = objects;

        this.arraylist = new ArrayList<StadeModel>();
        this.arraylist.addAll(customers5);
        inflater = LayoutInflater.from(mContext);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StadeModel customer = getItem(position);
        final Context context = getContext();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.autocomp_item, parent, false);
        }
        TextView txtCustomer = (TextView) convertView.findViewById(R.id.tvCustomer);
        TextView localisation = (TextView) convertView.findViewById(R.id.loc1);
        final CircleImageView ivCustomerImage = (CircleImageView) convertView.findViewById(R.id.ivCustomerImage);
        if (txtCustomer != null)
            txtCustomer.setText(customer.getFirstName());
            localisation.setText(customer.getLocalisation());

        return convertView;
    }



    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        customers5.clear();
        if (charText.length() == 0) {
            customers5.addAll(arraylist);
        } else {
            for (StadeModel wp : arraylist) {
                if (wp.getFirstName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    customers5.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}