package com.android.be_gain.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.android.be_gain.databinding.ActivityPasswordResetBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordResetActivity extends AppCompatActivity {

    // view binding
    private ActivityPasswordResetBinding binding;

    // firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordResetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();


        // handle click, go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // handle click, begin login
        binding.submitBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = binding.emailEt.getText().toString().trim();

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(PasswordResetActivity.this, "Please Enter the Email Address", Toast.LENGTH_LONG).show();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    Toast.makeText(PasswordResetActivity.this, "Invalid email pattern...!", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(PasswordResetActivity.this, "Password Reset link sent to your email", Toast.LENGTH_SHORT).show();

                                finish();

                            } else {

                                Toast.makeText(PasswordResetActivity.this, "Please Enter the Registered Email Address", Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                }
            }
        });

    }
}