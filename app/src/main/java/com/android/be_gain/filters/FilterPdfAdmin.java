package com.android.be_gain.filters;
import android.widget.Filter;

import com.android.be_gain.adapters.AdapterCategory;
import com.android.be_gain.adapters.AdapterPdfAdmin;
import com.android.be_gain.models.ModelCategory;
import com.android.be_gain.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfAdmin extends Filter {

    // arraylist in which we want to search
    ArrayList<ModelPdf> filterList;

    // adapter in which filter need to be implemented
    AdapterPdfAdmin adapterPdfAdmin;

    // constructor

    public FilterPdfAdmin(ArrayList<ModelPdf> filterList, AdapterPdfAdmin adapterPdfAdmin) {
        this.filterList = filterList;
        this.adapterPdfAdmin = adapterPdfAdmin;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // value should not be null and empty
        if (constraint != null && constraint.length() > 0)
        {
            // change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++)
            {
                // validate
                if (filterList.get(i).getTitle().toUpperCase().contains(constraint))
                {
                    // add to filtered list
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else
        {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results; // don't miss
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        // apply filter changes
        adapterPdfAdmin.pdfArrayList = (ArrayList<ModelPdf>)results.values;

        // notify changes
        adapterPdfAdmin.notifyDataSetChanged();
    }
}
