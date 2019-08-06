package com.youwangd.productsearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar navBar = findViewById(R.id.navBar);
        navBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        navBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        String searchURL = getIntent().getStringExtra("searchURL");
        final String Keyword = getIntent().getStringExtra("keyword");
        Log.d("search request =>", searchURL);
        Log.d("keyword", Keyword);
        final ProgressBar pg = findViewById(R.id.progressBar);
        final LinearLayout progress = findViewById(R.id.progress);
        final ScrollView scroll = findViewById(R.id.scroll_view);
        final TextView no_result = findViewById(R.id.no_result);
        final ScrollGridView grid = findViewById(R.id.scroll_grid);
        final LinearLayout result_bar = findViewById(R.id.result_bar);
        final TextView count = findViewById(R.id.result_count);
        final TextView keyword = findViewById(R.id.search_keyword);
        JsonObjectRequest searchRequest = new JsonObjectRequest
                (Request.Method.GET, searchURL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        progress.setVisibility(View.GONE);
                        String ack = response.optJSONArray("findItemsAdvancedResponse").optJSONObject(0).optJSONArray("ack").optString(0);
                        if(ack.equals("Success") && !response.optJSONArray("findItemsAdvancedResponse").optJSONObject(0).optJSONArray("searchResult").optJSONObject(0).optString("@count").equals("0")) {
                            JSONArray items = response.optJSONArray("findItemsAdvancedResponse").optJSONObject(0).optJSONArray("searchResult").optJSONObject(0).optJSONArray("item");
                            count.setText(Integer.toString(items.length()));
                            keyword.setText(Keyword);
                            final List<Product> list = parseItems(items);
                            ProductAdapter pAdapter = new ProductAdapter(list, SearchResult.this);
                            grid.setAdapter(pAdapter);
                            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent detail = new Intent(SearchResult.this, DetailContainer.class);
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
//                            scroll.setFocusable(true);
//                            scroll.setFocusableInTouchMode(true);
//                            scroll.requestFocus();
                            result_bar.setVisibility(View.VISIBLE);
                            scroll.setVisibility(View.VISIBLE);
                            no_result.setVisibility(View.GONE);
                        } else {
                            result_bar.setVisibility(View.GONE);
                            scroll.setVisibility(View.GONE);
                            no_result.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("search request =>", "fail");
                        progress.setVisibility(View.GONE);
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast,
                                (ViewGroup) findViewById(R.id.toast));
                        TextView text = (TextView) layout.findViewById(R.id.toast_text);
                        text.setText("no network, search api failed");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 80);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
        queue.add(searchRequest);

    }

    @Override
    public void onResume() {
        super.onResume();
        ScrollGridView grid = findViewById(R.id.scroll_grid);
        if(grid.getAdapter() != null) {
            ((BaseAdapter) grid.getAdapter()).notifyDataSetChanged();
        }
        Log.d("resume => ", "success!");
    }

    public List<Product> parseItems(JSONArray items) {
        List<Product> list = new ArrayList<>();
        for(int i = 0; i < items.length(); i++) {
            JSONObject item = items.optJSONObject(i);
            String itemId = item.optJSONArray("itemId").optString(0);
            String title = item.optJSONArray("title").optString(0);
            String imgURL = item.has("galleryURL") ?  item.optJSONArray("galleryURL").optString(0) : "https://via.placeholder.com/180x180?text=N/A";
            String zipcode = item.has("postalCode") ?  item.optJSONArray("postalCode").optString(0) : "N/A";
            String shipping = item.has("shippingInfo") && item.optJSONArray("shippingInfo").optJSONObject(0).has("shippingServiceCost") ? item.optJSONArray("shippingInfo").optJSONObject(0).optJSONArray("shippingServiceCost").optJSONObject(0).optString("__value__").equals("0.0") ? "Free Shipping" : "$" + item.optJSONArray("shippingInfo").optJSONObject(0).optJSONArray("shippingServiceCost").optJSONObject(0).optString("__value__") : "N/A";
            String price = "$" + item.optJSONArray("sellingStatus").optJSONObject(0).optJSONArray("currentPrice").optJSONObject(0).optString("__value__");
            String condition = item.has("condition") ? item.optJSONArray("condition").optJSONObject(0).optJSONArray("conditionDisplayName").optString(0) : "N/A";
            list.add(new Product(itemId, imgURL, title, zipcode, shipping, condition, price));
        }
        return list;
    }
}
