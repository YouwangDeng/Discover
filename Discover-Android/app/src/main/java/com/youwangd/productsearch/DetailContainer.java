package com.youwangd.productsearch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class DetailContainer extends AppCompatActivity {
    private JSONObject detailResult;
    private String shipping_cost;
    private String title;
    private String itemId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_container);

        final SharedPreferences storage = getSharedPreferences("wish_list_storage", 0);
        final SharedPreferences.Editor editor = storage.edit();
        final FloatingActionButton wish_list_detail = findViewById(R.id.wish_list_detail);

        final LinearLayout detail_progress = findViewById(R.id.detail_progress);
        Toolbar detail_navBar = findViewById(R.id.detail_navBar);
        ImageButton facebook = findViewById(R.id.facebook);
        final ViewPager viewPager = findViewById(R.id.detail_viewPager);
        TabLayout tabLayout = findViewById(R.id.detail_tabLayout);

        itemId = getIntent().getStringExtra("itemId");
        final String detail = getIntent().getStringExtra("detail");
        if(storage.contains(itemId)) {
            wish_list_detail.setImageResource(R.drawable.cart_remove_white);
        } else {
            wish_list_detail.setImageResource(R.drawable.cart_plus_white);
        }

        wish_list_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storage.contains(itemId)) {
                    wish_list_detail.setImageResource(R.drawable.cart_plus_white);
                    editor.remove(itemId);
                    editor.commit();
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast,
                            (ViewGroup) findViewById(R.id.toast));
                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                    text.setText(title + " was removed from wish list");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 80);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                } else {
                    wish_list_detail.setImageResource(R.drawable.cart_remove_white);
                    editor.putString(itemId, detail);
                    editor.commit();
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast,
                            (ViewGroup) findViewById(R.id.toast));
                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                    text.setText(title + " was added to wish list");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 80);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });
        title = getIntent().getStringExtra("title");
        shipping_cost = getIntent().getStringExtra("shipping_cost");
        String url = "http://hw8-youwangd.appspot.com/api/detail?ItemId=" + itemId;
        Log.d("detail url => ", url);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest detailRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        detailResult = response;
                        detail_progress.setVisibility(View.GONE);
                        viewPager.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("detail request =>", "fail");
                    }
                });
        queue.add(detailRequest);
        detail_navBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        detail_navBar.setTitle(trunkAgain(title,24));
        detail_navBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(buildFacebookURL()));
                startActivity(browserIntent);
            }
        });

        NavTabAdapter navAdapter = new NavTabAdapter(getSupportFragmentManager());
        navAdapter.addFragment(new ProductDetail(), "PRODUCT");
        navAdapter.addFragment(new ShippingDetail(), "SHIPPING");
        navAdapter.addFragment(new PhotosDetail(), "PHOTOS");
        navAdapter.addFragment(new SimilarDetail(), "SIMILAR");
        viewPager.setAdapter(navAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.information_variant_selected);
        tabLayout.getTabAt(1).setIcon(R.drawable.truck_delivery_selected);
        tabLayout.getTabAt(2).setIcon(R.drawable.google_selected);
        tabLayout.getTabAt(3).setIcon(R.drawable.equal_selected);
    }

    public String trunkAgain(String title, int limit) {
        if(title.substring(title.length() - 3).equals("...") || title.length() > 24) {
            title = title.substring(0,limit) + "...";
        }
        return title;
    }

    public JSONObject getDetailResult() {
        return this.detailResult;
    }

    public String getShipping_cost() {
        return this.shipping_cost;
    }

    public String getTitleData() {
        return this.title;
    }

    public String getItemId() {
        return this.itemId;
    }

    public String buildFacebookURL() {
        String url = "http://www.facebook.com/sharer/sharer.php?u=";
        if(detailResult.has("Item")) {
            JSONObject item = detailResult.optJSONObject("Item");
            String productURL = item.optString("ViewItemURLForNaturalSearch");
            String title = trunkAgain(item.optString("Title"), 30);
            String price = "$" + item.optJSONObject("CurrentPrice").optString("Value");
            String quote = "Buy " + title + " for " + price + " from Ebay!";
            url += productURL;
            url += "&quote=" + quote;
            url += "&hashtag=%23CSCI571Spring2019Ebay";
        }
        return url;
    }
}
