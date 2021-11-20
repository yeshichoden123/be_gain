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

import com.android.be_gain.R;
import com.android.be_gain.databinding.ActivityFeedbackBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {

    private ActivityFeedbackBinding binding;

    // firebase auth
    private FirebaseAuth firebaseAuth;

    private String feedback = "";

    // progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // setup progress dialog
        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Please Wait");
//        progressDialog.setCanceledOnTouchOutside(false);

        // handle click, goback
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // handle click, begin register
        binding.submitBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // get data
                feedback = binding.feedbackEt.getText().toString().trim();

                // validate data
                if (TextUtils.isEmpty(feedback))
                {
                    Toast.makeText(FeedbackActivity.this, "Enter your feedback...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.setMessage(("Submitting your feedback..."));
                    progressDialog.show();

                    // get current user uid, since user is registered so we can get now
                    String uid = firebaseAuth.getUid();

                    // timestamp
                    long timestamp = System.currentTimeMillis();

                    // setup data to add in db
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("uid", uid);
                    hashMap.put("time", timestamp);
                    hashMap.put("feedback", feedback);

                    //set data to db
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Feedbacks");
                    ref.child(String.valueOf(timestamp))
                            .setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void unused)
                                {
                                    // data added to db
                                    progressDialog.dismiss();
                                    Toast.makeText(FeedbackActivity.this, "Successfully sent your feedback", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    // data failed adding to db
                                    progressDialog.dismiss();
                                    Toast.makeText(FeedbackActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });
    }


}