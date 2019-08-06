package com.youwangd.productsearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WishList extends Fragment {

    private SharedPreferences storage;
    private SharedPreferences.Editor editor;
    private List<Product> list;

    @Override
    public void onResume() {
        super.onResume();
        updateView();
        Log.d("resume wish list => ", "success!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        storage = getActivity().getSharedPreferences("wish_list_storage", 0);
        editor = storage.edit();
        View v = inflater.inflate(R.layout.fragment_wish_list, container, false);

        TextView no_wishes = v.findViewById(R.id.no_wishes);
        ScrollView wish_list_scroll_view = v.findViewById(R.id.wish_list_scroll_view);
        ScrollGridView wish_list_grid_view = v.findViewById(R.id.wish_list_scroll_grid);
        TextView wish_list_count = v.findViewById(R.id.wish_list_count);
        TextView wish_list_total = v.findViewById(R.id.wish_list_total);

        list = new ArrayList<>();
        parseWishListStorage(storage, list);
        ProductAdapter wishAdapter = new ProductAdapter(list, getActivity());
        wish_list_grid_view.setAdapter(wishAdapter);
        if(list.size() > 0) {
            no_wishes.setVisibility(View.GONE);
            wish_list_scroll_view.setVisibility(View.VISIBLE);
        } else {
            no_wishes.setVisibility(View.VISIBLE);
            wish_list_scroll_view.setVisibility(View.GONE);
        }
        wish_list_count.setText(Integer.toString(list.size()));
        wish_list_total.setText(calculateTotal(list));
        wish_list_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent(getActivity(), DetailContainer.class);
                detail.putExtra("itemId", list.get(position).getItemId());
                detail.putExtra("title", list.get(position).getTitle());
                detail.putExtra("shipping_cost", list.get(position).getShipping());
                JSONObject product_item = new JSONObject();
                try {
                    product_item.put("img", list.get(position).getImg());
                    product_item.put("title", list.get(position).getTitle());
                    product_item.put("zip", list.get(position).getZipcode());
                    product_item.put("shipping", list.get(position).getShipping());
                    product_item.put("condition", list.get(position).getCondition());
                    product_item.put("price", list.get(position).getPrice());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                detail.putExtra("detail", product_item.toString());
                startActivity(detail);
            }
        });

        return  v;
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

    public void updateView() {
        View v = getView();
        if(v != null) {
            ScrollGridView grid = v.findViewById(R.id.wish_list_scroll_grid);
            TextView count = v.findViewById(R.id.wish_list_count);
            TextView total = v.findViewById(R.id.wish_list_total);
            ScrollView scroll_layout = v.findViewById(R.id.wish_list_scroll_view);
            TextView no_wishes = v.findViewById(R.id.no_wishes);
            parseWishListStorage(storage, list);
            if(grid.getAdapter() != null) {
                ((BaseAdapter) grid.getAdapter()).notifyDataSetChanged();
            }
            if(list.size() > 0) {
                no_wishes.setVisibility(View.GONE);
                scroll_layout.setVisibility(View.VISIBLE);
                Log.d("list size => ", " > 0");
            } else {
                scroll_layout.setVisibility(View.GONE);
                no_wishes.setVisibility(View.VISIBLE);
                Log.d("list size => ", " = 0");
            }
            count.setText(Integer.toString(list.size()));
            total.setText(calculateTotal(list));
        }
    }

}
