package com.android.be_gain.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.be_gain.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
{

    // view binding
    private ActivityMainBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // handle loginBtn click, start login screen
        binding.loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        // handle skipBtn click, start continue without login screen
        binding.skipBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                checkUser();

            }
        });

    }

    private void checkUser() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null)
        {
            // user not logged in
            // start main screen
            startActivity(new Intent(MainActivity.this, DashboardUserActivity.class));
            finish(); // finish this activity

            /////////////////////////// for user
//            startActivity(new Intent(SplashActivity.this, DashboardUserActivity.class));
//            finish(); // finish this activity
        }
        else
        {
            // user logged in check user type, same as with login
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            // get user type
                            String userType = ""+snapshot.child("userType").getValue();
                            // check user type
                            if (userType.equals("user"))
                            {
                                // this is simple user, open user dashboard
                                startActivity(new Intent(MainActivity.this, DashboardUserActivity.class));
                                finish();
                            }
                            else if (userType.equals("admin"))
                            {
                                // this is admin, open admin dashboard
                                startActivity(new Intent(MainActivity.this, DashboardAdminActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

    }
}