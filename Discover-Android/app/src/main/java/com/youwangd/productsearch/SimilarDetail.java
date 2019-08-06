package com.youwangd.productsearch;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SimilarDetail extends Fragment {

    private JSONObject similarResult;
    private List<Product> originalList;
    private List<Product> dynamicList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_similar_detail, container, false);
        String itemId = ((DetailContainer) getActivity()).getItemId();
        String url = "http://hw8-youwangd.appspot.com/api/similar?ItemId=" + itemId;
        String [] sort_item = {"Default", "Name", "Price", "Days"};
        String [] sort_order = {"Ascending", "Descending"};
        final LinearLayout similar_progress = v.findViewById(R.id.similar_progress);
        final TextView similar_no_result = v.findViewById(R.id.similar_no_result);
        final ScrollView similar_scroll_view = v.findViewById(R.id.similar_scroll_view);
        final ScrollGridView similar_grid_view = v.findViewById(R.id.similar_scroll_grid);
        final Spinner sort_item_spinner = v.findViewById(R.id.sort_item_spinner);
        final Spinner sort_order_spinner = v.findViewById(R.id.sort_order_spinner);

        ArrayAdapter<String> sortItemAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, sort_item);
        sortItemAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sort_item_spinner.setAdapter(sortItemAdapter);

        ArrayAdapter<String> sortOrderAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, sort_order);
        sortOrderAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sort_order_spinner.setAdapter(sortOrderAdapter);

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest photoRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        similarResult = response;
                        similar_progress.setVisibility(View.GONE);
                        sort_order_spinner.setEnabled(false);
                        String status = similarResult.optJSONObject("getSimilarItemsResponse").optString("ack");
                        if(status.equals("Success") && similarResult.optJSONObject("getSimilarItemsResponse").optJSONObject("itemRecommendations").optJSONArray("item").length() > 0) {
                            JSONArray items = similarResult.optJSONObject("getSimilarItemsResponse").optJSONObject("itemRecommendations").optJSONArray("item");
                            dynamicList = parseItems(items);
                            originalList = new ArrayList<>();
                            copyProduct(dynamicList, originalList);
                            SimilarItemAdapter sAdapter = new SimilarItemAdapter(dynamicList, getActivity());
                            similar_grid_view.setAdapter(sAdapter);
                            similar_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                    Intent detail = new Intent(getActivity(), DetailContainer.class);
//                                    detail.putExtra("itemId", list.get(position).getItemId());
//                                    detail.putExtra("title", list.get(position).getTitle());
//                                    detail.putExtra("shipping_cost", list.get(position).getShipping());
//                                    startActivity(detail);
                                    Intent openIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dynamicList.get(position).getViewURL()));
                                    startActivity(openIntent);
                                }
                            });
                            sort_item_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    sortSimilar(dynamicList, sort_item_spinner, sort_order_spinner);
                                    ((BaseAdapter) similar_grid_view.getAdapter()).notifyDataSetChanged();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            sort_order_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    sortSimilar(dynamicList, sort_item_spinner, sort_order_spinner);
                                    ((BaseAdapter) similar_grid_view.getAdapter()).notifyDataSetChanged();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            similar_scroll_view.setVisibility(View.VISIBLE);

                        } else {
                            sort_item_spinner.setEnabled(false);
                            sort_order_spinner.setEnabled(false);
                            similar_scroll_view.setVisibility(View.GONE);
                            similar_no_result.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("similar request =>", "fail");
                    }
                });
        queue.add(photoRequest);
        return v;
    }

    public List<Product> parseItems(JSONArray items) {
        List<Product> list = new ArrayList<>();
        for(int i = 0; i < items.length(); i++) {
            JSONObject item = items.optJSONObject(i);
            String itemId = item.optString("itemId");
            String categotyId = item.optString("primaryCategoryId");
            String imgURL = item.has("imageURL") ?  item.optString("imageURL") : "https://via.placeholder.com/180x180?text=N/A";
            String title = item.optString("title");
            String shipping = item.has("shippingCost") ? item.optJSONObject("shippingCost").optString("__value__").equals("0.00") ? "Free Shipping" : "$" + item.optJSONObject("shippingCost").optString("__value__") : "N/A";
            String daysLeft = parseDays(item.optString("timeLeft"));
            String price = "$" + item.optJSONObject("buyItNowPrice").optString("__value__");
            String viewURL = item.optString("viewItemURL");
            list.add(new Product(itemId,categotyId,imgURL, title, shipping, daysLeft, price, viewURL));
        }
        return list;
    }

    public String parseDays(String str) {
        return str.substring(str.indexOf("P") + 1, str.indexOf("D"));

    }

    public void copyProduct(List<Product> fromList, List<Product> toList) {
        toList.clear();
        for(int i = 0; i < fromList.size(); i++) {
            Product item = fromList.get(i);
            toList.add(new Product(item.getItemId(),item.getCategotyId(), item.getImg(), item.getTitle(), item.getShipping(),item.getDaysLeft(),item.getPrice(),item.getViewURL()));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortSimilar(List<Product> list, Spinner item, Spinner order) {
        String item_selected = item.getSelectedItem().toString();
        String order_selected = order.getSelectedItem().toString();
        if(item_selected.equals("Default")) {
            order.setEnabled(false);
            copyProduct(originalList, list);
        } else {
            order.setEnabled(true);
            if(item_selected.equals("Name")) {
                list.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                });
            } else if(item_selected.equals("Price")) {
                list.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return (int) (Double.valueOf(o1.getPrice().substring(1)) - Double.valueOf(o2.getPrice().substring(1)));
                    }
                });
            } else if(item_selected.equals("Days")) {
                list.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return Integer.valueOf(o1.getDaysLeft()) - Integer.valueOf(o2.getDaysLeft());
                    }
                });
            }

            if(order_selected.equals("Descending")) {
                Collections.reverse(list);
            }
        }
    }

}
