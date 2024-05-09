package com.advance.crs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.OrderRequest;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EventDetailsActivity extends AppCompatActivity {


    private static final String TAG = "MyTag";
    PaymentButtonContainer paymentButtonContainer70;
    private Double balance;
    private Integer total_amount;
    private Integer eventId;
    private String buyerEmail;
    private Double basePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_scheduled_details);

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

        String productIdString = getIntent().getStringExtra("PRODUCT_ID");
        buyerEmail = SharedPreferencesUtils.getStoredEmail(this);


        if (buyerEmail != null && !buyerEmail.isEmpty()) {
            fetchProductDetails(buyerEmail);
        } else {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            finish();
        }

        Button orderReceived = findViewById(R.id.order_received);
        orderReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle login button click
                // For example, start LoginActivity
                updateOrderStatus(eventId);
            }
        });

        paymentButtonContainer70 = findViewById(R.id.payment_button_container_70);
        paymentButtonContainer70.setup(

                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        Log.d(TAG, "create: ");
                        total_amount = (int) (basePrice - 5000);
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.USD)
                                                        .value(String.valueOf(total_amount))
                                                        .build()
                                        )
                                        .build()
                        );
                        OrderRequest order = new OrderRequest(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        // Check if required fields are selected
                        handleApproval(approval);

                    }
                }
        );

    }
    private void handleApproval(@NotNull Approval approval) {
        approval.getOrderActions().capture(new OnCaptureComplete() {
            @Override
            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                // Handle capture completion
                // Accessing the transaction ID (payment_id) directly from the captures array
                String resultString = String.format("CaptureOrderResult: %s", result);
                // Define a regular expression pattern to match the id
                Pattern pattern = Pattern.compile("Capture\\(id=(\\w+),");
                // Create a matcher with the input string
                Matcher matcher = pattern.matcher(resultString);
                if (matcher.find()) {
                    String transactionId = matcher.group(1);
                    // Further processing with the transaction ID
                    Toast.makeText(EventDetailsActivity.this, "Successfully Paid", Toast.LENGTH_SHORT).show();

                    Log.e("Transaction ID: ", transactionId);
                    String payerId = String.format("%s", approval.getData().getPayerId());
                    String payerPaymentEmail1 = String.format("%s", approval.getData().getPayer().getEmail());
                    String payerGivenName = String.format("%s", approval.getData().getPayer().getName().getGivenName());
                    String payerFamilyName = String.format("%s", approval.getData().getPayer().getName().getFamilyName());
                    String payerPaymentEmail = payerPaymentEmail1.substring(payerPaymentEmail1.indexOf("=") + 1, payerPaymentEmail1.indexOf(",")).trim();

                    // Send order details to server based on payment percent
                    sendOrderDetailsToServer(eventId, basePrice, buyerEmail, payerId, transactionId, payerPaymentEmail, payerGivenName, payerFamilyName);
                } else {
                    // Handle case when no match is found
                    Log.e("Transaction ID: ", "No match found");
                    Toast.makeText(EventDetailsActivity.this, "Failed to process transaction ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void fetchProductDetails(String buyerEmail) {
        String url = "http://192.168.1.11/CRS/includes/event_details.php?email=" + buyerEmail;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "Response: " + response.toString());

                            if (!response.isNull("product_details")) {
                                JSONObject productDetails = response.getJSONObject("product_details");
                                Log.d(TAG, "Product Details JSON: " + productDetails.toString());
                                // Display product name and description
                                // Hide parent layout if no data is available


                                TextView productNameTextView = findViewById(R.id.textViewProductName);
                                TextView productDescriptionTextView = findViewById(R.id.textViewProductDescription);
                                TextView productPrice = findViewById(R.id.baseprice);
                                TextView productPax = findViewById(R.id.pax);
                                TextView productInclusion = findViewById(R.id.inclusiontext);
                                TextView productFreebies = findViewById(R.id.freebiestext);
                                TextView Eventdate = findViewById(R.id.datetext);
                                TextView Eventtime = findViewById(R.id.timetext);
                                TextView Additional = findViewById(R.id.additionalstext);
                                TextView Balance = findViewById(R.id.balancetext);
                                TextView PaymentStatus = findViewById(R.id.paymentstatustext);
                                paymentButtonContainer70 = findViewById(R.id.payment_button_container_70);

                                productNameTextView.setText(productDetails.getString("product_name"));
                                productDescriptionTextView.setText(productDetails.getString("product_description"));
                                productPrice.setText("₱" + productDetails.getString("base_price"));
                                productPax.setText("Pax: " + productDetails.getString("pax"));
                                productInclusion.setText(productDetails.getString("inclusion"));
                                String freebies = productDetails.getString("freebies");
                                basePrice = productDetails.getDouble("base_price");
                                String date = productDetails.getString("date");
                                String time = productDetails.getString("time");
                                eventId = productDetails.getInt("event_id");

                                String paymentstatus = productDetails.getString("payment_status");
                                Button orderReceived = findViewById(R.id.order_received);

                                if (paymentstatus.equals("partially paid")) {
                                    PaymentStatus.setTextColor(Color.parseColor("#FE9705"));
                                    orderReceived.setVisibility(View.GONE);
                                    PaymentStatus.setText("Partially Paid");
                                    balance = basePrice - 5000;
                                    Balance.setText("₱" + balance);
                                } else {
                                    paymentButtonContainer70.setVisibility(View.GONE);
                                    PaymentStatus.setTextColor(Color.parseColor("#3AC430"));
                                    orderReceived.setVisibility(View.VISIBLE);
                                    PaymentStatus.setText("Fully Paid");
                                    balance = basePrice - basePrice;
                                    Balance.setText("₱0.00");

                                }

                                Eventtime.setText(time);
                                Eventdate.setText(date);
                                String additional = productDetails.getString("additional");


                                if (freebies == null || freebies.isEmpty()) {
                                    productFreebies.setText("No Freebies in this Package");
                                } else {
                                    productFreebies.setText(freebies);
                                }
                                if (additional == null || additional.isEmpty()) {
                                    Additional.setText("No Additionals in this Package");
                                } else {
                                    Additional.setText(additional);
                                }

                                byte[] imageData = Base64.decode(productDetails.getString("image"), Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                                ImageView imageView = findViewById(R.id.images_container); // Update with correct ImageView ID
                                imageView.setImageBitmap(bitmap);
                            }else{
                                ScrollView scrollView = findViewById(R.id.parent_layout);
                                scrollView.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }



    private void sendOrderDetailsToServer(Integer eventId, Double basePrice, String buyer_email, String payerId, String transactionId, String payerPaymentEmail, String payerGivenName, String payerFamilyName) {
        // Example:
        String url = "http://192.168.1.11/CRS/includes/pay_again.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle server response
                        if (response.trim().equalsIgnoreCase("success")) {
                            // Order placed successfully
                            Toast.makeText(EventDetailsActivity.this, "Payment Success! You are now fully paid!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EventDetailsActivity.this, EventDetailsActivity.class);
                            startActivity(intent);
                        } else {
                            // Order placement failed
                            Log.e("Response", response);
                            Toast.makeText(EventDetailsActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(EventDetailsActivity.this, "Failed to place order. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Add parameters for the order details
                params.put("base_price", String.valueOf(basePrice));
                params.put("email", buyer_email);
                params.put("transaction_id", transactionId);
                params.put("payer_id", payerId);
                params.put("payer_givenname", payerGivenName);
                params.put("payer_familyname", payerFamilyName);
                params.put("payer_email", payerPaymentEmail);
                params.put("event_id", String.valueOf(eventId));

                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void updateOrderStatus(int eventId) {
        // Example:
        String url = "http://192.168.1.11/CRS/includes/update_orderstatus.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle server response
                        Log.e("Response from server", response); // Log the response

                        if (response.trim().equalsIgnoreCase("success")) {
                            // Order placed successfully
                            Toast.makeText(EventDetailsActivity.this, "Order status successfully updated!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EventDetailsActivity.this, PaymentHistory.class);
                            startActivity(intent);
                            Log.e("Order ID", String.valueOf(eventId));
                        } else {
                            // Order placement failed
                            Toast.makeText(EventDetailsActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(EventDetailsActivity.this, "Failed to place order. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("event_id", String.valueOf(eventId));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }


}

