package com.advance.crs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    private static final String PREF_EMAIL = "email";
    TextView fullname, emailsTv, contact, fullnameLabel, contactLabel;
    EditText fullnameEdit, contactEdit;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button uploadPhotoButton;
    Uri selectedImageUri;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        FloatingActionButton fab = findViewById(R.id.fab);

        fullnameLabel = findViewById(R.id.fullnameLabel);
        contactLabel = findViewById(R.id.ContactLabel);
        fullnameEdit = findViewById(R.id.fullnameEdit);
        contactEdit = findViewById(R.id.contactEdit);
        Button cancelBtn = findViewById(R.id.cancelbtn);
        Button submitChangeUserInfo = findViewById(R.id.submitChangeUserInfo);
        submitChangeUserInfo.setVisibility(View.GONE);
        Button submitChangeUserPhoto = findViewById(R.id.submitChangeUserPhoto);
        submitChangeUserPhoto.setVisibility(View.GONE);
        Button uploadPhotoProfile = findViewById(R.id.UploadPhoto);
        uploadPhotoProfile.setVisibility(View.GONE);

        fullnameLabel.setVisibility(View.GONE);
        contactLabel.setVisibility(View.GONE);
        fullnameEdit.setVisibility(View.GONE);
        contactEdit.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);


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
        emailsTv = findViewById(R.id.emailtextview);
        emailsTv.setText("Email: " + storedEmail);
        if (storedEmail == null || storedEmail.isEmpty()) {
            fab.setVisibility(View.GONE); // Hide the FAB
            Intent intent = new Intent(Profile.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
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

        Button Logout = findViewById(R.id.logout);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                SharedPreferencesUtils.deleteStoredEmail(context);
                Intent intent = new Intent(Profile.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        Button ChangeProfileInfo = findViewById(R.id.editInfo);
        ChangeProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fullnameLabel = findViewById(R.id.fullnameLabel);
                contactLabel = findViewById(R.id.ContactLabel);
                fullnameEdit = findViewById(R.id.fullnameEdit);
                contactEdit = findViewById(R.id.contactEdit);
                fullnameLabel.setVisibility(View.VISIBLE);
                contactLabel.setVisibility(View.VISIBLE);
                fullnameEdit.setVisibility(View.VISIBLE);
                contactEdit.setVisibility(View.VISIBLE);

                fullname = findViewById(R.id.fullnametextview);
                emailsTv = findViewById(R.id.emailtextview);
                contact = findViewById(R.id.contactnumtextview);
                emailsTv.setVisibility(View.GONE);
                contact.setVisibility(View.GONE);
                fullname.setVisibility(View.GONE);

                Button changeprofile = findViewById(R.id.changeprofilephoto);
                changeprofile.setVisibility(View.GONE);
                Button changeinfo = findViewById(R.id.editInfo);
                changeinfo.setVisibility(View.GONE);
                Button logout = findViewById(R.id.logout);
                logout.setVisibility(View.GONE);
                Button cancelBtn = findViewById(R.id.cancelbtn);
                cancelBtn.setVisibility(View.VISIBLE);
                Button submitChangeUserInfo = findViewById(R.id.submitChangeUserInfo);
                submitChangeUserInfo.setVisibility(View.VISIBLE);
                Button submitChangeUserPhoto = findViewById(R.id.submitChangeUserPhoto);
                submitChangeUserPhoto.setVisibility(View.GONE);
                Button uploadPhotoProfile = findViewById(R.id.UploadPhoto);
                uploadPhotoProfile.setVisibility(View.GONE);


            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullnameLabel = findViewById(R.id.fullnameLabel);
                contactLabel = findViewById(R.id.ContactLabel);
                fullnameEdit = findViewById(R.id.fullnameEdit);
                contactEdit = findViewById(R.id.contactEdit);
                fullnameLabel.setVisibility(View.GONE);
                contactLabel.setVisibility(View.GONE);
                fullnameEdit.setVisibility(View.GONE);
                contactEdit.setVisibility(View.GONE);

                fullname = findViewById(R.id.fullnametextview);
                emailsTv = findViewById(R.id.emailtextview);
                contact = findViewById(R.id.contactnumtextview);
                emailsTv.setVisibility(View.VISIBLE);
                contact.setVisibility(View.VISIBLE);
                fullname.setVisibility(View.VISIBLE);

                Button changeprofile = findViewById(R.id.changeprofilephoto);
                changeprofile.setVisibility(View.VISIBLE);
                Button changeinfo = findViewById(R.id.editInfo);
                changeinfo.setVisibility(View.VISIBLE);
                Button logout = findViewById(R.id.logout);
                logout.setVisibility(View.VISIBLE);
                Button cancelBtn = findViewById(R.id.cancelbtn);
                cancelBtn.setVisibility(View.GONE);
                Button submitChangeUserInfo = findViewById(R.id.submitChangeUserInfo);
                submitChangeUserInfo.setVisibility(View.GONE);
                Button submitChangeUserPhoto = findViewById(R.id.submitChangeUserPhoto);
                submitChangeUserPhoto.setVisibility(View.GONE);
                Button uploadPhotoProfile = findViewById(R.id.UploadPhoto);
                uploadPhotoProfile.setVisibility(View.GONE);



            }
        });

        Button ChangeProfilePhoto = findViewById(R.id.changeprofilephoto);
        ChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullnameLabel = findViewById(R.id.fullnameLabel);
                contactLabel = findViewById(R.id.ContactLabel);
                fullnameEdit = findViewById(R.id.fullnameEdit);
                contactEdit = findViewById(R.id.contactEdit);
                fullnameLabel.setVisibility(View.GONE);
                contactLabel.setVisibility(View.GONE);
                fullnameEdit.setVisibility(View.GONE);
                contactEdit.setVisibility(View.GONE);

                fullname = findViewById(R.id.fullnametextview);
                emailsTv = findViewById(R.id.emailtextview);
                contact = findViewById(R.id.contactnumtextview);
                emailsTv.setVisibility(View.GONE);
                contact.setVisibility(View.GONE);
                fullname.setVisibility(View.GONE);

                Button changeprofile = findViewById(R.id.changeprofilephoto);
                changeprofile.setVisibility(View.GONE);
                Button changeinfo = findViewById(R.id.editInfo);
                changeinfo.setVisibility(View.GONE);
                Button logout = findViewById(R.id.logout);
                logout.setVisibility(View.GONE);
                Button cancelBtn = findViewById(R.id.cancelbtn);
                cancelBtn.setVisibility(View.VISIBLE);
                Button submitChangeUserInfo = findViewById(R.id.submitChangeUserInfo);
                submitChangeUserInfo.setVisibility(View.GONE);
                Button submitChangeUserPhoto = findViewById(R.id.submitChangeUserPhoto);
                submitChangeUserPhoto.setVisibility(View.VISIBLE);
                Button uploadPhotoProfile = findViewById(R.id.UploadPhoto);
                uploadPhotoProfile.setVisibility(View.VISIBLE);


            }
        });

        submitChangeUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullnameEdit.getText().toString();
                String contact = contactEdit.getText().toString();

                updateUserInfo(storedEmail, fullName, contact);

            }
        });

        uploadPhotoButton = findViewById(R.id.UploadPhoto);

        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open the gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*"); // Allow only images to be selected
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });


        submitChangeUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri == null) {
                    // Notify the user to login first
                    Toast.makeText(Profile.this, "Please select a photo first", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateUserImage(storedEmail, selectedImageUri);

            }
        });

        fetchDetails(storedEmail);
    }
    private void updateUserImage(String storedEmail, Uri selectedImageUri) {
        if (storedEmail == null || storedEmail.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            byte[] imageBytes = getImageBytes(selectedImageUri);
            uploadImage(storedEmail, imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getImageBytes(Uri selectedImageUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadImage(String storedEmail, byte[] imageBytes) {
        String uploadUrl = "http://192.168.1.11/CRS/includes/upload_image.php"; // Replace with your server URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, uploadUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Profile.this, response, Toast.LENGTH_SHORT).show();
                        Log.e("ServerResponse", response);
                        Intent intent = new Intent(Profile.this, Profile.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Profile.this, "Error uploading image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", storedEmail);
                params.put("image", Base64.encodeToString(imageBytes, Base64.DEFAULT));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // User has successfully selected an image
            // Get the image URI from the intent
            selectedImageUri = data.getData();

            ImageView imageView = findViewById(R.id.profileimage);
            imageView.setImageURI(selectedImageUri);

        }
    }
    private void fetchDetails(String storedEmail) {
        // URL of your server endpoint to fetch the image
        String url = "http://192.168.1.11/CRS/includes/fetch_user.php?email=" + storedEmail;

        // Create a request using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String encodedProfile = jsonObject.getString("profile");
                            String fullname = jsonObject.getString("fullname");
                            String contact = jsonObject.getString("contact");

                            ImageView imageView = findViewById(R.id.profileimage);
                            if (encodedProfile != null && !encodedProfile.isEmpty()) {
                                // If response contains profile image, decode and set it to the ImageView
                                byte[] decodedBytes = Base64.decode(encodedProfile, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                imageView.setImageBitmap(bitmap);
                            } else {
                                // If no profile image found, set a default image
                                imageView.setImageResource(R.drawable.noprofile);
                            }
                            TextView fullnames = findViewById(R.id.fullnametextview);
                            TextView contacts = findViewById(R.id.contactnumtextview);
                            fullnameEdit = findViewById(R.id.fullnameEdit);
                             contactEdit = findViewById(R.id.contactEdit);
                            // Set full name and contact
                            fullnameEdit.setText(fullname);
                            contactEdit.setText(contact);
                            fullnames.setText("Fullname: " + fullname);
                            contacts.setText("Contact Number: " +contact);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Toast.makeText(getApplicationContext(), "Error fetching image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void updateUserInfo(String storedEmail, String fullname, String contact_num) {
        // Check if email is empty or null
        if (storedEmail == null || storedEmail.isEmpty()) {
            // Notify the user to login first
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fullname == null || fullname.isEmpty()) {
            // Notify the user to login first
            Toast.makeText(this, "Please insert your fullname", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contact_num == null || contact_num.isEmpty()) {
            // Notify the user to login first
            Toast.makeText(this, "Please insert your contact number", Toast.LENGTH_SHORT).show();
            return;
        }

        // If all required fields are selected, proceed with adding to cart
        // You can perform the HTTP request here to add the selected product to the cart
        // Construct the request URL with all the required parameters (size, variant, quantity, product ID)
        String addToCartUrl = "http://192.168.1.11/CRS/includes/update_userinfo.php?" +
                "email=" + storedEmail +
                "&fullname=" + fullname +
                "&contact_num=" + contact_num;

        // Create a request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create a string request to send the data to the server
        StringRequest stringRequest = new StringRequest(Request.Method.GET, addToCartUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response from the server
                        // If the response contains "success", show success message
                        // Otherwise, show error message
                        if (response.contains("success")) {
                            Toast.makeText(Profile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Profile.this, Profile.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Profile.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors that occur during the request
                        Toast.makeText(Profile.this, "Error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        // Add the request to the request queue
        queue.add(stringRequest);
    }
}