package com.android.be_gain.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.be_gain.MyApplication;
import com.android.be_gain.activities.PdfDetailActivity;
import com.android.be_gain.activities.PdfEditActivity;
import com.android.be_gain.databinding.RowPdfAdminBinding;
import com.android.be_gain.filters.FilterPdfAdmin;
import com.android.be_gain.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    // context
    private Context context;
    // arraylist to hold list of type ModelPdf
    public ArrayList<ModelPdf> pdfArrayList, filterList;

    // view binding row_pdf_admin.xml
    private RowPdfAdminBinding binding;

    private FilterPdfAdmin filter;

    private static final String TAG = "PDF_ADAPTER_TAG";

    // progress
    private ProgressDialog progressDialog;

    // constructor
    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        // init progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind layout using view binding
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfAdmin.HolderPdfAdmin holder, int position) {
        /* Get data, set data, handle clicks.... */

        // get data
        ModelPdf model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        long timestamp = model.getTimestamp();

        // we need to convert timestamp to dd/MM/yyyy format
        String formattedDate = MyApplication.formatTimestamp(timestamp);

        // set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);

        // load further details like category, pdf from url, pdf size in separate functions
        MyApplication.loadCategory(
                ""+categoryId,
                holder.categoryTv
        );

        MyApplication.loadPdfFromUrlSinglePage(
                ""+pdfUrl,
                ""+title,
                holder.pdfView,
                holder.progressBar
        );


        MyApplication.loadPdfSize(
                ""+pdfUrl,
                ""+title,
                holder.sizeTv
        );

        // handle click, show dialog with options 1) edit 2) delete
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionDialog(model, holder);
            }
        });

        // handle click, open pdf details page, pass pdf/note id to get details of it
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("nodeId", pdfId);
                context.startActivity(intent);
            }
        });

    }

    private void moreOptionDialog(ModelPdf model, HolderPdfAdmin holder) {

        String noteId = model.getId();
        String noteUrl = model.getUrl();
        String noteTitle = model.getTitle();

        // options to show in dialog
        String[] options = {"Edit", "Delete"};

        // alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // handle dialog option click
                        if (which == 0)
                        {
                            // Edit clicked, open PdfEditActivity to edit note info
                            Intent intent = new Intent(context, PdfEditActivity.class);
                            intent.putExtra("noteId", noteId);
                            context.startActivity(intent);

                        }
                        else if(which == 1)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Delete");
                            builder.setMessage("Are you sure You want to delete?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    MyApplication.deleteNote(
                                            context,
                                            ""+noteId,
                                            ""+noteUrl,
                                            ""+noteTitle
                                    );

                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.create().show();

                        }
                    }
                })
                .show();
    }


    @Override
    public int getItemCount() {
        return pdfArrayList.size();  // return number of records | list size
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
        {
            filter = new FilterPdfAdmin(filterList, this);
        }
        return filter;
    }

    /* View holder class for row_pdf_admin.xml*/
    class HolderPdfAdmin extends RecyclerView.ViewHolder{

        // UI views of row_pdf_admin.xml
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv, descriptionTv, categoryTv, sizeTv, dateTv;
        ImageButton moreBtn;

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);

            // init ui views
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
            moreBtn = binding.moreBtn;

        }
    }

}
