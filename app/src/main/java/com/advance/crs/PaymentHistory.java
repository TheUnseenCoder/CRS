package com.advance.crs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PaymentHistory extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<PaymentHistoryItem> paymentHistoryItems;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        FloatingActionButton fab = findViewById(R.id.fab);

        String email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) {
            fab.setVisibility(View.GONE); // Hide the FAB
        } else {
            fab.setVisibility(View.VISIBLE); // Show the FAB
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExpandedMenuFragment expandedMenuFragment = new ExpandedMenuFragment();
                    expandedMenuFragment.show(getSupportFragmentManager(), "ExpandedMenuFragment");
                }
            });
        }

        String storedEmail = SharedPreferencesUtils.getStoredEmail(this);
        if (storedEmail == null || storedEmail.isEmpty()) {
            fab.setVisibility(View.GONE); // Hide the FAB
        } else {
            fab.setVisibility(View.VISIBLE); // Show the FAB
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExpandedMenuFragment expandedMenuFragment = new ExpandedMenuFragment();
                    expandedMenuFragment.show(getSupportFragmentManager(), "ExpandedMenuFragment");
                }
            });
        }


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        paymentHistoryItems = new ArrayList<>();
        adapter = new PaymentHistoryAdapter(this, paymentHistoryItems); // Pass 'this' as the Context
        recyclerView.setAdapter(adapter);
        fetchDataFromServer(storedEmail);

    }
    private void fetchDataFromServer(String email) {
        // URL of your PHP script
        String PHP_URL = "http://192.168.1.11/CRS/includes/fetch_payments.php?email=" + email;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, PHP_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String transaction_id = jsonObject.getString("transaction_id");
                                String payer_id = jsonObject.getString("payer_id");
                                String payer_fullname = jsonObject.getString("payer_fullname");
                                int order_id = Integer.parseInt(jsonObject.getString("order_id"));
                                String payment_status = jsonObject.getString("payment_status");
                                String timestamp = jsonObject.getString("timestamp");


                                paymentHistoryItems.add(new PaymentHistoryItem(transaction_id, payer_id, payer_fullname, order_id, payment_status, timestamp));
                            }
                            adapter.notifyDataSetChanged();
                            // Display a success Toast message
                            Toast.makeText(PaymentHistory.this, "Data fetched successfully", Toast.LENGTH_SHORT).show();
                            // Log success message
                            Log.d("FetchData", "Data fetched successfully");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Display an error Toast message
                            Toast.makeText(PaymentHistory.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                            // Log error message
                            Log.e("FetchData", "Error parsing JSON data", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Display an error Toast message
                Toast.makeText(PaymentHistory.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                // Log error message
                Log.e("FetchData", "Error fetching data", error);
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }
}