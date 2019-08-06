package com.youwangd.productsearch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class ProductDetail extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_product_detail, container, false);
        ScrollView scroll_view = v.findViewById(R.id.detail_scroll_view);
        TextView title = v.findViewById(R.id.product_detail_title);
        TextView price = v.findViewById(R.id.detail_price);
        TextView shipping = v.findViewById(R.id.detail_shipping);
        TextView highlight_subtitle = v.findViewById(R.id.highlights_subtitle);
        TextView highlight_price = v.findViewById(R.id.highlights_price);
        TextView highlight_brand = v.findViewById(R.id.highlights_brand);
        LinearLayout specifications_layout = v.findViewById(R.id.detail_specifications);
        LinearLayout subtitle_layout = v.findViewById(R.id.subtitle_layout);
        LinearLayout brand_layout = v.findViewById(R.id.brand_layout);
        TextView spec_items = v.findViewById(R.id.spec_items);
        JSONObject detailData = ((DetailContainer) getActivity()).getDetailResult();
        String shipping_cost = ((DetailContainer) getActivity()).getShipping_cost();
        if(detailData.optString("Ack").equals("Success")) {
            JSONObject item = detailData.optJSONObject("Item");
            JSONArray images = item.optJSONArray("PictureURL");
            String product_title = item.optString("Title");
            String product_price = item.optJSONObject("CurrentPrice").optString("Value");
            String brand = "";
            if(item.has("ItemSpecifics")) {
                specifications_layout.setVisibility(View.VISIBLE);
                brand = findBrand(item.optJSONObject("ItemSpecifics").optJSONArray("NameValueList"));
                spec_items.setText(buildItems(item.optJSONObject("ItemSpecifics").optJSONArray("NameValueList")));
            }
            if(brand.equals("")) {
                brand_layout.setVisibility(View.GONE);
            } else {
                highlight_brand.setText(brand);
            }
            if(item.has("Subtitle")) {
                subtitle_layout.setVisibility(View.VISIBLE);
                highlight_subtitle.setText(item.optString("Subtitle"));
            }
            highlight_price.setText("$" + product_price);
            LinearLayout product_images = v.findViewById(R.id.product_images);
            for(int i = 0; i < images.length(); i++) {
                View singleImage = LayoutInflater.from(getActivity()).inflate(R.layout.product_image, product_images, false);
                ImageView img = singleImage.findViewById(R.id.product_image_item);
                Picasso.get().load(images.optString(i)).resize(0,788).error(R.drawable.error_800_800).into(img);
                product_images.addView(singleImage);
            }
            title.setText(product_title);
            price.setText("$" + product_price);
            shipping.setText(shipping_cost.indexOf("$") != -1 ? " With " + shipping_cost +" Shipping" : " With " + shipping_cost);

        } else {
            scroll_view.setVisibility(View.GONE);
            v.findViewById(R.id.detail_no_result).setVisibility(View.VISIBLE);
        }

        return v;
    }

    public String findBrand(JSONArray spec) {
        for(int i = 0; i < spec.length(); i++) {
            if(spec.optJSONObject(i).optString("Name").equals("Brand")) {
                return spec.optJSONObject(i).optJSONArray("Value").optString(0);
            }
        }
        return "";
    }

    public String buildItems(JSONArray spec) {
        StringBuilder sb = new StringBuilder();
        String brandItem = "";
        for(int i = 0; i < spec.length(); i++) {
            String itemName = spec.optJSONObject(i).optString("Name");
            JSONArray itemValue = spec.optJSONObject(i).optJSONArray("Value");
            if(itemName.equals("Brand")) {
                brandItem = "\u2022 " + itemValue.optString(0) + "\n\n";
                continue;
            }
            if(itemValue.optString(0).equals("No") || itemValue.optString(0).equals("Yes") || itemValue.optString(0).equals("Does not apply") || itemValue.optString(0).equals("Does Not Apply")) continue;
            for(int j = 0; j < itemValue.length(); j++) {
                sb.append("\u2022 " + itemValue.optString(j) + "\n\n");
            }
        }
        return brandItem + sb.toString();
    }

}
