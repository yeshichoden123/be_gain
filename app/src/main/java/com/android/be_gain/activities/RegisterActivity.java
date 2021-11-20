package com.android.be_gain.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.android.be_gain.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{

    // view binding
    private ActivityRegisterBinding binding;

    // firebase auth
    private FirebaseAuth firebaseAuth;

    // progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // handle click, go back
        binding.backBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        // handle click, begin register
        binding.registerBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validateData();
            }
        });

    }

    private String name = "", email = "", password = "";

    private void validateData()
    {
        /*before creating account, lets do some data validation*/

        // get data
        name = binding.nameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        String cPassword = binding.cPasswordEt.getText().toString().trim();

        // password validation.. atleast four characters
        String pwLength = ".{4,}";

        // validate data
        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Enter your email...", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this, "Invalid email pattern...!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Enter Password...", Toast.LENGTH_SHORT).show();
        }
        else if (!password.matches(pwLength))
        {
            Toast.makeText(this, "Enter at least 4 characters.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cPassword))
        {
            Toast.makeText(this, "Confirm Password...", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(cPassword))
        {
            Toast.makeText(this, "Password didn't match...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // all data validated and begin creating account
            createUserAccount();
        }
    }

    private void createUserAccount()
    {
        // show progress
        progressDialog.setMessage(("Creating account..."));
        progressDialog.show();

        // create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                {
                    @Override
                    public void onSuccess(AuthResult authResult)
                    {
                        SendEmailVerificationMessage();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(Exception e)
                    {
                        // account creation failure
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void SendEmailVerificationMessage(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {

                        // account creation success, now add in firebase realtime database
                        updateUserInfo();

                    }
                    else
                    {

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void updateUserInfo()
    {
        progressDialog.setMessage("Saving user info...");

        // timestamp
        Long timestamp = System.currentTimeMillis();

        // get current user uid, since user is registered so we can get now
        String uid = firebaseAuth.getUid();

        // setup data to add in db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", ""); // add empty, will do later
        hashMap.put("userType", "user"); // userType to "admin" for admin// possible values are user, admin: will make admin manually in firebase realtime database by changing this value
        hashMap.put("time", timestamp);

        //set data to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void unused)
                    {
                        // data added to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Successfully Registered User, Please Verify!", Toast.LENGTH_SHORT).show();

                        //Toast.makeText(RegisterActivity.this, "Account created...", Toast.LENGTH_SHORT).show();
                        // since user account is created so start dashboard of user
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        // data failed adding to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


}