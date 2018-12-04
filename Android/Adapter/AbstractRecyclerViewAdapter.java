package distributor.w2a.com.distributor.adapter;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import distributor.w2a.com.distributor.R;
import distributor.w2a.com.distributor.activity.AbstractDashboardActivity;
import distributor.w2a.com.distributor.fragment.AbstractSearchFragment;
import distributor.w2a.com.distributor.interfaces.AbstractSearchInterface;

public abstract class AbstractRecyclerViewAdapter<T extends AbstractRecyclerViewAdapter.ViewHolder, X> extends RecyclerView.Adapter<T> implements Filterable {
    protected AbstractDashboardActivity abstractDashboardActivity;
    protected AbstractSearchInterface abstractSearchInterface;

    protected int[] androidColors;

    protected List<X> list;
    protected List<X> filteredList;
    protected Callback<X> callback;

    public interface Callback<X> {
        void callback(int pos, X x, int mode);
    }

    public void setCallback(Callback<X> callback) {
        this.callback = callback;
    }

    protected abstract boolean doesPassFilter(X x, String s);

    protected abstract char getFirstLetterToDisplay(X x);

    public AbstractRecyclerViewAdapter(AbstractDashboardActivity abstractDashboardActivity, List<X> list) {
        this.abstractDashboardActivity = abstractDashboardActivity;
        this.list = list;
        this.filteredList = list;
        androidColors = abstractDashboardActivity.getResources().getIntArray(R.array.androidcolors);
    }

    public void callHideKeyboard() {
        abstractDashboardActivity.hideItem();
    }

    public void setAbstractFragment(AbstractSearchInterface abstractSearchInterface) {
        this.abstractSearchInterface = abstractSearchInterface;
    }

    protected String convertToString(double value, int afterZero) {
        if (value == 0D)
            return "";
        return String.format("%." + afterZero + "f", value);
    }

    protected String convertToString(int value, int afterZero) {
        if (afterZero <= 0)
            return String.valueOf(value);
        return String.format("%." + afterZero + "d", value);
    }

    protected String convertToString(long value, int afterZero) {
        if (afterZero <= 0)
            return String.valueOf(value);
        return String.format("%." + afterZero + "d", value);
    }

    protected boolean isFilteredResults() {
        return filteredList.size() != list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {
        X x = filteredList.get(position);

        int randomAndroidColor = androidColors[position % androidColors.length];

        Drawable drawable = ContextCompat.getDrawable(abstractDashboardActivity, R.drawable.circle);
        drawable.setColorFilter(randomAndroidColor, PorterDuff.Mode.SRC_ATOP);
        try {
            holder.firstLetterTv.setText(String.valueOf(getFirstLetterToDisplay(x)).toUpperCase());
        } catch (Exception e) {
        }
        holder.firstLetterTv.setBackground(drawable);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList = list;
                } else {
                    List<X> filteredList = new ArrayList<>();
                    for (X row : list) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        try {
                            if (doesPassFilter(row, charString)) {
                                filteredList.add(row);
                            }
                        } catch (Exception e) {
                            filteredList.add(row);
                        }
                    }

                    AbstractRecyclerViewAdapter.this.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<X>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    protected X getModel(int position) {
        return filteredList.get(position);
    }

    protected void deleteModelFromList(X x) {
        filteredList.remove(x);
        list.remove(x);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstLetterTv;

        ViewHolder(View itemView) {
            super(itemView);
            firstLetterTv = itemView.findViewById(R.id.first_letter_tv);
        }
    }
}