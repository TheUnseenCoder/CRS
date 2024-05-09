package com.advance.crs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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

public class ProductList extends AppCompatActivity implements ProductAdapter.OnProductClickListener {
    private static final String PREF_EMAIL = "email";
    private String email;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> products;
    String categoryname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize data list
        products = new ArrayList<>();
        categoryname = getIntent().getStringExtra("CATEGORY_NAME");
        TextView titlelabel = findViewById(R.id.LabelTitle);
        titlelabel.setText(categoryname + " Package");

        Toast.makeText(ProductList.this, categoryname, Toast.LENGTH_SHORT).show();

        // Fetch data using Volley
        fetchProductsFromServer(categoryname);
    }



    private void fetchProductsFromServer(String categoryname) {
        String url = "http://192.168.1.11/CRS/includes/products.php?category_name=" + categoryname; // Replace with your actual URL

        // Create a request using Volley
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parse JSON response and populate products list
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Product product = new Product();
                                product.setProductId(jsonObject.getString("product_id"));
                                product.setName(jsonObject.getString("name"));
                                product.setDescription(jsonObject.getString("description"));
                                product.setPax(jsonObject.getInt("pax"));
                                product.setBasePrice(jsonObject.getDouble("base_price"));
                                byte[] imageData = Base64.decode(jsonObject.getString("image"), Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                                product.setImage(bitmap);
                                products.add(product);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Create and set adapter
                        adapter = new ProductAdapter(ProductList.this, products, ProductList.this);
                        recyclerView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Log.e("ProductList", "Error fetching products: " + error.getMessage());
                Toast.makeText(ProductList.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    @Override
    public void onProductClick(Product product) {
        // Handle product click here
        // For example, you can start the ProductDetailsActivity and pass the product_id
        String email = getIntent().getStringExtra("email");
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("PRODUCT_ID", product.getProductId());
        intent.putExtra("email", email);
        startActivity(intent);
    }
}
