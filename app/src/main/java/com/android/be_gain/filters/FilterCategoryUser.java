package com.android.be_gain.filters;

import android.widget.Filter;

import com.android.be_gain.adapters.AdapterCategoryUser;
import com.android.be_gain.models.ModelCategoryUser;

import java.util.ArrayList;

public class FilterCategoryUser extends Filter {

    // arraylist in which we want to search
    ArrayList<ModelCategoryUser> filterList;

    // adapter in which filter need to be implemented
    AdapterCategoryUser adapterCategoryUser;

    public FilterCategoryUser(ArrayList<ModelCategoryUser> filterList, AdapterCategoryUser adapterCategoryUser) {
        this.filterList = filterList;
        this.adapterCategoryUser = adapterCategoryUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // value should not be null and empty
        if (constraint != null && constraint.length() > 0)
        {
            // change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelCategoryUser> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++)
            {
                // validate
                if (filterList.get(i).getCategory().toUpperCase().contains(constraint))
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
        adapterCategoryUser.categoryArrayList = (ArrayList<ModelCategoryUser>)results.values;

        // notify changes
        adapterCategoryUser.notifyDataSetChanged();

    }
}
