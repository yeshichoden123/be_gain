package com.android.be_gain.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.be_gain.MyApplication;
import com.android.be_gain.databinding.ActivityPdfDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PdfDetailActivity extends AppCompatActivity {

    // view binding
    private ActivityPdfDetailBinding binding;

    // Pdf id, get from intent
    String noteId, noteTitle, noteUrl;

    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get data from intent e.g noteId
        Intent intent = getIntent();
        noteId = intent.getStringExtra("nodeId");

        // at start hide downlaod button, because we need note url that we will load later by loadNoteDetails();
        binding.downloadNoteBtn.setVisibility(View.GONE);

        loadNoteDetails();

        // increment book view count, whenever this page starts
        MyApplication.incrementNoteViewCount(noteId);

        // handle click, goback
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // handle click, open to view pdf
        binding.readNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(PdfDetailActivity.this, PdfViewActivity.class);
                intent1.putExtra("noteId", noteId);
                startActivity(intent1);
            }
        });

        // handle click, download pdf
        binding.downloadNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG_DOWNLOAD, "onClick: Checking permission");
                if (ContextCompat.checkSelfPermission(PdfDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG_DOWNLOAD, "onClick: Permission already granted, can download book");
                    MyApplication.downloadNote(PdfDetailActivity.this, ""+noteId, ""+noteTitle, ""+noteUrl);
                }
                else
                {
                    Log.d(TAG_DOWNLOAD, "onClick: Permission was not granted, request permission...");
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

            }
        });

    }

    // request storage permission
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted)
                {
                    Log.d(TAG_DOWNLOAD, "Permission Granted");
                    MyApplication.downloadNote(this, ""+noteId, ""+noteTitle, ""+noteUrl);
                }
                else
                {
                    Log.d(TAG_DOWNLOAD, "Permission was denied...");
                    Toast.makeText(this, "Permission was denied...", Toast.LENGTH_SHORT).show();
                }
            });

    private void loadNoteDetails() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notes");
        ref.child(noteId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // get data
                        noteTitle = ""+snapshot.child("title").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        String downloadsCount = ""+snapshot.child("downloadsCount").getValue();
                        noteUrl = ""+snapshot.child("url").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();

                        // required data is loaded, show download button
                        binding.downloadNoteBtn.setVisibility(View.VISIBLE);

                        // format date
                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        MyApplication.loadCategory(
                                ""+categoryId,
                                binding.categoryTv
                        );

                        MyApplication.loadPdfFromUrlSinglePage(
                                ""+noteUrl,
                                ""+noteTitle,
                                binding.pdfView,
                                binding.progressBar
                        );

                        MyApplication.loadPdfSize(
                                ""+noteUrl,
                                ""+noteTitle,
                                binding.sizeTv
                        );

                        // set data
                        binding.titleTv.setText(noteTitle);
                        binding.descriptionTv.setText(description);
                        binding.viewsTv.setText(viewsCount.replace("null", "N/A"));
                        binding.downloadsTv.setText(downloadsCount.replace("null", "N/A"));
                        binding.dateTv.setText(date);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}