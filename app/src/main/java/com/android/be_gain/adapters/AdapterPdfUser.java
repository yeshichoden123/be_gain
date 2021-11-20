package com.android.be_gain.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.be_gain.MyApplication;
import com.android.be_gain.activities.PdfDetailActivity;
import com.android.be_gain.activities.PdfDetailUserActivity;
import com.android.be_gain.databinding.RowPdfUserBinding;
import com.android.be_gain.filters.FilterPdfUser;
import com.android.be_gain.models.ModelPdfUser;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfUser extends RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser> implements Filterable{

    // context
    private Context context;
    // arraylist to hold list of type ModelPdf
    public ArrayList<ModelPdfUser> pdfArrayList, filterList;

    private RowPdfUserBinding binding;

    private FilterPdfUser filter;

    // progress
    private ProgressDialog progressDialog;

    public AdapterPdfUser(Context context, ArrayList<ModelPdfUser> pdfArrayList) {
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
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind layout using view binding
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfUser holder, int position) {

        /* Get data, set data, handle clicks.... */

        // get data
        ModelPdfUser model = pdfArrayList.get(position);
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

        // handle click, open pdf details page, pass pdf/note id to get details of it
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailUserActivity.class);
                intent.putExtra("nodeId", pdfId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
        {
            filter = new FilterPdfUser(filterList, this);
        }
        return filter;
    }

    class HolderPdfUser extends RecyclerView.ViewHolder
    {

        // UI views of row_pdf_admin.xml
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv, descriptionTv, categoryTv, sizeTv, dateTv;

        public HolderPdfUser(@NonNull View itemView)
        {
            super(itemView);

            // init ui views
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;

        }
    }

}
