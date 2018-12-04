package distributor.w2a.com.distributor.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import distributor.w2a.com.distributor.R;

public class OptionBottomSheetAdapter<X> extends RecyclerView.Adapter<OptionBottomSheetAdapter.OptionBottomSheetHolder> implements Filterable {
    private List<X> list;
    private List<X> filteredList;
    private Context mContext;
    private int[] androidColors;
    private ListOnItemClickListener listOnItemClickListener;

    public interface ListOnItemClickListener<X> {
        void onItemClick(X stateForSpinner);
    }

    public OptionBottomSheetAdapter(List<X> list, Context mContext, ListOnItemClickListener<X> listOnItemClickListener) {
        this.list = list;
        this.filteredList = list;
        this.mContext = mContext;
        this.listOnItemClickListener = listOnItemClickListener;
        androidColors = mContext.getResources().getIntArray(R.array.android_colors_list);
    }

    @Override
    public OptionBottomSheetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.auto_search_option_item_layout, parent, false);
        OptionBottomSheetHolder viewHolder = new OptionBottomSheetHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OptionBottomSheetAdapter.OptionBottomSheetHolder holder, final int position) {
        X x = filteredList.get(position);

        int randomAndroidColor = androidColors[position % androidColors.length];


        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.hollow_circle);
        drawable.setColorFilter(randomAndroidColor, PorterDuff.Mode.SRC_ATOP);

        holder.itemTag.setBackground(drawable);
        holder.itemTag.setText(String.valueOf(x.toString().charAt(0)));
        holder.item.setText(x.toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listOnItemClickListener.onItemClick(filteredList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class OptionBottomSheetHolder extends RecyclerView.ViewHolder {
        TextView itemTag;
        TextView item;

        public OptionBottomSheetHolder(View itemView) {
            super(itemView);
            itemTag = itemView.findViewById(R.id.option_label);
            item = itemView.findViewById(R.id.option_item);
        }
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
                    for (X x : list) {
                        if (x.toString().contains(charString.toLowerCase())) {
                            filteredList.add(x);
                        }
                    }

                    OptionBottomSheetAdapter.this.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (List<X>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}