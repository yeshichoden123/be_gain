package com.android.be_gain.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.be_gain.MyApplication;
import com.android.be_gain.R;
import com.android.be_gain.databinding.ActivityProfileEditBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileEditActivity extends AppCompatActivity {

    // view binding
    private ActivityProfileEditBinding binding;

    // firebase auth, get/update user data using uid
    private FirebaseAuth firebaseAuth;

    // progress dialog
    private ProgressDialog progressDialog;

    private static final String TAG = "PROFILE_EDIT_TAG";

    private Uri imageUri = null;

    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // setup firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        loadUserInfo();

        // setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);   // don't dismiss while clicking outside of progress dialog

        // handle click, goback
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // handle click, pick image
        binding.profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageAttachMenu();
            }
        });

        // handle click, update profile
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        binding.changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent password_reset_intent = new Intent(ProfileEditActivity.this, PasswordResetActivity.class);
                startActivity(password_reset_intent);
            }
        });


    }

    private void validateData() {
        // get data
        name = binding.nameEt.getText().toString().trim();

        // validate data
        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Enter name...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // name is entered
            if (imageUri == null)
            {
                // need to update withoud image
                updateProfile("");
            }
            else
            {
                uploadImage();
            }
        }

    }

    private void uploadImage() {
        Log.d(TAG, "uploadImage: Uploading profile image...");
        progressDialog.setMessage("Updating profile image");
        progressDialog.show();

        // image path and name, use uid to replace previous
        String filePathName = "ProfileImages/"+firebaseAuth.getUid();

        // storage reference
        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathName);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: Profile image uploaded");
                        Log.d(TAG, "onSuccess: Getting url of uploaded image");

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        String uploadedImageUrl = ""+uriTask.getResult();

                        Log.d(TAG, "onSuccess: Uploaded Image URL: "+uploadedImageUrl);
                        updateProfile(uploadedImageUrl);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to upload image due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, "Failed to upload image due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProfile(String imageUrl) {

        Log.d(TAG, "updateProfile: Updating user profile");

        progressDialog.setMessage("Updating user profile...");
        progressDialog.show();

        // setup data to update in db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", ""+name);

        if (imageUri != null)
        {
            hashMap.put("profileImage", ""+imageUrl);
        }


        // update data to db
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG, "onSuccess: Profile updated...");
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, "Profile updated..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to update due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, "Failed to update db due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImageAttachMenu() {

        PopupMenu popupMenu = new PopupMenu(this, binding.profileIv);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");

        popupMenu.show();

        // handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // get id of item clicked
                int which = item.getItemId();
                if (which == 0)
                {
                    // camera clicked
                    pickImageCamera();
                }
                else if (which == 1)
                {
                    // gallery clicked

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (ContextCompat.checkSelfPermission(ProfileEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        {
                            pickImageGallery();
                        }
                        else
                        {
                            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }

                    }


                }
                return false;
            }
        });

    }

    private void pickImageCamera() {

        // intent to pick image from camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick"); // image title
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);

    }

    private void pickImageGallery() {

        // intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    // used to handle result of camera result
                    // get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        Log.d(TAG, "onActivityResult: Picked from camera "+imageUri);
                        Intent data = result.getData(); // No need here as in camera case we already have image in imageUri varible
                        binding.profileIv.setImageURI(imageUri);
                    }
                    else {
                        Toast.makeText(ProfileEditActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    // used to handle result of gallery intent
                    // get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        Log.d(TAG, "onActivityResult: "+imageUri);
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: Picked from Gallery "+imageUri);

                        binding.profileIv.setImageURI(imageUri);
                    }
                    else {
                        Toast.makeText(ProfileEditActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );


    private void loadUserInfo() {
        Log.d(TAG,"LoadUserInfo: Loading user info of user "+firebaseAuth.getUid());

        // get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null)
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            // get all info of user here from snapshot
                            String email = ""+snapshot.child("email").getValue();
                            String name = ""+snapshot.child("name").getValue();
                            String profileImage = ""+snapshot.child("profileImage").getValue();
                            String timestamp = ""+snapshot.child("time").getValue();
                            String uid = ""+snapshot.child("uid").getValue();
                            String userType = ""+snapshot.child("userType").getValue();

                            // set data to ui
                            binding.nameEt.setText(name);

                            // set image, using glide
                            Glide.with(ProfileEditActivity.this)
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_person_gray)
                                    .into(binding.profileIv);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
        else
        {
            Toast.makeText(ProfileEditActivity.this, "Log into your account.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileEditActivity.this, LoginActivity.class));

        }


    }

}