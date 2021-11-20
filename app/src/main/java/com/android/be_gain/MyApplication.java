package com.android.be_gain;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.be_gain.adapters.AdapterPdfAdmin;
import com.android.be_gain.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.android.be_gain.Constants.MAX_BYTES_PDF;

// application class runs before launcher activity
public class MyApplication extends Application {

    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // created a static method to convert timestamp to prepare date format, so we can use it everywhere in project, no need to rewrite again
    public static final String formatTimestamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);

        // format timestamp to dd/MM/yyyy
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();

        return date;
    }

    public static void deleteNote(Context context, String noteId, String noteUrl, String noteTitle) {

        String TAG = "DELETE_NOTE_TAG";

        Log.d(TAG, "deleteNote: Deleting...");
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait.");
        progressDialog.setMessage("Deleting "+noteTitle+"..."); //e.g Deleting Note abc
        progressDialog.show();

        Log.d(TAG, "deleteNote: Deleting from storage...");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(noteUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Deleted from storage");

                        Log.d(TAG, "onSuccess: Now deleting info from db");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notes");
                        reference.child(noteId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: Deleted from db too");
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Failed to delete from db due to "+e.getMessage());
                                        progressDialog.dismiss();
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete from storage due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void loadPdfSize(String pdfUrl, String pdfTitle, TextView sizeTv) {
        String TAG = "PDF_SIZE_TAG";
        // using url we can get file and its metadata from firebase storage

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        // get size in bytes
                        double bytes = storageMetadata.getSizeBytes();
                        Log.d(TAG, "onSuccess: "+pdfTitle +" "+bytes);

                        // convert bytes to KB, Mb
                        double kb = bytes/1024;
                        double mb = kb/1024;

                        if (mb >=1)
                        {
                            sizeTv.setText(String.format("%.2f", mb)+" MB");
                        }
                        else if (kb >=1)
                        {
                            sizeTv.setText(String.format("%.2f", kb)+" KB");
                        }
                        else
                        {
                            sizeTv.setText(String.format("%.2f", bytes)+" Bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed getting metadata
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });
    }

    public static void loadPdfFromUrlSinglePage(String pdfUrl, String pdfTitle, PDFView pdfView, ProgressBar progressBar) {
        // using url we can get file and its metadata from firebase storage
        String TAG = "PDF_LOAD_SINGlE_TAG";

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "onSuccess: "+pdfTitle +" successfully got the file");

                        // set pdfview
                        pdfView.fromBytes(bytes)
                                .pages(0) // show only first page
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {

                                        // hide progress
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onError: "+t.getMessage());
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {

                                        // hide progress
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onError: "+t.getMessage());
                                    }
                                })
                                .onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        // pdf loaded
                                        // hide progress
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "LoadComplete: pdf loaded");
                                    }
                                })
                                .load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // hide progress
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onFailure: Failed getting file from url due to "+e.getMessage());

                    }
                });

    }

    public static void loadCategory(String categoryId, TextView categoryTv) {
        // get category using categoryId

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // get category
                        String category = ""+snapshot.child("category").getValue();

                        // set to category text view
                        categoryTv.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void incrementNoteViewCount(String noteId)
    {

        // 1) get note views count
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notes");
        ref.child(noteId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // get views count
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();

                        // in case of null replace with 0
                        if (viewsCount.equals("") || viewsCount.equals("null"))
                        {
                            viewsCount = "0";
                        }

                        // 2) increment views count
                        long newViewsCount = Long.parseLong(viewsCount) + 1;

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("viewsCount", newViewsCount);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notes");
                        reference.child(noteId)
                                .updateChildren(hashMap);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    public static void downloadNote(Context context, String noteId, String noteTitle, String noteUrl)
    {
        Log.d(TAG_DOWNLOAD, "downloadBook: downloading book...");

        String nameWithExtension = noteTitle + ".pdf";
        Log.d(TAG_DOWNLOAD, "downloadNote: NAME: "+nameWithExtension);

        // progress dialog
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Downloading "+nameWithExtension+" ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // download from firebase storage using url
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(noteUrl);
        storageReference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG_DOWNLOAD, "onSuccess: Note Downloaded");
                        Log.d(TAG_DOWNLOAD, "onSuccess: Saving book...");
                        saveDownloadedNote(context, progressDialog, bytes, nameWithExtension, noteId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_DOWNLOAD, "onFailure: Failed to download due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed to download due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private static void saveDownloadedNote(Context context, ProgressDialog progressDialog, byte[] bytes, String nameWithExtension, String noteId)
    {

        Log.d(TAG_DOWNLOAD, "saveDownloadedNote: Saving downloaded note");
        try
        {

            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadsFolder.mkdirs();

            String filePath = downloadsFolder.getPath() + "/" + nameWithExtension;

            FileOutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.close();

            Toast.makeText(context, "Saved to Downloads Folder", Toast.LENGTH_SHORT).show();
            Log.d(TAG_DOWNLOAD, "savedDownloadedNote: Saved to Downloads Folder");
            progressDialog.dismiss();

            incrementNoteDownloadCount(noteId);


        }
        catch (Exception e)
        {
            Log.d(TAG_DOWNLOAD, "saveDownloadedBook: Failed saving to Downloads Folder due to "+e.getMessage());
            Toast.makeText(context, "Failed saving to Downloads Folder due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }

    private static void incrementNoteDownloadCount(String noteId) {

        Log.d(TAG_DOWNLOAD, "incrementNoteDownloadCount: Incrementing Note Downloads Count");


        // Step 1): Get previous download count
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notes");
        ref.child(noteId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String downloadsCount = ""+snapshot.child("downloadsCount").getValue();
                        Log.d(TAG_DOWNLOAD, "onDataChange: Downloads count: "+downloadsCount);

                        if (downloadsCount.equals("") || downloadsCount.equals("null"))
                        {
                            downloadsCount = "0";
                        }
                        // convert to long and increment
                        long newDownloadsCount = Long.parseLong(downloadsCount) + 1;
                        Log.d(TAG_DOWNLOAD, "onDataChange: New Download Count: "+newDownloadsCount);

                        // setup data to update
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("downloadsCount", newDownloadsCount);

                        // Step 2) Update new incremented downloads count to db
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notes");
                        reference.child(noteId).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG_DOWNLOAD, "onSuccess: Downloads count updated...");

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG_DOWNLOAD, "onFailure: Failed to update Downloads count due to "+e.getMessage());
                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}
