package com.advance.crs;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.Calendar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProductDetailsActivity extends AppCompatActivity {

    private List<String> sizesList = new ArrayList<>();
    private CalendarView calendarView;
    List<String> bookedDates = new ArrayList<>();
    private String selectedTime;
    private String additional;
    private static final String TAG = "MyTag";
    PaymentButtonContainer paymentButtonContainer70;

    private static final int PAYPAL_REQUEST_CODE = 123;
    private String buyerEmail;
    private int productID;
    private String selectedDate;
    private Double basePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize variants container

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
        productID = Integer.parseInt(getIntent().getStringExtra("PRODUCT_ID"));


        // Check if productIdString is not nullProdu
        if (productIdString != null) {
            try {
                // Parse the productIdString to an int
                int productId = Integer.parseInt(productIdString);

                // Fetch product details, sizes, and variants
                fetchProductDetails(productId);
            } catch (NumberFormatException e) {
                // Handle the case where productIdString cannot be parsed to an int
                Toast.makeText(this, "Invalid product ID format", Toast.LENGTH_SHORT).show();
                // Optionally, close the activity or take appropriate action
                finish();
            }
        } else {
            // Handle the case where productIdString is null
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show();
            // Optionally, close the activity or take appropriate action
            finish();
        }



        calendarView = findViewById(R.id.calendarView);

        // Disable past dates and dates before 1 week from today
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 0);
        minDate.add(Calendar.DAY_OF_MONTH, 7);
        calendarView.setMinDate(minDate.getTimeInMillis());

        // Set today's date as selected
        calendarView.setDate(Calendar.getInstance().getTimeInMillis(), false, true);

        // Fetch booked dates from server
        fetchBookedDates();

        EditText additionalEditText = findViewById(R.id.additionalstext);
        additional = additionalEditText.getText().toString();

        Button timeButton = findViewById(R.id.time_button);
        timeButton.setOnClickListener(v -> showTimePicker());

        paymentButtonContainer70 = findViewById(R.id.payment_button_container_70);
        paymentButtonContainer70.setup(

                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        Log.d(TAG, "create: ");
                        if (selectedTime == null || selectedDate == null || selectedDate.isEmpty() || selectedTime.isEmpty()) {
                            // Show an error message if date or time is not selected
                            Toast.makeText(ProductDetailsActivity.this, "Please select date and time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //add the if else here of selected date toast
                        // Check if selected date is booked
                        if (bookedDates.contains(selectedDate)) {
                            // Date is booked, show toast and return
                            Toast.makeText(ProductDetailsActivity.this, "Selected date is booked", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        checkExistingEvent(buyerEmail, new EventCheckCallback() {
                            @Override
                            public void onEventCheckResult(boolean eventExists) {
                                if (eventExists) {
                                    // Existing event found, display a message to the user
                                    Toast.makeText(ProductDetailsActivity.this, "You already have an existing event scheduled", Toast.LENGTH_SHORT).show();
                                } else {
                                    // No existing event found, proceed with order creation
                                    createOrder(createOrderActions);
                                }
                            }
                        });
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
    interface EventCheckCallback {
        void onEventCheckResult(boolean eventExists);
    }
    private void createOrder(@NotNull CreateOrderActions createOrderActions) {
        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
        purchaseUnits.add(
                new PurchaseUnit.Builder()
                        .amount(
                                new Amount.Builder()
                                        .currencyCode(CurrencyCode.USD)
                                        .value(String.valueOf(5000))
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
                    Toast.makeText(ProductDetailsActivity.this, "Successfully Paid", Toast.LENGTH_SHORT).show();

                    Log.e("Transaction ID: ", transactionId);
                    String payerId = String.format("%s", approval.getData().getPayerId());
                    String payerPaymentEmail1 = String.format("%s", approval.getData().getPayer().getEmail());
                    String payerGivenName = String.format("%s", approval.getData().getPayer().getName().getGivenName());
                    String payerFamilyName = String.format("%s", approval.getData().getPayer().getName().getFamilyName());
                    String payerPaymentEmail = payerPaymentEmail1.substring(payerPaymentEmail1.indexOf("=") + 1, payerPaymentEmail1.indexOf(",")).trim();

                    // Send order details to server based on payment percent
                    sendOrderDetailsToServer(additional, basePrice, buyerEmail, productID, payerId, selectedTime, selectedDate, transactionId, payerPaymentEmail, payerGivenName, payerFamilyName);
                } else {
                    // Handle case when no match is found
                    Log.e("Transaction ID: ", "No match found");
                    Toast.makeText(ProductDetailsActivity.this, "Failed to process transaction ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            // Handle the selected time
            Button timeButton = findViewById(R.id.time_button);
            selectedTime = selectedHour + ":" + selectedMinute;
            timeButton.setText(selectedTime);
            // Update the UI or perform other actions
        }, hour, minute, false);
        timePickerDialog.show();
    }
    private void fetchProductDetails(int productId) {
        String url = "http://192.168.1.11/CRS/includes/product_details.php?product_id=" + productId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject productDetails = response.getJSONObject("product_details");

                            // Display product name and description
                            TextView productNameTextView = findViewById(R.id.textViewProductName);
                            TextView productDescriptionTextView = findViewById(R.id.textViewProductDescription);
                            TextView productPrice = findViewById(R.id.baseprice);
                            TextView productPax = findViewById(R.id.pax);
                            TextView productInclusion = findViewById(R.id.inclusiontext);
                            TextView productFreebies = findViewById(R.id.freebiestext);

                            productNameTextView.setText(productDetails.getString("product_name"));
                            productDescriptionTextView.setText(productDetails.getString("product_description"));
                            productPrice.setText("₱" + productDetails.getString("base_price"));
                            productPax.setText("Pax: " + productDetails.getString("pax"));
                            productInclusion.setText(productDetails.getString("inclusion"));
                            basePrice = productDetails.getDouble("base_price");
                            String freebies = productDetails.getString("freebies");
                            if (freebies == null || freebies.isEmpty()) {
                                productFreebies.setText("No Freebies in this Package");
                            } else {
                                productFreebies.setText(freebies);
                            }

                            String imageBlob = productDetails.getString("image");

                            byte[] imageData = Base64.decode(productDetails.getString("image"), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                            ImageView imageView = findViewById(R.id.images_container); // Update with correct ImageView ID
                            imageView.setImageBitmap(bitmap);
                            // Display images
//                            displayImages(productDetails.getJSONArray("images"));
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


    private void fetchBookedDates() {
        String url = "http://192.168.1.11/CRS/includes/fetch_booked_dates.php";

        // Fetch booked dates from server
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Iterate over booked dates and store them
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                String dateString = response.getString(i);
                                bookedDates.add(dateString);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // Disable booked dates in the calendar
                        disableBookedDates();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    // Method to disable booked dates in the calendar
    private void disableBookedDates() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Format selected date
                selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                // Check if selected date is in bookedDates list
                if (bookedDates.contains(selectedDate)) {
                    // Date is booked, prevent selection
                    Toast.makeText(ProductDetailsActivity.this, "Date is booked", Toast.LENGTH_SHORT).show();
                    view.setDate(calendarView.getDate(), false, true); // Reset to current date
                }
            }
        });
    }
    private void sendOrderDetailsToServer(String additional, Double basePrice, String buyer_email, int product_id, String payerId, String selectedTime, String selectedDate, String transactionId, String payerPaymentEmail, String payerGivenName, String payerFamilyName) {
        // Example:
        String url = "http://192.168.1.11/CRS/includes/buynow.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle server response
                        if (response.trim().equalsIgnoreCase("success")) {
                            // Order placed successfully
                            Toast.makeText(ProductDetailsActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProductDetailsActivity.this, CategoryList.class);
                            startActivity(intent);
                        } else {
                            // Order placement failed
                            Toast.makeText(ProductDetailsActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(ProductDetailsActivity.this, "Failed to place order. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Add parameters for the order details
                params.put("base_price", String.valueOf(basePrice));
                params.put("email", buyer_email);
                params.put("product_id", String.valueOf(product_id));
                params.put("transaction_id", transactionId);
                params.put("payer_id", payerId);
                params.put("payer_givenname", payerGivenName);
                params.put("payer_familyname", payerFamilyName);
                params.put("payer_email", payerPaymentEmail);
                params.put("time", selectedTime);
                params.put("date", selectedDate);
                params.put("additional", additional);

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

    // Inside your checkExistingEvent method
    private void checkExistingEvent(String buyerEmail, EventCheckCallback callback) {
        String url = "http://192.168.1.11/CRS/includes/event_checker.php?email=" + buyerEmail;

        // Create a JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean eventExists = response.getBoolean("event_exists");
                            callback.onEventCheckResult(eventExists);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // Handle error response
                    }
                });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

}

