package com.youwangd.productsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchForm extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search_form, container, false);
        String [] categoryChoice =
                {"All","Art","Baby","Books","Clothing, Shoes & Accessories","Computer/Tablets & Networking","Health & Beauty","Music","Video Games & Consoles"};
        final EditText keyword = v.findViewById(R.id.keyword);
        final Spinner spinner = v.findViewById(R.id.spinner);
        final CheckBox New = v.findViewById(R.id.checkbox_new);
        final CheckBox Used = v.findViewById(R.id.checkbox_used);
        final CheckBox Unspec = v.findViewById(R.id.checkbox_unspec);
        final CheckBox Local = v.findViewById(R.id.checkbox_local);
        final CheckBox Free = v.findViewById(R.id.checkbox_free);
        final CheckBox nearby = v.findViewById(R.id.checkbox_nearby);
        final LinearLayout nearbyGroup = v.findViewById(R.id.nearby_search);
        final RadioGroup radgroup = v.findViewById(R.id.radioGroup);
        final RadioButton curLoc = v.findViewById(R.id.cur_location);
        final EditText miles = v.findViewById(R.id.miles_input);
        final AutoCompleteTextView zip = v.findViewById(R.id.zip_content);
        final TextView keyword_error = v.findViewById(R.id.keyword_error);
        final TextView keyword_invalid_error = v.findViewById(R.id.keyword_invalid_error);
        final TextView zip_error = v.findViewById(R.id.zip_error);
        final TextView zip_invalid_error = v.findViewById(R.id.zip_invalid_error);
        final String[] curZip = {""};
        final RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url ="http://ip-api.com/json/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        curZip[0] = response.optString("zip");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("ip-api request =>", "fail");
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast,
                                (ViewGroup) getActivity().findViewById(R.id.toast));
                        TextView text = (TextView) layout.findViewById(R.id.toast_text);
                        text.setText("no network, ip-api request failed");
                        Toast toast = new Toast(getActivity().getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 80);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
        queue.add(jsonObjectRequest);
        Button search = v.findViewById(R.id.search_btn);
        Button clear = v.findViewById(R.id.clear_btn);
        ArrayAdapter<String> arrAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, categoryChoice);
        arrAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(arrAdapter);
        nearby.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    nearbyGroup.setVisibility(View.VISIBLE);
                } else {
                    nearbyGroup.setVisibility(View.GONE);
//                    radgroup.check(R.id.cur_location);
//                    zip.setText("");
//                    zip_error.setVisibility(View.GONE);
//                    zip_invalid_error.setVisibility(View.GONE);
//                    zip.setEnabled(false);
                }
            }
        });
        radgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(R.id.cur_location == checkedId) {
                    zip.setText("");
                    zip_error.setVisibility(View.GONE);
                    zip_invalid_error.setVisibility(View.GONE);
                    zip.setEnabled(false);
                } else {
                    zip.setEnabled(true);
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "";
                boolean isValid = true;
                String Keyword = keyword.getText().toString().trim();
                if(Keyword.equals("")) {
                    keyword_error.setVisibility(View.VISIBLE);
                    keyword_invalid_error.setVisibility(View.GONE);
                    isValid = false;
                } else if(Keyword.length() == 1) {
                    keyword_invalid_error.setVisibility(View.VISIBLE);
                    keyword_error.setVisibility(View.GONE);
                    isValid = false;
                } else {
                    Log.d("keyword", Keyword);
                    url += "Keyword=" + Keyword + "&";
                    keyword_error.setVisibility(View.GONE);
                    keyword_invalid_error.setVisibility(View.GONE);
                }
                String Category = spinner.getSelectedItem().toString();
                Log.d("category", Category);
                url += "Category=";
                if(!Category.equals("All")) {
                    if(Category.equals("Art")) {
                        url += "550";
                    } else if(Category.equals("Baby")){
                        url += "2984";
                    } else if(Category.equals("Books")) {
                        url += "267";
                    } else if(Category.equals("Clothing, Shoes & Accessories")) {
                        url += "11450";
                    } else if(Category.equals("Computer/Tablets & Networking")) {
                        url += "58058";
                    } else if(Category.equals("Health & Beauty")) {
                        url += "26395";
                    } else if(Category.equals("Music")) {
                        url += "11233";
                    } else {
                        url += "1249";
                    }
                } else {
                    url += "All categories";
                }
                url += "&";
                url += "New=";
                if(New.isChecked()) {
                    Log.d("new", "true");
                    url += "true&";
                } else {
                    Log.d("new", "false");
                    url += "false&";
                }
                url += "Used=";
                if(Used.isChecked()) {
                    Log.d("used", "true");
                    url += "true&";
                } else {
                    Log.d("used", "false");
                    url += "false&";
                }
                url += "Unsepc=";
                if(Unspec.isChecked()) {
                    Log.d("unspec", "true");
                    url += "true&";
                } else {
                    Log.d("unsepc", "false");
                    url += "false&";
                }
                url += "Local=";
                if(Local.isChecked()) {
                    Log.d("local pickup", "true");
                    url += "true&";
                } else {
                    Log.d("local pickup", "false");
                    url += "false&";
                }
                url += "Free=";
                if(Free.isChecked()) {
                    Log.d("free shipping", "true");
                    url += "true&";
                } else {
                    Log.d("free shipping", "false");
                    url += "false&";
                }
                String zipInput = zip.getText().toString().trim();
                if(nearby.isChecked()) {
                    url = "http://hw8-youwangd.appspot.com/api/search?" + url;
                    Log.d("nearby", "true");
                    url += "Distance=";
                    String Miles = miles.getText().toString();
                    if(Miles.equals("")) {
                        Log.d("mile input", "10");
                        url += "10&";
                    } else {
                        Log.d("mile input", Miles);
                        url += Miles + "&";
                    }
                    url += "Location=";
                    if(curLoc.isChecked()) {
                        Log.d("zip input", curZip[0]);
                        zip_error.setVisibility(View.GONE);
                        zip_invalid_error.setVisibility(View.GONE);
                        url += "cur&";
                    } else {
                        url += "zip&";
                        if(zipInput.equals("")) {
                            zip_error.setVisibility(View.VISIBLE);
                            zip_invalid_error.setVisibility(View.GONE);
                            isValid = false;
                        } else if(zipInput.length() != 5){
                            zip_error.setVisibility(View.GONE);
                            zip_invalid_error.setVisibility(View.VISIBLE);
                            isValid = false;
                        } else {
                            Log.d("zip input", zipInput);
                            zip_error.setVisibility(View.GONE);
                            zip_invalid_error.setVisibility(View.GONE);
                        }
                    }
                } else {
                    url = "http://hw8-youwangd.appspot.com/api/nearby_disabled_search?" + url;
                    url += "Distance=10&Location=cur&";
                    Log.d("nearby", "false");
                }
                url += "ZipInput=" + zipInput + "&";
                url += "CurZipCode=" + curZip[0] + "&";
                if(!isValid) {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast,
                            (ViewGroup) getActivity().findViewById(R.id.toast));
                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                    text.setText("Please fix all fields with errors");
                    Toast toast = new Toast(getActivity().getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 80);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                } else {
                    Log.d("url => ", url);
                    Intent result = new Intent(getActivity(), SearchResult.class);
                    result.putExtra("searchURL", url);
                    result.putExtra("keyword", Keyword);
                    startActivity(result);
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword.setText("");
                keyword_error.setVisibility(View.GONE);
                keyword_invalid_error.setVisibility(View.GONE);
                spinner.setSelection(0);
                nearbyGroup.setVisibility(View.GONE);
                nearby.setChecked(false);
                radgroup.check(R.id.cur_location);
                zip.setText("");
                zip_error.setVisibility(View.GONE);
                zip_invalid_error.setVisibility(View.GONE);
                zip.setEnabled(false);
            }
        });
        zip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(zip.getText().toString().length() >= 2) {
                    String url ="http://hw8-youwangd.appspot.com/api/autoComplete?zip=" + zip.getText().toString();
                    JsonArrayRequest autoCompleteRequest = new JsonArrayRequest
                            (Request.Method.GET, url,null, new Response.Listener<JSONArray>() {

                                @Override
                                public void onResponse(JSONArray response) {
                                    Log.d("autocomplete length => ", Integer.toString(response.length()));
                                    List<String> list = new ArrayList<>();
                                    for(int i = 0; i < response.length(); i++) list.add(response.optString(i));
                                    String[] autoZip = new String[list.size()];
                                    for(int i = 0; i < list.size(); i++) autoZip[i] = list.get(i);
                                    ArrayAdapter<String> zipAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, autoZip);
                                    zip.setAdapter(zipAdapter);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error
                                    Log.d("autoComplete request =>", "fail");
                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.toast,
                                            (ViewGroup) getActivity().findViewById(R.id.toast));
                                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                                    text.setText("no network, autoComplete api failed");
                                    Toast toast = new Toast(getActivity().getApplicationContext());
                                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 80);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);
                                    toast.show();

                                }
                            });
                    queue.add(autoCompleteRequest);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }

}
