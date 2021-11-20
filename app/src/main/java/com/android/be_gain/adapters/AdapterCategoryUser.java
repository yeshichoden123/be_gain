package com.android.be_gain.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.be_gain.activities.PdfListUserActivity;
import com.android.be_gain.databinding.RowCategoryUserBinding;
import com.android.be_gain.filters.FilterCategoryUser;
import com.android.be_gain.models.ModelCategoryUser;

import java.util.ArrayList;

public class AdapterCategoryUser extends RecyclerView.Adapter<AdapterCategoryUser.HolderCategoryUser> implements Filterable {

    private Context context;
    public ArrayList<ModelCategoryUser> categoryArrayList, filterList;

    // view binding
    private RowCategoryUserBinding binding;

    // instance of our filter class
    private FilterCategoryUser filter;

    public AdapterCategoryUser(Context context, ArrayList<ModelCategoryUser> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.filterList = categoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategoryUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind row_category.xml
        binding = RowCategoryUserBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderCategoryUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategoryUser holder, int position) {

        // get data
        ModelCategoryUser model = categoryArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        String timestamp = model.getTimestamp();

        // set data
        holder.categoryTv.setText(category);

        // handle item click, goto PdfListAdminActivity, also pass pdf categoryId
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfListUserActivity.class);
                intent.putExtra("categoryId", id);
                intent.putExtra("categoryTitle", category);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
        {
            filter = new FilterCategoryUser(filterList, this);
        }
        return filter;
    }


    /* View holder to UI views for row_category.xml*/
    class HolderCategoryUser extends RecyclerView.ViewHolder
    {

        // ui views of row_category.xml
        TextView categoryTv;

        public HolderCategoryUser(@NonNull View itemView)
        {
            super(itemView);
            categoryTv = binding.categoryTv;

        }
    }
}
