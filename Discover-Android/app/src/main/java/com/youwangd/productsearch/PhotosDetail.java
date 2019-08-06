package com.youwangd.productsearch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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


public class PhotosDetail extends Fragment {

    private JSONObject photoResult;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photos_detail, container, false);
        JSONObject detailData = ((DetailContainer) getActivity()).getDetailResult();
        String title = ((DetailContainer) getActivity()).getTitleData();
        String url = "http://hw8-youwangd.appspot.com/api/photos?Title=" + title;
        final LinearLayout photo_progress = v.findViewById(R.id.photo_progress);
        final ScrollView photo_scroll_view = v.findViewById(R.id.photo_scroll_view);
        final LinearLayout photos_layout = v.findViewById(R.id.photos_layout);
        final TextView photo_no_result = v.findViewById(R.id.photo_no_result);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        Log.d("photo request => ", url);
        JsonObjectRequest photoRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        photoResult = response;
                        photo_progress.setVisibility(View.GONE);
                        if(photoResult.has("items") && photoResult.optJSONArray("items").length() > 0) {
                            JSONArray items = photoResult.optJSONArray("items");
                            for(int i = 0; i < items.length(); i++) {
                                View singleImage = LayoutInflater.from(getActivity()).inflate(R.layout.product_photo, photos_layout, false);
                                ImageView img = singleImage.findViewById(R.id.photo_image);
                                Picasso.get().load(items.optJSONObject(i).optString("link")).resize(1200,0).error(R.drawable.error_1000_200).into(img);
                                photos_layout.addView(singleImage);
                            }
                            photo_scroll_view.setVisibility(View.VISIBLE);
                        } else {
                            photo_no_result.setVisibility(View.VISIBLE);
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("detail request =>", "fail");
                    }
                });
        queue.add(photoRequest);
        return v;
    }

}
