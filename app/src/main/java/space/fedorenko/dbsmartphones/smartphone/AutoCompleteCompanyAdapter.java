package space.fedorenko.dbsmartphones.smartphone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import space.fedorenko.dbsmartphones.R;

public class AutoCompleteCompanyAdapter extends ArrayAdapter<CompanyItem> {
    private List<CompanyItem> countryListFull;

    public AutoCompleteCompanyAdapter(@NonNull Context context, @NonNull List<CompanyItem> countryList) {
        super(context, 0, countryList);
        countryListFull = new ArrayList<>(countryList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return companyFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.company_autocomplete_row, parent, false
            );
        }
        TextView textViewName = convertView.findViewById(R.id.text_view_name);
        ImageView imageViewFlag = convertView.findViewById(R.id.image_view_flag);
        CompanyItem countryItem = getItem(position);

        if (countryItem != null) {
            textViewName.setText(countryItem.getCompanyName());
            imageViewFlag.setImageResource(countryItem.getLogoImage());
        }
        return convertView;
    }

    private Filter companyFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<CompanyItem> suggestions = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(countryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CompanyItem item : countryListFull) {
                    if (item.getCompanyName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((CompanyItem) resultValue).getCompanyName();
        }
    };
}
