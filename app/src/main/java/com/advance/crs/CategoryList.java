package com.advance.crs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CategoryList extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {
    private static final String PREF_EMAIL = "email";
    private String email;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<CategoryItem> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);

        FloatingActionButton fab = findViewById(R.id.fab);

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize data list
        categories = new ArrayList<>();

        // Fetch data using Volley
        fetchCategoriesFromServer();
    }


    private void fetchCategoriesFromServer() {
        String url = "http://192.168.1.11/CRS/includes/categories.php"; // Replace with your actual URL

        // Create a request using Volley
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parse JSON response and populate products list
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                CategoryItem categoryItem = new CategoryItem();
                                categoryItem.setProductId(jsonObject.getString("category_id"));
                                categoryItem.setName(jsonObject.getString("category_name"));
                                categories.add(categoryItem);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Create and set adapter
                        adapter = new CategoryAdapter(CategoryList.this, categories, CategoryList.this);
                        recyclerView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(CategoryList.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    @Override
    public void onCategoryClick(CategoryItem categoryItem) {
        // Handle product click here
        // For example, you can start the ProductDetailsActivity and pass the product_id
        String email = getIntent().getStringExtra("email");
        Intent intent = new Intent(this, ProductList.class);
        intent.putExtra("CATEGORY_NAME", categoryItem.getName());
        intent.putExtra("email", email);
        startActivity(intent);
    }
}
