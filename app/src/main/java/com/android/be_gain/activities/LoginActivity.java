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

import com.android.be_gain.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity
{

    // view binding
    private ActivityLoginBinding binding;

    // firebase auth
    private FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    // firebase dialog
    private ProgressDialog progressDialog;
    private Boolean emailVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();


        // setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);


        // forgot password
        binding.forgotTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent password_reset_intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
                startActivity(password_reset_intent);

            }
        });

        // handle click, go to register screen
        binding.noAccountTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // handle click, begin login
        binding.loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validateData();
            }
        });
    }

    private String email = "", password = "";

    private void validateData()
    {
        /*before login account, lets do some data validation*/
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();

        // validate data
        if (TextUtils.isEmpty(email))
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
        else
        {
            // data is validated, begin login
            loginUser();
        }

    }

    private void loginUser()
    {
        // show progress
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        //login user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                {
                    @Override
                    public void onSuccess(AuthResult authResult)
                    {
                        progressDialog.dismiss();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        emailVerified = user.isEmailVerified();

                        if (emailVerified)
                        {

                            // login success
                            checkUser();

                        }
                        else {

                            Toast.makeText(LoginActivity.this, "Your account is not Verified! Please check your mail.", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser()
    {
        progressDialog.setMessage("Checking User...");
        progressDialog.show();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        // check in db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        progressDialog.dismiss();
                        // get user type
                        String userType = ""+snapshot.child("userType").getValue();
                        // check user type
                        if (userType.equals("user"))
                        {
                            // this is simple user, open user dashboard
                            startActivity(new Intent(LoginActivity.this, DashboardUserActivity.class));
                            finish();
                        }
                        else if (userType.equals("admin"))
                        {
                            // this is admin, open admin dashboard
                            startActivity(new Intent(LoginActivity.this, DashboardAdminActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}