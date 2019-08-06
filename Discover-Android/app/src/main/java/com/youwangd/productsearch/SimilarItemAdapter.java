package com.youwangd.productsearch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class SimilarItemAdapter extends BaseAdapter {

    private List<Product> mData;
    private Context mContext;

    public SimilarItemAdapter(List<Product> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.similar_item, parent, false);
        ImageView similar_img = convertView.findViewById(R.id.similar_product_img);
        TextView similar_title = convertView.findViewById(R.id.similar_product_title);
        TextView similar_shipping = convertView.findViewById(R.id.similar_shipping);
        TextView similar_daysLeft = convertView.findViewById(R.id.similar_daysLeft);
        TextView similar_day = convertView.findViewById(R.id.similar_day);
        TextView similar_days = convertView.findViewById(R.id.similar_days);
        TextView similar_price = convertView.findViewById(R.id.similar_price);
        Picasso.get().load(mData.get(position).getImg()).resize(150,150).error(R.drawable.error_150_150).into(similar_img);
        similar_title.setText(mData.get(position).getTitle());
        similar_shipping.setText(mData.get(position).getShipping());
        similar_daysLeft.setText(mData.get(position).getDaysLeft());
        if(Integer.parseInt(mData.get(position).getDaysLeft()) <=1) {
            similar_day.setVisibility(View.VISIBLE);
        } else {
            similar_days.setVisibility(View.VISIBLE);
        }
        similar_price.setText(mData.get(position).getPrice());
        return  convertView;
    }
}
