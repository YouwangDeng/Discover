package com.youwangd.productsearch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class ProductAdapter extends BaseAdapter {
    private List<Product> mData;
    private Context mContext;

    public ProductAdapter(List<Product> mData, Context mContext) {
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.product_item,parent,false);
        ImageView img = convertView.findViewById(R.id.product_img);
        TextView title = convertView.findViewById(R.id.product_title);
        TextView zip = convertView.findViewById(R.id.zip_result);
        TextView shipping = convertView.findViewById(R.id.shipping_result);
        final ImageButton wish_list = convertView.findViewById(R.id.wish_list_result);
        TextView condition = convertView.findViewById(R.id.condition_result);
        TextView price = convertView.findViewById(R.id.price_result);
        Picasso.get().load(mData.get(position).getImg()).resize(180,0).error(R.drawable.error_180_180).into(img);

        title.setText(mData.get(position).getTitle());
        zip.setText(mData.get(position).getZipcode());
        shipping.setText(mData.get(position).getShipping());
        condition.setText(mData.get(position).getCondition());
        price.setText(mData.get(position).getPrice());
        final SharedPreferences storage = mContext.getSharedPreferences("wish_list_storage", 0);
        final SharedPreferences.Editor editor = storage.edit();
        final String itemid = mData.get(position).getItemId();
        if(storage.contains(itemid)) {
            wish_list.setImageResource(R.drawable.cart_remove);
        } else {
            wish_list.setImageResource(R.drawable.cart_plus);
        }
        wish_list.setFocusable(false);
        wish_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storage.contains(itemid)) {
                    wish_list.setImageResource(R.drawable.cart_plus);
                    editor.remove(itemid);
                    editor.commit();
                    LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast,
                            (ViewGroup) ((Activity)mContext).findViewById(R.id.toast));
                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                    text.setText(mData.get(position).getTitle() + " was removed from wish list");
                    Toast toast = new Toast(mContext.getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 80);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                } else {
                    wish_list.setImageResource(R.drawable.cart_remove);
                    JSONObject product_item = new JSONObject();
                    try {
                        product_item.put("img", mData.get(position).getImg());
                        product_item.put("title", mData.get(position).getTitle());
                        product_item.put("zip", mData.get(position).getZipcode());
                        product_item.put("shipping", mData.get(position).getShipping());
                        product_item.put("condition", mData.get(position).getCondition());
                        product_item.put("price", mData.get(position).getPrice());
                        editor.putString(itemid, product_item.toString());
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast,
                            (ViewGroup) ((Activity)mContext).findViewById(R.id.toast));
                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                    text.setText(mData.get(position).getTitle() + " was added to wish list");
                    Toast toast = new Toast(mContext.getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 80);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }
                if(!(mContext instanceof SearchResult)) {
                    updateView(storage, mData);
                    notifyDataSetChanged();
                }
            }
        });
        return convertView;
    }

    public String trunk(String title) {
        if(title.length() > 30) {
            title = title.substring(0,31) + "...";
        }
        return title;
    }

    public void updateView(SharedPreferences storage, List<Product> list) {
        Activity ac = (Activity) mContext;
        if(ac != null) {
            TextView count = ac.findViewById(R.id.wish_list_count);
            TextView total = ac.findViewById(R.id.wish_list_total);
            ScrollView scroll_layout = ac.findViewById(R.id.wish_list_scroll_view);
            TextView no_wishes = ac.findViewById(R.id.no_wishes);
            parseWishListStorage(storage, list);
            if(list.size() > 0) {
                if(scroll_layout != null) scroll_layout.setVisibility(View.VISIBLE);
                if(no_wishes != null) no_wishes.setVisibility(View.GONE);
            } else {
                if(scroll_layout != null) scroll_layout.setVisibility(View.GONE);
                if(scroll_layout != null) no_wishes.setVisibility(View.VISIBLE);
            }
            if(count != null) count.setText(Integer.toString(list.size()));
            if(total != null) total.setText(calculateTotal(list));
        }
    }

    public void parseWishListStorage(SharedPreferences storage, List<Product> list) {
        list.clear();
        Map<String, ?> allEntries = storage.getAll();
        for(Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String itemId = entry.getKey();
            String detail = entry.getValue().toString();
            JSONObject items = null;
            try {
                items = new JSONObject(detail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String img = items.optString("img");
            String title = items.optString("title");
            String zip = items.optString("zip");
            String shipping = items.optString("shipping");
            String condition = items.optString("condition");
            String price = items.optString("price");
            list.add(new Product(itemId, img, title, zip, shipping, condition, price));
        }
    }

    public String calculateTotal(List<Product> list) {
        double total = 0;
        for(int i = 0; i < list.size(); i++) total +=  Double.valueOf(list.get(i).getPrice().substring(1));
        return "$" + Double.toString(total);
    }
}
