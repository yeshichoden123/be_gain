package com.android.be_gain.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.android.be_gain.adapters.AdapterPdfUser;
import com.android.be_gain.databinding.ActivityPdfListUserBinding;
import com.android.be_gain.models.ModelPdfUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PdfListUserActivity extends AppCompatActivity {

    // viewbinding
    private ActivityPdfListUserBinding binding;

    //    // arraylist to hold list of type ModelPdfUser
    private ArrayList<ModelPdfUser> pdfArrayList;

    // adapter
    private AdapterPdfUser adapterPdfUser;

    private String categoryId, categoryTitle;

    private static final String TAG = "PDF_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfListUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get data from intent
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

        // set pdf category
        binding.subTitleTv.setText(categoryTitle);

        loadPdfList();

        // search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // search as and when user type each letter
                try {
                    adapterPdfUser.getFilter().filter(s);
                }
                catch (Exception e)
                {
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // handle click, go to previous activity
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadPdfList() {
        // init list before adding data
        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notes");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();

                        for (DataSnapshot ds: snapshot.getChildren())
                        {
                            // get data
                            ModelPdfUser model = ds.getValue(ModelPdfUser.class);

                            // add to list
                            pdfArrayList.add(model);

                            Log.d(TAG, "onDataChange: "+model.getId()+" "+model.getTitle());
                        }
                        // setup adapter
                        adapterPdfUser = new AdapterPdfUser(PdfListUserActivity.this, pdfArrayList);
                        binding.bookRv.setAdapter(adapterPdfUser);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}